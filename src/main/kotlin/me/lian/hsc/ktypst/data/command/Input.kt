package me.lian.hsc.ktypst.data.command

import java.nio.file.Path

sealed interface Input {

    data class Content(val value: String) : Input
    data class File(val path: Path) : Input

}
