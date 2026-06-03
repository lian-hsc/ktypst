package me.lian.hsc.ktypst.data.command.compile

enum class DependenciesFormat(val value: String) {

    /**
     * Encodes as JSON, failing for non-Unicode paths.
     */
    Json("json"),

    /**
     * Separates paths with NULL bytes and can express all paths.
     */
    Zero("zero"),

    /**
     * Emits in Make format, omitting inexpressible paths.
     */
    Make("make"),

}
