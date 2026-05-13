package me.lian.hsc.ktypst.structures.tree.layout

/**
 * A tree layout engine that gets the root node of a tree as [TreeNodeHolder] and
 * returns the [TreeNodeLayout] of the root node, which can then be used by a renderer to render the tree.
 */
interface TreeLayoutEngine {

    /**
     * Layouts the given [model] and returns the layout of the root node.
     * The engine must assign each node a position and size.
     */
    fun <T> layout(model: TreeNodeHolder<T>): TreeNodeLayout<T>

}

/**
 * A helper function that assigns the given [node] a position based on the given [y] coordinate.
 * This is the most primitive approach but should be enough for the y coordinate for almost all layout engines.
 */
fun <T> assignY(node: TreeNodeLayout<T>, y: Double): TreeNodeLayout<T> {
    val children = node.children.map {
        val childY = y - node.height / 2 - node.levelSpace - it.height / 2
        assignY(it, childY)
    }

    return node.copy(y = y, children = children)
}
