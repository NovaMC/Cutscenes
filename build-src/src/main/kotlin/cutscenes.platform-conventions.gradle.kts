plugins {
    id("cutscenes.base-conventions")
}

tasks {
    jar {
        archiveFileName.set("${rootProject.name}-${project.version}.jar")
    }
    build {
        dependsOn(jar)
    }
}
