package me.lian.hsc.ktypst.data.output

/**
 * The output of a Typst compile command.
 */
data class TypstCompileOutput(
    val status: Status,
    val error: String?,
    val downloadedPackages: List<String>,
    val stdArtifact: Artifact?,
)
