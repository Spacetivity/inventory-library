import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.21"
}

allprojects {
    group = "net.spacetivity.inventorylib"
    version = "1.0-SNAPSHOT"

    repositories {
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        mavenCentral()
    }

    apply(plugin = "java")
    apply(plugin = "maven-publish")

    afterEvaluate {
        dependencies {
            compileOnly(libs.kotlinx.serialization.json)
            compileOnly(libs.paper.api)
        }
    }
}

tasks.jar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}