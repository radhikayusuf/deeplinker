package id.radhikayusuf.lib.deeplinkerprocessor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies.Companion.ALL_FILES
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.writeTo
import id.radhikayusuf.lib.deeplinker.annotations.RouteTarget
import id.radhikayusuf.lib.deeplinker.annotations.model.Signal
import id.radhikayusuf.lib.deeplinkerprocessor.utils.DeeplinkerConst.PACKAGE
import id.radhikayusuf.lib.deeplinkerprocessor.utils.TypeNameUtils
import id.radhikayusuf.lib.deeplinkerprocessor.utils.TypeNameUtils.getAndroidContextTypeName
import id.radhikayusuf.lib.deeplinkerprocessor.utils.TypeNameUtils.getPackageName
import id.radhikayusuf.lib.deeplinkerprocessor.utils.TypeNameUtils.getSimpleName

/**
 * @author radhikayusuf
 * Created 23/11/22
 */

@KotlinPoetKspPreview
class DeeplinkStoreGenerator(private val codeGenerator: CodeGenerator) {

    fun generate(generatedFileNames: List<String>) {
        val deeplinkStoreClassName = "DeeplinkStore"
        val typeSpecBuilder: TypeSpec.Builder = TypeSpec.classBuilder(deeplinkStoreClassName)
        var entryPointInitializer = """
            lazy { 
                arrayListOf<${getSimpleName(Signal::class)}>().apply {
        """.trimIndent()
        generatedFileNames.forEach { fileName ->
            val propClass = PropertySpec.builder(
                "_instance${fileName}",
                TypeNameUtils.getDeeplinkTargetTypeName(),
                KModifier.PRIVATE
            )
            entryPointInitializer += "\n\t\taddAll(_instance${fileName}.deeplinks)"
            val field = propClass.mutable(false).initializer("${fileName}(context)").build()
            typeSpecBuilder.addProperty(field)
        }
        entryPointInitializer += "\n\t}\n}"

        typeSpecBuilder.addProperty(createEntryProperty(entryPointInitializer))
        typeSpecBuilder.primaryConstructor(createContextConstructorProperty())

        val spec: FileSpec.Builder =
            FileSpec.builder(PACKAGE, deeplinkStoreClassName)
                .addImport(getPackageName(RouteTarget::class), getSimpleName(RouteTarget::class))
                .addType(typeSpecBuilder.build())

        spec.build().writeTo(codeGenerator, ALL_FILES)
    }

    private fun createEntryProperty(initializer: String): PropertySpec {
        val entryTypeName = TypeNameUtils.getListOfDeeplinkSignalTypeName()
        val lazyTypeName = TypeNameUtils.getLazyGeneratedTypeName(entryTypeName)
        val propClass = PropertySpec.builder("deeplinkContent", lazyTypeName, KModifier.PUBLIC)
        return propClass.mutable(false).initializer(initializer).build()
    }

    private fun createContextConstructorProperty(): FunSpec {
        val propClass = FunSpec.builder("constructor()")
        return propClass.addParameter(
            "context", getAndroidContextTypeName()
        ).build()
    }
}