package id.radhikayusuf.sample.deeplinker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import id.radhikayusuf.lib.R
import id.radhikayusuf.lib.deeplinker.annotations.model.Signal
import id.radhikayusuf.sample.deeplinker.MainApplication.Companion.ROUTER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var data: List<Signal>? = null
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = GlobalScope.launch(Dispatchers.IO) {
            data = ROUTER?.peek()
            Log.wtf("radhikalog-onCreate: ", "$data")
            Log.wtf("radhikalog-onCreate: ", "${data?.size}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}