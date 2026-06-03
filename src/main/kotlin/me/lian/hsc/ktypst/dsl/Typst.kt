package me.lian.hsc.ktypst.dsl

import me.lian.hsc.ktypst.backend.TypstBackend
import me.lian.hsc.ktypst.data.command.*
import me.lian.hsc.ktypst.data.command.compile.DependenciesFormat
import me.lian.hsc.ktypst.data.command.compile.Output
import me.lian.hsc.ktypst.data.command.compile.CompileOutputFormat
import me.lian.hsc.ktypst.data.command.compile.Pages
import me.lian.hsc.ktypst.data.command.compile.PdfStandard
import me.lian.hsc.ktypst.data.command.compile.TypstCompileCommand
import me.lian.hsc.ktypst.data.output.TypstCompileOutput
import java.nio.file.Path
import java.time.ZonedDateTime

@DslMarker
@Target(AnnotationTarget.CLASS)
annotation class TypstMarker

@TypstMarker
class Typst {

    private var _input: Input? = null
    private var _output: Output? = null
    private val inputs: MutableMap<String, String> = mutableMapOf()
    private val fontPaths: MutableList<Path> = mutableListOf()
    private val pages: MutableList<Pages> = mutableListOf()
    private val pdfStandard: MutableList<PdfStandard> = mutableListOf()
    private val features: MutableList<TypstFeatures> = mutableListOf()


    var input: Input
        get() = checkNotNull(_input) { "Input is not defined" }
        set(value) {
            check(_input == null) { "Input is already defined" }
            _input = value
        }
    var output: Output
        get() = checkNotNull(_output) { "Output is not defined" }
        set(value) {
            check(_output == null) { "Output is already defined" }
            _output = value
        }

    var cert: Path? = null
    var format: CompileOutputFormat? = null
    var projectRoot: Path? = null
    var ignoreSystemFonts: Boolean = false
    var ignoreEmbeddedFonts: Boolean = false
    var packageDirectory: Path? = null
    var packageCacheDirectory: Path? = null
    var creationTime: ZonedDateTime? = null
    var noPdfTags: Boolean = false
    var ppi: Int? = null
    var dependenciesPath: Path? = null
    var dependenciesFormat: DependenciesFormat? = null
    var jobs: Int? = null
    var diagnosticsFormat: DiagnosticsFormat? = null

    operator fun String.unaryPlus() {
        input = Input.Content(this)
    }

    fun fileInput(path: Path) {
        input = Input.File(path)
    }

    fun outputAsResult() {
        output = Output.ToResult
    }

    fun outputAsFile(name: String) {
        output = Output.Name(name)
    }

    fun outputAsFile(path: Path) {
        output = Output.File(path)
    }

    fun inputForTypst(key: String, value: String) {
        check(key !in inputs) { "Input is already defined" }
        inputs[key] = value
    }

    fun fontPath(path: Path) {
        fontPaths.add(path)
    }

    fun pages(pages: Pages) {
        this.pages += pages
    }

    operator fun Pages.unaryPlus(): Unit = pages(this)
    fun singlePage(page: UInt): Unit = pages(Pages.SinglePage(page))
    fun pageRange(startInclusive: UInt, endExclusive: UInt): Unit =
        pages(Pages.ClosedRange(startInclusive, endExclusive))

    fun pageRange(range: UIntRange): Unit = pageRange(range.first, range.last)
    fun pagesFrom(startInclusive: UInt): Unit = pages(Pages.OpenEndRange(startInclusive))
    fun pagesUntil(endInclusive: UInt): Unit = pages(Pages.OpenStartRange(endInclusive))

    operator fun PdfStandard.unaryPlus() {
        pdfStandard += this
    }

    operator fun TypstFeatures.unaryPlus() {
        features += this
    }

    fun createCommand() = TypstCompileCommand(
        input = checkNotNull(_input) { "Input was not defined" },
        output = checkNotNull(_output) { "Output was not defined" },
        cert = cert,
        format = format,
        projectRoot = projectRoot,
        inputs = inputs,
        fontPaths = fontPaths,
        ignoreSystemFonts = ignoreSystemFonts,
        ignoreEmbeddedFonts = ignoreEmbeddedFonts,
        packageDirectory = packageDirectory,
        packageCacheDirectory = packageCacheDirectory,
        creationTime = creationTime,
        pages = pages,
        pdfStandards = pdfStandard.toList(),
        noPdfTags = noPdfTags,
        ppi = ppi,
        dependenciesPath = dependenciesPath,
        dependenciesFormat = dependenciesFormat,
        jobs = jobs,
        features = features.toList(),
        diagnosticsFormat = diagnosticsFormat
    )

}

suspend fun typst(backend: TypstBackend, block: Typst.() -> Unit): TypstCompileOutput {
    val typstCommand = Typst().apply(block).createCommand()
    return backend.execute(typstCommand)
}
