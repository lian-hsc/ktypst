package lian.hsc.ktypst.stdlib.visualize

/**
 * A point in 2D space.
 */
data class Point<T : Any>(val x: T, val y: T) {

    fun toTypst(unit: String, transform: (T) -> String = { it.toString() }) =
        "(${transform(x)}$unit, ${transform(y)}$unit)"

}
