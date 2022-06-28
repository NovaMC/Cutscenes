import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    `java-library`
    `maven-publish`
}

val prefixedName = "cutscenes-${project.name}"

tasks {
    processResources {
        filter<ReplaceTokens>("tokens" to mapOf(
            "name" to rootProject.name,
            "version" to project.version
        ))
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    jar {
        archiveBaseName.set(prefixedName)
    }
    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = prefixedName
                from(components["java"])
            }
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}