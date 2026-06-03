package me.lian.hsc.ktypst.data.output

import kotlin.io.encoding.Base64

data class Artifact(val content: ByteArray) {

    val base64 = Base64.encode(content)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Artifact

        if (!content.contentEquals(other.content)) return false
        if (base64 != other.base64) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content.contentHashCode()
        result = 31 * result + base64.hashCode()
        return result
    }

}
