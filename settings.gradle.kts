rootProject.name = "ktor-ddd"

include(":core")
include(":infra:exposed-postgres")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
