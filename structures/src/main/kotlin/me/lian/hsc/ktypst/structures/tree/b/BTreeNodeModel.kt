package me.lian.hsc.ktypst.structures.tree.b

import lian.hsc.ktypst.stdlib.cetz.CetzLine
import lian.hsc.ktypst.stdlib.cetz.CetzShape
import lian.hsc.ktypst.stdlib.visualize.Stroke
import lian.hsc.ktypst.stdlib.visualize.paint.Paint

/**
 * A model of a b tree.
 */
data class BTreeNodeModel(
    val keys: List<String>,
    val pointer: CetzShape,
    val content: CetzShape,
    val contentFill: Paint,
    val stroke: CetzLine,
) {

    val name = keys.joinToString(separator = "-")

}
