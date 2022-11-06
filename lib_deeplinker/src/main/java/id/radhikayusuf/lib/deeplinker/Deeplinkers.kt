package id.radhikayusuf.lib.deeplinker

import android.content.Context

/**
 * @author radhikayusuf
 * Created 06/11/22
 */

object Deeplinkers {

    private lateinit var deeplinkRetriever: DeeplinkRetriever
    private lateinit var deeplinkRouter: DeeplinkRouter

    fun initialize(context: Context) {
        deeplinkRetriever = DeeplinkRetriever(context)
        deeplinkRouter = DeeplinkRouter(deeplinkRetriever)
    }

    fun getDeeplinkRetrieverInstance(): DeeplinkRetriever {
        return if (::deeplinkRetriever.isInitialized) {
            deeplinkRetriever
        } else {
            throw IllegalStateException("Please call initialize before get instance of every component")
        }
    }

    fun getDeeplinkRouterInstance(): DeeplinkRouter {
        return if (::deeplinkRetriever.isInitialized) {
            deeplinkRouter
        } else {
            throw IllegalStateException("Please call initialize before get instance of every component")
        }
    }
}