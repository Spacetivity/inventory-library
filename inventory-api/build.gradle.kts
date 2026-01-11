plugins {
    kotlin("jvm")
    `maven-publish`
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
            val repository = project.findProperty("github.repository") as String? 
                ?: System.getenv("GITHUB_REPOSITORY") 
                ?: "OWNER/REPO"
            url = uri("https://maven.pkg.github.com/$repository")
            credentials {
                username = project.findProperty("github.user") as String? 
                    ?: System.getenv("GITHUB_ACTOR") 
                    ?: System.getenv("USERNAME")
                password = project.findProperty("github.token") as String? 
                    ?: System.getenv("GITHUB_TOKEN") 
                    ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}