package id.radhikayusuf.lib.deeplinker.utils

import android.content.Context
import id.radhikayusuf.lib.deeplinker.DeeplinkRouter
import id.radhikayusuf.lib.deeplinker.model.DeeplinkData
import kotlin.coroutines.Continuation

/**
 * @author radhikayusuf
 * Created 17/10/22
 */

typealias DeeplinkSignalClosure = suspend (Context, DeeplinkData, DeeplinkRouter) -> Unit

typealias DeeplinkSignalClosureRaw = (Context, DeeplinkData, DeeplinkRouter, Continuation<*>) -> Unit