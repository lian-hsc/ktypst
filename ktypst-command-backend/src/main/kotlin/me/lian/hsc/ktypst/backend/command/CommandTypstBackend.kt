package me.lian.hsc.ktypst.backend.command

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import me.lian.hsc.ktypst.backend.TypstBackend
import me.lian.hsc.ktypst.data.command.Input
import me.lian.hsc.ktypst.data.command.Output
import me.lian.hsc.ktypst.data.command.TypstCompileCommand
import me.lian.hsc.ktypst.data.output.Artifact
import me.lian.hsc.ktypst.data.output.TypstCompileOutput
import me.lian.hsc.ktypst.dsl.Typst
import me.lian.hsc.ktypst.dsl.typst
import kotlin.io.path.absolutePathString

object CommandTypstBackend : TypstBackend {

    override suspend fun execute(command: TypstCompileCommand): TypstCompileOutput = coroutineScope {
        val isWindows = System.getProperty("os.name").lowercase().startsWith("windows")

        val process = ProcessBuilder()
            .command(buildList {
                add("typst")
                add("--color=never")
                add("compile")

                if (command.format != null) add("--format=${command.format!!.value}")
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
                if (command.packageCacheDirectory != null) add("--package-cache-path=${command.packageCacheDirectory!!.absolutePathString()}")
                if (command.creationTime != null) add("--creation-timestamp=${command.creationTime!!.toEpochSecond()}")
                if (command.pages.isNotEmpty()) add("--pages=${command.pages.joinToString(",") { it.value }}")
                if (command.pdfStandards.isNotEmpty()) add("--pdf-standards=${command.pdfStandards.joinToString(",") { it.value }}")
                if (command.noPdfTags) add("--no-pdf-tags")
                if (command.ppi != null) add("--ppi=${command.ppi}")
                if (command.dependenciesPath != null) add("--deps=${command.dependenciesPath!!.absolutePathString()}")
                if (command.dependenciesFormat != null) add("--deps-format=${command.dependenciesFormat!!.value}")
                if (command.jobs != null) add("--jobs=${command.jobs}")
                if (command.features.isNotEmpty()) add("--features=${command.features.joinToString(",") { it.value }}")
                if (command.diagnosticsFormat != null) add("--diagnostic-format=${command.diagnosticsFormat!!.value}")

                add(
                    when (command.input) {
                        is Input.Content -> "-"
                        is Input.File -> (command.input as Input.File).path.absolutePathString()
                    }
                )

                add(
                    when (command.output) {
                        is Output.ToResult -> "-"
                        is Output.File -> (command.output as Output.File).path.absolutePathString()
                        is Output.Name -> (command.output as Output.Name).value
                    }
                )
            })
            .redirectInput(ProcessBuilder.Redirect.PIPE)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        val stdoutDeferred = async(Dispatchers.IO) { process.inputStream.readBytes() }
        val stderrDeferred = async(Dispatchers.IO) { process.errorStream.readBytes() }

        withContext(Dispatchers.IO) {
            if (command.input is Input.Content) {
                process.outputWriter().use {
                    it.write((command.input as Input.Content).value)
                }
            } else {
                process.outputWriter().close()
            }
        }

        withContext(Dispatchers.IO) { process.waitFor() }

        val stdout = stdoutDeferred.await()
        val stderr = stderrDeferred.await().decodeToString()

        if (process.exitValue() != 0) {
            return@coroutineScope TypstCompileOutput(
                TypstCompileOutput.Status.Failure,
                stderr,
                emptyList(),
                null
            )
        }

        println(stderr)

        TypstCompileOutput(
            TypstCompileOutput.Status.Success,
            null,
            stderr.lines()
                .filter { it.startsWith("downloading ") }
                .map { it.removePrefix("downloading ").trim() },
            if (stdout.isNotEmpty()) Artifact(stdout) else null
        )
    }

}

suspend fun typst(block: Typst.() -> Unit) = typst(CommandTypstBackend, block)