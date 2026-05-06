package me.lian.hsc.ktypst.data.output

import kotlin.io.encoding.Base64

data class Artifact(val content: String) {

    val base64 = Base64.UrlSafe.encode(content.toByteArray())

}
