package me.lian.hsc.ktypst.dsl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import me.lian.hsc.ktypst.backend.TypstBackend
import me.lian.hsc.ktypst.data.command.*
import me.lian.hsc.ktypst.data.command.compile.DependenciesFormat
import me.lian.hsc.ktypst.data.command.compile.Output
import me.lian.hsc.ktypst.data.command.compile.CompileOutputFormat
import me.lian.hsc.ktypst.data.command.compile.Pages
import me.lian.hsc.ktypst.data.command.compile.PdfStandard
import me.lian.hsc.ktypst.data.command.compile.TypstCompileCommand
import me.lian.hsc.ktypst.data.command.query.TypstQueryCommand
import me.lian.hsc.ktypst.data.output.Artifact
import me.lian.hsc.ktypst.data.output.Status
import me.lian.hsc.ktypst.data.output.TypstCompileOutput
import me.lian.hsc.ktypst.data.output.TypstQueryOutput
import me.lian.hsc.ktypst.util.ExperimentalTypstFeature
import java.nio.file.Path
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class TypstDslTest {

    @Test
    @OptIn(ExperimentalTypstFeature::class)
    fun `typst dsl builds command and calls backend`() = runBlocking {
        val fixedCreationTime = ZonedDateTime.parse("2024-01-01T00:00:00Z")
        val expectedOutput = TypstCompileOutput(
            status = Status.Success,
            error = null,
            downloadedPackages = emptyList(),
            stdArtifact = Artifact("ok".encodeToByteArray())
        )
        val backend = CapturingBackend(expectedOutput)

        val result = typst(backend) {
            +"= Hello"
            outputAsResult()
            cert = Path.of("/tmp/cert.pem")
            format = CompileOutputFormat.PDF
            projectRoot = Path.of("/tmp/project")
            inputForTypst("name", "world")
            fontPath(Path.of("/tmp/fonts"))
            ignoreSystemFonts = true
            ignoreEmbeddedFonts = true
            packageDirectory = Path.of("/tmp/packages")
            packageCacheDirectory = Path.of("/tmp/cache")
            creationTime = fixedCreationTime
            singlePage(1u)
            pageRange(2u, 4u)
            pagesFrom(5u)
            pagesUntil(7u)
            +PdfStandard.PdfA1b
            noPdfTags = true
            ppi = 300
            dependenciesPath = Path.of("/tmp/deps.txt")
            dependenciesFormat = DependenciesFormat.Json
            jobs = 4
            +TypstFeatures.HTML
            diagnosticsFormat = DiagnosticsFormat.Short
        }

        assertEquals(expectedOutput, result)
        assertEquals(1, backend.calls)
        val command = assertNotNull(backend.lastCommand)

        assertEquals(Input.Content("= Hello"), command.input)
        assertEquals(Output.ToResult, command.output)
        assertEquals(Path.of("/tmp/cert.pem"), command.cert)
        assertEquals(CompileOutputFormat.PDF, command.format)
        assertEquals(Path.of("/tmp/project"), command.projectRoot)
        assertEquals(mapOf("name" to "world"), command.inputs)
        assertEquals(listOf(Path.of("/tmp/fonts")), command.fontPaths)
        assertEquals(true, command.ignoreSystemFonts)
        assertEquals(true, command.ignoreEmbeddedFonts)
        assertEquals(Path.of("/tmp/packages"), command.packageDirectory)
        assertEquals(Path.of("/tmp/cache"), command.packageCacheDirectory)
        assertEquals(fixedCreationTime, command.creationTime)
        assertEquals(
            listOf(
                Pages.SinglePage(1u),
                Pages.ClosedRange(2u, 4u),
                Pages.OpenEndRange(5u),
                Pages.OpenStartRange(7u)
            ),
            command.pages
        )
        assertEquals(listOf(PdfStandard.PdfA1b), command.pdfStandards)
        assertEquals(true, command.noPdfTags)
        assertEquals(300, command.ppi)
        assertEquals(Path.of("/tmp/deps.txt"), command.dependenciesPath)
        assertEquals(DependenciesFormat.Json, command.dependenciesFormat)
        assertEquals(4, command.jobs)
        assertEquals(listOf(TypstFeatures.HTML), command.features)
        assertEquals(DiagnosticsFormat.Short, command.diagnosticsFormat)
    }

    @Test
    fun `dsl requires input and output`() {
        assertFailsWith<IllegalStateException> {
            Typst().apply {
                outputAsResult()
            }.createCommand()
        }

        assertFailsWith<IllegalStateException> {
            Typst().apply {
                +"= Hello"
            }.createCommand()
        }
    }

    private class CapturingBackend(
        private val response: TypstCompileOutput
    ) : TypstBackend {
        var calls: Int = 0
            private set
        var lastCommand: TypstCompileCommand? = null
            private set

        override suspend fun execute(
            command: TypstCompileCommand,
            dispatcher: CoroutineDispatcher
        ): TypstCompileOutput {
            calls++
            lastCommand = command
            return response
        }

        override suspend fun execute(command: TypstQueryCommand, dispatcher: CoroutineDispatcher): TypstQueryOutput {
            throw NotImplementedError("Query command is not expected to be called in this test")
        }
    }
}
