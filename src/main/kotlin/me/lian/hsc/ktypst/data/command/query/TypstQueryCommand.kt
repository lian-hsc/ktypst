package me.lian.hsc.ktypst.data.command.query

import me.lian.hsc.ktypst.data.command.DiagnosticsFormat
import me.lian.hsc.ktypst.data.command.Input
import me.lian.hsc.ktypst.data.command.TypstCommand
import me.lian.hsc.ktypst.data.command.TypstFeatures
import java.nio.file.Path
import java.time.ZonedDateTime

/**
 * Processes an input file to extract provided metadata.
 *
 * @property selector Defines which elements to retrieve.
 * @property field Extracts just one field from all retrieved elements.
 * @property one Expects and retrieves exactly one element.
 * @property format The format of the output.
 * @property pretty Whether to pretty-print the serialized output.
 * Only applies to [JSON format][QueryOutputFormat.JSON].
 * @property target The target to compile for.
 */
class TypstQueryCommand(
    input: Input,
    val selector: String,
    val field: String?,
    val one: Boolean,
    val format: QueryOutputFormat?,
    val pretty: Boolean,
    val target: QueryTarget?,
    projectRoot: Path?,
    inputs: Map<String, String>,
    fontPaths: List<Path>,
    ignoreSystemFonts: Boolean,
    ignoreEmbeddedFonts: Boolean,
    packageDirectory: Path?,
    packageCacheDirectory: Path?,
    creationTime: ZonedDateTime?,
    jobs: Int?,
    features: List<TypstFeatures>,
    diagnosticsFormat: DiagnosticsFormat?,
) : TypstCommand(
    input,
    projectRoot,
    inputs,
    fontPaths,
    ignoreSystemFonts,
    ignoreEmbeddedFonts,
    packageDirectory,
    packageCacheDirectory,
    creationTime,
    jobs,
    features,
    diagnosticsFormat
)
