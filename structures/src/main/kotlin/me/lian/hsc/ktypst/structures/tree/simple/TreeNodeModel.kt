package me.lian.hsc.ktypst.structures.tree.simple

import lian.hsc.ktypst.stdlib.cetz.CetzLine
import lian.hsc.ktypst.stdlib.cetz.CetzShape
import lian.hsc.ktypst.stdlib.visualize.Stroke
import lian.hsc.ktypst.stdlib.visualize.paint.Paint

/**
 * A model of a tree node.
 */
data class SimpleTreeNodeModel(
    val key: String,
    val content: String?,
    val cetzShape: CetzShape,
    val fill: Paint,
    val contentFill: Paint,
    val nodeStroke: Stroke,
    val connectionStroke: CetzLine,
)
