package lian.hsc.ktypst.stdlib.layout

/**
 * A angle.
 */
interface Angle {

    /**
     * The representation of the angle in Typst.
     */
    val value: String

    /**
     * An angle in degrees.
     */
    data class Degrees(val degrees: Double) : Angle {
        override val value: String = "${degrees}deg"
    }

    /**
     * An angle in radians.
     */
    data class Radians(val radians: Double) : Angle {
        override val value: String = "${radians}rad"
    }

}
