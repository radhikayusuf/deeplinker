package id.radhikayusuf.lib.deeplinker.model

data class DeeplinkData(
    val schema: String,
    val host: String,
    val path: String,
    val queries: Map<String, String> = mapOf(),
    val dynamicPaths: Map<String, String> = mapOf(),
    val fullUrl: String
)