package me.lian.hsc.ktypst.data.command

import me.lian.hsc.ktypst.util.ExperimentalTypstFeature

enum class OutputFormat(val value: String) {

    PDF("pdf"),
    PNG("png"),
    SVG("svg"),

    @ExperimentalTypstFeature
    HTML("html"),

}
