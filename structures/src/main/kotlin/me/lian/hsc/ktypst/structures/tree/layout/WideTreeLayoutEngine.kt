package me.lian.hsc.ktypst.structures.tree.layout

/**
 * A layout engine that calculates the total width of each subtree and puts them next to each other.
 * In contrast to the [packed layout engine][PackedTreeLayoutEngine],
 * this layout engine does not try to pack the tree as tightly as possible,
 * but rather tries to make it as wide as necessary to avoid overlapping nodes.
 * This engine ensures that overlaps will never occoure at the cost of potentially much wider trees.
 */
object WideTreeLayoutEngine : TreeLayoutEngine {

    private data class Subtree<T>(
        val node: TreeNodeLayout<T>,
        val width: Double,
    )

    override fun <T> layout(model: TreeNodeHolder<T>): TreeNodeLayout<T> {
        val laidOut = layoutSubtree(model, 0.0).node
        return assignY(laidOut, 0.0)
    }

    private fun <T> layoutSubtree(model: TreeNodeHolder<T>, offset: Double): Subtree<T> {
        if (model.children.isEmpty()) {
            // no children, just lay out the node
            return Subtree(
                TreeNodeLayout(
                    model = model.model,
                    x = offset + model.width / 2.0,
                    y = 0.0,
                    width = model.width,
                    height = model.height,
                    siblingSpace = model.siblingSpace,
                    levelSpace = model.levelSpace,
                    children = emptyList()
                ),
                model.width
            )
        }

        // find widths of the subtrees and this width
        val childWidths = model.children.map { measureSubtree(it) }
        val childrenWidth = childWidths.sum() + model.siblingSpace * (childWidths.size - 1)
        val width = maxOf(model.width, childrenWidth)

        // find start offset of the children
        var childOffset = offset + (width - childrenWidth) / 2.0

        // layout subtrees of all children
        val positionedChildren = mutableListOf<TreeNodeLayout<T>>()

        for ((child, childWidth) in model.children.zip(childWidths)) {
            val subtree = layoutSubtree(child, childOffset)
            positionedChildren += subtree.node

            childOffset += childWidth + model.siblingSpace
        }

        // find x position of the root node
        val leftMost = positionedChildren.minOf { it.x - it.width / 2 }
        val rightMost = positionedChildren.maxOf { it.x + it.width / 2 }
        val rootX = (leftMost + rightMost) / 2

        return Subtree(
            TreeNodeLayout(
                model.model,
                rootX, 0.0,
                model.width, model.height,
                model.siblingSpace, model.levelSpace,
                positionedChildren
            ),
            width
        )
    }

    private fun <T> measureSubtree(model: TreeNodeHolder<T>): Double {
        if (model.children.isEmpty()) {
            return model.width
        }

        val childrenWidth =
            model.children.sumOf { measureSubtree(it) } +
                model.siblingSpace * (model.children.size - 1)

        return maxOf(model.width, childrenWidth)
    }

}
