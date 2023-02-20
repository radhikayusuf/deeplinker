package id.radhikayusuf.sample.deeplinker

import android.app.Application
import id.radhikayusuf.deeplinker.generated.DeeplinkStore
import id.radhikayusuf.lib.deeplinker.DeeplinkRouter

/**
 * @author radhikayusuf
 * Created 06/11/22
 */

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ROUTER = DeeplinkRouter(DeeplinkStore(this).deeplinkContent)
    }

    companion object {
        var ROUTER: DeeplinkRouter? = null
    }
}