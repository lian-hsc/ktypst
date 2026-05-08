package me.lian.hsc.ktypst.data.command

import java.nio.file.Path
import java.time.ZonedDateTime

/**
 * Compiles an input file into a supported output format.
 *
 * @property input Input files for Typst.
 * @property output Where to put the output file (PDF, PNG, SVG, or HTML).
 * For output formats emitting one file per page (PNG & SVG),
 * a page number template must be present if the source document renders to multiple pages.
 * Use `{p}` for page numbers, `{0p}` for zero padded page numbers and `{t}` for page count.
 * For example, `page-{0p}-of-{t}.png` creates `page-01-of-10.png`, `page-02-of-10.png`, and so on.
 * @property cert Path to a custom CA certificate to use when making network requests.
 * @property format The format of the output file, inferred from the extension by default.
 * @property projectRoot Configures the project root (for absolute paths).
 * @property inputs Adds string key-value pairs visible through `sys.inputs` in Typst.
 * @property fontPaths Adds additional directories that are recursively searched for fonts.
 * @property ignoreSystemFonts Ensures system fonts won't be searched, unless explicitly included via [fontPaths].
 * @property ignoreEmbeddedFonts Ensures fonts embedded into Typst won't be considered.
 * @property packageDirectory Custom path to local packages, defaults to system-dependent location.
 * @property packageCacheDirectory Custom path to package cache, defaults to system-dependent location
 * @property creationTime The document's creation date formatted as a UNIX timestamp.
 * @property pages Which pages to export. When unspecified, all pages are exported.
 * @property pdfStandards One (or multiple comma-separated) PDF standards that Typst will enforce conformance with.
 * @property noPdfTags By default, even when not producing a `PDF/UA-1` document,
 * a tagged PDF document is written to provide a baseline of accessibility.
 * In some circumstances (for example, when trying to reduce the size of a document)
 * it can be desirable to disable tagged PDF.
 * @property ppi The PPI (pixels per inch) to use for PNG export (default: 144)
 * @property dependenciesPath File path to which a list of current compilation's dependencies will be written.
 * @property dependenciesFormat File format to use for dependencies.
 * @property jobs Number of parallel jobs spawned during compilation.
 * Defaults to number of CPUs. Setting it to 1 disables parallelism.
 * @property features Enables in-development features that may be changed or removed at any time.
 * @property diagnosticsFormat The format to emit diagnostics in.
 */
data class TypstCompileCommand(
    val input: Input,
    val output: Output,
    val cert: Path?,
    val format: OutputFormat?,
    val projectRoot: Path?,
    val inputs: Map<String, String>,
    val fontPaths: List<Path>,
    val ignoreSystemFonts: Boolean,
    val ignoreEmbeddedFonts: Boolean,
    val packageDirectory: Path?,
    val packageCacheDirectory: Path?,
    val creationTime: ZonedDateTime?,
    val pages: List<Pages>,
    val pdfStandards: List<PdfStandard>,
    val noPdfTags: Boolean,
    val ppi: Int?,
    val dependenciesPath: Path?,
    val dependenciesFormat: DependenciesFormat?,
    val jobs: Int?,
    val features: List<TypstFeatures>,
    val diagnosticsFormat: DiagnosticsFormat?,
)
