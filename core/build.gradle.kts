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
