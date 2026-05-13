package me.lian.hsc.ktypst.structures.graph

import lian.hsc.ktypst.stdlib.visualize.Point

/**
 * A position in a graph.
 */
interface Position {

    fun resolve(positions: Map<String, Position>): Point<Double>

}

/**
 * An absolute position in a graph, defined by its coordinates.
 */
data class AbsolutePosition(val x: Double, val y: Double) : Position {

    override fun resolve(positions: Map<String, Position>) = Point(x, y)

}

/**
 * A relative position in a graph, defined by a reference to another position.
 */
data class RelativePosition(val delta: Point<Double>, val reference: String) : Position {

    override fun resolve(positions: Map<String, Position>) = positions[reference]
        ?.resolve(positions)
        ?.let { Point(it.x + delta.x, it.y + delta.y) }
        ?: error("Reference '$reference' not found in positions map")

}

// Helpers for creating relative positions
data class RelativeCoordinates(val x: Double, val y: Double) {

    operator fun plus(other: RelativeCoordinates) = RelativeCoordinates(x + other.x, y + other.y)

}

val Double.above get() = RelativeCoordinates(0.0, -this)
val Double.below get() = RelativeCoordinates(0.0, this)
val Double.left get() = RelativeCoordinates(-this, 0.0)
val Double.right get() = RelativeCoordinates(this, 0.0)

val Int.above get() = RelativeCoordinates(0.0, this.toDouble())
val Int.below get() = RelativeCoordinates(0.0, -this.toDouble())
val Int.left get() = RelativeCoordinates(-this.toDouble(), 0.0)
val Int.right get() = RelativeCoordinates(this.toDouble(), 0.0)
