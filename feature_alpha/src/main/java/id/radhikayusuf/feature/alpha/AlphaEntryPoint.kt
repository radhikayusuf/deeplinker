package id.radhikayusuf.feature.alpha

import android.content.Context
import android.content.Intent
import id.radhikayusuf.lib.deeplinker.DeeplinkRouter
import id.radhikayusuf.lib.deeplinker.annotations.Deeplink
import id.radhikayusuf.lib.deeplinker.annotations.DeeplinkEntry
import id.radhikayusuf.lib.deeplinker.annotations.Deeplinkable
import id.radhikayusuf.lib.deeplinker.model.DeeplinkData

/**
 * @author radhikayusuf
 * Created 06/11/22
 */

@Deeplinkable
class AlphaEntryPoint : DeeplinkEntry {
    override val moduleName: String get() = "alpha"

    override fun onModuleConnect() = Unit

    @Deeplink(["radhikayusuf.id"], "/alpha", false)
    suspend fun openAlphaPage(context: Context, data: DeeplinkData, router: DeeplinkRouter) {
        val intentAlpha = Intent(context, AlphaActivity::class.java)
        context.startActivity(intentAlpha)
    }
}