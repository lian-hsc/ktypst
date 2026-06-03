package me.lian.hsc.ktypst.backend

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import me.lian.hsc.ktypst.data.command.compile.TypstCompileCommand
import me.lian.hsc.ktypst.data.command.query.TypstQueryCommand
import me.lian.hsc.ktypst.data.output.TypstCompileOutput
import me.lian.hsc.ktypst.data.output.TypstQueryOutput

/**
 * A backend that can execute Typst commands.
 * Currently, only compiling with Typst is supported.
 */
interface TypstBackend {

    /**
     * Executes the given [TypstCompileCommand] and returns the result of executing it.
     */
    suspend fun execute(
        command: TypstCompileCommand,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): TypstCompileOutput

    /**
     * Executes the given [TypstQueryCommand] and returns the result of executing it.
     */
    suspend fun execute(
        command: TypstQueryCommand,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): TypstQueryOutput

}
