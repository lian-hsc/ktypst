package me.lian.hsc.ktypst.structures.tree.simple

import lian.hsc.ktypst.stdlib.visualize.Point
import me.lian.hsc.ktypst.structures.layout.Box
import me.lian.hsc.ktypst.structures.layout.LayoutPart
import me.lian.hsc.ktypst.structures.layout.LayoutPartBuilder
import me.lian.hsc.ktypst.structures.layout.buildLayoutPart
import me.lian.hsc.ktypst.structures.tree.layout.TreeNodeLayout

/**
 * A tree render engine that gets the root node of a simple tree as a [TreeNodeLayout] and renders it as Typst code.
 */
interface SimpleTreeRenderEngine {

    /**
     * Renders the given [node] and all its children as Typst code.
     */
    fun render(node: TreeNodeLayout<SimpleTreeNodeModel>): LayoutPart

}

/**
 * An abstract render engine that handles placement.
 */
abstract class AbstractSimpleTreeRenderEngine : SimpleTreeRenderEngine {

    abstract fun getContent(node: SimpleTreeNodeModel): String?

    override fun render(node: TreeNodeLayout<SimpleTreeNodeModel>): LayoutPart = buildLayoutPart {
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

    private fun LayoutPartBuilder.render(node: TreeNodeLayout<SimpleTreeNodeModel>) {
        +Box(
            name = node.model.key,
            center = Point(node.x, -node.y),
            width = node.model.cetzShape.width, height = node.model.cetzShape.height
        )
        appendLine(node.model.cetzShape.create(Point(node.x, node.y), node.model.key))

        val content = getContent(node.model)
        if (content != null) {
            appendLine("content(\"${node.model.key}\", text(fill: ${node.model.contentFill.value})[$content])")
        }

        node.children.forEach {
            render(it)
            appendLine(it.model.stroke.create("\"${node.model.key}\"", "\"${it.model.key}\""))
        }
    }

}

/**
 * A render engine that renders nodes with no content as nodes with their key as content.
 */
object EmptyContentAsKeyRenderEngine : AbstractSimpleTreeRenderEngine() {

    override fun getContent(node: SimpleTreeNodeModel): String = node.content ?: node.key

}

/**
 * A render engine that renders nodes with no content as empty nodes.
 */
object EmptyContentAsEmptyRenderEngine : AbstractSimpleTreeRenderEngine() {

    override fun getContent(node: SimpleTreeNodeModel): String? = node.content

}
