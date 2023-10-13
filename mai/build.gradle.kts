plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    //Jama
    implementation("gov.nist.math:jama:1.0.3")

    //Kotlin Serialization
    val serializtion_version = "1.5.1"
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializtion_version")
}