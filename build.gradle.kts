import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

fun property(key: String) = providers.gradleProperty(key).get()
fun environment(key: String) = providers.environmentVariable(key).get()

version = property("version")
group = property("group")

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("compiler-embeddable"))
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.2")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup:kotlinpoet:1.13.2")
    implementation("net.pearx.kasechange:kasechange-jvm:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.1")

}

tasks.withType<KotlinCompile<*>> {
    kotlinOptions {
//        allWarningsAsErrors = true
        freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers=true"
    }
}

tasks.wrapper {
    gradleVersion = "8.1.1"
}


tasks.register("pushNextVersion") {
    group = "git"
    doLast {
        val v = project.version as String
        val versionParts = v.split(".")
        val nextVersion = "${versionParts[0]}.${versionParts[1]}.${versionParts[2].toInt() + 1}"
        println("Next version: $nextVersion")
        project.version = nextVersion
        val gradleProperties = project.file("gradle.properties")
        val gradlePropertiesText = gradleProperties.readText()
        val newGradlePropertiesText = gradlePropertiesText.replace("version=${v}", "version=${nextVersion}")
        gradleProperties.writeText(newGradlePropertiesText)
        try {
            runCommands("git tag -d $nextVersion")
        } catch (_: Exception) { }
        runCommands(
            "git add .",
            "git commit -m \"Version bump to $nextVersion\"",
            "git status",
            "git tag $nextVersion",
            "git push --tags",
            "git push",
        )
    }
}

tasks.register("pushRelease") {
    group = "git"
    doLast {
        val v = project.version as String

        try {
            runCommands("git tag -d $v")
        } catch (_: Exception) { }

        runCommands(
            "git add .",
            "git commit -m \"Updating version $v\"",
            "git status",
            "git tag $v",
            "git push --tags",
            "git push",
        )
    }
}

fun runCommands(vararg commands: String) {
    commands.forEach {
        println("Running command: $it")
        val process = Runtime.getRuntime().exec(it)
        process.waitFor()
        val output = process.inputStream.bufferedReader().readText()
        if (output.isNotEmpty()) println("Command output: $output")
    }
}


publishing {

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/IvanEOD/${project.name}")
            credentials {
                username = environment("GITHUB_PACKAGES_USERID")
                password = environment("GITHUB_PACKAGES_PUBLISH_TOKEN")
            }
        }
    }

    publications {
        register<MavenPublication>("gpr") {
            from(components["kotlin"])
        }
    }
}

//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            from(components["kotlin"])
//            groupId = project.group.toString()
//            artifactId = "declaration-generation"
//            version = project.version.toString()
//        }
//    }
//}
