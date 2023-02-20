package id.radhikayusuf.lib.deeplinkerprocessor.utils

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

/**
 * @author radhikayusuf
 * Created 23/11/22
 */

class Logger(private val env: SymbolProcessorEnvironment) {
    fun error(message: String) = env.logger.error(message)
    fun note(message: String) = env.logger.info(message)
}