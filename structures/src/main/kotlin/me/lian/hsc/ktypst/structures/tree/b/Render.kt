package me.lian.hsc.ktypst.structures.tree.b

import lian.hsc.ktypst.stdlib.visualize.Point
import me.lian.hsc.ktypst.structures.tree.layout.TreeNodeLayout


/**
 * A tree render engine that gets the root node of a b tree as a [TreeNodeLayout] and renders it as Typst code.
 */
interface BTreeRenderEngine {

    /**
     * Renders the given [node] and all its children as Typst code.
     */
    fun render(node: TreeNodeLayout<BTreeNodeModel>): String

}

/**
 * The only currently available useful render engines for a b tree.
 */
object TheBTreeRenderEngine : BTreeRenderEngine {

    override fun render(node: TreeNodeLayout<BTreeNodeModel>): String = buildString {
        appendLine(
            """
            #set page(width: auto, height: auto, fill: none, margin: 1em)

            #import "@preview/cetz:0.5.1"

            #cetz.canvas({
            import cetz.draw: *
        """.trimIndent()
        )

        render(node)

        appendLine("})")
    }

    private fun StringBuilder.render(node: TreeNodeLayout<BTreeNodeModel>) {
        if (node.children.isEmpty()) {
            renderLeaf(node)
            return
        }

        check(node.children.size == node.model.keys.size + 1) {
            "Expected ${node.model.keys.size + 1} children, got ${node.children.size}"
        }

        appendLine("group({")
        val left = node.x - node.width / 2
        for ((index, child) in node.children.withIndex()) {
            val pointerX =
                left +
                    node.model.content.width * index +
                    node.model.pointer.width * index +
                    node.model.pointer.width / 2

            appendLine(
                node.model.pointer.create(
                    Point(pointerX, node.y),
                    name = "${node.model.name}>p$index"
                )
            )
            render(child)
            appendLine(
                child.model.stroke.create(
                    "\"${node.model.name}>p$index.south\"",
                    "\"${child.model.name}.north\""
                )
            )
        }

        for ((index, key) in node.model.keys.withIndex()) {
            val centerX =
                left +
                    node.model.content.width * index +
                    node.model.pointer.width * (index + 1) +
                    node.model.content.width / 2

            appendLine(
                node.model.content.create(
                    Point(centerX, node.y),
                    name = "${node.model.name}>$key"
                )
            )
            appendLine(
                "content(\"${node.model.name}>$key\", " +
                    "text(fill: ${node.model.contentFill.value})[$key])"
            )
        }
        appendLine("}, name: \"${node.model.name}\")")
    }

    private fun StringBuilder.renderLeaf(node: TreeNodeLayout<BTreeNodeModel>) {
        val left = node.x - node.width / 2

        appendLine("group({")
        for ((index, key) in node.model.keys.withIndex()) {
            val centerX = left + node.model.content.width * index + node.model.content.width / 2
            appendLine(
                node.model.content.create(
                    Point(centerX, node.y),
                    name = "${node.model.name}>$key"
                )
            )
            appendLine(
                "content(\"${node.model.name}>$key\", " +
                    "text(fill: ${node.model.contentFill.value})[$key])"
            )
        }
        appendLine("}, name: \"${node.model.name}\")")
    }

}
