package lian.hsc.ktypst.stdlib.visualize.paint

import lian.hsc.ktypst.stdlib.layout.Angle
import lian.hsc.ktypst.stdlib.util.checkRatio
import lian.hsc.ktypst.stdlib.util.toRatio
import lian.hsc.ktypst.stdlib.visualize.Point

/**
 * A gradient.
 */
sealed interface Gradient : Paint {

    /**
     * The relative placement of the gradient.
     *
     * For an element placed at the root/top level of the document, the parent is the page itself.
     * For other elements, the parent is the innermost block, box, column, grid, or stack that contains the element.
     */
    enum class Relative(val value: String) {

        Self("\"self\""),
        Parent("\"parent\""),

    }

    /**
     * A gradient's stops.
     */
    sealed interface Stops {

        /**
         * How the gradient is represented in Typst.
         */
        val value: String

        /**
         * Predefined gradient stops.
         */
        enum class Named(override val value: String) : Stops {

            /**
             * A perceptually uniform rainbow-like color map.
             * Read [this blog](https://research.google/blog/turbo-an-improved-rainbow-colormap-for-visualization/) post for more details.
             */
            Turbo("..color.map.turbo"),

            /**
             * A blue to gray to yellow color map.
             * Read [this blog](https://bids.github.io/colormap/) post for more details.
             */
            Cividis("..color.map.cividis"),

            /**
             * Cycles through the full color spectrum.
             * This color map is best used by setting the interpolation color space to [ColorSpace.HSL].
             * The rainbow gradient is not suitable for data visualization because it is not perceptually uniform,
             * so the differences between values become unclear to your readers.
             * It should only be used for decorative purposes.
             */
            Rainbow("..color.map.rainbow"),

            /**
             * Red to yellow to blue color map.
             */
            Spectral("..color.map.spectral"),

            /**
             * A purple to teal to yellow color map.
             */
            Viridis("..color.map.viridis"),

            /**
             * A black to red to yellow color map.
             */
            Inferno("..color.map.inferno"),

            /**
             * A black to purple to yellow color map.
             */
            Magma("..color.map.magma"),

            /**
             * A purple to pink to yellow color map.
             */
            Plasma("..color.map.plasma"),

            /**
             * A black to red to white color map.
             */
            Rocket("..color.map.rocket"),

            /**
             * A black to teal to white color map.
             */
            Mako("..color.map.mako"),

            /**
             * A light blue to white to red color map.
             */
            Vlag("..color.map.vlag"),

            /**
             * A light teal to black to orange color map.
             */
            Icefire("..color.map.icefire"),

            /**
             * A orange to purple color map that is perceptually uniform.
             */
            Flare("..color.map.flare"),

            /**
             * A light green to blue color map.
             */
            Crest("..color.map.crest"),

        }

        /**
         * A gradient that consists of multiple colors as stops.
         */
        data class Colors(val colors: List<Color>) : Stops {

            override val value: String = colors.joinToString(", ", prefix = "(", postfix = ")") { it.value }

        }

        /**
         * A gradient that consists of multiple colors and their relative location as stops.
         */
        data class PositionedColors(val stops: List<Pair<Color, Double>>) : Stops {

            init {
                check(stops.size >= 2) { "At least two stops are required" }
                check(stops.first().second == 0.0) { "First stop must have a location of 0.0" }
                check(stops.last().second == 1.0) { "Last stop must have a location of 1.0" }
                stops.forEachIndexed { index, pair ->
                    pair.second.checkRatio("Stop ${index + 1}")
                }
                check(stops.sortedBy { it.second } == stops) { "Stops must be sorted by location" }
            }

            override val value: String =
                stops.joinToString(", ", prefix = "(", postfix = ")") { "(${it.first.value}, ${it.second.toRatio()})" }

        }

    }

    /**
     * A linear gradient, in which colors transition along a straight line.
     *
     * @property stops The gradient's stops.
     * @property space The color space in which to interpolate the gradient.
     * @property relative The relative placement of the gradient.
     * @property angle The angle of the gradient.
     */
    data class Linear(
        val stops: Stops,
        val space: ColorSpace = ColorSpace.Oklab,
        val relative: Relative = Relative.Self,
        val angle: Angle = Angle.Degrees(0.0)
    ) : Gradient {

        override val value: String =
            "gradient.linear(" +
                "${stops.value}, " +
                "space: ${space.value}, " +
                "relative: ${relative.value}, " +
                "angle: ${angle.value})"

    }

