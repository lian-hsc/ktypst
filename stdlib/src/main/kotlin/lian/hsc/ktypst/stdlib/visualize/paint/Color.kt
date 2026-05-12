package lian.hsc.ktypst.stdlib.visualize.paint

import lian.hsc.ktypst.stdlib.layout.Angle
import lian.hsc.ktypst.stdlib.util.checkRatio
import lian.hsc.ktypst.stdlib.util.toRatio

/**
 * A color.
 */
sealed interface Color : Paint {

    /**
     * A predefined color.
     */
    enum class Named(override val value: String) : Color {

        Black("color.black"),
        Gray("color.gray"),
        Silver("color.silver"),
        White("color.white"),
        Navy("color.navy"),
        Blue("color.blue"),
        Aqua("color.aqua"),
        Teal("color.teal"),
        Eastern("color.eastern"),
        Purple("color.purple"),
        Fuchsia("color.fuchsia"),
        Maroon("color.maroon"),
        Red("color.red"),
        Orange("color.orange"),
        Yellow("color.yellow"),
        Olive("color.olive"),
        Green("color.green"),
        Lime("color.lime"),

    }

    /**
     * A grayscale color.
     * @property luma The luma component as a ratio.
     * @property alpha The alpha component as a ratio.
     */
    data class Luma(val luma: Double, val alpha: Double = 1.0) : Color {

        init {
            luma.checkRatio("luma")
            alpha.checkRatio("alpha")
        }

        override val value: String = "color.luma(${luma.toRatio()}, ${alpha.toRatio()})"

    }

    /**
     * An oklab color.
     *
     * This color space is well suited for the following use cases:
     * - Color manipulation such as saturating while keeping perceived hue
     * - Creating grayscale images with uniform perceived lightness
     * - Creating smooth and uniform color transition and gradients
     *
     * @property lightness The lightness component as a ratio.
     * @property a The a (green/red) component.
     * @property b The b (blue/yellow) component.
     * @property alpha The alpha component as a ratio.
     */
    data class Oklab(val lightness: Double, val a: Double, val b: Double, val alpha: Double = 1.0) : Color {

        init {
            lightness.checkRatio("lightness")
            alpha.checkRatio("alpha")
        }

        override val value: String = "color.oklab(${lightness.toRatio()}, $a, $b, ${alpha.toRatio()})"

    }

    /**
     * An oklch color.
     * @property lightness The lightness component as a ratio.
     * @property chroma The chroma component.
     * @property hue The hue component as a ratio.
     * @property alpha The alpha component as a ratio.
     */
    data class Oklch(val lightness: Double, val chroma: Double, val hue: Double, val alpha: Double = 1.0) : Color {

        init {
            lightness.checkRatio("lightness")
            hue.checkRatio("hue")
            alpha.checkRatio("alpha")
        }

        override val value: String =
            "color.oklch(${lightness.toRatio()}, $chroma, ${hue.toRatio()}, ${alpha.toRatio()})"

    }

    /**
     * An RGB color with linear luma.
     *
     * This color space is similar to [sRGB][Rgb],
     * but with the distinction that the color component are not gamma corrected.
     * This makes it easier to perform color operations such as blending and interpolation.
     * Although, you should prefer to use the [oklab][Oklab] colors for these.
     * @property red The red component as an integer between 0 and 255.
     * @property green The green component an integer between 0 and 255.
     * @property blue The blue component an integer between 0 and 255.
     * @property alpha The alpha component as a ratio.
     */
    data class LinearRgb(val red: Int, val green: Int, val blue: Int, val alpha: Double = 1.0) : Color {

        init {
            check(red in 0..255) { "Red component must be between 0 and 255, but was $red" }
            check(green in 0..255) { "Green component must be between 0 and 255, but was $green" }
            check(blue in 0..255) { "Blue component must be between 0 and 255, but was $blue" }
            alpha.checkRatio("alpha")
        }

        override val value: String = "color.linear-rgb($red, $green, $blue, ${alpha.toRatio()})"

    }

