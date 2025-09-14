import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    `java-library`
    kotlin("plugin.serialization") version "2.1.0"
    alias(libs.plugins.vanniktech.mavenPublish)
    signing
}

val artifactId = "core"
group = "dk.holonet"
version = "0.0.2"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev/")
    maven("https://repo1.maven.org/maven2/")
    mavenLocal()
}

kotlin {
    jvm()
//    linuxX64()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        val commonMain by getting {
           dependencies {
               implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
               implementation("org.jetbrains.compose.runtime:runtime:1.7.3")
           }
        }

        val jvmMain by getting {
            dependencies {
                api(libs.pf4j)
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
//    implementation(libs.koin.compose.viewmodel)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.androidx.lifecycle.runtime.compose)
            }
        }
    }
}

publishing {
    repositories {
        mavenLocal()
        mavenCentral() {
            credentials {
                username = System.getenv("mavenCentralUsername")
                password = System.getenv("mavenCentralPassword")
            }
        }
    }
}

signing {
    // Only sign if not publishing locally
    isRequired = !isPublishingLocally
    useGpgCmd()
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

    if (!isPublishingLocally) {
        signAllPublications()
    }

    coordinates(group.toString(), artifactId, version.toString())

    pom {
        name = "Holonet Core"
        description = "Holonet Core library containing the core functionality of the Holonet framework."
        inceptionYear = "2025"
        url = "https://github.com/Home-One-Tactical-Headquarters/HolonetCore/"
        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/license/MIT"
                distribution = "https://opensource.org/license/MIT"
            }
        }
        developers {
            developer {
                id = "GlitzyWorm"
                name = "Mathias Jelshøj"
                url = "https://github.com/GlitzyWorm/"
            }
        }
        scm {
            url = "https://github.com/Home-One-Tactical-Headquarters/HolonetCore/"
            connection = "scm:git:git://github.com/Home-One-Tactical-Headquarters/HolonetCore.git"
        }
    }
}

tasks.withType<PublishToMavenLocal>().configureEach {
    doFirst {
        println("Publishing: ${publication.groupId}:${publication.artifactId}:${publication.version}")
    }
}

val isPublishingLocally = gradle.startParameter.taskNames.any { it.contains("MavenLocal") }
