plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    
    // Testing
    testImplementation(libs.junit)
}
