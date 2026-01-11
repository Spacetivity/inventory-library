import java.util.Properties
import java.io.FileInputStream

plugins {
    kotlin("jvm")
    `maven-publish`
}

// Lade local.properties falls vorhanden
val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
    localProperties.forEach { key, value ->
        project.extensions.extraProperties.set(key.toString(), value)
    }
}

dependencies {

}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

task("sourcesJar", type = Jar::class) {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Spacetivity/inventory-library")
            credentials {
                username = localProperties.getProperty("gpr.user") ?: System.getenv("GPR_USER") ?: ""
                password = localProperties.getProperty("gpr.key") ?: System.getenv("GPR_KEY") ?: ""
            }
        }
    }
    
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}