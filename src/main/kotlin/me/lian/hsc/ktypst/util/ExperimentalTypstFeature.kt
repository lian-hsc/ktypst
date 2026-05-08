package me.lian.hsc.ktypst.util

import kotlin.annotation.AnnotationTarget.*

/**
 * The marker for experimental Typst features.
 * May require setting some flags or similar for the Typst compiler too.
 *
 * Any usage of a declaration annotated with `@ExperimentalTypstFeature` must be accepted either by
 * annotating that usage with the [OptIn] annotation, e.g. `@OptIn(ExperimentalTypstFeature::class)`,
 * or by using the compiler argument `-opt-in=me.lian.hsc.ktypst.ExperimentalTypstFeature`.
 */
@RequiresOptIn
@MustBeDocumented
@Target(
    CLASS,
    ANNOTATION_CLASS,
    PROPERTY,
    FIELD,
    LOCAL_VARIABLE,
    VALUE_PARAMETER,
    CONSTRUCTOR,
    FUNCTION,
    PROPERTY_GETTER,
    PROPERTY_SETTER,
    TYPEALIAS
)
annotation class ExperimentalTypstFeature
