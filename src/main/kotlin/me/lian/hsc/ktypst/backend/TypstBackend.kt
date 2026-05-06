package me.lian.hsc.ktypst.backend

import me.lian.hsc.ktypst.data.command.TypstCompileCommand
import me.lian.hsc.ktypst.data.output.TypstCompileOutput

/**
 * A backend that can execute Typst commands.
 * Currently only compiling with Typst is supported.
 */
interface TypstBackend {

    /**
     * Executes the given [TypstCompileCommand] and returns the result of executing it.
     */
    suspend fun execute(command: TypstCompileCommand): TypstCompileOutput

}