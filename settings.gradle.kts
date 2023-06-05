rootProject.name = "declaration-generation"

pluginManagement {
    repositories {
        mavenCentral()
    }

    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        kotlin("jvm") version kotlinVersion
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
