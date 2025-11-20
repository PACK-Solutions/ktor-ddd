plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.example"
version = "0.0.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.postgresql)
    implementation(libs.kotlinx.coroutines.core)
}
