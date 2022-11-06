package id.radhikayusuf.lib.deeplinker.utils

import id.radhikayusuf.lib.deeplinker.model.DeeplinkMatcher

object DeeplinkUtils {

    fun checkMatchUrl(
        clickedDeeplinkPath: String,
        deeplinkCriteriaPath: String
    ): DeeplinkMatcher {
        val clickedPaths: List<String> = clickedDeeplinkPath.removeQueries().split("/").filter { it.isNotBlank() }
        val criteriaPaths: List<String> = deeplinkCriteriaPath.split("/").filter { it.isNotBlank() }
        if (criteriaPaths.size != clickedPaths.size) {
            return DeeplinkMatcher(false, mapOf())
        }

        val dynamicPathsResult = mutableMapOf<String, String>()
        clickedPaths.forEachIndexed { index, path ->
            val criteriaPath = criteriaPaths.getOrNull(index).orEmpty()
            if (path != criteriaPath) {
                if (!isContainDynamicPath(criteriaPath)) return DeeplinkMatcher(false, mapOf())
                val pathInfo: Pair<String, String> = getPathInfo(criteriaPath)
                val regex = Regex(pathInfo.second)
                if (!path.matches(regex)) {
                    return DeeplinkMatcher(false, mapOf())
                }
                dynamicPathsResult[pathInfo.first] = path
            }
        }
        return DeeplinkMatcher(true, dynamicPathsResult)
    }

    private fun isContainDynamicPath(path: String): Boolean {
        return path.startsWith("<", false) && path.endsWith(">", false)
    }

    fun isDeeplinkContainsDynamicPath(path: String): Boolean {
        return path.contains("<", false) &&
                path.contains(">", false) &&
                path.contains(":", false)
    }

    private fun getPathInfo(path: String): Pair<String, String> {
        // example <username:.+> then result should be -> .+
        val splitPaths = path.replace("<", "")
            .replace(">", "")
            .split(":")
        if (splitPaths.size != 2 || splitPaths.firstOrNull() == splitPaths.lastOrNull().orEmpty()) {
            throw IllegalArgumentException("Illegal path format for $path")
        }
        return splitPaths.firstOrNull().orEmpty() to splitPaths.lastOrNull().orEmpty()
    }

    fun String.removeQueries(): String {
        return this.split("?").firstOrNull().orEmpty()
    }
}