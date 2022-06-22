plugins {
    id("cutscenes.parent")
}

allprojects {
    group = "xyz.novaserver.cutscenes"
}

subprojects {
    plugins.apply("cutscenes.base-conventions")
}
