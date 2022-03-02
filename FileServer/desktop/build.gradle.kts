//import org.jetbrains.compose.compose

group = "com.cool1024"
version = "1.0.0-SNAPSHOT"

plugins {

    kotlin("jvm") version "1.4.10"
    kotlin("plugin.spring") version "1.4.10"
    id("org.springframework.boot") version "2.4.0"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.twelvemonkeys.imageio:imageio-batik:3.5")
    implementation("org.apache.xmlgraphics:batik-transcoder:1.10")
    implementation("org.apache.xmlgraphics:batik-anim:1.10")
    implementation("org.apache.pdfbox:pdfbox:2.0.21")
    implementation("com.github.junrar:junrar:7.4.0")
    implementation("org.zeroturnaround:zt-zip:1.14")
    implementation("com.github.vatbub:mslinks:1.0.6")
    implementation("org.bouncycastle:bcprov-jdk15on:1.69")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}