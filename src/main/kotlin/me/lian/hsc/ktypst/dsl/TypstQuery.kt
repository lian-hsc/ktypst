package me.lian.hsc.ktypst.dsl

import me.lian.hsc.ktypst.backend.TypstBackend
import me.lian.hsc.ktypst.data.command.DiagnosticsFormat
import me.lian.hsc.ktypst.data.command.Input
import me.lian.hsc.ktypst.data.command.TypstFeatures
import me.lian.hsc.ktypst.data.command.query.QueryOutputFormat
import me.lian.hsc.ktypst.data.command.query.QueryTarget
import me.lian.hsc.ktypst.data.command.query.TypstQueryCommand
import me.lian.hsc.ktypst.data.output.TypstCompileOutput
import me.lian.hsc.ktypst.data.output.TypstQueryOutput
import java.nio.file.Path
import java.time.ZonedDateTime

@TypstMarker
class TypstQuery {

    private var _input: Input? = null
    private val inputs: MutableMap<String, String> = mutableMapOf()
    private val fontPaths: MutableList<Path> = mutableListOf()
    private val features: MutableList<TypstFeatures> = mutableListOf()


    var input: Input
        get() = checkNotNull(_input) { "Input is not defined" }
        set(value) {
            check(_input == null) { "Input is already defined" }
            _input = value
        }

    var selector: String? = null
    var field: String? = null
    var one: Boolean = false
    var format: QueryOutputFormat? = null
    var pretty: Boolean = false
    var target: QueryTarget? = null
    var projectRoot: Path? = null
    var ignoreSystemFonts: Boolean = false
    var ignoreEmbeddedFonts: Boolean = false
    var packageDirectory: Path? = null
    var packageCacheDirectory: Path? = null
    var creationTime: ZonedDateTime? = null
    var jobs: Int? = null
    var diagnosticsFormat: DiagnosticsFormat? = null

    operator fun String.unaryPlus() {
        input = Input.Content(this)
    }

    fun fileInput(path: Path) {
        input = Input.File(path)
    }

    fun inputForTypst(key: String, value: String) {
        check(key !in inputs) { "Input is already defined" }
        inputs[key] = value
    }

    fun fontPath(path: Path) {
        fontPaths.add(path)
    }

    operator fun TypstFeatures.unaryPlus() {
        features += this
    }

    fun createCommand() = TypstQueryCommand(
        input = checkNotNull(_input) { "Input was not defined" },
        selector = checkNotNull(selector) { "Selector was not defined" },
        field = field,
        one = one,
        format = format,
        pretty = pretty,
        target = target,
        projectRoot = projectRoot,
        inputs = inputs,
        fontPaths = fontPaths,
        ignoreSystemFonts = ignoreSystemFonts,
        ignoreEmbeddedFonts = ignoreEmbeddedFonts,
        packageDirectory = packageDirectory,
        packageCacheDirectory = packageCacheDirectory,
        creationTime = creationTime,
        jobs = jobs,
        features = features.toList(),
        diagnosticsFormat = diagnosticsFormat
    )

}

suspend fun typstQuery(backend: TypstBackend, block: TypstQuery.() -> Unit): TypstQueryOutput {
    val typstCommand = TypstQuery().apply(block).createCommand()
    return backend.execute(typstCommand)
}
