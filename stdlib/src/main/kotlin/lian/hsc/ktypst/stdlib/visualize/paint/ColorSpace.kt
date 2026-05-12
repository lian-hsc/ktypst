package lian.hsc.ktypst.stdlib.visualize.paint

/**
 * A color space, which is, for example, used to interpolate colors for [gradients][Gradient].
 *
 * @property value The name of the color space in Typst.
 */
enum class ColorSpace(val value: String) {

    Oklab("oklab"),
    Oklch("oklch"),
    SRgb("sRGB"),
    LinearRgb("linear-rgb"),
    Cmyk("cmyk"),
    Grayscale("grayscale"),
    Hsl("hsl"),
    Hsv("hsv"),

}
