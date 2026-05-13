package me.lian.hsc.ktypst.structures.tree

import lian.hsc.ktypst.stdlib.cetz.CetzLine
import lian.hsc.ktypst.stdlib.cetz.CetzShape
import lian.hsc.ktypst.stdlib.visualize.Stroke
import lian.hsc.ktypst.stdlib.visualize.paint.Color
import lian.hsc.ktypst.stdlib.visualize.paint.Paint
import me.lian.hsc.ktypst.structures.StructureDslMarker
import me.lian.hsc.ktypst.structures.util.NotNullable

/**
 * A model of a tree node.
 */
data class TreeNodeModel(
    val key: String,
    val content: String?,
    val cetzShape: CetzShape,
    val fill: Paint,
    val contentFill: Paint,
    val nodeStroke: Stroke,
    val connectionStroke: CetzLine,
    val siblingSpace: Double,
    val levelSpace: Double,
    val children: List<TreeNodeModel>
)

/**
 * DSL for defining the style of a tree node.
 */
@StructureDslMarker
class TreeNodeStyleDsl {

    var cetzShape: CetzShape? = null
    var fill: Paint? = null
    var contentFill: Paint? = null
    var nodeStroke: Stroke? = null
    var connectionStroke: CetzLine? = null

    var siblingSpace: Double? = null
    var levelSpace: Double? = null

}

/**
 * DSL for defining a tree structure.
 */
@StructureDslMarker
class TreeDsl {

    /**
     * The key of the tree node.
     */
    var key by NotNullable(::_key)

    /**
     * The content of the tree node as Typst content.
     * If set to null, depending on the renderer, the node will either be rendered empty or with the [key] as content.
     */
    var content: String? = null

    private var _key: String? = null
    private var style: TreeNodeStyleDsl? = null
    private val children: MutableList<TreeDsl> = mutableListOf()

    /**
     * Defines the style of the tree node.
     * Can only be called once per node.
     */
    fun style(block: TreeNodeStyleDsl.() -> Unit) {
        check(style == null) { "Style can only be defined once per node." }
        style = TreeNodeStyleDsl().apply(block)
    }

    /**
     * Add a child node to the tree.
     */
    fun child(block: TreeDsl.() -> Unit) {
        children += TreeDsl().apply(block)
    }

    internal fun toModel(
        cetzShape: CetzShape = CetzShape.Circle(2.0),
        fill: Paint = Color.Named.White,
        contentFill: Paint = Color.Named.Black,
        nodeStroke: Stroke = Stroke(),
        connectionStroke: CetzLine = CetzLine(),
        siblingSpace: Double = 1.0,
        levelSpace: Double = 2.0,
    ): TreeNodeModel {
        val newShape = style?.cetzShape ?: cetzShape
        val newFill = style?.fill ?: fill
        val newContentFill = style?.contentFill ?: contentFill
        val newNodeStroke = style?.nodeStroke ?: nodeStroke
        val newConnectionStroke = style?.connectionStroke ?: connectionStroke
        val newSiblingSpace = style?.siblingSpace ?: siblingSpace
        val newLevelSpace = style?.levelSpace ?: levelSpace

        return TreeNodeModel(
            key = key,
            content = content,
            cetzShape = newShape,
            fill = newFill,
            contentFill = newContentFill,
            nodeStroke = newNodeStroke,
            connectionStroke = newConnectionStroke,
            siblingSpace = newSiblingSpace,
            levelSpace = newLevelSpace,
            children = children.map {
                it.toModel(
                    cetzShape = newShape,
                    fill = newFill,
                    contentFill = newContentFill,
                    nodeStroke = newNodeStroke,
                    connectionStroke = newConnectionStroke,
                    siblingSpace = newSiblingSpace,
                    levelSpace = newLevelSpace
                )
            }
        )
    }

    internal fun build(layoutEngine: TreeLayoutEngine, renderEngine: TreeRenderEngine): String {
        val model = toModel()
        val layout = layoutEngine.layout(model)
        return renderEngine.render(layout)
    }

}

fun tree(
    layoutEngine: TreeLayoutEngine = PackedTreeLayoutEngine,
    renderEngine: TreeRenderEngine = EmptyContentAsKeyRenderEngine,
    block: TreeDsl.() -> Unit
) = TreeDsl().apply(block).build(layoutEngine, renderEngine)