    /**
     * An RGB color.
     * The color is specified in the sRGB color space.
     *
     * @property red The red component as an integer between 0 and 255.
     * @property green The green component an integer between 0 and 255.
     * @property blue The blue component an integer between 0 and 255.
     * @property alpha The alpha component as a ratio.
     */
    data class Rgb(val red: Int, val green: Int, val blue: Int, val alpha: Double = 1.0) : Color {

        init {
            check(red in 0..255) { "Red component must be between 0 and 255, but was $red" }
            check(green in 0..255) { "Green component must be between 0 and 255, but was $green" }
            check(blue in 0..255) { "Blue component must be between 0 and 255, but was $blue" }
            alpha.checkRatio("alpha")
        }

        override val value: String = "color.rgb($red, $green, $blue, ${alpha.toRatio()})"

    }

    /**
     * A [rgb][Rgb] color specified as a hexadecimal string.
     *
     * @property hex The hexadecimal string.
     * Can be a three, four, six, or eight digit string.
     */
    data class Hex(val hex: String) : Color {

        init {
            check(hex.matches(Regex("^([0-9a-fA-F]{3,4}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})$"))) {
                "Hexadecimal string must be a valid hexadecimal number, but was $hex"
            }
        }

        override val value: String = "color.rgb(\"#$hex\")"

    }

    /**
     * A cmyk color.
     *
     * This is useful if you want to target a specific printer.
     * The conversion to RGB for display preview might differ from how your printer reproduces the color.
     *
     * @property cyan The cyan component as a ratio.
     * @property magenta The magenta component as a ratio.
     * @property yellow The yellow component as a ratio.
     * @property black The black component as a ratio.
     * @property alpha The alpha component as a ratio.
     */
    data class Cmyk(
        val cyan: Double,
        val magenta: Double,
        val yellow: Double,
        val black: Double,
        val alpha: Double = 1.0
    ) : Color {

        init {
            cyan.checkRatio("cyan")
            magenta.checkRatio("magenta")
            yellow.checkRatio("yellow")
            black.checkRatio("black")
            alpha.checkRatio("alpha")
        }

        override val value: String =
            "color.cmyk(${cyan.toRatio()}, ${magenta.toRatio()}, ${yellow.toRatio()}, ${black.toRatio()}, ${alpha.toRatio()})"

    }

    /**
     * An HSL color.
     *
     * This color space is useful for specifying colors by hue, saturation, and lightness.
     * It is also useful for color manipulation, such as saturating while keeping perceived hue.
     *
     * @property hue The hue.
     * @property saturation The saturation as a ratio.
     * @property lightness The lightness as a ratio.
     * @property alpha The alpha component as a ratio.
     */
    data class Hsl(val hue: Angle, val saturation: Double, val lightness: Double, val alpha: Double = 1.0) : Color {

        init {
            saturation.checkRatio("saturation")
            lightness.checkRatio("lightness")
            alpha.checkRatio("alpha")
        }

        override val value: String =
            "color.hsl(${hue.value}, ${saturation.toRatio()}, ${lightness.toRatio()}, ${alpha.toRatio()})"

    }

    /**
     * An HSV color.
     *
     * This color space is similar to [HSL][Hsl], but with the distinction that it uses value instead of lightness.
     * This makes it easier to perform color operations such as blending and interpolation.
     *
     * @property hue The hue.
     * @property saturation The saturation as a ratio.
     * @property hsvValue The value as a ratio.
     * @property alpha The alpha component as a ratio.
     */
    data class Hsv(val hue: Angle, val saturation: Double, val hsvValue: Double, val alpha: Double = 1.0) : Color {

        init {
            saturation.checkRatio("saturation")
            hsvValue.checkRatio("value")
            alpha.checkRatio("alpha")
        }

        override val value: String =
            "color.hsv(${hue.value}, ${saturation.toRatio()}, ${hsvValue.toRatio()}, ${alpha.toRatio()})"
    }

