import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    signing
    java
    `java-library`
    `maven-publish`
}

group = rootProject.group
version = rootProject.version
description = rootProject.description

val targetJavaVersion = 17

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://repo.kryptonmc.org/releases")
    maven("https://repo.novaserver.xyz/snapshots/")
}

tasks {
    processResources {
        filter<ReplaceTokens>("tokens" to mapOf(
            "name" to rootProject.name,
            "version" to project.version
        ))
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(targetJavaVersion)
    }
    publishing {
        repositories {
            maven {
                name = "novaReleases"
                url = uri("https://repo.novaserver.xyz/releases")
                credentials(PasswordCredentials::class)
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
            maven {
                name = "novaSnapshots"
                url = uri("https://repo.novaserver.xyz/snapshots")
                credentials(PasswordCredentials::class)
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}
