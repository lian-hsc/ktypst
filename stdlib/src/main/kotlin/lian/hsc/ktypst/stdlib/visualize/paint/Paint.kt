package lian.hsc.ktypst.stdlib.visualize.paint

/**
 * A paint that can be used to fill shapes or draw lines.
 */
sealed interface Paint {

    /**
     * How the paint is represented in Typst.
     */
    val value: String

    companion object {

        /**
         * A paint that should not be rendered.
         */
        val None = Color.Rgb(red = 0, green = 0, blue = 0, alpha = 0.0)

    }

}

