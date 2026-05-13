package me.lian.hsc.ktypst.structures.tree

import lian.hsc.ktypst.stdlib.visualize.Point

/**
 * A tree render engine that gets the root node of a tree as a [TreeNodeLayout] and renders it as Typst code.
 */
interface TreeRenderEngine {

    /**
     * Renders the given [node] and all its children as Typst code.
     */
    fun render(node: TreeNodeLayout): String

}

/**
 * A render engine that renders nodes with no content as nodes with their key as content.
 */
object EmptyContentAsKeyRenderEngine : TreeRenderEngine {

    override fun render(node: TreeNodeLayout): String = buildString {
        appendLine("""
            #set page(width: auto, height: auto, fill: none, margin: 1em)

            #import "@preview/cetz:0.5.1"

            #cetz.canvas({
            import cetz.draw: *
        """.trimIndent())

        render(node)

        appendLine("})")
    }

    private fun StringBuilder.render(node: TreeNodeLayout) {
        appendLine(
            node.model.cetzShape.create(
                Point(node.x, node.y),
                name = node.model.key,
                fill = node.model.fill,
                stroke = node.model.nodeStroke,
            )
        )
        appendLine(
            "content(\"${node.model.key}\", " +
                "text(fill: ${node.model.contentFill.value})[${node.model.content ?: node.model.key}])"
        )

        node.children.forEach {
            render(it)
            appendLine(it.model.connectionStroke.create("\"${node.model.key}\"", "\"${it.model.key}\""))
        }
    }

}

/**
 * A render engine that renders nodes with no content as empty nodes.
 */
object EmptyContentAsEmptyRenderEngine : TreeRenderEngine {

    override fun render(node: TreeNodeLayout): String = buildString { render(node) }

    private fun StringBuilder.render(node: TreeNodeLayout) {
        appendLine(
            node.model.cetzShape.create(
                Point(node.x, node.y),
                name = node.model.key,
                fill = node.model.fill,
                stroke = node.model.nodeStroke,
            )
        )
        if (node.model.content != null) {
            appendLine(
                "content(\"${node.model.key}\", " +
                    "text(fill: ${node.model.contentFill.value})[${node.model.content}])"
            )
        }

        node.children.forEach {
            render(it)
            appendLine(it.model.connectionStroke.create("\"${node.model.key}\"", "\"${it.model.key}\""))
        }
    }

}
