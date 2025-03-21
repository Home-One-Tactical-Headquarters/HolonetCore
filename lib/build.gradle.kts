plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    `java-library`
    `maven-publish`
    kotlin("plugin.serialization") version "2.1.0"
}

group = "dk.holonet"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.pf4j)
    implementation("io.insert-koin:koin-core:4.0.2")
    implementation("io.insert-koin:koin-compose:4.0.2")
    implementation("org.jetbrains.compose.runtime:runtime:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "dk.holonet"
            artifactId = "core"
            version = project.version.toString()
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}

tasks.withType<PublishToMavenLocal>().configureEach {
    doFirst {
        println("Publishing: ${publication.groupId}:${publication.artifactId}:${publication.version}")
    }
}
