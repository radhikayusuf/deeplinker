package id.radhikayusuf.lib.deeplinker.annotations


/**
 * Don't forget to update the proguard rules if there's any
 * change to this file location and also the implementor
 */
interface DeeplinkEntry {
    val moduleName: String
    fun onModuleConnect()
}