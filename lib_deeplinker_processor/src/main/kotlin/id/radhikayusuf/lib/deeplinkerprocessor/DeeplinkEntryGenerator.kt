package id.radhikayusuf.lib.deeplinkerprocessor

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.writeTo
import id.radhikayusuf.lib.deeplinker.annotations.model.Signal
import id.radhikayusuf.lib.deeplinker.annotations.utils.DeeplinkerConst
import id.radhikayusuf.lib.deeplinkerprocessor.model.DeeplinkEntryMetaData
import id.radhikayusuf.lib.deeplinkerprocessor.model.DeeplinkMetaData
import id.radhikayusuf.lib.deeplinkerprocessor.utils.DeeplinkerConst.PACKAGE
import id.radhikayusuf.lib.deeplinkerprocessor.utils.TypeNameUtils
import id.radhikayusuf.lib.deeplinkerprocessor.utils.TypeNameUtils.getNewDeeplinkTargetTypeName
import id.radhikayusuf.lib.deeplinkerprocessor.utils.TypeNameUtils.getSimpleName

/**
 * @author radhikayusuf
 * Created 23/11/22
 */

@KotlinPoetKspPreview
class DeeplinkEntryGenerator(private val codeGenerator: CodeGenerator) {

    fun generate(contentMetaData: List<DeeplinkMetaData>) {
        contentMetaData.forEach { deeplinkMetaData ->
            val generatedClassName = createGeneratedClassName(deeplinkMetaData.entryName)
            val entryPropertySpec =
                createEntryProperty(deeplinkMetaData.packageName, deeplinkMetaData.entryName)
            val deeplinkPropertySpec =
                createDeeplinkProperty(deeplinkMetaData.entryName, deeplinkMetaData.deeplinkEntries)

            val typeSpec: TypeSpec =
                TypeSpec.classBuilder(generatedClassName)
                    .addSuperinterface(getNewDeeplinkTargetTypeName())
                    .addProperty(entryPropertySpec)
                    .addProperty(deeplinkPropertySpec)
                    .primaryConstructor(createContextConstructorProperty())
                    .build()

            val spec: FileSpec.Builder =
                FileSpec.builder(PACKAGE, generatedClassName)
                    .addType(typeSpec)

            spec.build().writeTo(codeGenerator, true)
        }
    }

    private fun createEntryProperty(packageName: String, entryClassName: String): PropertySpec {
        val entryTypeName = ClassName(packageName, entryClassName)
        val propClass = PropertySpec.builder("instance$entryClassName", entryTypeName, KModifier.PRIVATE)
        return propClass.mutable(false).initializer("$entryClassName(context)").build()
    }

    private fun createDeeplinkProperty(
        entryName: String,
        deeplinkEntries: List<DeeplinkEntryMetaData>
    ): PropertySpec {
        val initializer = createDeeplinkInitializer(entryName, deeplinkEntries)
        val propBuilder = PropertySpec.builder(
            name = "deeplinks",
            type = TypeNameUtils.getListOfDeeplinkSignalTypeName(),
            modifiers = arrayOf(KModifier.PUBLIC, KModifier.OVERRIDE)
        ).mutable(false).initializer(initializer)
        return propBuilder.build()
    }

    private fun createDeeplinkInitializer(
        entryName: String,
        listOfMapEntry: List<DeeplinkEntryMetaData>
    ): String {
        var initializerValue = """
            arrayListOf<${getSimpleName(Signal::class)}>().apply {
            
        """.trimIndent()
        listOfMapEntry.forEach {
            val annotation = it.annotation
            val hosts = annotation.hosts.joinToString(prefix = "\"", postfix = "\"")
            val pathPatterns = "\"${annotation.pathPatterns}\""
            val authType = annotation.authRequired
            val schemas = annotation.schemes.joinToString(prefix = "\"", postfix = "\"")

            initializerValue +=
                "add(${getSimpleName(Signal::class)}(" +
                        "listOf($hosts), " +
                        "$pathPatterns, " +
                        "$authType, " +
                        "listOf($schemas), " +
                        "instance$entryName::${it.funcName})" +
                        ");\n"
        }
        initializerValue += """
            }
        """.trimIndent()

        return initializerValue
    }

    private fun createGeneratedClassName(deeplinkEntryName: String): String {
        return "${deeplinkEntryName.replaceFirstChar { it.uppercaseChar() }}${DeeplinkerConst.DEEPLINKER_POSTFIX_CLASS_NAME}"
    }

    private fun createContextConstructorProperty(): FunSpec {
        val propClass = FunSpec.builder("constructor()")
        return propClass.addParameter(
            "context", TypeNameUtils.getAndroidContextTypeName()
        ).build()
    }
}