    /**
     * Lightens the color by the given factor as a ratio.
     *
     * @param ratio The factor to lighten the color by.
     * @return A new color with the lightness increased by the given ratio.
     */
    fun lighten(ratio: Double): Color {
        ratio.checkRatio("ratio")
        return ModifiedColor(this, "lighten(${ratio.toRatio()})")
    }

    /**
     * Darkens the color by the given factor as a ratio.
     *
     * @param ratio The factor to darken the color by.
     * @return A new color with the lightness decreased by the given ratio.
     */
    fun darken(ratio: Double): Color {
        ratio.checkRatio("ratio")
        return ModifiedColor(this, "darken(${ratio.toRatio()})")
    }

    /**
     * Increases the saturation by the given factor as a ratio.
     *
     * @param ratio The factor to saturate the color by.
     * @return A new color with the saturation increased by the given ratio.
     */
    fun saturate(ratio: Double): Color {
        ratio.checkRatio("ratio")
        return ModifiedColor(this, "saturate(${ratio.toRatio()})")
    }

    /**
     * Decreases the saturation by the given factor as a ratio.
     *
     * @param ratio The factor to desaturate the color by.
     * @return A new color with the saturation decreased by the given ratio.
     */
    fun desaturate(ratio: Double): Color {
        ratio.checkRatio("ratio")
        return ModifiedColor(this, "desaturate(${ratio.toRatio()})")
    }

    /**
     * Produces the complementary color using a provided color space.
     * You can think of it as the opposite side on a color wheel.
     *
     * @param space The color space used for the transformation.
     * @return A new color representing the complementary color.
     */
    fun negate(space: ColorSpace? = null): Color {
        return if (space == null) ModifiedColor(this, "negate()")
        else ModifiedColor(this, "negate(space: ${space.value})")
    }

    /**
     * Rotates the hue of the color by a given angle.
     *
     * @param angle The angle in degrees to rotate the hue by in degrees.
     * @param space The color space used for the transformation.
     * @return A new color representing the rotated color.
     */
    fun rotate(angle: Angle, space: ColorSpace? = null): Color {
        return if (space == null) ModifiedColor(this, "rotate(${angle.value})")
        else ModifiedColor(this, "rotate(${angle.value}, space: ${space.value})")
    }

    /**
     * Create a color by mixing two or more colors.
     *
     * In color spaces with a hue component (hsl, hsv, oklch), only two colors can be mixed at once.
     * Mixing more than two colors in such a space will result in an error!
     *
     * @param space The color space used for the transformation.
     * @param colors The colors to mix.
     * @return A new color representing the mixed color.
     */
    fun mix(space: ColorSpace? = null, vararg colors: Color): Color {
        require(colors.size >= 2) { "Mixing requires at least two colors, but got ${colors.size}" }

        return if (space == null) ModifiedColor(this, "mix(${colors.joinToString(", ") { it.value }})")
        else ModifiedColor(this, "mix(${colors.joinToString(", ") { it.value }}, space: ${space.value})")
    }

    /**
     * Makes a color more transparent by a given factor.
     *
     * This method is relative to the existing alpha value.
     * If the scale is positive, calculates `alpha - alpha * scale`.
     * Negative scales behave like color.opacify(-scale).
     * @param scale The factor to make the color more transparent by.
     * @return A new color with the alpha value decreased by the given scale.
     */
    fun transparentize(scale: Double): Color = ModifiedColor(this, "transparentize(${scale.toRatio()})")

    /**
     * Makes a color more opaque by a given scale.
     *
     * This method is relative to the existing alpha value.
     * If the scale is positive, calculates `alpha + scale - alpha * scale`.
     * Negative scales behave like color.transparentize(-scale).
     * @param scale The factor to make the color more opaque by.
     * @return A new color with the alpha value increased by the given scale.
     */
    fun opacify(scale: Double): Color = ModifiedColor(this, "opacify(${scale.toRatio()})")

}

internal data class ModifiedColor(val color: Color, val modification: String) : Color {

    override val value: String = "${color.value}.$modification"

}
