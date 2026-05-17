package me.lian.hsc.ktypst.structures.layout

import lian.hsc.ktypst.stdlib.visualize.paint.Paint

data class LayoutPart(
    val width: Double,
    val height: Double,
    val boxes: List<Box>,
    val headers: List<String>,
    val content: String
) {

    fun layout(fill: Paint? = null, margin: Double = 1.0): Layout = Layout(
        width + 2 * margin,
        height + 2 * margin,
        """
        #set page(width: auto, height: auto, fill: ${fill?.value ?: "none"}, margin: ${margin}em)

        ${headers.joinToString("\n")}

        $content
    """.trimIndent(), boxes.map { it.translate(margin, margin) })

}

fun combine(
    vararg parts: LayoutPart,
    direction: Direction = Direction.TopToBottom,
    spacing: Double = 2.0,
    alignment: Alignments = Alignment.Center + Alignment.Horizon
): LayoutPart {
    var delta = 0.0
    val boxes = mutableListOf<Box>()

    val newHeight = parts.maxOf { it.height }
    val newWidth = parts.maxOf { it.width }

    for ((index, part) in parts.withIndex()) {
        boxes += part.boxes.map {
            var (deltaX, deltaY) = direction.translate(delta)
            for (alignment in alignment) {
                val (alignX, alignY) = alignment.transform(
                    part.width, part.height,
                    if (direction == Direction.TopToBottom) newWidth else part.width,
                    if (direction == Direction.TopToBottom) part.height else newHeight
                )
                deltaX += alignX
                deltaY += alignY
            }

            it.translate(deltaX, deltaY).rename("$index-${it.name}")
        }

        delta += (if (direction == Direction.TopToBottom) part.height else part.width) + spacing
    }

    return LayoutPart(
        if (direction == Direction.TopToBottom) parts.maxOf { it.width } else delta - spacing,
        if (direction == Direction.TopToBottom) delta - spacing else parts.maxOf { it.height },
        boxes,
        parts.flatMap { it.headers }.distinct(),
        """
            #stack(
                dir: ${direction.value},
                spacing: ${spacing}em,
                ${parts.joinToString(",\n") { "align(${alignment.value})[${it.content}]" }}
            )
        """.trimIndent()
    )
}

class LayoutPartBuilder {

    private val boxes = mutableListOf<Box>()
    private val headers = mutableListOf<String>()
    val content: StringBuilder = StringBuilder()

    operator fun Box.unaryPlus() {
        boxes += this
    }

    operator fun String.unaryPlus() {
        headers += this
    }

    fun appendLine(line: String) {
        content.appendLine(line)
    }

    fun build(): LayoutPart {
        val negX = boxes.minOf { it.left }
        val negY = boxes.minOf { it.top }

        val newBoxes = boxes.map { it.translate(-negX, -negY) }
        val width = newBoxes.maxOf { it.right }
        val height = newBoxes.maxOf { it.bottom }

        return LayoutPart(
            width,
            height,
            newBoxes,
            headers,
            content.toString()
        )
    }

}

fun buildLayoutPart(block: LayoutPartBuilder.() -> Unit): LayoutPart = LayoutPartBuilder().apply(block).build()
