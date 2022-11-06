package id.radhikayusuf.lib.deeplinkerprocessor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import id.radhikayusuf.lib.deeplinker.annotations.Deeplink
import id.radhikayusuf.lib.deeplinker.annotations.Deeplinkable
import id.radhikayusuf.lib.deeplinker.annotations.DeeplinkerConst.DEEPLINKER_GENERATED_PACKAGE
import id.radhikayusuf.lib.deeplinker.annotations.DeeplinkerConst.DEEPLINKER_POSTFIX_CLASS_NAME
import id.radhikayusuf.lib.deeplinkerprocessor.utils.DeeplinkerClassComponentCreator
import id.radhikayusuf.lib.deeplinkerprocessor.utils.ResultReporter
import id.radhikayusuf.lib.deeplinkerprocessor.utils.TypeNameUtils.getDeeplinkTargetTypeName
import id.radhikayusuf.lib.deeplinkerprocessor.utils.addMandatoryDeeplinkerImports
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * @author radhikayusuf
 * Created 17/10/22
 */

@AutoService(Processor::class)
class DeeplinkConnectorGenerator : AbstractProcessor() {

    private val resultReporter: ResultReporter by lazy { ResultReporter(processingEnv) }
    private val propertiesCreator: DeeplinkerClassComponentCreator by lazy { DeeplinkerClassComponentCreator(resultReporter) }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Deeplink::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val entryFilesWithDeeplinkable = roundEnv.getElementsAnnotatedWith(Deeplinkable::class.java)
        for (element in entryFilesWithDeeplinkable) {
            val deeplinkEntryName = element.simpleName.toString()
            val generatedClassName = propertiesCreator.createGeneratedClassName(deeplinkEntryName)

            val entryHandlerClassTypeSpec = createEntryHandlerTypeSpec(deeplinkEntryName, element)
            val entryHandlerFileTypeSpec = createEntryHandlerFileSpec(generatedClassName, entryHandlerClassTypeSpec)

            val generatedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
            entryHandlerFileTypeSpec.writeTo(File(generatedDir, "$generatedClassName.kt"))
        }
        return false
    }

    private fun createEntryHandlerTypeSpec(deeplinkEntryName: String, element: Element): TypeSpec {
        val classBuilder = TypeSpec.classBuilder(propertiesCreator.createGeneratedClassName(deeplinkEntryName))
        classBuilder.addSuperinterface(getDeeplinkTargetTypeName())
        classBuilder.addProperty(propertiesCreator.createEntryProperty(element))
        classBuilder.addProperty(propertiesCreator.createDeeplinksProperty(deeplinkEntryName, element.enclosedElements))
        return classBuilder.build()
    }

    private fun createEntryHandlerFileSpec(generatedClassName: String, classTypeSpec: TypeSpec): FileSpec {
        return FileSpec.builder(DEEPLINKER_GENERATED_PACKAGE, generatedClassName)
            .addType(classTypeSpec)
            .addMandatoryDeeplinkerImports()
            .build()
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        val PARAMETER_RULES = arrayOf(
            "android.content.Context",
            "id.radhikayusuf.lib.deeplinker.model.DeeplinkData",
            "id.radhikayusuf.lib.deeplinker.DeeplinkRouter",
            "kotlin.coroutines.Continuation<? super kotlin.Unit>"
        )
    }
}