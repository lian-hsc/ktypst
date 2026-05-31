import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension

plugins {
    `maven-publish`
    kotlin("jvm") version "2.3.10"
    id("dev.detekt") version "2.0.0-alpha.3"
}

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "kotlin")
    apply(plugin = "dev.detekt")

    group = "me.lian-hsc.ktypst"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

        testImplementation(kotlin("test"))
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    }

    publishing {
        repositories {
            mavenLocal()
        }

        publications {
            create<MavenPublication>("maven") {
                from(components["java"])

                artifactId = project.path
                    .removePrefix(":")
                    .replace(":", "-")
                    .ifBlank { rootProject.name }
            }
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    configure<DetektExtension> {
        buildUponDefaultConfig = true
        config.setFrom(rootProject.file("detekt.yml"))
        parallel = true
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = "21"
        reports {
            html.required.set(true)
            sarif.required.set(true)
        }
    }

    tasks.named("check") {
        dependsOn("detektMain", "detektTest")
    }
}

kotlin {
    jvmToolchain(21)
}
