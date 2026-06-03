package me.lian.hsc.ktypst.data.command.compile

import me.lian.hsc.ktypst.data.command.DiagnosticsFormat
import me.lian.hsc.ktypst.data.command.Input
import me.lian.hsc.ktypst.data.command.TypstCommand
import me.lian.hsc.ktypst.data.command.TypstFeatures
import java.nio.file.Path
import java.time.ZonedDateTime

/**
 * Compiles an input file into a supported output format.
 *
 * @property output Where to put the output file (PDF, PNG, SVG, or HTML).
 * For output formats emitting one file per page (PNG & SVG),
 * a page number template must be present if the source document renders to multiple pages.
 * Use `{p}` for page numbers, `{0p}` for zero padded page numbers and `{t}` for page count.
 * For example, `page-{0p}-of-{t}.png` creates `page-01-of-10.png`, `page-02-of-10.png`, and so on.
 * @property cert Path to a custom CA certificate to use when making network requests.
 * @property format The format of the output file, inferred from the extension by default.
 * @property pages Which pages to export. When unspecified, all pages are exported.
 * @property pdfStandards One (or multiple comma-separated) PDF standards that Typst will enforce conformance with.
 * @property noPdfTags By default, even when not producing a `PDF/UA-1` document,
 * a tagged PDF document is written to provide a baseline of accessibility.
 * In some circumstances (for example, when trying to reduce the size of a document)
 * it can be desirable to disable tagged PDF.
 * @property ppi The PPI (pixels per inch) to use for PNG export (default: 144)
 * @property dependenciesPath File path to which a list of current compilation's dependencies will be written.
 * @property dependenciesFormat File format to use for dependencies.
 */
class TypstCompileCommand(
    input: Input,
    val output: Output,
    val cert: Path?,
    val format: CompileOutputFormat?,
    projectRoot: Path?,
    inputs: Map<String, String>,
    fontPaths: List<Path>,
    ignoreSystemFonts: Boolean,
    ignoreEmbeddedFonts: Boolean,
    packageDirectory: Path?,
    packageCacheDirectory: Path?,
    creationTime: ZonedDateTime?,
    val pages: List<Pages>,
    val pdfStandards: List<PdfStandard>,
    val noPdfTags: Boolean,
    val ppi: Int?,
    val dependenciesPath: Path?,
    val dependenciesFormat: DependenciesFormat?,
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
