package me.lian.hsc.ktypst.data.command

import java.nio.file.Path
import java.time.ZonedDateTime

/**
 * A typst command.
 *
 * @property input Input files for Typst.
 * @property projectRoot Configures the project root (for absolute paths).
 * @property inputs Adds string key-value pairs visible through `sys.inputs` in Typst.
 * @property fontPaths Adds additional directories that are recursively searched for fonts.
 * @property ignoreSystemFonts Ensures system fonts won't be searched, unless explicitly included via [fontPaths].
 * @property ignoreEmbeddedFonts Ensures fonts embedded into Typst won't be considered.
 * @property packageDirectory Custom path to local packages, defaults to system-dependent location.
 * @property packageCacheDirectory Custom path to package cache, defaults to system-dependent location
 * @property creationTime The document's creation date formatted as a UNIX timestamp.
 * @property jobs Number of parallel jobs spawned during compilation.
 * Defaults to number of CPUs. Setting it to 1 disables parallelism.
 * @property features Enables in-development features that may be changed or removed at any time.
 * @property diagnosticsFormat The format to emit diagnostics in.
 */
open class TypstCommand(
    val input: Input,
    val projectRoot: Path?,
    val inputs: Map<String, String>,
    val fontPaths: List<Path>,
    val ignoreSystemFonts: Boolean,
    val ignoreEmbeddedFonts: Boolean,
    val packageDirectory: Path?,
    val packageCacheDirectory: Path?,
    val creationTime: ZonedDateTime?,
    val jobs: Int?,
    val features: List<TypstFeatures>,
    val diagnosticsFormat: DiagnosticsFormat?,
)
