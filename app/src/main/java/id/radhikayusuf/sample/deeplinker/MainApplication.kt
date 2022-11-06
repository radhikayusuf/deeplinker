package id.radhikayusuf.sample.deeplinker

import android.app.Application
import id.radhikayusuf.lib.deeplinker.Deeplinkers

/**
 * @author radhikayusuf
 * Created 06/11/22
 */

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Deeplinkers.initialize(this)
    }
}