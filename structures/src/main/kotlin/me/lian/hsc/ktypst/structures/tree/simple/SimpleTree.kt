package me.lian.hsc.ktypst.structures.tree.simple

import lian.hsc.ktypst.stdlib.cetz.CetzLine
import lian.hsc.ktypst.stdlib.cetz.CetzShape
import lian.hsc.ktypst.stdlib.visualize.paint.Color
import lian.hsc.ktypst.stdlib.visualize.paint.Paint
import me.lian.hsc.ktypst.structures.StructureDslMarker
import me.lian.hsc.ktypst.structures.tree.layout.PackedTreeLayoutEngine
import me.lian.hsc.ktypst.structures.tree.layout.TreeLayoutEngine
import me.lian.hsc.ktypst.structures.tree.layout.TreeNodeHolder
import me.lian.hsc.ktypst.structures.util.GetOrDelegate
import me.lian.hsc.ktypst.structures.util.NotNullable
import kotlin.random.Random

/**
 * DSL for defining the style of a tree node.
 */
@StructureDslMarker
class TreeNodeStyleDsl(parent: TreeNodeStyleDsl? = null) {

    var shape: CetzShape by GetOrDelegate(::_shape, parent?.let { it::shape })
    var contentFill: Paint by GetOrDelegate(::_contentFill, parent?.let { it::contentFill })
    var stroke: CetzLine by GetOrDelegate(::_stroke, parent?.let { it::stroke })
    var siblingSpace: Double by GetOrDelegate(::_siblingSpace, parent?.let { it::siblingSpace })
    var levelSpace: Double by GetOrDelegate(::_levelSpace, parent?.let { it::levelSpace })

    private var _shape: CetzShape? = null
    private var _contentFill: Paint? = null
    private var _stroke: CetzLine? = null
    private var _siblingSpace: Double? = null
    private var _levelSpace: Double? = null

}

/**
 * DSL for defining a tree structure.
 */
@StructureDslMarker
sealed class TreeDsl<Type : TreeDsl<Type>>(parent: Type?) {

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
    internal var style: TreeNodeStyleDsl = TreeNodeStyleDsl(parent?.style)
    protected val children: MutableList<Type> = mutableListOf()

    /**
     * Defines the style of the tree node.
     * Can only be called once per node.
     */
    fun style(block: TreeNodeStyleDsl.() -> Unit) {
        style.apply(block)
    }

    /**
     * Add a child node to the tree.
     */
    fun child(block: Type.() -> Unit) {
        children += createDsl().apply(block)
    }

    /**
     * Adds a missing child (i.e., a child that is not rendered)
     */
    fun missingChild() = child {
        style {
            shape = shape.transparent()
            contentFill = Paint.None
            stroke = CetzLine.None
            siblingSpace = 0.0
            levelSpace = 0.0
        }
        key = "missing-${Random.nextInt()}"
        content = ""
    }

    protected abstract fun createDsl(): Type

    internal fun applyDefaultStyle() = style {
        shape = CetzShape.Circle(2.0)
        contentFill = Color.Named.Black
        stroke = CetzLine()
        siblingSpace = 1.0
        levelSpace = 2.0
    }

    internal fun toHolder(): TreeNodeHolder<SimpleTreeNodeModel> {
        return TreeNodeHolder(
            SimpleTreeNodeModel(
                key = key,
                content = content,
                cetzShape = style.shape,
                contentFill = style.contentFill,
                stroke = style.stroke,
            ),
            width = style.shape.width,
            height = style.shape.height,
            siblingSpace = style.siblingSpace,
            levelSpace = style.levelSpace,
            children = children.map { it.toHolder() }
        )
    }

    internal fun build(layoutEngine: TreeLayoutEngine, renderEngine: SimpleTreeRenderEngine): String {
        val holder = toHolder()
        val layout = layoutEngine.layout(holder)
        return renderEngine.render(layout)
    }

}

class SimpleTreeDsl(parent: SimpleTreeDsl?) : TreeDsl<SimpleTreeDsl>(parent) {

    override fun createDsl() = SimpleTreeDsl(this)

}

fun tree(
    layoutEngine: TreeLayoutEngine = PackedTreeLayoutEngine,
    renderEngine: SimpleTreeRenderEngine = EmptyContentAsKeyRenderEngine,
    block: SimpleTreeDsl.() -> Unit
) = SimpleTreeDsl(null).apply { applyDefaultStyle() }.apply(block).build(layoutEngine, renderEngine)

