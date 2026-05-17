package me.lian.hsc.ktypst.structures.layout

enum class Direction(val value: String, val translate: (Double) -> Pair<Double, Double>) {

    TopToBottom("ttb", { by -> 0.0 to by }),
    LeftToRight("ltr", { by -> by to 0.0 });

}
