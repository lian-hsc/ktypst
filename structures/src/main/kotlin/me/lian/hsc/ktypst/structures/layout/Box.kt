package me.lian.hsc.ktypst.structures.layout

import lian.hsc.ktypst.stdlib.visualize.Point

data class Box(
    val name: String,
    val center: Point<Double>,
    val width: Double,
    val height: Double
) {

    val left = center.x - width / 2.0
    val right = center.x + width / 2.0
    val top = center.y - height / 2.0
    val bottom = center.y + height / 2.0

    fun rename(name: String) = copy(name = name)

    fun scale(factor: Double) = copy(
        center = Point(center.x * factor, center.y * factor),
        width = width * factor,
        height = height * factor
    )

    fun translate(dx: Double, dy: Double) = copy(center = Point(center.x + dx, center.y + dy))

}
