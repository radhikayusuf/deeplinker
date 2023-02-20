//package id.radhikayusuf.lib.deeplinker
//
//import android.content.Context
//import android.content.pm.PackageManager
//import android.os.Bundle
//import androidx.annotation.VisibleForTesting
//import id.radhikayusuf.lib.deeplinker.annotations.model.DeeplinkMap
//import id.radhikayusuf.lib.deeplinker.annotations.DeeplinkTarget
//import id.radhikayusuf.lib.deeplinker.annotations.utils.DeeplinkerConst.DEEPLINKER_GENERATED_PACKAGE
//import id.radhikayusuf.lib.deeplinker.annotations.utils.DeeplinkerConst.DEEPLINKER_META_IDENTIFIER
//import id.radhikayusuf.lib.deeplinker.annotations.utils.DeeplinkerConst.DEEPLINKER_POSTFIX_CLASS_NAME
//import id.radhikayusuf.lib.deeplinker.annotations.model.DeeplinkData
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import java.util.*
//
//class DeeplinkRetriever internal constructor(context: Context) {
//
//    val registeredDeeplinks: ArrayList<DeeplinkMap<Context, DeeplinkData, DeeplinkRouter>> = arrayListOf()
//
//    private val applicationInfo by lazy {
//        val appContext = context.applicationContext
//        appContext.packageManager.getApplicationInfo(appContext.packageName, PackageManager.GET_META_DATA)
//    }
//
//    suspend fun initialize() {
//        val entries = withContext(Dispatchers.IO) { retrieveModuleEntries() }
//        val deeplinkData = entries.flatMap { it.deeplinks.values }
//
//        registeredDeeplinks.clear()
//        registeredDeeplinks.addAll(deeplinkData)
//    }
//
//    private fun retrieveModuleEntries(): List<DeeplinkTarget<Context, DeeplinkData, DeeplinkRouter>> {
//        val nullabelDataSet: Bundle? = applicationInfo.metaData
//        val dataSet = nullabelDataSet ?: Bundle()
//        val filteredKeys = dataSet.keySet().filter { dataSet.get(it) == DEEPLINKER_META_IDENTIFIER }
//        return filteredKeys.mapNotNull { moduleClassPath ->
//            try {
//                val fileName = moduleClassPath.split(".").lastOrNull().orEmpty() + DEEPLINKER_POSTFIX_CLASS_NAME
//                val classPath = "$DEEPLINKER_GENERATED_PACKAGE.$fileName"
//                val clazz = instantiateClass(classPath)
//                instantiateModule(clazz)
//            } catch (e: Exception) {
//                null
//            }
//        }
//    }
//
//    @VisibleForTesting
//    internal fun instantiateClass(classPath: String): Class<*>? {
//        return try {
//            Class.forName(classPath)
//        } catch (exception: Exception) {
//            null
//        }
//    }
//
//    @VisibleForTesting
//    internal fun instantiateModule(cls: Class<*>?): DeeplinkTarget<Context, DeeplinkData, DeeplinkRouter>? {
//        return try {
//            cls?.newInstance() as? DeeplinkTarget<Context, DeeplinkData, DeeplinkRouter>
//        } catch (e:Exception) {
//            null
//        }
//    }
//}