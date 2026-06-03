package lian.hsc.ktypst.stdlib.layout

/**
 * A margin.
 */
data class Margin(
    val top: Double,
    val left: Double,
    val bottom: Double,
    val right: Double,
) {

    constructor(all: Double) : this(all, all, all, all)
    constructor(x: Double, y: Double) : this(y, x, y, x)

}
