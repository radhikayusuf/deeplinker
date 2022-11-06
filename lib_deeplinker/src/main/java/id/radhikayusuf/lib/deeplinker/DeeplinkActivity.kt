package id.radhikayusuf.lib.deeplinker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import id.radhikayusuf.lib.deeplinker.model.RedirectResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeeplinkActivity : AppCompatActivity() {

    private val deeplinkRouter by lazy { Deeplinkers.getDeeplinkRouterInstance() }
    private val deeplinkRetriever by lazy { Deeplinkers.getDeeplinkRetrieverInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deeplink)
        lifecycleScope.launch(Dispatchers.IO) {
            if (deeplinkRetriever.registeredDeeplinks.isEmpty()) {
                deeplinkRetriever.initialize()
            }

            val url = intent.getStringExtra(Intent.EXTRA_TEXT)?.let { extractLinks(it) }?.firstOrNull()
            var uri: Uri = intent.data ?: Uri.parse(url)


            when (deeplinkRouter.proceedDeeplinkUrl(this@DeeplinkActivity, uri)) {
                RedirectResult.AUTH_FAIL -> {}
                RedirectResult.NOT_FOUND -> {}
                RedirectResult.SUCCESS -> {}
            }
            this@DeeplinkActivity.finish()
        }
    }

    private fun extractLinks(text: String): List<String> {
        val links: MutableList<String> = ArrayList()
        val matcher = Patterns.WEB_URL.matcher(text)
        while (matcher.find()) {
            val url = matcher.group()
            links.add(url)
        }
        return links
    }
}