    /**
     * A radial gradient, in which colors radiate away from an origin.
     *
     * The gradient is defined by two circles: the focal circle and the end circle.
     * The focal circle is a circle with center [focalCenter] and radius [focalRadius],
     * that defines the points at which the gradient starts and has the color of the first stop.
     * The end circle is a circle with center [center] and radius [radius],
     * that defines the points at which the gradient ends and has the color of the last stop.
     * The gradient is then interpolated between these two circles.
     *
     * Using these four values, also called the focal point for the starting circle and the center and radius for the end circle,
     * we can define a gradient with more interesting properties than a basic radial gradient.
     *
     * @property stops The gradient's stops.
     * @property space The color space in which to interpolate the gradient.
     * @property relative The relative placement of the gradient.
     * @property center The center of the end circle.
     * @property radius The radius of the end circle.
     * @property focalCenter The center of the focal circle.
     * @property focalRadius The radius of the focal circle.
     */
    data class Radial(
        val stops: Stops,
        val space: ColorSpace = ColorSpace.Oklab,
        val relative: Relative = Relative.Self,
        val center: Point<Double> = Point(0.5, 0.5),
        val radius: Double = 0.5,
        val focalCenter: Point<Double> = Point(0.5, 0.5),
        val focalRadius: Double = 0.0
    ) : Gradient {

        override val value: String =
            "gradient.radial(" +
                "${stops.value}, " +
                "space: ${space.value}, " +
                "relative: ${relative.value}, " +
                "center: ${center.toTypst("") { it.toRatio() }}, " +
                "radius: ${radius.toRatio()}, " +
                "focal-center: ${focalCenter.toTypst("") { it.toRatio() }}, " +
                "focal-radius: ${focalRadius.toRatio()})"

    }

    /**
     * A conic gradient, in which colors change radially around a center point.
     *
     * You can control the center point of the gradient by using the [center] argument.
     * By default, the center point is the center of the shape.
     *
     * @property stops The gradient's stops.
     * @property angle The angle of the gradient.
     * @property space The color space in which to interpolate the gradient.
     * @property relative The relative placement of the gradient.
     * @property center The center point of the gradient.
     */
    data class Conic(
        val stops: Stops,
        val angle: Angle = Angle.Degrees(0.0),
        val space: ColorSpace = ColorSpace.Oklab,
        val relative: Relative = Relative.Self,
        val center: Point<Double> = Point(0.5, 0.5)
    ) : Gradient {

        override val value: String =
            "gradient.conic(${stops.value}, " +
                "angle: ${angle.value}, " +
                "space: ${space.value}, " +
                "relative: ${relative.value}, " +
                "center: ${center.toTypst("") { it.toRatio() }})"

    }

    /**
     * Creates a sharp version of this gradient.
     *
     * Sharp gradients have discrete jumps between colors, instead of a smooth transition.
     * They are particularly useful for creating color lists for a preset gradient.
     *
     * @param steps The number of steps to use for the sharp gradient.
     * @param smoothness How much to smooth the gradient.
     * @return A sharp version of this gradient.
     */
    fun sharp(steps: Int, smoothness: Double = 0.0): Gradient {
        smoothness.checkRatio("smoothness")
        return ModifiedGradient(this, "sharp(${steps}, smoothness: ${smoothness.toRatio()})")
    }

    /**
     * Repeats this gradient a given number of times, optionally mirroring it at every second repetition.
     *
     * @param repetitions The number of times to repeat the gradient.
     * @param mirror Whether to mirror the gradient at every second repetition,
     * i.e., the first instance (and all odd ones) stays unchanged.
     * @return A new gradient that repeats this gradient [repetitions] times.
     */
    fun repeat(repetitions: Int, mirror: Boolean = false): Gradient =
        ModifiedGradient(this, "repeat(${repetitions}, mirror: $mirror)")

}

internal class ModifiedGradient(val gradient: Gradient, val modification: String) : Gradient {

    override val value: String = "${gradient.value}.$modification"

}
