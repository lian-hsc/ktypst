package me.lian.hsc.ktypst.structures.tree.layout

/**
 * A [tree layout engine][TreeLayoutEngine] that tries to pack the tree as tightly as possible.
 * If the tree uses different level spacings or shape heights, the layout might result in overlapping nodes.
 */
object PackedTreeLayoutEngine : TreeLayoutEngine {

    private data class Subtree<T>(
        val node: TreeNodeLayout<T>,
        val left: Map<Int, Double>,
        val right: Map<Int, Double>,
    )

    override fun <T> layout(model: TreeNodeHolder<T>): TreeNodeLayout<T> {
        val laidOut = layoutSubtree(model).node
        return assignY(laidOut, 0.0)
    }

    private fun <T> layoutSubtree(model: TreeNodeHolder<T>): Subtree<T> {
        if (model.children.isEmpty()) {
            // no children, just lay out the node
            return Subtree(
                TreeNodeLayout(
                    model = model.model,
                    x = 0.0,
                    y = 0.0,
                    width = model.width,
                    height = model.height,
                    siblingSpace = model.siblingSpace,
                    levelSpace = model.levelSpace,
                    children = emptyList()
                ),
                mapOf(0 to -model.width / 2.0),
                mapOf(0 to model.width / 2.0)
            )
        }

        // layout subtrees of all children
        val children = model.children.map { layoutSubtree(it) }

        val positionedChildren = mutableListOf<TreeNodeLayout<T>>()
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
        val leftMost = positionedChildren.minOf { it.x - it.width / 2 }
        val rightMost = positionedChildren.maxOf { it.x + it.width / 2 }
        val rootX = (leftMost + rightMost) / 2

        // add root node to bounding box
        combinedLeft[0] = rootX - model.width / 2
        combinedRight[0] = rootX + model.width / 2

        // return bounding box of the root node
        return Subtree(
            TreeNodeLayout(
                model.model,
                rootX, 0.0,
                model.width, model.height,
                model.siblingSpace, model.levelSpace,
                positionedChildren
            ),
            combinedLeft,
            combinedRight
        )
    }


    private fun <T> shift(node: TreeNodeLayout<T>, shift: Double): TreeNodeLayout<T> {
        return node.copy(
            x = node.x + shift,
            children = node.children.map { shift(it, shift) }
        )
    }

}
