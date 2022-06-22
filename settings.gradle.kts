@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://repo.opencollab.dev/maven-snapshots/")
        maven("https://repo.kryptonmc.org/releases")
        mavenLocal()
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
    includeBuild("build-src")
}

rootProject.name = "NovaCutscenes"

include(":api")
include(":paper")

findProject(":api")?.name = "cutscenes-api"
findProject(":paper")?.name = "cutscenes-paper"
