package me.lian.hsc.ktypst.structures.layout

typealias Alignments = List<Alignment>
internal typealias AlignmentTransformation = (
    currentWidth: Double, currentHeight: Double,
    newWidth: Double, newHeight: Double
) -> Pair<Double, Double>

/**
 * An alignment.
 */
enum class Alignment(val value: String, val transform: AlignmentTransformation) {

    Left("alignment.left", { _, _, _, _ -> 0.0 to 0.0 }),
    Center("alignment.center", { currentWidth, _, newWidth, _ -> (newWidth - currentWidth) / 2 to 0.0 }),
    Right("alignment.right", { currentWidth, _, newWidth, _ -> (newWidth - currentWidth) to 0.0 }),
    Top("alignment.top", { _, _, _, _ -> 0.0 to 0.0 }),
    Horizon("alignment.horizon", { _, currentHeight, _, newHeight -> 0.0 to (newHeight - currentHeight) / 2 }),
    Bottom("alignment.bottom", { _, currentHeight, _, newHeight -> 0.0 to (newHeight - currentHeight) });

    operator fun plus(other: Alignment) = listOf(this, other)

}

val Alignments.value
    get() = joinToString(" + ") { it.value }
