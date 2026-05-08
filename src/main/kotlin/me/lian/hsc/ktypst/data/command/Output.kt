package me.lian.hsc.ktypst.data.command

import java.nio.file.Path

sealed interface Output {

    object ToResult : Output
    data class Name(val value: String) : Output
    data class File(val path: Path) : Output

}
