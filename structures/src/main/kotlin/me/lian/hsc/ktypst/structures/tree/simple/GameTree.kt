package me.lian.hsc.ktypst.structures.tree.simple

import lian.hsc.ktypst.stdlib.cetz.CetzLine
import lian.hsc.ktypst.stdlib.cetz.CetzShape
import lian.hsc.ktypst.stdlib.visualize.Stroke
import lian.hsc.ktypst.stdlib.visualize.paint.Color
import lian.hsc.ktypst.stdlib.visualize.paint.Paint
import me.lian.hsc.ktypst.structures.StructureDslMarker
import me.lian.hsc.ktypst.structures.tree.layout.PackedTreeLayoutEngine
import me.lian.hsc.ktypst.structures.tree.layout.TreeLayoutEngine
import me.lian.hsc.ktypst.structures.util.NotNullable

/**
 * DSL for defining leave nodes of a game tree.
 */
@StructureDslMarker
class GameTreeLeafDsl(parent: GameTreeDsl) {

    /**
     * The key of the tree node.
     */
    var key by NotNullable(::_key)

    /**
     * The value of the tree node.
     */
    var value by NotNullable(::_value)

    /**
     * The content of the tree node as Typst content.
     * If set to null, depending on the renderer, the node will either be rendered empty or with the [key] as content.
     */
    var content: String? = null

    private var _key: String? = null
    private var _value: String? = null

    internal var style: TreeNodeStyleDsl = TreeNodeStyleDsl(parent.style)

    /**
     * Defines the style of the tree node.
     * Can only be called once per node.
     */
    fun style(block: TreeNodeStyleDsl.() -> Unit) {
        style = TreeNodeStyleDsl().apply(block)
    }

}

/**
 * DSL for defining game trees.
 */
@StructureDslMarker
class GameTreeDsl(parent: GameTreeDsl?) : TreeDsl<GameTreeDsl>(parent) {

    override fun createDsl() = GameTreeDsl(this)

    /**
     * Add a leaf node to the tree.
     */
    fun leaf(block: GameTreeLeafDsl.() -> Unit) {
        val leaf = GameTreeLeafDsl(this).apply(block)
        children += GameTreeDsl(this).apply {
            key = leaf.key
            content = leaf.content
            style = leaf.style

            child {
                style {
                    shape = CetzShape.Circle(2.0, fill = Paint.None, stroke = Stroke.None)
                    contentFill = Color.Named.Black
                    stroke = CetzLine.None
                }

                key = leaf.value
            }
        }
    }

    /**
     * Adds a leaf with the given key and value to the tree.
     */
    fun leaf(key: String, value: String, block: GameTreeLeafDsl.() -> Unit = { }) {
        leaf {
            this.key = key
            this.value = value
            apply(block)
        }
    }

}

fun gameTree(
    layoutEngine: TreeLayoutEngine = PackedTreeLayoutEngine,
    renderEngine: SimpleTreeRenderEngine = EmptyContentAsKeyRenderEngine,
    block: GameTreeDsl.() -> Unit
) = GameTreeDsl(null).apply { applyDefaultStyle() }.apply(block).build(layoutEngine, renderEngine)

