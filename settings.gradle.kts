pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.6"
    id("dev.kikugie.loom-back-compat") version "0.4"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "F3Advanced"

stonecutter {
    create(rootProject) {
        fun mc(version: String) {
            version(version, version)
        }

        mc("1.21.1")
        mc("1.21.4")
        mc("1.21.5")
        mc("1.21.8")
        mc("1.21.11")
        mc("26.1.1")
        mc("26.1.2")
        mc("26.2")
        mc("26.3")

        vcsVersion = "1.21.8"
    }
}
