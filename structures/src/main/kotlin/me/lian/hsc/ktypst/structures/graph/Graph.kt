package me.lian.hsc.ktypst.structures.graph

import lian.hsc.ktypst.stdlib.cetz.CetzLine
import lian.hsc.ktypst.stdlib.cetz.CetzShape
import lian.hsc.ktypst.stdlib.visualize.Point
import lian.hsc.ktypst.stdlib.visualize.paint.Color
import lian.hsc.ktypst.stdlib.visualize.paint.Paint
import me.lian.hsc.ktypst.structures.StructureDslMarker
import me.lian.hsc.ktypst.structures.util.GetOrDelegate
import me.lian.hsc.ktypst.structures.util.NotNullable

@StructureDslMarker
class GraphDsl {

    var nodeShape: CetzShape = CetzShape.Circle(2.0, fill = Color.Named.White)
    var fillNodeContent: Paint = Color.Named.Black
    var edgeLine: CetzLine = CetzLine()
    var fillEdgeContent: Paint = Color.Named.Black

    private val nodes = mutableListOf<GraphNodeDsl>()
    private val edges = mutableListOf<GraphEdgeDsl>()

    fun node(block: GraphNodeDsl.() -> Unit) {
        nodes += GraphNodeDsl(this).apply(block)
    }

    fun node(key: String, block: GraphNodeDsl.() -> Unit = { }): Unit = node {
        this.key = key
        block()
    }

    fun edge(block: GraphEdgeDsl.() -> Unit) {
        edges += GraphEdgeDsl("", "", this).apply(block)
    }

    fun edge(from: String, to: String, block: GraphEdgeDsl.() -> Unit = { }): Unit = edge {
        this.from = from
        this.to = to
        block()
    }

    infix fun String.to(that: String): Unit = edge(this, that)

    fun asDirectedGraph() {
        edgeLine = CetzLine(endMark = CetzLine.Mark.Single(CetzLine.Symbol.Triangle, fill = Color.Named.Black))
    }

    internal fun build(renderEngine: GraphRenderEngine): String {
        val positions = nodes.associate { it.key to it.position }
        val nodes = nodes.map { it.toModel(positions) }
        val edges = edges.map { it.toModel() }

        for (edge in edges) {
            check(edge.from in positions) {
                "Node ${edge.from} not found, but required for edge ${edge.from} -> ${edge.to}."
            }

            check(edge.to in positions) {
                "Node ${edge.to} not found, but required for edge ${edge.from} -> ${edge.to}."
            }
        }

        return renderEngine.render(nodes, edges)
    }

}


@StructureDslMarker
class GraphNodeDsl(graph: GraphDsl) {

    var key by NotNullable(::_key)
    var content by NotNullable(::_content)
    var position by NotNullable(::_position)
    var shape: CetzShape by GetOrDelegate(::_shape, graph::nodeShape)
    var fillContent: Paint by GetOrDelegate(::_fillContent, graph::fillNodeContent)

    private var _key: String? = null
    private var _content: String? = null
    private var _position: Position? = null
    private var _shape: CetzShape? = null
    private var _fillContent: Paint? = null

    fun at(x: Double, y: Double) {
        position = AbsolutePosition(x, y)
    }

    fun at(x: Int, y: Int): Unit = at(x.toDouble(), y.toDouble())

    infix fun RelativeCoordinates.of(reference: String) {
        position = RelativePosition(Point(x, y), reference)
    }

    internal fun toModel(positions: Map<String, Position>) = GraphNode(
        key = key,
        content = _content,
        position = position.resolve(positions),
        shape = shape,
        fillContent = fillContent,
    )

}

@StructureDslMarker
class GraphEdgeDsl(var from: String, var to: String, graph: GraphDsl) {

    var line: CetzLine by GetOrDelegate(::_line, graph::edgeLine)
    var content: String? = null
    var contentPosition: Double? = null
    var contentAngle: ContentAngle? = null
    var contentAnchor: Direction? = null
    var contentPadding: Double? = null
    var bend: Double? = null
    var fillContent: Paint by GetOrDelegate(::_fillContent, graph::fillEdgeContent)

    private var _line: CetzLine? = null
    private var _fillContent: Paint? = null

    internal fun toModel() = GraphEdge(
        from,
        to,
        line,
        content,
        contentPosition,
        contentAngle,
        contentAnchor,
        contentPadding,
        bend,
        fillContent,
    )

}

sealed interface ContentAngle {

    fun getValue(name: String): String

    data class Fixed(val angle: Double) : ContentAngle {
        override fun getValue(name: String) = "${angle}deg"
    }

    data class Relative(val reference: Refence) : ContentAngle {
        override fun getValue(name: String) = "\"$name.${reference.value}\""
    }

    enum class Refence(val value: String) {
        Start("start"),
        Middle("mid"),
        End("end"),
    }

}

fun graph(renderEngine: GraphRenderEngine = EmptyContentAsKeyGraphRender, block: GraphDsl.() -> Unit) =
    GraphDsl().apply(block).build(renderEngine)
