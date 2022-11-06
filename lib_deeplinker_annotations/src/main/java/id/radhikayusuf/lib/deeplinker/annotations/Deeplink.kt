package id.radhikayusuf.lib.deeplinker.annotations

annotation class Deeplink(
    val hosts: Array<String>,
    val pathPatterns: String,
    val authRequired: Boolean = true,
    val schemes: Array<String> = ["http", "https"]
)
