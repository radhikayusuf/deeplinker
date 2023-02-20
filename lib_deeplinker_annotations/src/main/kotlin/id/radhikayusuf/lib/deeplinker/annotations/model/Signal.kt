package id.radhikayusuf.lib.deeplinker.annotations.model

/**
 * @author radhikayusuf
 * Created 12/12/22
 */

data class Signal(
    val hosts: List<String>,
    val pathPatterns: String,
    val authRequired: Boolean,
    val schemes: List<String>,
    val closure: DeeplinkClosure
)

typealias DeeplinkClosure = suspend (Result) -> Unit