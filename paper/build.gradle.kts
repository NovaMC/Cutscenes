plugins {
    id("platform-conventions")
    id("io.papermc.paperweight.userdev")
}

val minecraftVersion = libs.versions.minecraft.get()

dependencies {
    paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")

    api(projects.cutscenesApi)
    implementation(projects.cutscenesApi)
    implementation(libs.jgltf)
    implementation(libs.configurate)

    compileOnly(libs.paperApi)
    compileOnly(libs.protocollib)
    compileOnly(libs.floodgate)
    compileOnly(libs.tab)
    compileOnly(libs.placeholders)
    compileOnly(files("../libs/Themis.jar"))
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}