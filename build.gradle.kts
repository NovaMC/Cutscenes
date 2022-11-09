plugins {
    id("cutscenes.parent")
}

val platforms = setOf(
    projects.paper
).map { it.dependencyProject }

subprojects {
    when (this) {
        in platforms -> plugins.apply("cutscenes.platform-conventions")
        else -> plugins.apply("cutscenes.base-conventions")
    }
}
