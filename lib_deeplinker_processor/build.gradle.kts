plugins {
    kotlin("jvm")
}

kotlin.sourceSets.main {
    kotlin.srcDirs(
        file("$buildDir/generated/ksp/main/kotlin"),
        file("$buildDir/generated/ksp/debug/kotlin"),
        file("$buildDir/generated/ksp"),
        file("src/main/kotlin")
    )
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.20")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.7.20")

    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.20-1.0.7")

    implementation("com.squareup:kotlinpoet:1.8.0")
    implementation("com.squareup:kotlinpoet-metadata:1.8.0")
    implementation("com.squareup:kotlinpoet-metadata-specs:1.8.0")
    implementation("com.squareup:kotlinpoet-ksp:1.10.2")

    implementation("com.google.code.gson:gson:2.9.1")
    implementation(project(path = ":lib_deeplinker_annotations"))
}