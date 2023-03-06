plugins {
    id("base-conventions")
    id("com.github.johnrengelman.shadow")
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${projectDir.name}-${project.version}.jar")

        minimize()
        relocate("de.javagl.jgltf", "${rootProject.group}.libs.de.javagl.jgltf")
        relocate("org.spongepowered.configurate", "${rootProject.group}.libs.org.spongepowered.configurate")
        relocate("io.leangen.geantyref", "${rootProject.group}.libs.io.leangen.geantyref")
        relocate("com.fasterxml.jackson", "${rootProject.group}.libs.com.fasterxml.jackson")
    }
    build {
        dependsOn(shadowJar)
    }
}
