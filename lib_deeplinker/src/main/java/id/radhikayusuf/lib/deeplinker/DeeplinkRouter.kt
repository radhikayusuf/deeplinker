package id.radhikayusuf.lib.deeplinker

import android.content.Context
import android.net.Uri
import id.radhikayusuf.lib.deeplinker.annotations.model.Result
import id.radhikayusuf.lib.deeplinker.annotations.model.Signal
import id.radhikayusuf.lib.deeplinker.model.DeeplinkMatcher
import id.radhikayusuf.lib.deeplinker.model.RedirectResult
import id.radhikayusuf.lib.deeplinker.utils.DeeplinkUtils.checkMatchUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeeplinkRouter(
    private val deeplinkSignals: Lazy<List<Signal>>
) {

    fun peek() = deeplinkSignals.value

    suspend fun proceedDeeplinkUrl(context: Context, url: String) =
        proceedDeeplinkUrl(context, Uri.parse(url))

    suspend fun proceedDeeplinkUrl(context: Context, contentUri: Uri): RedirectResult {
        val dynamicPaths = mutableMapOf<String, String>()
        val uri = reformatUri(contentUri)
        val target = withContext(Dispatchers.IO) { findMatchDeeplink(uri, dynamicPaths) }
        return if (target != null) {
            if (target.authRequired && !isLoggedIn()) {
                return RedirectResult.AUTH_FAIL
            }
            val queries = withContext(Dispatchers.IO) {
                uri.queryParameterNames.orEmpty()
                    .mapNotNull { uri.getQueryParameter(it)?.let { value -> Pair(it, value) } }
                    .associateBy({ it.first }, { it.second })
            }

            val result = Result(
                schema = uri.scheme.orEmpty(),
                host = uri.authority.orEmpty(),
                path = uri.path.orEmpty(),
                queries = queries,
                dynamicPaths = dynamicPaths,
                fullUrl = uri.toString()
            )
            target.closure.invoke(result)
            RedirectResult.SUCCESS
        } else {
            RedirectResult.NOT_FOUND
        }
    }

    private fun findMatchDeeplink(uri: Uri, dynamicPaths: MutableMap<String, String>): Signal? {
        return deeplinkSignals.value.firstOrNull { signal ->
            val patternCheckerResult = checkHostAndPatternMatches(uri, signal)
            val hasFound = patternCheckerResult.isMatch &&
                    uri.authority.orEmpty() in signal.hosts &&
                    uri.scheme.orEmpty() in signal.schemes
            if (hasFound) dynamicPaths.putAll(patternCheckerResult.dynamicPaths)
            return@firstOrNull hasFound
        }
    }

    private fun isLoggedIn() = true

    private fun checkHostAndPatternMatches(uri: Uri, signal: Signal): DeeplinkMatcher {
        return checkMatchUrl(uri.path.orEmpty(), signal.pathPatterns)
    }

    private fun reformatUri(uri: Uri): Uri {
        val scheme = uri.scheme.orEmpty() + "://"
        val host = uri.authority.orEmpty()
        val isContainHash = uri.toString()
            .replace(host, "")
            .replace(scheme, "")
            .startsWith("/#")
        return if (isContainHash) {
            Uri.parse(uri.toString().replaceFirst("/#", ""))
        } else uri
    }
}