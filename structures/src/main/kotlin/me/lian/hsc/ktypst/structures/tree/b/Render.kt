package me.lian.hsc.ktypst.structures.tree.b

import lian.hsc.ktypst.stdlib.visualize.Point
import me.lian.hsc.ktypst.structures.layout.Box
import me.lian.hsc.ktypst.structures.layout.LayoutPart
import me.lian.hsc.ktypst.structures.layout.LayoutPartBuilder
import me.lian.hsc.ktypst.structures.layout.buildLayoutPart
import me.lian.hsc.ktypst.structures.tree.layout.TreeNodeLayout


/**
 * A tree render engine that gets the root node of a b tree as a [TreeNodeLayout] and renders it as Typst code.
 */
interface BTreeRenderEngine {

    /**
     * Renders the given [node] and all its children as Typst code.
     */
    fun render(node: TreeNodeLayout<BTreeNodeModel>): LayoutPart

}


/**
 * An abstract BTree render engine that handles positioned rendering.
 */
abstract class AbstractBTreeRenderEngine : BTreeRenderEngine {

    abstract fun getContent(key: String): String?

    override fun render(node: TreeNodeLayout<BTreeNodeModel>): LayoutPart = buildLayoutPart {
        +"#import \"@preview/cetz:0.5.1\""
        appendLine(
            """
            #cetz.canvas({
            import cetz.draw: *
            """.trimIndent()
        )

        render(node)

        appendLine("})")
    }

    private fun LayoutPartBuilder.render(node: TreeNodeLayout<BTreeNodeModel>) {
        if (node.children.isEmpty()) {
            renderLeaf(node)
            return
        }

        check(node.children.size == node.model.keys.size + 1) {
            "Expected ${node.model.keys.size + 1} children for node ${node.model.name}, got ${node.children.size}"
        }

        appendLine("group({")
        val left = node.x - node.width / 2
        for ((index, _) in node.children.withIndex()) {
            val pointerX =
                left +
                    node.model.content.width * index +
                    node.model.pointer.width * index +
                    node.model.pointer.width / 2

            +Box(
                name = "${node.model.name}>p$index",
                center = Point(pointerX, -node.y),
                width = node.model.pointer.width, height = node.model.pointer.height
            )
            appendLine(
                node.model.pointer.create(
                    Point(pointerX, node.y),
                    name = "p$index"
                )
            )
        }

        for ((index, key) in node.model.keys.withIndex()) {
            val centerX =
                left +
                    node.model.content.width * index +
                    node.model.pointer.width * (index + 1) +
                    node.model.content.width / 2

            +Box(
                name = "${node.model.name}>$key",
                center = Point(centerX, -node.y),
                width = node.model.content.width, height = node.model.content.height
            )
            appendLine(
                node.model.content.create(
                    Point(centerX, node.y),
                    name = key
                )
            )

            val content = getContent(key)
            if (content != null) {
                appendLine(
                    "content(\"$key\", text(fill: ${node.model.contentFill.value})[$content])"
                )
            }
        }
        appendLine("}, name: \"${node.model.name}\")")

        for ((index, child) in node.children.withIndex()) {
            render(child)
            appendLine(
                child.model.stroke.create(
                    "\"${node.model.name}.p$index.south\"",
                    "\"${child.model.name}.north\""
                )
            )
        }
    }

    private fun LayoutPartBuilder.renderLeaf(node: TreeNodeLayout<BTreeNodeModel>) {
        val left = node.x - node.width / 2

        appendLine("group({")
        for ((index, key) in node.model.keys.withIndex()) {
            val centerX = left + node.model.content.width * index + node.model.content.width / 2

            +Box(
                name = "${node.model.name}>$key",
                center = Point(centerX, -node.y),
                width = node.model.content.width, height = node.model.content.height

            )
            appendLine(
                node.model.content.create(
                    Point(centerX, node.y),
                    name = "${node.model.name}>$key"
                )
            )
            val content = getContent(key)
            if (content != null) {
                appendLine(
                    "content(\"${node.model.name}>$key\", " +
                        "text(fill: ${node.model.contentFill.value})[$content])"
                )
            }
        }
        appendLine("}, name: \"${node.model.name}\")")
    }

}

/**
 * The main BTree render engine that renders all nodes with their keys as their content.
 */
object TheBTreeRenderEngine : AbstractBTreeRenderEngine() {

    override fun getContent(key: String): String = key

}

/**
 * A secondary BTree render engine that renders all nodes as empty nodes.
 */
object EmptyContentBTreeRenderEngine : AbstractBTreeRenderEngine() {

    override fun getContent(key: String): String? = null

}
