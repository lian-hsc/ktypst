package me.lian.hsc.ktypst.util

import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.CONSTRUCTOR
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.LOCAL_VARIABLE
import kotlin.annotation.AnnotationTarget.PROPERTY
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.annotation.AnnotationTarget.PROPERTY_SETTER
import kotlin.annotation.AnnotationTarget.TYPEALIAS
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

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
