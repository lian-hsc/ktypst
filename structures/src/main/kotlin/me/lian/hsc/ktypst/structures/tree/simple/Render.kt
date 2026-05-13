package me.lian.hsc.ktypst.structures.tree.simple

import lian.hsc.ktypst.stdlib.visualize.Point
import me.lian.hsc.ktypst.structures.tree.layout.TreeNodeLayout

/**
 * A tree render engine that gets the root node of a simple tree as a [TreeNodeLayout] and renders it as Typst code.
 */
interface SimpleTreeRenderEngine {

    /**
     * Renders the given [node] and all its children as Typst code.
     */
    fun render(node: TreeNodeLayout<SimpleTreeNodeModel>): String

}


/**
 * A render engine that renders nodes with no content as nodes with their key as content.
 */
object EmptyContentAsKeyRenderEngine : SimpleTreeRenderEngine {

    override fun render(node: TreeNodeLayout<SimpleTreeNodeModel>): String = buildString {
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

    private fun StringBuilder.render(node: TreeNodeLayout<SimpleTreeNodeModel>) {
        appendLine(node.model.cetzShape.create(Point(node.x, node.y), node.model.key))

        appendLine(
            "content(\"${node.model.key}\", " +
                "text(fill: ${node.model.contentFill.value})[${node.model.content ?: node.model.key}])"
        )

        node.children.forEach {
            render(it)
            appendLine(it.model.stroke.create("\"${node.model.key}\"", "\"${it.model.key}\""))
        }
    }

}

/**
 * A render engine that renders nodes with no content as empty nodes.
 */
object EmptyContentAsEmptyRenderEngine : SimpleTreeRenderEngine {

    override fun render(node: TreeNodeLayout<SimpleTreeNodeModel>): String = buildString {
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

    private fun StringBuilder.render(node: TreeNodeLayout<SimpleTreeNodeModel>) {
        appendLine(node.model.cetzShape.create(Point(node.x, node.y), node.model.key))

        if (node.model.content != null) {
            appendLine(
                "content(\"${node.model.key}\", " +
                    "text(fill: ${node.model.contentFill.value})[${node.model.content}])"
            )
        }

        node.children.forEach {
            render(it)
            appendLine(it.model.stroke.create("\"${node.model.key}\"", "\"${it.model.key}\""))
        }
    }

}
