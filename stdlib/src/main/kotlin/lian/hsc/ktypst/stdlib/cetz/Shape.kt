package lian.hsc.ktypst.stdlib.cetz

import lian.hsc.ktypst.stdlib.layout.Angle
import lian.hsc.ktypst.stdlib.visualize.Point
import lian.hsc.ktypst.stdlib.visualize.Stroke
import lian.hsc.ktypst.stdlib.visualize.paint.Paint

/**
 * A (selected subset of) shape(s) that can be used with the CeTZ library.
 * Always works with `em` as unit.
 *
 * All shapes require the `cetz.draw` functions to be directly accessible in the current scope.
 * This can be done for example via `import cetz.draw: *`.
 *
 * You can create your own shapes by implementing this interface.
 */
interface Shape {

    /**
     * The width of the shape.
     */
    val width: Double

    /**
     * The height of the shape.
     */
    val height: Double

    /**
     * Creates the shape in Typst.
     *
     * @param center The center point of the shape.
     * @param name The name of the shape.
     * @param fill The paint to fill the shape with.
     * @param stroke The stroke to use for the shape.
     * @return The shape in Typst.
     */
    fun create(
        center: Point<Double>,
        name: String? = null,
        fill: Paint? = null,
        stroke: Stroke? = null,
    ): String

    /**
     * Appends the name, fill, and stroke to the string builder if they are not null.
     */
    fun StringBuilder.appendOptionalAttributes(name: String?, fill: Paint?, stroke: Stroke?) {
        if (name != null) append("name: \"$name\", ")
        if (fill != null) append("fill: ${fill.value}, ")
        if (stroke != null) append("stroke: ${stroke.value}, ")
    }

    /**
     * A ellipse.
     * @property width The width of the ellipse.
     * @property height The height of the ellipse.
     */
    data class Ellipse(override val width: Double, override val height: Double) : Shape {

        override fun create(center: Point<Double>, name: String?, fill: Paint?, stroke: Stroke?): String = buildString {
            append("circle(")
            append("${center.toTypst("em")}, ")
            append("radius: (${width / 2}em, ${height / 2}em), ")

            appendOptionalAttributes(name, fill, stroke)

            append(")")
        }

    }

    /**
     * A rectangle.
     * @property width The width of the rectangle.
     * @property height The height of the rectangle.
     */
    data class Rectangle(override val width: Double, override val height: Double) : Shape {

        override fun create(center: Point<Double>, name: String?, fill: Paint?, stroke: Stroke?): String = buildString {
            append("rect(")
            append("(${center.x - width / 2}em, ${center.y - height / 2}em), ")
            append("(rel: (${width}em, ${height}em)), ")

            appendOptionalAttributes(name, fill, stroke)

            append(")")
        }

    }

    /**
     * An n-pointed star.
     * @property points The number of points in the star.
     * @property radius The radius of the star's outer points.
     * @property angle The angel to rotate the star around its origin.
     * @property innerRadius The radius of the star's inner points.
     * @property showInner Whether to show lines connecting the inner points of the star.
     */
    data class Star(
        val points: Int,
        val radius: Double,
        val angle: Angle? = null,
        val innerRadius: Double? = null,
        val showInner: Boolean = false
    ) : Shape {

        override val width: Double = 2 * radius
        override val height: Double = 2 * radius

        override fun create(center: Point<Double>, name: String?, fill: Paint?, stroke: Stroke?): String = buildString {
            append("n-star(")
            append("${center.toTypst("em")}, ")
            append("$points, ")
            append("radius: ${radius}em, ")

            if (angle != null) append("angle: ${angle.value}, ")
            if (innerRadius != null) append("inner-radius: ${innerRadius}em, ")
            if (showInner) append("show-inner: true, ")

            appendOptionalAttributes(name, fill, stroke)

            append(")")
        }

    }

    @Suppress("FunctionName")
    companion object {

        /**
         * A circle.
         * @param radius The radius of the circle.
         */
        fun Circle(radius: Double): Shape = Ellipse(radius, radius)

        /**
         * A square.
         * @param size The size of the square.
         */
        fun Square(size: Double): Shape = Rectangle(size, size)


    }

}
