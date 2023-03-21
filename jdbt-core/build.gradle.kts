plugins {
    id("jdbt.conventions")
}

dependencies {
    //implementation("com.hubspot.jinjava:jinjava:2.6.0")
    implementation("io.pebbletemplates:pebble:3.2.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")

    testImplementation("com.google.jimfs:jimfs:1.2")
}
