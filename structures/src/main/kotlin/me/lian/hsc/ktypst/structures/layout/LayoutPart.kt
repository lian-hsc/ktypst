package me.lian.hsc.ktypst.structures.layout

import lian.hsc.ktypst.stdlib.layout.Length
import lian.hsc.ktypst.stdlib.layout.Margin
import lian.hsc.ktypst.stdlib.visualize.Point
import lian.hsc.ktypst.stdlib.visualize.paint.Paint

data class LayoutPart(
    val unit: Length.Unit,
    val width: Double,
    val height: Double,
    val boxes: List<Box>,
    val headers: List<String>,
    val content: String
) {

    fun toUnit(target: Length.Unit) = LayoutPart(
        unit = target,
        width = unit.to(width, target),
        height = unit.to(height, target),
        boxes = boxes.map {
            Box(
                name = it.name,
                center = Point(unit.to(it.center.x, target), unit.to(it.center.y, target)),
                width = unit.to(width, target),
                height = unit.to(height, target)
            )
        },
        headers = headers,
        content = content
    )

    fun layout(
        fill: Paint? = null,
        margin: Margin = Margin(1.0)
    ) = Layout(
        width = width + margin.left + margin.right,
        height = height + margin.top + margin.bottom,
        content = """
                #set page(width: auto, height: auto, fill: ${fill?.value ?: "none"}, margin: (
                    top: ${margin.top}${unit.value},
                    right: ${margin.right}${unit.value},
                    bottom: ${margin.bottom}${unit.value},
                    left: ${margin.left}${unit.value}
                ))

                ${headers.joinToString("\n")}

                $content
            """.trimIndent(), boxes = boxes.map { it.translate(margin.left, margin.top) }
    )

}

fun combine(
    vararg parts: LayoutPart,
    direction: Direction = Direction.TopToBottom,
    spacing: Double = 2.0,
    alignments: Alignments = Alignment.Center + Alignment.Horizon
): LayoutPart {
    val targetUnit = parts.first().unit
    val unitParts = parts.map { it.toUnit(targetUnit) }

    var delta = 0.0
    val boxes = mutableListOf<Box>()

    val newHeight = unitParts.maxOf { it.height }
    val newWidth = unitParts.maxOf { it.width }

    for ((index, part) in unitParts.withIndex()) {
        boxes += part.boxes.map {
            var (deltaX, deltaY) = direction.translate(delta)
            for (alignment in alignments) {
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
        unit = targetUnit,
        width = if (direction == Direction.TopToBottom) parts.maxOf { it.width } else delta - spacing,
        height = if (direction == Direction.TopToBottom) delta - spacing else parts.maxOf { it.height },
        boxes = boxes,
        headers = parts.flatMap { it.headers }.distinct(),
        content = """
                    #stack(
                        dir: ${direction.value},
                        spacing: ${spacing}em,
                        ${parts.joinToString(",\n") { "align(${alignments.value})[${it.content}]" }}
                    )
                """.trimIndent()
    )
}

class LayoutPartBuilder {

    private val boxes = mutableListOf<Box>()
    private val headers = mutableListOf<String>()

    var unit: Length.Unit = Length.Unit.Em

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

    fun setContent(content: String) {
        this.content.clear()
        this.content.append(content)
    }

    fun build(): LayoutPart {
        val negX = boxes.minOf { it.left }
        val negY = boxes.minOf { it.top }

        val newBoxes = boxes.map { it.translate(-negX, -negY) }
        val width = newBoxes.maxOf { it.right }
        val height = newBoxes.maxOf { it.bottom }

        return LayoutPart(
            unit = unit,
            width = width,
            height = height,
            boxes = newBoxes,
            headers = headers,
            content = content.toString()
        )
    }

}

inline fun buildLayoutPart(block: LayoutPartBuilder.() -> Unit): LayoutPart = LayoutPartBuilder().apply(block).build()
