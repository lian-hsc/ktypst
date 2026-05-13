package me.lian.hsc.ktypst.structures.tree.b

import lian.hsc.ktypst.stdlib.cetz.CetzLine
import lian.hsc.ktypst.stdlib.cetz.CetzShape
import lian.hsc.ktypst.stdlib.visualize.paint.Color
import lian.hsc.ktypst.stdlib.visualize.paint.Paint
import me.lian.hsc.ktypst.structures.StructureDslMarker
import me.lian.hsc.ktypst.structures.tree.layout.PackedTreeLayoutEngine
import me.lian.hsc.ktypst.structures.tree.layout.TreeLayoutEngine
import me.lian.hsc.ktypst.structures.tree.layout.TreeNodeHolder
import me.lian.hsc.ktypst.structures.util.GetOrDelegate


/**
 * DSL for defining the style of a b tree node.
 */
@StructureDslMarker
class BTreeNodeStyleDsl(parent: BTreeNodeStyleDsl? = null) {

    var pointer: CetzShape by GetOrDelegate(::_pointer, parent?.let { it::pointer })
    var content: CetzShape by GetOrDelegate(::_content, parent?.let { it::content })
    var contentFill: Paint by GetOrDelegate(::_contentFill, parent?.let { it::contentFill })
    var stroke: CetzLine by GetOrDelegate(::_stroke, parent?.let { it::stroke })
    var siblingSpace: Double by GetOrDelegate(::_siblingSpace, parent?.let { it::siblingSpace })
    var levelSpace: Double by GetOrDelegate(::_levelSpace, parent?.let { it::levelSpace })

    private var _pointer: CetzShape? = null
    private var _content: CetzShape? = null
    private var _contentFill: Paint? = null
    private var _stroke: CetzLine? = null
    private var _siblingSpace: Double? = null
    private var _levelSpace: Double? = null

}

/**
 * DSL for defining a b tree structure.
 *
 * At build time, the dls must either have no children at all (leaf node) or
 * have exactly one more child than keys (internal node).
 * Otherwise, an exception will be thrown.
 */
@StructureDslMarker
class BTreeDsl(parent: BTreeDsl? = null) {

    internal var style: BTreeNodeStyleDsl = BTreeNodeStyleDsl(parent?.style)
    private val keys = mutableListOf<String>()
    private val children = mutableListOf<BTreeDsl>()

    /**
     * Defines the style of the tree node.
     * Can only be called once per node.
     */
    fun style(block: BTreeNodeStyleDsl.() -> Unit) {
        style.apply(block)
    }

    /**
     * Adds a key to the tree.
     */
    fun key(key: String) {
        keys += key
    }

    /**
     * Add a child node to the tree.
     */
    fun child(block: BTreeDsl.() -> Unit) {
        children += BTreeDsl(this).apply(block)
    }

    internal fun applyDefaultStyle(): Unit = style {
        pointer = CetzShape.Rectangle(.5, 2.0, fill = Color.Named.Gray)
        content = CetzShape.Square(2.0, fill = Color.Named.White)
        contentFill = Color.Named.Black
        stroke = CetzLine()
        siblingSpace = 1.0
        levelSpace = 2.0
    }

    internal fun toHolder(): TreeNodeHolder<BTreeNodeModel> {
        check(children.isEmpty() || children.size == keys.size + 1) {
            "Expected 0 or ${keys.size + 1} children, got ${children.size}"
        }

        return TreeNodeHolder(
            model = BTreeNodeModel(
                keys = keys,
                pointer = style.pointer,
                content = style.content,
                contentFill = style.contentFill,
                stroke = style.stroke
            ),
            width = keys.size * style.content.width + children.size * style.pointer.width,
            height = maxOf(style.content.height, style.pointer.height),
            siblingSpace = style.siblingSpace,
            levelSpace = style.levelSpace,
            children = children.map { it.toHolder() }
        )
    }

    internal fun build(layoutEngine: TreeLayoutEngine, renderEngine: BTreeRenderEngine): String {
        val holder = toHolder()
        val layout = layoutEngine.layout(holder)
        return renderEngine.render(layout)
    }

}

fun btree(
    layoutEngine: TreeLayoutEngine = PackedTreeLayoutEngine,
    renderEngine: BTreeRenderEngine = TheBTreeRenderEngine,
    block: BTreeDsl.() -> Unit
) = BTreeDsl(null).apply { applyDefaultStyle() }.apply(block).build(layoutEngine, renderEngine)
