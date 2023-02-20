package id.radhikayusuf.lib.deeplinkerprocessor.model

import id.radhikayusuf.lib.deeplinker.annotations.Deeplink

data class DeeplinkEntryMetaData(
    val funcName: String,
    val annotation: Deeplink
)