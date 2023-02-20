package id.radhikayusuf.lib.deeplinkerprocessor.model

/**
 * @author radhikayusuf
 * Created 29/11/22
 */

data class DeeplinkMetaData(
    val entryName: String,
    val packageName: String,
    val deeplinkEntries: List<DeeplinkEntryMetaData>
)

