package lian.hsc.ktypst.stdlib.visualize.paint

/**
 * A paint that can be used to fill shapes or draw lines.
 */
sealed interface Paint {

    /**
     * How the paint is represented in Typst.
     */
    val value: String

}

