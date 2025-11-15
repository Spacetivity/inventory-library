import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.21"
    id("com.gradleup.shadow") version "9.2.2"
}

dependencies {
    implementation(project(":inventory-api"))
}

tasks.shadowJar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}

tasks.shadowJar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
    archiveFileName.set("inventory-library.jar")
    mergeServiceFiles()
    archiveClassifier.set("")
    
    // Exclude all Kotlin runtime classes to avoid ClassLoader conflicts
    // Kotlin stdlib should be provided by the server runtime
    exclude("kotlin/")
    exclude("kotlin/**")
    exclude("kotlinx/")
    exclude("kotlinx/**")
    exclude("META-INF/**kotlin**")
    exclude("META-INF/**/*kotlin*")
    exclude("**/META-INF/**/*kotlin*")
    
    dependencies {
        // Exclude all Kotlin runtime dependencies to avoid ClassLoader conflicts
        exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
        exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8"))
        exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-common"))
        exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7"))
        exclude(dependency("org.jetbrains.kotlin:kotlin-reflect"))
        
        // Exclude all kotlinx dependencies
        exclude(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core"))
        exclude(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8"))
        exclude(dependency("org.jetbrains.kotlinx:kotlinx-serialization-core"))
        exclude(dependency("org.jetbrains.kotlinx:kotlinx-serialization-json"))
    }
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}