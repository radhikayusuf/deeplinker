package id.radhikayusuf.lib.deeplinkerprocessor.utils

import javax.annotation.processing.ProcessingEnvironment
import javax.tools.Diagnostic

/**
 * @author radhikayusuf
 * Created 06/11/22
 */

class ResultReporter(
    private val processingEnv: ProcessingEnvironment
) {

    fun reportInvalidFuncFormat(moduleName: String, funcName: String) {
        processingEnv.messager.printMessage(
            Diagnostic.Kind.ERROR,
            "Illegal parameters for func: $moduleName#$funcName. Function with annotation @Deeplink should be suspend function and have only 3 parameters (Context, DeeplinkData, DeeplinkRouter)"
        )
    }

    fun reportInvalidPathPattern(moduleName: String, funcName: String, pathPattern: String) {
        processingEnv.messager.printMessage(
            Diagnostic.Kind.ERROR,
            "Invalid PathPattern ($moduleName#$funcName -> $pathPattern). PathPattern should start with /"
        )
    }

    fun log(log: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.OTHER, "radhika-log: $log")
    }
}