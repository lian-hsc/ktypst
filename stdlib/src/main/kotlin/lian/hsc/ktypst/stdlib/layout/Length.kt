package lian.hsc.ktypst.stdlib.layout

/**
 * A length with a unit.
 */
data class Length(val length : Double, val unit: Unit) {

    val value: String = "${length}${unit.value}"

    enum class Unit(val value: String, val to: (Double, Unit) -> Double) {

        Points("pt", { length, target ->
            when (target) {
                Points -> length
                Millimeters -> length * 25.4 / 72.0
                Centimeters -> length * 2.54 / 72.0
                Inches -> length / 72.0
                Em -> throw IllegalArgumentException("Cannot convert points to em")
            }
        }),
        Millimeters("mm", { length, target ->
            when (target) {
                Points -> length * 72.0 / 25.4
                Millimeters -> length
                Centimeters -> length / 10.0
                Inches -> length / 25.4
                Em -> throw IllegalArgumentException("Cannot convert millimeters to em")
            }
        }),
        Centimeters("cm", { length, target ->
            when (target) {
                Points -> length * 72.0 / 2.54
                Millimeters -> length * 10.0
                Centimeters -> length
                Inches -> length / 2.54
                Em -> throw IllegalArgumentException("Cannot convert centimeters to em")
            }
        }),
        Inches("in", { length, target ->
            when (target) {
                Points -> length * 72.0
                Millimeters -> length * 25.4
                Centimeters -> length * 2.54
                Inches -> length
                Em -> throw IllegalArgumentException("Cannot convert inches to em")
            }
        }),
        Em("em", { length, target ->
            when (target) {
                Points -> throw IllegalArgumentException("Cannot convert em to points")
                Millimeters -> throw IllegalArgumentException("Cannot convert em to millimeters")
                Centimeters -> throw IllegalArgumentException("Cannot convert em to centimeters")
                Inches -> throw IllegalArgumentException("Cannot convert em to inches")
                Em -> length
            }
        });

    }

}
