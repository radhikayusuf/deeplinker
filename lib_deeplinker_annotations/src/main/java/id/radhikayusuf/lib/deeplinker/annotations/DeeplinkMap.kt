package id.radhikayusuf.lib.deeplinker.annotations


/**
 * @author radhikayusuf
 * Created 27/08/22
 */

data class DeeplinkMap<A, B, C>(
    val entry: DeeplinkEntry,
    val closure: suspend (A, B, C) -> Unit,
    val hosts: List<String>,
    val pathPatterns: String,
    val authRequired: Boolean,
    val schemes: List<String>
)