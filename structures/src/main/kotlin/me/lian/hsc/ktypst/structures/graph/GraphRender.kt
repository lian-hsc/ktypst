package me.lian.hsc.ktypst.structures.graph

import lian.hsc.ktypst.stdlib.cetz.CetzLine
import lian.hsc.ktypst.stdlib.cetz.CetzShape
import lian.hsc.ktypst.stdlib.visualize.Point
import lian.hsc.ktypst.stdlib.visualize.paint.Paint

data class GraphNode(
    val key: String,
    val content: String?,
    val position: Point<Double>,
    val shape: CetzShape,
    val fillContent: Paint,
)

data class GraphEdge(
    val from: String,
    val to: String,
    val line: CetzLine,
    val content: String?,
    val contentPosition: Double?,
    val contentAngle: ContentAngle?,
    val contentAnchor: Direction?,
    val contentPadding: Double?,
    val bend: Double?,
    val fillContent: Paint,
)

interface GraphRenderEngine {

    fun render(nodes: List<GraphNode>, edges: List<GraphEdge>): String

}

abstract class GraphRenderHelper : GraphRenderEngine {

    abstract fun getContent(node: GraphNode): String?

    override fun render(nodes: List<GraphNode>, edges: List<GraphEdge>) = buildString {
        appendLine(
            """
            #set page(width: auto, height: auto, fill: none, margin: 1em)

            #import "@preview/cetz:0.5.1"

            #let curve-points(ctx, from, to, bend) = {
              import cetz.vector

              let (ctx, a, b) = cetz.coordinate.resolve(ctx, from, to)

              let mid = vector.div(vector.add(a, b), 2)
              let direction = ((b.at(0) - a.at(0)), (b.at(1) - a.at(1)))
              let perpendicular = (-direction.at(1), direction.at(0))
              let nPerpendicular = vector.norm(perpendicular)
              let ctrl-point = vector.add(mid, vector.scale(nPerpendicular, bend * 2))

              let start-angle = vector.angle2(a, ctrl-point)
              let end-angle = vector.angle2(b, ctrl-point)

              return (
                (name: from, anchor: start-angle),
                (name: to, anchor: end-angle),
                curve-through,
              )
            }

            #cetz.canvas({
            import cetz.draw: *
            """.trimIndent()
        )

        for (node in nodes) {
            render(node)
        }

        appendLine("on-layer(-1, {")
        for (edge in edges) {
            render(edge)
        }
        appendLine("})")

        appendLine("})")
    }

    fun StringBuilder.render(node: GraphNode) {
        appendLine(node.shape.create(node.position, node.key))
        val content = getContent(node)
        if (content != null) {
            appendLine("content(\"${node.key}\", text(fill: ${node.fillContent.value})[${content}])")
        }
    }

    fun StringBuilder.render(edge: GraphEdge) {
        if (edge.bend != null) appendLine("get-ctx(ctx => {")
        appendLine(
            edge.line.create(
                function = if (edge.bend != null) "bezier" else "line",
                points =
                    if (edge.bend != null) arrayOf("..curve-points(ctx, \"${edge.from}\", \"${edge.to}\", ${edge.bend})")
                    else arrayOf("\"${edge.from}\"", "\"${edge.to}\""),
                name = "${edge.from}-${edge.to}"
            )
        )
        if (edge.bend != null) appendLine("})")

        if (edge.content != null) {
            val angle = (edge.contentAngle ?: ContentAngle.Relative(ContentAngle.Refence.End))
                .getValue("${edge.from}-${edge.to}")

            appendLine(
                "content(" +
                    "(\"${edge.from}-${edge.to}.start\", " +
                    "${edge.contentPosition?.let { it * 100 } ?: 50}%, " +
                    "\"${edge.from}-${edge.to}.end\"), " +
                    "text(fill: ${edge.fillContent.value})[${edge.content}], " +
                    "angle: $angle" +
                    "anchor: ${edge.contentAnchor?.value ?: "\"south\""}," +
                    "padding: ${edge.contentPadding ?: 0.1})")
        }
    }

}

object EmptyContentAsKeyGraphRender : GraphRenderHelper() {

    override fun getContent(node: GraphNode): String = node.content ?: node.key

}

object EmptyContentAsEmptyGraphRender : GraphRenderHelper() {

    override fun getContent(node: GraphNode): String? = node.content

}
