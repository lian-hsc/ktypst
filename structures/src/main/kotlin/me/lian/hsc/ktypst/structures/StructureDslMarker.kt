package me.lian.hsc.ktypst.structures

/**
 * The marker for structure-related DSLs.
 */
@DslMarker
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class StructureDslMarker
