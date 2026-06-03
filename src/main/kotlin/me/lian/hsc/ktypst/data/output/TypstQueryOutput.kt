package me.lian.hsc.ktypst.data.output

/**
 * The output of a Typst query command.
 */
data class TypstQueryOutput(
    val status: Status,
    val error: String?,
    val result: String?,
)
