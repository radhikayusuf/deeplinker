package id.radhikayusuf.lib.deeplinker.model

data class DeeplinkMatcher(
    val isMatch: Boolean,
    val dynamicPaths: Map<String, String>
)