package me.lian.hsc.ktypst.data.command.compile

import me.lian.hsc.ktypst.util.ExperimentalTypstFeature

enum class CompileOutputFormat(val value: String) {

    PDF("pdf"),
    PNG("png"),
    SVG("svg"),

    @ExperimentalTypstFeature
    HTML("html"),

}
