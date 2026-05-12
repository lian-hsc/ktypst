package lian.hsc.ktypst.stdlib.visualize

import lian.hsc.ktypst.stdlib.visualize.paint.Color
import lian.hsc.ktypst.stdlib.visualize.paint.Paint

/**
 * Defines how to draw a line.
 *
 * @property paint The color or gradient to use for the stroke.
 * @property thickness The stroke's thickness in pts.
 * @property cap How the ends of the stroke are rendered.
 * @property join How sharp turns are rendered.
 * @property dash The dash pattern to use.
 * @property miterLimit Number at which protruding sharp bends are rendered with a bevel instead or a miter join.
 * The higher the number, the sharper an angle can be before it is beveled.
 * Only applicable if join is "miter".
 * Specifically, the miter limit is the maximum ratio between the corner's protrusion length and the stroke's thickness.
 */
data class Stroke(
    val paint: Paint = Color.Named.Black,
    val thickness: Double = 1.0,
    val cap: Cap = Cap.Butt,
    val join: Join = Join.Miter,
    val dash: Dash = Dash.Named.Solid,
    val miterLimit: Double = 4.0,
) {

    init {
        require(thickness > 0) { "Stroke thickness must be positive, but was $thickness" }
        require(miterLimit > 0) { "Miter limit must be positive, but was $miterLimit" }
    }


    val value: String =
        "(paint: ${paint.value}, thickness: ${thickness}pt, cap: ${cap.value}, join: ${join.value}, dash: ${dash.value}, miter-limit: $miterLimit)"

    /**
     * How the ends of the stroke are rendered.
     */
    enum class Cap(val value: String) {

        /**
         * Square stroke cap with the edge at the stroke's end point.
         */
        Butt("\"butt\""),

        /**
         * Circular stroke cap centered at the stroke's end point.
         */
        Round("\"round\""),

        /**
         * Square stroke cap centered at the stroke's end point.
         */
        Square("\"square\""),

    }

    /**
     * How sharp turns are rendered.
     */
    enum class Join(val value: String) {

        /**
         * Segments are joined with sharp edges. Sharp bends exceeding the miter limit are beveled instead.
         */
        Miter("\"miter\""),

        /**
         * Segments are joined with circular corners.
         */
        Round("\"round\""),

        /**
         * Segments are joined with a bevel (a straight edge connecting the butts of the joined segments).
         */
        Bevel("\"bevel\""),

    }

    /**
     * The dash pattern to use.
     */
    interface Dash {

        val value: String

        /**
         * Predefined dash patterns.
         */
        enum class Named(override val value: String) : Dash {

            Solid("\"solid\""),
            Dotted("\"dotted\""),
            DenselyDotted("\"densely-dotted\""),
            LooselyDotted("\"loosely-dotted\""),
            Dashed("\"dashed\""),
            DenselyDashed("\"densely-dashed\""),
            LooselyDashed("\"loosely-dashed\""),
            DashDotted("\"dash-dotted\""),
            DenselyDashDotted("\"densely-dash-dotted\""),
            LooselyDashDotted("\"loosely-dash-dotted\""),

        }

        /**
         * Alternating lengths for dashes and gaps.
         * You can also use `null` for a length equal to the line thickness.
         */
        data class Custom(val pattern: List<Double?>) : Dash {

            init {
                require(pattern.isNotEmpty()) { "Custom dash pattern cannot be empty" }
                require(pattern.all { it == null || it > 0 }) { "Custom dash pattern values must be null or positive" }
            }

            override val value: String = pattern.joinToString(
                separator = ", ",
                prefix = "(",
                postfix = ")"
            ) { if (it == null) "dot" else "${it}pt" }

        }

        /**
         * A [custom pattern][Custom] with an offset.
         */
        data class CustomOffset(val pattern: Custom, val offset: Double) : Dash {

            override val value: String = "(array: ${pattern.value}, phase: ${offset}pt)"

        }

    }

}
