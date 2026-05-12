package me.lian.hsc.ktypst.backend.command

import io.mockk.*
import kotlinx.coroutines.test.runTest
import me.lian.hsc.ktypst.data.command.*
import me.lian.hsc.ktypst.data.output.TypstCompileOutput
import me.lian.hsc.ktypst.util.ExperimentalTypstFeature
import java.io.BufferedWriter
import java.io.ByteArrayInputStream
import java.io.StringWriter
import java.nio.file.Path
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CommandTypstBackendArgumentTest {

    @Test
    @OptIn(ExperimentalTypstFeature::class)
    fun `passes compile command arguments to process builder`() = runTest {
        val process = mockk<Process>()
        val chainedBuilder = mockk<ProcessBuilder>()
        val commandSlot = slot<List<String>>()
        val writer = BufferedWriter(StringWriter())

        mockkConstructor(ProcessBuilder::class)
        every { constructedWith<ProcessBuilder>().command(capture(commandSlot)) } returns chainedBuilder
        every { chainedBuilder.redirectInput(ProcessBuilder.Redirect.PIPE) } returns chainedBuilder
        every { chainedBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE) } returns chainedBuilder
        every { chainedBuilder.redirectError(ProcessBuilder.Redirect.PIPE) } returns chainedBuilder
        every { chainedBuilder.start() } returns process

        every { process.inputStream } returns ByteArrayInputStream(byteArrayOf())
        every { process.errorStream } returns ByteArrayInputStream(byteArrayOf())
        every { process.outputWriter() } returns writer
        every { process.waitFor() } returns 0
        every { process.exitValue() } returns 0

        try {
            val command = TypstCompileCommand(
                input = Input.File(Path.of("/tmp/input.typ")),
                output = Output.Name("result.pdf"),
                cert = Path.of("/tmp/cert.pem"),
                format = OutputFormat.PDF,
                projectRoot = Path.of("/tmp/project"),
                inputs = linkedMapOf("a" to "1", "b" to "2"),
                fontPaths = listOf(Path.of("/tmp/fonts-1"), Path.of("/tmp/fonts-2")),
                ignoreSystemFonts = true,
                ignoreEmbeddedFonts = true,
                packageDirectory = Path.of("/tmp/packages"),
                packageCacheDirectory = Path.of("/tmp/cache"),
                creationTime = Instant.ofEpochSecond(1704067200).atZone(ZoneOffset.UTC),
                pages = listOf(Pages.SinglePage(1u), Pages.ClosedRange(2u, 4u)),
                pdfStandards = listOf(PdfStandard.PdfA1b, PdfStandard.PdfUA1),
                noPdfTags = true,
                ppi = 300,
                dependenciesPath = Path.of("/tmp/deps"),
                dependenciesFormat = DependenciesFormat.Json,
                jobs = 8,
                features = listOf(TypstFeatures.A11yExtras),
                diagnosticsFormat = DiagnosticsFormat.Short
            )

            val output = CommandTypstBackend.execute(command)

            assertEquals(TypstCompileOutput.Status.Success, output.status)
            assertNull(output.error)
            assertNull(output.stdArtifact)

            val separator = if (System.getProperty("os.name").lowercase().startsWith("windows")) ";" else ":"
            assertEquals(
                listOf(
                    "typst",
                    "--color=never",
                    "compile",
                    "--cert=/tmp/cert.pem",
                    "--format=pdf",
                    "--project-root=/tmp/project",
                    "--input=a=1",
                    "--input=b=2",
                    "--font-paths=/tmp/fonts-1${separator}/tmp/fonts-2",
                    "--ignore-system-fonts",
                    "--ignore-embedded-fonts",
                    "--package-path=/tmp/packages",
                    "--package-cache-path=/tmp/cache",
                    "--creation-timestamp=1704067200",
                    "--pages=1,2-4",
                    "--pdf-standards=a-1b,ua-1",
                    "--no-pdf-tags",
                    "--ppi=300",
                    "--deps=/tmp/deps",
                    "--deps-format=json",
                    "--jobs=8",
                    "--features=a11y-extras",
                    "--diagnostic-format=short",
                    "/tmp/input.typ",
                    "result.pdf"
                ),
                commandSlot.captured
            )

            verify(exactly = 1) { constructedWith<ProcessBuilder>().command(any<List<String>>()) }
            verify(exactly = 1) { chainedBuilder.start() }
        } finally {
            unmockkAll()
        }
    }
}
