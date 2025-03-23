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
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev/")
    maven("https://repo1.maven.org/maven2/")
    mavenLocal()
}

dependencies {
    api(libs.pf4j)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation("org.jetbrains.compose.runtime:runtime:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
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
