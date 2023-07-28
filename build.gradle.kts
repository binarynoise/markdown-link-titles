import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val javaVersion = JavaVersion.VERSION_17
val javaVersionNumber = javaVersion.name.substringAfter('_').replace('_', '.')
val javaVersionMajor = javaVersion.name.substringAfterLast('_')

val main = "MainKt"

dependencies {
    implementation("com.tfowl.jsoup:jsoup-ktx-ktor:0.1.0")
    implementation("io.ktor:ktor-client:2.3.2")
    implementation("org.jsoup:jsoup:1.16.1")
}

kotlin {
    jvmToolchain(javaVersionMajor.toInt())
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    modularity.inferModulePath.set(true)
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf("Main-Class" to main))
    }
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("shadow")
    minimize {
    }
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}
