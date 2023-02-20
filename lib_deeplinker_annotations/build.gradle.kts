plugins {
    kotlin("jvm")
}

kotlin.sourceSets.main {
    kotlin.srcDirs(
        file("$buildDir/generated/ksp/main/kotlin"),
        file("src/main/kotlin")
    )
}