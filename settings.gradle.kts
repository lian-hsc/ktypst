plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "ktypst"

include(
    fileTree(".")
        .apply {
            include("**/*.gradle.kts")
            exclude("build.gradle.kts")
        }
        .map { it.relativeTo(rootProject.projectDir) }
        .mapNotNull { it.parent }
        .map { it.replace(File.separator, ":") }
)
