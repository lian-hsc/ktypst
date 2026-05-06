package me.lian.hsc.ktypst.data.output

/**
 * The output of a Typst compile command.
 */
data class TypstCompileOutput(
    val downloadedDependencies: List<String>,
    val stdArtifact: Artifact?,
)