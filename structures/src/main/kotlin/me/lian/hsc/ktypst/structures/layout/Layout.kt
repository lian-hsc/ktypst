package me.lian.hsc.ktypst.structures.layout

data class Layout(
    val width: Double,
    val height: Double,
    val content: String,
    val boxes: List<Box>,
) {

    fun scale(factor: Double) = copy(
        width = width * factor,
        height = height * factor,
        content = content,
        boxes = boxes.map { it.scale(factor) }
    )

    operator fun get(name: String): Box = boxes.first { it.name == name }

}
