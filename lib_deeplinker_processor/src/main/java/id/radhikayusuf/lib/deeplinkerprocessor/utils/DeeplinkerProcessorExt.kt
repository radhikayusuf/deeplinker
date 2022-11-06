package id.radhikayusuf.lib.deeplinkerprocessor.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import id.radhikayusuf.lib.deeplinker.annotations.Deeplink
import id.radhikayusuf.lib.deeplinkerprocessor.DeeplinkConnectorGenerator
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement

/**
 * @author radhikayusuf
 * Created 06/11/22
 */

fun ExecutableElement.isFunctionHasInvalidFormat(): Boolean {
    return parameters.size != 4 || parameters.any { it.asType().toString() !in DeeplinkConnectorGenerator.PARAMETER_RULES }
}

fun ExecutableElement.getDeeplinkAnnotation(): Deeplink {
    return getAnnotation(Deeplink::class.java)
}

fun List<Element>.onlyFunctionWithDeeplinkAnnotation(): List<Element> {
    return this.filter {  element ->
        element.kind == ElementKind.METHOD &&
            element.annotationMirrors.any { it.annotationType.toString() == Deeplink::class.java.name }
    }
}

fun FileSpec.Builder.addMandatoryDeeplinkerImports(): FileSpec.Builder {
    return addImport("android.content", "Context")
        .addImport("id.radhikayusuf.lib.deeplinker.annotations", "DeeplinkTarget")
        .addImport("id.radhikayusuf.lib.deeplinker.annotations", "DeeplinkMap")
        .addImport("id.radhikayusuf.lib.deeplinker.model", "DeeplinkData")
        .addImport("id.radhikayusuf.lib.deeplinker", "DeeplinkRouter")
}