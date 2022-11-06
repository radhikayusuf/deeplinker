package id.radhikayusuf.lib.deeplinkerprocessor.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import id.radhikayusuf.lib.deeplinker.annotations.Deeplink
import id.radhikayusuf.lib.deeplinker.annotations.DeeplinkerConst
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

/**
 * @author radhikayusuf
 * Created 06/11/22
 */

class DeeplinkerClassComponentCreator(
    private val resultReporter: ResultReporter
) {

    fun createDeeplinksProperty(moduleName: String, elementInModule: List<Element>): PropertySpec {
        val propBuilder = PropertySpec.builder(
            name = "deeplinks",
            type = TypeNameUtils.getDeeplinkMapTypeName(),
            modifiers = arrayOf(KModifier.PUBLIC, KModifier.OVERRIDE)
        ).mutable(false).initializer(createDeeplinksPropertyInitializer(moduleName, elementInModule))
        return propBuilder.build()
    }

    fun createEntryProperty(entryElement: Element): PropertySpec {
        val entryClassName = entryElement.simpleName.toString()
        val packageName = entryElement.asType().toString().replace(".$entryClassName", "")
        val entryTypeName = ClassName(packageName, entryClassName)
        val propClass = PropertySpec.builder("instance$entryClassName", entryTypeName, KModifier.PRIVATE)
        return propClass.mutable(false).initializer("$entryClassName()").build()
    }

    private fun createDeeplinksPropertyInitializer(
        moduleName: String,
        elementInModule: List<Element>
    ): String {
        val listOfMapEntry = arrayListOf<String>()
        elementInModule.onlyFunctionWithDeeplinkAnnotation()
            .forEach { el ->
                val funcName = el.simpleName
                if (el is ExecutableElement && !el.isFunctionHasInvalidFormat()) {
                    val deeplinkConf = el.getDeeplinkAnnotation()
                    validateDeeplinkConfig(moduleName, funcName.toString(), deeplinkConf)
                    listOfMapEntry.add(
                        createDeeplinkMapEntry(
                            moduleName,
                            funcName.toString(),
                            deeplinkConf
                        )
                    )
                } else {
                    resultReporter.reportInvalidFuncFormat(moduleName, funcName.toString())
                }
            }

        return createDeeplinkInitializer(listOfMapEntry)
    }

    private fun validateDeeplinkConfig(
        moduleName: String,
        funcName: String,
        deeplinkConfig: Deeplink
    ) {
        if (!deeplinkConfig.pathPatterns.startsWith("/")) {
            resultReporter.reportInvalidPathPattern(moduleName, funcName, deeplinkConfig.pathPatterns)
        }
    }

    private fun createDeeplinkInitializer(listOfMapEntry: List<String>): String {
        var initializerValue = """
            mutableMapOf<String, DeeplinkMap<Context, DeeplinkData, DeeplinkRouter>>().apply {
            
        """.trimIndent()
        listOfMapEntry.forEach {
            initializerValue += (it + "\n")
        }
        initializerValue += """
            }
        """.trimIndent()

        return initializerValue
    }

    private fun createDeeplinkMapEntry(
        moduleName: String,
        funcName: String,
        deeplinkConfig: Deeplink
    ): String {
        return """
            put(
                "${deeplinkConfig.pathPatterns}", 
                DeeplinkMap(
                    entry = instance$moduleName, 
                    hosts = listOf(${deeplinkConfig.hosts.map { "\"$it\"" }.joinToString()}),
                    pathPatterns = "${deeplinkConfig.pathPatterns}",
                    authRequired = ${deeplinkConfig.authRequired},
                    schemes = listOf(${deeplinkConfig.schemes.map { "\"$it\"" }.joinToString()}),
                    closure = instance$moduleName::$funcName
                )
            )
        """.trimIndent()
    }

    fun createGeneratedClassName(deeplinkEntryName: String): String {
        return "${deeplinkEntryName.replaceFirstChar { it.uppercaseChar() }}${DeeplinkerConst.DEEPLINKER_POSTFIX_CLASS_NAME}"
    }
}