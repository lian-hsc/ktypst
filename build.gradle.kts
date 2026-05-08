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

    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

        testImplementation(kotlin("test"))
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

kotlin {
    jvmToolchain(21)
}
