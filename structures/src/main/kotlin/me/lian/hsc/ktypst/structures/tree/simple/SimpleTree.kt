package me.lian.hsc.ktypst.structures.tree.simple

import lian.hsc.ktypst.stdlib.cetz.CetzLine
import lian.hsc.ktypst.stdlib.cetz.CetzShape
import lian.hsc.ktypst.stdlib.visualize.Stroke
import lian.hsc.ktypst.stdlib.visualize.paint.Color
import lian.hsc.ktypst.stdlib.visualize.paint.Paint
import me.lian.hsc.ktypst.structures.StructureDslMarker
import me.lian.hsc.ktypst.structures.tree.layout.PackedTreeLayoutEngine
import me.lian.hsc.ktypst.structures.tree.layout.TreeLayoutEngine
import me.lian.hsc.ktypst.structures.tree.layout.TreeNodeHolder
import me.lian.hsc.ktypst.structures.util.NotNullable
import java.net.SocketTimeoutException


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
sealed class TreeDsl<Type : TreeDsl<Type>> {

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
    protected var style: TreeNodeStyleDsl? = null
    protected val children: MutableList<Type> = mutableListOf()

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
    fun child(block: Type.() -> Unit) {
        children += createDsl().apply(block)
    }

    protected abstract fun createDsl(): Type

    internal fun toHolder(
        cetzShape: CetzShape = CetzShape.Circle(2.0),
        fill: Paint = Color.Named.White,
        contentFill: Paint = Color.Named.Black,
        nodeStroke: Stroke = Stroke(),
        connectionStroke: CetzLine = CetzLine(),
        siblingSpace: Double = 1.0,
        levelSpace: Double = 2.0,
    ): TreeNodeHolder<SimpleTreeNodeModel> {
        val newShape = style?.cetzShape ?: cetzShape
        val newFill = style?.fill ?: fill
        val newContentFill = style?.contentFill ?: contentFill
        val newNodeStroke = style?.nodeStroke ?: nodeStroke
        val newConnectionStroke = style?.connectionStroke ?: connectionStroke
        val newSiblingSpace = style?.siblingSpace ?: siblingSpace
        val newLevelSpace = style?.levelSpace ?: levelSpace

        return TreeNodeHolder(
            SimpleTreeNodeModel(
                key = key,
                content = content,
                cetzShape = newShape,
                fill = newFill,
                contentFill = newContentFill,
                nodeStroke = newNodeStroke,
                connectionStroke = newConnectionStroke,
            ),
            width = newShape.width,
            height = newShape.height,
            siblingSpace = newSiblingSpace,
            levelSpace = newLevelSpace,
            children = children.map {
                it.toHolder(
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

    internal fun build(layoutEngine: TreeLayoutEngine, renderEngine: SimpleTreeRenderEngine): String {
        val holder = toHolder()
        val layout = layoutEngine.layout(holder)
        return renderEngine.render(layout)
    }

}

class SimpleTreeDsl : TreeDsl<SimpleTreeDsl>() {

    override fun createDsl() = SimpleTreeDsl()

}

fun tree(
    layoutEngine: TreeLayoutEngine = PackedTreeLayoutEngine,
    renderEngine: SimpleTreeRenderEngine = EmptyContentAsKeyRenderEngine,
    block: SimpleTreeDsl.() -> Unit
) = SimpleTreeDsl().apply(block).build(layoutEngine, renderEngine)

