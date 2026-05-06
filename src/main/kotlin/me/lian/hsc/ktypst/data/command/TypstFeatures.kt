package me.lian.hsc.ktypst.data.command

import me.lian.hsc.ktypst.util.ExperimentalTypstFeature

enum class TypstFeatures(val value: String) {

    @ExperimentalTypstFeature
    HTML("html"),

    @ExperimentalTypstFeature
    A11yExtras("a11y-extras")

}