plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    `java-library`
    `maven-publish`
}

group = "dk.holonet"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    // This dependency is exported to consumers, that is to say found on their compile classpath.
//    api(libs.commons.math3)

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
//    implementation(libs.guava)

    api(libs.pf4j)
    implementation("org.jetbrains.compose.runtime:runtime:1.5.1")
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
