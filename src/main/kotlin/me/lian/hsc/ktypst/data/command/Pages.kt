package me.lian.hsc.ktypst.data.command

sealed interface Pages {

    val value: String

    data class SinglePage(val page: UInt) : Pages {

        override val value: String = "$page"

    }

    data class ClosedRange(val startInclusive: UInt, val endInclusive: UInt) : Pages {

        override val value: String = "$startInclusive-$endInclusive"

    }

    data class OpenEndRange(val startInclusive: UInt) : Pages {

        override val value: String = "$startInclusive-"

    }

    data class OpenStartRange(val endInclusive: UInt) : Pages {

        override val value: String = "-$endInclusive"

    }

}
