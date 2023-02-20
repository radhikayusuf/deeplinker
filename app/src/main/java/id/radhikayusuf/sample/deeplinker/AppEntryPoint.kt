package id.radhikayusuf.sample.deeplinker

import android.content.Context
import android.content.Intent
import id.radhikayusuf.feature.beta.BetaActivity
import id.radhikayusuf.lib.deeplinker.annotations.Deeplink
import id.radhikayusuf.lib.deeplinker.annotations.DeeplinkEntry
import id.radhikayusuf.lib.deeplinker.annotations.Deeplinkable
import id.radhikayusuf.lib.deeplinker.annotations.model.Result

/**
 * @author radhikayusuf
 * Created 29/11/22
 */

@Deeplinkable
class AppEntryPoint(
    private val context: Context
) : DeeplinkEntry {

    override val moduleName: String get() = "app"

    @Deeplink(["radhikayusuf.id"], "/app", false)
    suspend fun openApp(data: Result) {
        val intentAlpha = Intent(context, BetaActivity::class.java)
        context.startActivity(intentAlpha)
    }

    override fun onModuleConnect() {}
}