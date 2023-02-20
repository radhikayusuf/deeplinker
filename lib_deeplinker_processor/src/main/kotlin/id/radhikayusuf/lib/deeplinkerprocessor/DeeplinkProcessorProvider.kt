package id.radhikayusuf.lib.deeplinkerprocessor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import id.radhikayusuf.lib.deeplinkerprocessor.utils.Logger

/**
 * @author radhikayusuf
 * Created 23/11/22
 */

class DeeplinkProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return DeeplinkSymbolProcessor(environment, environment.options)
    }
}