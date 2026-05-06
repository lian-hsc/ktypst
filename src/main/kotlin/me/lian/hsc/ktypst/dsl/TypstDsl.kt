package me.lian.hsc.ktypst.dsl

import me.lian.hsc.ktypst.backend.TypstBackend
import me.lian.hsc.ktypst.data.command.DependenciesFormat
import me.lian.hsc.ktypst.data.command.DiagnosticsFormat
import me.lian.hsc.ktypst.data.command.Input
import me.lian.hsc.ktypst.data.command.Output
import me.lian.hsc.ktypst.data.command.OutputFormat
import me.lian.hsc.ktypst.data.command.Pages
import me.lian.hsc.ktypst.data.command.PdfStandard
import me.lian.hsc.ktypst.data.command.TypstCompileCommand
import me.lian.hsc.ktypst.data.command.TypstFeatures
import me.lian.hsc.ktypst.data.output.TypstCompileOutput
import java.nio.file.Path
import java.time.ZonedDateTime

@DslMarker
@Target(AnnotationTarget.CLASS)
annotation class TypstMarker

@TypstMarker
class Typst {

    var input: Input
        get() = _input ?: throw IllegalStateException("Input is not defined")
        set(value) {
            if (_input != null) throw IllegalStateException("Input is already defined")
            _input = value
        }
    var output: Output
        get() = _output ?: throw IllegalStateException("Output is not defined")
        set(value) {
            if (_output != null) throw IllegalStateException("Output is already defined")
            _output = value
        }

    var cert: Path? = null
    var format: OutputFormat? = null
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

    private var _input: Input? = null
    private var _output: Output? = null
    private val inputs: MutableMap<String, String> = mutableMapOf()
    private val fontPaths: MutableList<Path> = mutableListOf()
    private val pages: MutableList<Pages> = mutableListOf()
    private val pdfStandard: MutableList<PdfStandard> = mutableListOf()
    private val features: MutableList<TypstFeatures> = mutableListOf()

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
        if (key in inputs) throw IllegalStateException("Input is already defined")
        inputs[key] = value
    }

    fun fontPath(path: Path) {
        fontPaths.add(path)
    }

    fun pages(pages: Pages) {
        this.pages += pages
    }

    operator fun Pages.unaryPlus() = pages(this)
    fun singlePage(page: UInt) = pages(Pages.SinglePage(page))
    fun pageRange(startInclusive: UInt, endExclusive: UInt) = pages(Pages.ClosedRange(startInclusive, endExclusive))
    fun pageRange(range: UIntRange) = pageRange(range.first, range.last)
    fun pagesFrom(startInclusive: UInt) = pages(Pages.OpenEndRange(startInclusive))
    fun pagesUntil(endInclusive: UInt) = pages(Pages.OpenStartRange(endInclusive))

    operator fun PdfStandard.unaryPlus() {
        pdfStandard += this
    }

    operator fun TypstFeatures.unaryPlus() {
        features += this
    }

    fun createCommand() = TypstCompileCommand(
        _input ?: throw IllegalStateException("Input was not defined"),
        _output ?: throw IllegalStateException("Output was not defined"),
        cert,
        format,
        projectRoot,
        inputs,
        fontPaths,
        ignoreSystemFonts,
        ignoreEmbeddedFonts,
        packageDirectory,
        packageCacheDirectory,
        creationTime,
        pages,
        pdfStandard.toList(),
        noPdfTags,
        ppi,
        dependenciesPath,
        dependenciesFormat,
        jobs,
        features.toList(),
        diagnosticsFormat
    )

}

suspend fun typst(backend: TypstBackend, block: Typst.() -> Unit): TypstCompileOutput {
    val typstCommand = Typst().apply(block).createCommand()
    return backend.execute(typstCommand)
}