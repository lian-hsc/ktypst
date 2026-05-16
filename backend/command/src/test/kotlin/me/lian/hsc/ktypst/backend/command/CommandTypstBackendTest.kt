package me.lian.hsc.ktypst.backend.command

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.runTest
import me.lian.hsc.ktypst.data.command.Input
import me.lian.hsc.ktypst.data.command.Output
import me.lian.hsc.ktypst.data.command.OutputFormat
import me.lian.hsc.ktypst.data.command.TypstCompileCommand
import me.lian.hsc.ktypst.data.output.TypstCompileOutput
import java.nio.file.Files
import java.security.MessageDigest
import java.time.Instant
import java.time.ZoneOffset
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CommandTypstBackendTest {

    @Test
    fun `compile to result returns deterministic hashes for pdf png and svg`() = runTest {
//        assertHashForFormat(OutputFormat.PDF, "daa10a05ee45b5ff8904764380e58fc605acfcc6e25978f919a6a17a796a271a")
//        assertHashForFormat(OutputFormat.PNG, "0543633e41d9ec44fc9dcc057320715bbf86fb73a87bc494ddb29c25923d2546")
        assertHashForFormat(OutputFormat.SVG, "35f065e44c1b8a75c0587b31b4998e240fc3b1c081041bfc1631eb06ab905fb2")
    }

    @Test
    fun `compile test`() = runTest {
//        assertTempFileMatchesExpected(OutputFormat.PDF, "expected/hello.pdf", ".pdf")
//        assertTempFileMatchesExpected(OutputFormat.PNG, "expected/hello.png", ".png")
        assertTempFileMatchesExpected(OutputFormat.SVG, "expected/hello.svg", ".svg")
    }

    @Test
    fun `compile failure returns failure output`() = runTest {
        val output = CommandTypstBackend.execute(
            commandFor(
                source = "#let x =",
                format = OutputFormat.PDF
            )
        )

        assertEquals(TypstCompileOutput.Status.Failure, output.status)
        assertEquals(
            """
            error: expected expression
              ┌─ <stdin>:1:8
              │
            1 │ #let x =
              │         ^


            """.trimIndent(), output.error
        )
        assertNull(output.stdArtifact)
    }

    private suspend fun assertHashForFormat(format: OutputFormat, expectedHash: String) {
        val output = CommandTypstBackend.execute(commandFor(EXAMPLE_DOCUMENT, format))

        print(output.stdArtifact?.content?.let { String(it) })

        assertEquals(TypstCompileOutput.Status.Success, output.status)
        assertNull(output.error)
        val artifact = assertNotNull(output.stdArtifact)
        assertEquals(expectedHash, sha256Hex(artifact.content))
    }

    private suspend fun assertTempFileMatchesExpected(
        format: OutputFormat,
        expectedResource: String,
        suffix: String
    ): Unit = coroutineScope {
        val tmpFile = Files.createTempFile("ktypst-", suffix)
        try {
            val output = CommandTypstBackend.execute(
                commandFor(EXAMPLE_DOCUMENT, format, output = Output.File(tmpFile))
            )

            assertEquals(TypstCompileOutput.Status.Success, output.status)
            assertNull(output.error)
            assertNull(output.stdArtifact)

            val expected = expectedResourceBytes(expectedResource)
            val actual = Files.readAllBytes(tmpFile)
            assertEquals(expected.toList(), actual.toList())
        } finally {
            Files.deleteIfExists(tmpFile)
        }
    }

    private fun expectedResourceBytes(path: String): ByteArray =
        checkNotNull(this::class.java.classLoader.getResourceAsStream(path)) {
            "Missing expected resource: $path"
        }.use { it.readBytes() }

    private fun commandFor(source: String, format: OutputFormat, output: Output = Output.ToResult) =
        TypstCompileCommand(
            input = Input.Content(source),
            output = output,
            cert = null,
            format = format,
            projectRoot = null,
            inputs = emptyMap(),
            fontPaths = emptyList(),
            ignoreSystemFonts = false,
            ignoreEmbeddedFonts = false,
            packageDirectory = null,
            packageCacheDirectory = null,
            creationTime = FIXED_CREATION_TIME,
            pages = emptyList(),
            pdfStandards = emptyList(),
            noPdfTags = false,
            ppi = null,
            dependenciesPath = null,
            dependenciesFormat = null,
            jobs = null,
            features = emptyList(),
            diagnosticsFormat = null
        )

    private fun sha256Hex(value: ByteArray): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value)
            .joinToString("") { "%02x".format(Locale.US, it) }

    private companion object {
        private val FIXED_CREATION_TIME = Instant.ofEpochSecond(1704067200).atZone(ZoneOffset.UTC)

        private const val EXAMPLE_DOCUMENT = "#set page(width: 120pt, height: 80pt, margin: 8pt)\n" +
            "#set text(size: 11pt)\n" +
            "Hello, ktypst!\n"
    }
}
