package me.lian.hsc.ktypst.structures.tree.layout

data class TreeNodeLayout<T>(
    val model: T,
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double,
    val siblingSpace: Double,
    val levelSpace: Double,
    val children: List<TreeNodeLayout<T>>,
)
