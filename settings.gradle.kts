@file:Suppress("UnstableApiUsage")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.spongepowered.org/repository/maven-public/")
    }
    includeBuild("build-src")
}

plugins {
    id("ca.stellardrift.polyglot-version-catalogs") version "5.0.1"
}

rootProject.name = "NovaCutscenes"

setupSubproject("cutscenes-api") {
    projectDir = file("api")
}
setupSubproject("cutscenes-paper") {
    projectDir = file("paper")
}

inline fun setupSubproject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}
