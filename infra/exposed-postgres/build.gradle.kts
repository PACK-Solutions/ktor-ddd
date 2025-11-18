plugins {
    kotlin("jvm") version "2.2.20"
}

group = "com.example"
version = "0.0.1"

dependencies {
    implementation(project(":core"))

    // Exposed & PostgreSQL
    implementation("org.jetbrains.exposed:exposed-core:0.56.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.56.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.56.0")
    implementation("org.postgresql:postgresql:42.7.4")
}
