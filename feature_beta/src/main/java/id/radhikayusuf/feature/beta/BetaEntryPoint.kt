package id.radhikayusuf.feature.beta

import android.content.Context
import android.content.Intent
import android.widget.Toast
import id.radhikayusuf.lib.deeplinker.DeeplinkRouter
import id.radhikayusuf.lib.deeplinker.annotations.Deeplink
import id.radhikayusuf.lib.deeplinker.annotations.DeeplinkEntry
import id.radhikayusuf.lib.deeplinker.annotations.Deeplinkable
import id.radhikayusuf.lib.deeplinker.model.DeeplinkData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author radhikayusuf
 * Created 06/11/22
 */

@Deeplinkable
class BetaEntryPoint : DeeplinkEntry {
    override val moduleName: String get() = "alpha"

    override fun onModuleConnect() = Unit

    @Deeplink(["radhikayusuf.id"], "/beta", false)
    suspend fun openBetaPage(context: Context, data: DeeplinkData, router: DeeplinkRouter) {
        val intentAlpha = Intent(context, BetaActivity::class.java)
        context.startActivity(intentAlpha)
    }

    @Deeplink(["radhikayusuf.id"], "/beta/toast", false)
    suspend fun onlyToast(context: Context, data: DeeplinkData, router: DeeplinkRouter) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Hello beta!", Toast.LENGTH_SHORT).show()
        }
    }
}