plugins {
    kotlin("jvm") version "2.3.10"
}

allprojects {
    apply(plugin = "kotlin")

    group = "me.lian-hsc"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

kotlin {
    jvmToolchain(21)
}