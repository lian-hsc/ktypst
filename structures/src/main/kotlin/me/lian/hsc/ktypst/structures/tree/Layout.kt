package me.lian.hsc.ktypst.structures.tree

data class TreeNodeLayout(
    val model: TreeNodeModel,
    val x: Double,
    val y: Double,
    val children: List<TreeNodeLayout>,
)

/**
 * A tree layout engine that gets the root node of a tree as [TreeNodeModel] and
 * returns the [TreeNodeLayout] of the root node, which can then be used by a renderer to render the tree.
 */
interface TreeLayoutEngine {

    /**
     * Layouts the given [model] and returns the layout of the root node.
     * The engine must assign each node a position and size.
     */
    fun layout(model: TreeNodeModel): TreeNodeLayout

}

/**
 * A helper function that assigns the given [node] a position based on the given [y] coordinate.
 * This is the most primitive approach but should be enough for the y coordinate for almost all layout engines.
 */
fun assignY(node: TreeNodeLayout, y: Double): TreeNodeLayout {
    val children = node.children.map {
        val childY = y - node.model.cetzShape.height / 2 - node.model.levelSpace - it.model.cetzShape.height / 2
        assignY(it, childY)
    }

    return node.copy(y = y, children = children)
}

/**
 * A [tree layout engine][TreeLayoutEngine] that tries to pack the tree as tightly as possible.
 * If the tree uses different level spacings or shape heights, the layout might result in overlapping nodes.
 */
object PackedTreeLayoutEngine : TreeLayoutEngine {

    private data class Subtree(
        val node: TreeNodeLayout,
        val left: Map<Int, Double>,
        val right: Map<Int, Double>,
    )

    override fun layout(model: TreeNodeModel): TreeNodeLayout {
        val laidOut = layoutSubtree(model).node
        return assignY(laidOut, 0.0)
    }

    private fun layoutSubtree(model: TreeNodeModel): Subtree {
        if (model.children.isEmpty()) {
            // no children, just lay out the node
            return Subtree(
                TreeNodeLayout(model, 0.0, 0.0, emptyList()),
                mapOf(0 to -model.cetzShape.width / 2.0),
                mapOf(0 to model.cetzShape.width / 2.0)
            )
        }

        // layout subtrees of all children
        val children = model.children.map { layoutSubtree(it) }

        val positionedChildren = mutableListOf<TreeNodeLayout>()
        val combinedLeft = mutableMapOf<Int, Double>()
        val combinedRight = mutableMapOf<Int, Double>()

        for ((index, subtree) in children.withIndex()) {
            var shift = 0.0

            if (index > 0) {
                // move subtree to the right as long as it overlaps with other subtrees
                for ((depth, leftX) in subtree.left) {
                    val existingRight = combinedRight[depth + 1] ?: continue
                    val overlap = existingRight + model.siblingSpace - (leftX + shift)
                    if (overlap > shift) shift = overlap
                }
            }

            // shift subtree to the right
            val shiftedSubtree = shift(subtree.node, shift)
            positionedChildren += shiftedSubtree

            // add bounding boxes to combined bounding boxes
            for ((depth, x) in subtree.left) {
                combinedLeft[depth + 1] = minOf(
                    combinedLeft.getOrDefault(depth + 1, Double.POSITIVE_INFINITY),
                    x + shift
                )
            }

            for ((depth, x) in subtree.right) {
                combinedRight[depth + 1] = maxOf(
                    combinedRight.getOrDefault(depth + 1, Double.NEGATIVE_INFINITY),
                    x + shift
                )
            }
        }

        // find x position of the root node
        val leftMost = positionedChildren.minOf { it.x - it.model.cetzShape.width / 2 }
        val rightMost = positionedChildren.maxOf { it.x + it.model.cetzShape.width / 2 }
        val rootX = (leftMost + rightMost) / 2

        // add root node to bounding box
        combinedLeft[0] = rootX - model.cetzShape.width / 2
        combinedRight[0] = rootX + model.cetzShape.width / 2

        // return bounding box of the root node
        return Subtree(
            TreeNodeLayout(model, rootX, 0.0, positionedChildren),
            combinedLeft,
            combinedRight
        )
    }


    private fun shift(node: TreeNodeLayout, shift: Double): TreeNodeLayout {
        return node.copy(
            x = node.x + shift,
            children = node.children.map { shift(it, shift) }
        )
    }

}

/**
 * A layout engine that calculates the total width of each subtree and puts them next to each other.
 * In contrast to the [packed layout engine][PackedTreeLayoutEngine],
 * this layout engine does not try to pack the tree as tightly as possible,
 * but rather tries to make it as wide as necessary to avoid overlapping nodes.
 * This engine ensures that overlaps will never occoure at the cost of potentially much wider trees.
 */
object WideTreeLayoutEngine : TreeLayoutEngine {

    private data class Subtree(
        val node: TreeNodeLayout,
        val width: Double,
    )

    override fun layout(model: TreeNodeModel): TreeNodeLayout {
        val laidOut = layoutSubtree(model, 0.0).node
        return assignY(laidOut, 0.0)
    }

    private fun layoutSubtree(model: TreeNodeModel, offset: Double): Subtree {
        if (model.children.isEmpty()) {
            return Subtree(
                TreeNodeLayout(
                    model = model,
                    x = offset + model.cetzShape.width / 2.0,
                    y = 0.0,
                    children = emptyList()
                ),
                model.cetzShape.width
            )
        }

        val childWidths = model.children.map { measureSubtree(it) }

        val childrenWidth =
            childWidths.sum() + model.siblingSpace * (childWidths.size - 1)

        val width = maxOf(model.cetzShape.width, childrenWidth)

        var childOffset = offset + (width - childrenWidth) / 2.0

        val positionedChildren = mutableListOf<TreeNodeLayout>()

        for ((child, childWidth) in model.children.zip(childWidths)) {
            val subtree = layoutSubtree(child, childOffset)
            positionedChildren += subtree.node

            childOffset += childWidth + model.siblingSpace
        }

        val rootX = offset + width / 2.0

        return Subtree(
            TreeNodeLayout(model, rootX, 0.0, positionedChildren),
            width
        )
    }

    private fun measureSubtree(model: TreeNodeModel): Double {
        if (model.children.isEmpty()) {
            return model.cetzShape.width
        }

        val childrenWidth =
            model.children.sumOf { measureSubtree(it) } +
                model.siblingSpace * (model.children.size - 1)

        return maxOf(model.cetzShape.width, childrenWidth)
    }

}
