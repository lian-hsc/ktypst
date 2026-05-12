package lian.hsc.ktypst.stdlib.util

internal fun Double.checkRatio(name: String = "Ratio"): Unit =
    check(this in 0.0..1.0) { "$name must be between 0 and 1, but was $this." }

internal fun Double.toRatio() = "${this * 100}%"
