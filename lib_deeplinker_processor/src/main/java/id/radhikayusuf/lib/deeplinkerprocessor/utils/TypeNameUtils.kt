package id.radhikayusuf.lib.deeplinkerprocessor.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import id.radhikayusuf.lib.deeplinker.annotations.Deeplink
import id.radhikayusuf.lib.deeplinker.annotations.DeeplinkMap
import id.radhikayusuf.lib.deeplinker.annotations.DeeplinkTarget
import id.radhikayusuf.lib.deeplinkerprocessor.DeeplinkConnectorGenerator
import javax.lang.model.element.ExecutableElement

object TypeNameUtils {
    /**
     * TypeName utils
     */
    fun getDeeplinkTargetTypeName(): TypeName =
        (DeeplinkTarget::class).asClassName()
            .parameterizedBy(listOf(getAndroidContextTypeName(), getDeeplinkDataTypeName(), getDeeplinkRouterTypeName()))

    fun getDeeplinkMapTypeName(): TypeName {
        val deeplinkMapTypeName = DeeplinkMap::class.asClassName().parameterizedBy(
            getAndroidContextTypeName(), getDeeplinkDataTypeName(), getDeeplinkRouterTypeName()
        )
        return (Map::class).asClassName()
            .parameterizedBy(String::class.asTypeName(), deeplinkMapTypeName)
    }

    fun getAndroidContextTypeName(): TypeName {
        return ClassName("android.content", "Context")
    }

    fun getDeeplinkDataTypeName(): TypeName {
        return ClassName("id.radhikayusuf.lib.deeplinker.model", "DeeplinkData")
    }

    fun getDeeplinkRouterTypeName(): TypeName {
        return ClassName("id.radhikayusuf.lib.deeplinker", "DeeplinkRouter")
    }
}