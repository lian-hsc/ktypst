package me.lian.hsc.ktypst.structures.tree.simple

import lian.hsc.ktypst.stdlib.cetz.CetzLine
import lian.hsc.ktypst.stdlib.visualize.Stroke
import lian.hsc.ktypst.stdlib.visualize.paint.Color
import me.lian.hsc.ktypst.structures.StructureDslMarker
import me.lian.hsc.ktypst.structures.tree.layout.PackedTreeLayoutEngine
import me.lian.hsc.ktypst.structures.tree.layout.TreeLayoutEngine

/**
 * DSL for defining red-black trees.
 */
@StructureDslMarker
class RedBlackTreeDsl(parent: RedBlackTreeDsl?) : TreeDsl<RedBlackTreeDsl>(parent) {

    override fun createDsl() = RedBlackTreeDsl(this)

    fun red(block: RedBlackTreeDsl.() -> Unit): Unit = child {
        style {
            stroke = CetzLine(Stroke(Color.Named.Red))
        }

        block()
    }

    fun black(block: RedBlackTreeDsl.() -> Unit): Unit = child {
        style {
            stroke = CetzLine(Stroke(Color.Named.Black))
        }

        block()
    }

}

fun redBlackTree(
    layoutEngine: TreeLayoutEngine = PackedTreeLayoutEngine,
    renderEngine: SimpleTreeRenderEngine = EmptyContentAsKeyRenderEngine,
    block: RedBlackTreeDsl.() -> Unit
) = RedBlackTreeDsl(null).apply { applyDefaultStyle() }.apply(block).build(layoutEngine, renderEngine)
