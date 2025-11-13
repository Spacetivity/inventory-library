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
            name = "GitLabPackages"
            url = uri("https://gitlab.com/api/v4/projects/${project.findProperty("gitlab.projectId") ?: System.getenv("CI_PROJECT_ID") ?: "YOUR_PROJECT_ID"}/packages/maven")
            credentials {
                username = project.findProperty("gitlab.user") as String? ?: System.getenv("CI_JOB_USER") ?: "__token__"
                password = project.findProperty("gitlab.token") as String? ?: System.getenv("CI_JOB_TOKEN") ?: System.getenv("GITLAB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}