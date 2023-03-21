group = "com.github.kokorin.jdbt"
version = "0.0.1"

plugins {
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.oshai:kotlin-logging-jvm:4.0.0-beta-22")
    implementation("ch.qos.logback:logback-classic:1.4.5")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
