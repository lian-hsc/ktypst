package me.lian.hsc.ktypst.backend.command

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import me.lian.hsc.ktypst.backend.TypstBackend
import me.lian.hsc.ktypst.data.command.Input
import me.lian.hsc.ktypst.data.command.TypstCommand
import me.lian.hsc.ktypst.data.command.compile.Output
import me.lian.hsc.ktypst.data.command.compile.TypstCompileCommand
import me.lian.hsc.ktypst.data.command.query.TypstQueryCommand
import me.lian.hsc.ktypst.data.output.Artifact
import me.lian.hsc.ktypst.data.output.Status
import me.lian.hsc.ktypst.data.output.TypstCompileOutput
import me.lian.hsc.ktypst.data.output.TypstQueryOutput
import me.lian.hsc.ktypst.dsl.Typst
import me.lian.hsc.ktypst.dsl.TypstQuery
import me.lian.hsc.ktypst.dsl.typst
import me.lian.hsc.ktypst.dsl.typstQuery
import kotlin.io.path.absolutePathString

object CommandTypstBackend : TypstBackend {

    override suspend fun execute(command: TypstCompileCommand, dispatcher: CoroutineDispatcher): TypstCompileOutput {
        val result = executeCommon(command, createCompileCommand(command), dispatcher)

        if (result.first == null) {
            return TypstCompileOutput(
                status = Status.Failure,
                error = result.second,
                downloadedPackages = emptyList(),
                stdArtifact = null
            )
        }

        return TypstCompileOutput(
            status = Status.Success,
            error = null,
            downloadedPackages = result.second.lines()
                .filter { it.startsWith("downloading ") }
                .map { it.removePrefix("downloading ").trim() },
            stdArtifact = if (result.first!!.isNotEmpty()) Artifact(result.first!!) else null
        )
    }

    override suspend fun execute(command: TypstQueryCommand, dispatcher: CoroutineDispatcher): TypstQueryOutput {
        val result = executeCommon(command, createQueryCommand(command), dispatcher)

        if (result.first == null) {
            return TypstQueryOutput(
                status = Status.Failure,
                error = result.second,
                result = null
            )
        }

        return TypstQueryOutput(
            status = Status.Success,
            error = null,
            result = result.first!!.decodeToString()
        )
    }

    private fun createCompileCommand(command: TypstCompileCommand) = buildList {
        add("typst")
        add("--color=never")
        add("compile")

        if (command.cert != null) add("--cert=${command.cert!!.absolutePathString()}")
        if (command.format != null) add("--format=${command.format!!.value}")
        if (command.pages.isNotEmpty()) add("--pages=${command.pages.joinToString(",") { it.value }}")
        if (command.pdfStandards.isNotEmpty()) {
            add("--pdf-standards=${command.pdfStandards.joinToString(",") { it.value }}")
        }

        if (command.noPdfTags) add("--no-pdf-tags")
        if (command.ppi != null) add("--ppi=${command.ppi}")
        if (command.dependenciesPath != null) add("--deps=${command.dependenciesPath!!.absolutePathString()}")
        if (command.dependenciesFormat != null) add("--deps-format=${command.dependenciesFormat!!.value}")

        appendCommonArguments(command)

        add(
            when (command.output) {
                is Output.ToResult -> "-"
                is Output.File -> (command.output as Output.File).path.absolutePathString()
                is Output.Name -> (command.output as Output.Name).value
            }
        )
    }

    private fun createQueryCommand(command: TypstQueryCommand) = buildList {
        add("typst")
        add("--color=never")
        add("query")

        if (command.field != null) add("--field=${command.field!!}")
        if (command.one) add("--one")
        if (command.format != null) add("--format=${command.format!!.value}")
        if (command.pretty) add("--pretty")
        if (command.target != null) add("--target=${command.target!!.value}")

        appendCommonArguments(command)

        add(command.selector)
    }

    private fun MutableList<String>.appendCommonArguments(command: TypstCommand) {
        val isWindows = System.getProperty("os.name").lowercase().startsWith("windows")

        if (command.projectRoot != null) add("--project-root=${command.projectRoot!!.absolutePathString()}")

        command.inputs.forEach { (key, value) ->
            add("--input=$key=$value")
        }

        if (command.fontPaths.isNotEmpty()) {
            add("--font-paths=${command.fontPaths.joinToString(if (isWindows) ";" else ":")}")
        }

        if (command.ignoreSystemFonts) add("--ignore-system-fonts")
        if (command.ignoreEmbeddedFonts) add("--ignore-embedded-fonts")
        if (command.packageDirectory != null) add("--package-path=${command.packageDirectory!!.absolutePathString()}")

        if (command.packageCacheDirectory != null) {
            add("--package-cache-path=${command.packageCacheDirectory!!.absolutePathString()}")
        }

        if (command.creationTime != null) add("--creation-timestamp=${command.creationTime!!.toEpochSecond()}")

        if (command.jobs != null) add("--jobs=${command.jobs}")
        if (command.features.isNotEmpty()) add("--features=${command.features.joinToString(",") { it.value }}")
        if (command.diagnosticsFormat != null) add("--diagnostic-format=${command.diagnosticsFormat!!.value}")

        add(
            when (command.input) {
                is Input.Content -> "-"
                is Input.File -> (command.input as Input.File).path.absolutePathString()
            }
        )
    }

    private suspend fun executeCommon(
        command: TypstCommand,
        commandArguments: List<String>,
        dispatcher: CoroutineDispatcher
    ): Pair<ByteArray?, String> =
        coroutineScope {
            val process = ProcessBuilder()
                .command(commandArguments)
                .redirectInput(ProcessBuilder.Redirect.PIPE)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            val stdoutDeferred = async(dispatcher) { process.inputStream.readBytes() }
            val stderrDeferred = async(dispatcher) { process.errorStream.readBytes() }

            withContext(dispatcher) {
                if (command.input is Input.Content) {
                    process.outputWriter().use {
                        it.write((command.input as Input.Content).value)
                    }
                } else {
                    process.outputWriter().close()
                }
            }

            withContext(dispatcher) { process.waitFor() }

            val stdout = stdoutDeferred.await()
            val stderr = stderrDeferred.await().decodeToString()

            if (process.exitValue() != 0) {
                return@coroutineScope null to stderr
            }

            return@coroutineScope stdout to stderr
        }

}

suspend fun typst(block: Typst.() -> Unit) = typst(CommandTypstBackend, block)
suspend fun typstQuery(block: TypstQuery.() -> Unit) = typstQuery(CommandTypstBackend, block)
