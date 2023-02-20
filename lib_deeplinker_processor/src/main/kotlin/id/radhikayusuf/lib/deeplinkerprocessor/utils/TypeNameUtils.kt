package id.radhikayusuf.lib.deeplinkerprocessor.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import id.radhikayusuf.lib.deeplinker.annotations.RouteTarget
import id.radhikayusuf.lib.deeplinker.annotations.model.Signal
import kotlin.reflect.KClass

object TypeNameUtils {
    /**
     * TypeName utils
     */
    fun getDeeplinkTargetTypeName(): TypeName {
        return (RouteTarget::class).asClassName().parameterizedBy(Signal::class.asClassName())
    }

    fun getNewDeeplinkTargetTypeName(): TypeName {
        val deeplinkSignalTypeName = Signal::class.asClassName()
        return (RouteTarget::class).asClassName()
            .parameterizedBy(deeplinkSignalTypeName)
    }

    fun getListOfDeeplinkSignalTypeName(): TypeName {
        val deeplinkSignalTypeName = Signal::class.asClassName()
        return (ArrayList::class).asClassName()
            .parameterizedBy(deeplinkSignalTypeName)
    }

    fun getLazyGeneratedTypeName(typeArgumentName: TypeName): TypeName {
        return (Lazy::class).asClassName()
            .parameterizedBy(typeArgumentName)
    }

    fun getPackageName(clazz: KClass<*>): String {
        val simpleName = clazz.simpleName.orEmpty()
        val qualifiedName = RouteTarget::class.qualifiedName.orEmpty()
        return qualifiedName.replace(".$simpleName", "")
    }

    fun getSimpleName(clazz: KClass<*>): String =
        clazz.simpleName.orEmpty()

    fun getAndroidContextTypeName(): TypeName {
        return ClassName("android.content", "Context")
    }
}