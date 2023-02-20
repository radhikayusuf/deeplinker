package id.radhikayusuf.lib.deeplinkerprocessor.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import id.radhikayusuf.lib.deeplinker.annotations.model.Result

/**
 * @author radhikayusuf
 * Created 12/02/23
 */

object FunctionValidator {

    fun isParametersMatches(parameters: List<KSTypeReference>): Boolean {
        if (parameters.size != 1) return false
        val stringParams = parameters.map { it.toString() }
        return Result::class.java.simpleName in stringParams
    }

    fun validateEntriesAndThrow(logger: Logger, func: KSFunctionDeclaration) {
        if (!isParametersMatches(func.parameters.map { it.type })) {
            val packageName = func.packageName.asString()
            val className = (func.parent as? KSClassDeclaration)?.simpleName?.asString()
            val funcName = func.simpleName.asString()
            val errorLocation = "$packageName.$className#$funcName"
            logger.error(
                "Error at ${errorLocation}. \nParameter is not match the rule, " +
                        "please put ${Result::class.java.simpleName} as a only parameters in $funcName functions"
            )
        }
    }
}