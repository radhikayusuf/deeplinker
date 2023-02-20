package id.radhikayusuf.lib.deeplinkerprocessor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import id.radhikayusuf.lib.deeplinker.annotations.Deeplink
import id.radhikayusuf.lib.deeplinker.annotations.Deeplinkable
import id.radhikayusuf.lib.deeplinker.annotations.utils.DeeplinkerConst
import id.radhikayusuf.lib.deeplinkerprocessor.model.DeeplinkEntryMetaData
import id.radhikayusuf.lib.deeplinkerprocessor.model.DeeplinkMetaData
import id.radhikayusuf.lib.deeplinkerprocessor.utils.DeeplinkerConst.PACKAGE
import id.radhikayusuf.lib.deeplinkerprocessor.utils.FunctionValidator
import id.radhikayusuf.lib.deeplinkerprocessor.utils.Logger

/**
 * @author radhikayusuf
 * Created 23/11/22
 */

class DeeplinkSymbolProcessor(
    private val processingEnv: SymbolProcessorEnvironment,
    private val options: Map<String, String>,
) : SymbolProcessor {

    private val logger by lazy { Logger(processingEnv) }

    @KspExperimental
    @KotlinPoetKspPreview
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val deeplinkableQualifiedName = Deeplinkable::class.qualifiedName ?: error("Annotation name cannot be resolved")
        val annotationNameDeeplink = Deeplink::class.qualifiedName ?: error("Annotation name cannot be resolved")
        val entryPoints =
            resolver.getSymbolsWithAnnotation(deeplinkableQualifiedName).mapNotNull { it as? KSClassDeclaration }

        val constructorContainContextParam = checkEntryPointsHasContext(entryPoints)
        if (entryPoints.toList().isEmpty()) {
            return emptyList()
        } else if (constructorContainContextParam.first) {
            logger.error("Please put context as a constructor parameter at ${constructorContainContextParam.second}.")
            return emptyList()
        }

        val deeplinkSymbols = resolver.getSymbolsWithAnnotation(annotationNameDeeplink)
        if (deeplinkSymbols.any { it !is KSFunctionDeclaration }) {
            logger.error("@Deeplink Annotation can only be applied to method")
            return emptyList()
        }

        val isKSPForApplicationModule = isKSPForApplicationModule(options)
        val listOfDeeplinkMetaData = arrayListOf<DeeplinkMetaData>()

        entryPoints.forEach { classInfo ->
            val packageName = classInfo.packageName.asString()
            val className = classInfo.simpleName.asString()
            val deeplinkData = arrayListOf<DeeplinkEntryMetaData>()

            classInfo.getDeclaredFunctions().forEach { func ->
                val deeplinkAnnotation = func.getAnnotationsByType(Deeplink::class).firstOrNull()
                if (deeplinkAnnotation != null) {
                    FunctionValidator.validateEntriesAndThrow(logger, func)
                    deeplinkData.add(DeeplinkEntryMetaData(func.simpleName.asString(), deeplinkAnnotation))
                }
            }
            listOfDeeplinkMetaData.add(DeeplinkMetaData(className, packageName, deeplinkData))
        }

        val deeplinkEntryGenerator = DeeplinkEntryGenerator(processingEnv.codeGenerator)
        deeplinkEntryGenerator.generate(listOfDeeplinkMetaData)

        if (isKSPForApplicationModule) {
            val generatedClasses = resolver.getDeclarationsFromPackage(PACKAGE)
                .filter {
                    val className = (it as? KSClassDeclaration)?.simpleName?.asString()
                    className.orEmpty().endsWith(DeeplinkerConst.DEEPLINKER_POSTFIX_CLASS_NAME)
                }.mapNotNull {
                    it as? KSClassDeclaration
                }

            val deeplinkStoreGenerator = DeeplinkStoreGenerator(processingEnv.codeGenerator)
            deeplinkStoreGenerator.generate(
                generatedClasses.toList().map { it.simpleName.asString() } +
                        entryPoints.map { it.simpleName.asString() + DeeplinkerConst.DEEPLINKER_POSTFIX_CLASS_NAME }
            )
        }
        return emptyList()
    }

    private fun checkEntryPointsHasContext(entryPoints: Sequence<KSClassDeclaration>): Pair<Boolean, String> {
        var isConstructorContainContextParam = true
        var errorLocation = ""
        entryPoints.forEach { clazz ->
            val hasContextParam =
                clazz.primaryConstructor?.parameters.orEmpty().any { param ->
                    param.type.toString().equals("Context", true)
                }
            if (!hasContextParam) {
                errorLocation =
                    "${clazz.packageName.asString()}.${clazz.simpleName.asString()}#constructor()"
            }
            isConstructorContainContextParam = !hasContextParam
            if (!isConstructorContainContextParam) return false to errorLocation
        }
        return isConstructorContainContextParam to errorLocation
    }

    private fun isKSPForApplicationModule(options: Map<String, String>): Boolean {
        return options["module"] == "main"
    }
}