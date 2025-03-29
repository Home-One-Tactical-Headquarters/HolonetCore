import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    `java-library`
    kotlin("plugin.serialization") version "2.1.0"
    alias(libs.plugins.vanniktech.mavenPublish)
}

val artifactId = "core"
group = "dk.holonet"
version = "0.0.1"

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
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

//    signAllPublications()

    coordinates(group.toString(), artifactId, version.toString())

    pom {
        name = "Holonet Core"
        description = "Holonet Core library containing the core functionality of the Holonet framework."
        inceptionYear = "2025"
        url = "https://github.com/Home-One-Tactical-Headquarters/HolonetCore"
        /*licenses {
            license {
                name = "XXX"
                url = "YYY"
                distribution = "ZZZ"
            }
        }
        developers {
            developer {
                id = "XXX"
                name = "YYY"
                url = "ZZZ"
            }
        }
        scm {
            url = "XXX"
            connection = "YYY"
            developerConnection = "ZZZ"
        }*/
    }
}

tasks.withType<PublishToMavenLocal>().configureEach {
    doFirst {
        println("Publishing: ${publication.groupId}:${publication.artifactId}:${publication.version}")
    }
}
