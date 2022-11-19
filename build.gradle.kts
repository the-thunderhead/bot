import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application
}

application {
    mainClass.set("dev.skrub.thunderhead.bot.Main")
}
group = "dev.skrub.thunderhead"
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
    implementation("com.github.minndevelopment:jda-ktx:863470e")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.json:json:20220924")
    implementation("org.reflections:reflections:0.10.2")
    implementation("com.github.Redempt:Crunch:1.1.2")
    implementation("org.xerial:sqlite-jdbc:3.39.4.0")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}