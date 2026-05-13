package me.lian.hsc.ktypst.structures.tree.layout

data class TreeNodeHolder<T>(
    val model: T,
    val width: Double,
    val height: Double,
    val siblingSpace: Double,
    val levelSpace: Double,
    val children: List<TreeNodeHolder<T>>,
)
