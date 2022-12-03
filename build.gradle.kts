import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    application
}

application {
    mainClass.set("info.vivime.thunderhead.MainKt")
}
group = "info.vivime"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
    maven("https://m2.dv8tion.net/releases")
    maven("https://redempt.dev")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("net.dv8tion:JDA:5.0.0-alpha.22") {
        exclude(module="opus-java")
    }
    implementation("com.github.minndevelopment:jda-ktx:17eb77a")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.json:json:20220924")
    implementation("org.reflections:reflections:0.10.2")
    implementation("com.github.Redempt:Crunch:1.1.2")
    implementation("org.xerial:sqlite-jdbc:3.40.4.0")
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("shadow")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "info.vivime.thunderhead.MainKt"))
        }
    }
    build {
        dependsOn(shadowJar)
    }
    test {
        useJUnitPlatform()
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}