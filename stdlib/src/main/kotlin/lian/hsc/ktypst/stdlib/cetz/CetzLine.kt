package lian.hsc.ktypst.stdlib.cetz

import lian.hsc.ktypst.stdlib.visualize.Stroke
import lian.hsc.ktypst.stdlib.visualize.paint.Paint

/**
 * A line from the Typst cetz package.
 * @property stroke The stroke of the line.
 * @property startMark A mark at the start of the line.
 * @property endMark A mark at the end of the line.
 */
data class CetzLine(
    val stroke: Stroke = Stroke(),
    val startMark: Mark? = null,
    val endMark: Mark? = null,
) {

    /**
     * Creates a call to the cetz line function.
     */
    fun create(start: String, end: String, name: String? = null): String = buildString {
        append("line($start, $end, stroke: ${stroke.value}, mark: (")

        if (startMark != null) append("start: ${startMark.value}, ")
        if (endMark != null) append("end: ${endMark.value}")
        if (startMark == null && endMark == null) append(":")
        append("),")

        if (name != null) append(" name: \"$name\"")
        append(")")
    }

    /**
     * The start or end mark of a cetz line.
     */
    sealed interface Mark {

        /**
         * How the mark is represented in Typst.
         */
        val value: String

        /**
         * A single mark.
         * @property symbol The symbol of the mark.
         * @property fill How to fill the area with the mark.
         * @property stroke What the stroke of the mark should look like.
         * @property length The size of the mark in the direction it is pointing.
         * @property width The size of the mark along the normal of its direction.
         * @property inset Specifies a distance by which something inside the arrow tip is set inwards;
         * for the stealth arrow tip it is the distance by which the back angle is moved inwards.
         * @property scale A factor that is applied to the mark's length, width, and inset.
         * @property offset An offset along the path on which the mark is drawn.
         * @property anchor Anchor to position the mark at.
         * @property slant How much to slant the mark relative to the axis of the arrow.
         * 0% means no slant 100% slants at 45 degrees.
         * @property harpoon When true, only the top half of the mark is drawn.
         * @property flip When true, the mark is flipped along its axis.
         * @property reverse Reverses the direction of the mark.
         */
        data class Single(
            val symbol: Symbol,
            val fill: Paint? = null,
            val stroke: Stroke? = null,
            val length: Double? = null,
            val width: Double? = null,
            val inset: Double? = null,
            val scale: Double = 1.0,
            val offset: Double? = null,
            val anchor: MarkAnchor = MarkAnchor.Tip,
            val slant: Double = 0.0,
            val harpoon: Boolean = false,
            val flip: Boolean = false,
            val reverse: Boolean = false,
        ) : Mark {

            override val value: String = buildString {
                append("(symbol: ")
                append(symbol.value)
                fill?.let { append(", fill: ${it.value}") }
                stroke?.let { append(", stroke: ${it.value}") }
                this@Single.length?.let { append(", length: ${it}em") }
                width?.let { append(", width: ${it}em") }
                inset?.let { append(", inset: ${it}em") }
                append(", scale: $scale")
                offset?.let { append(", offset: ${it}em") }
                append(", anchor: ${anchor.value}")
                append(", slant: $slant")
                append(", harpoon: $harpoon")
                append(", flip: $flip")
                append(", reverse: $reverse)")
            }

        }

        /**
         * Multiple marks that are rendered one after another.
         */
        data class Multiple(val marks: List<Single>) : Mark {

            init {
                require(marks.isNotEmpty()) { "Multiple marks must contain at least one mark" }
            }

            override val value: String = marks.joinToString(separator = ", ", prefix = "(", postfix = ")") { it.value }

        }

    }

    /**
     * The symbol of a mark.
     */
    enum class Symbol(val value: String) {
        Triangle("\"triangle\""),
        Stealth("\"stealth\""),
        CurvedStealth("\"curved-stealth\""),
        Bar("\"bar\""),
        Ellipse("\"ellipse\""),
        Circle("\"circle\""),
        Bracket("\"bracket\""),
        Diamond("\"diamond\""),
        Rectangle("\"rect\""),
        Hook("\"hook\""),
        Straight("\"straight\""),
        Barbed("\"barbed\""),
        Plus("\"plus\""),
        Cross("\"x\""),
        Star("\"star\""),
        Parenthesis("\"parenthesis\""),
    }

    /**
     * The anchor of a mark, i.e., where it should be placed relative to the line.
     */
    enum class MarkAnchor(val value: String) {
        Tip("\"tip\""),
        Base("\"base\""),
        Center("\"center\""),
    }

    companion object {

        /**
         * Represents that a line should not be drawn.
         */
        val None = CetzLine(stroke = Stroke.None)

    }

}
