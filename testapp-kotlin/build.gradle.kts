plugins {
    kotlin("jvm") version Versions.kotlin
    id("no.skatteetaten.gradle.aurora") version PluginVersions.aurora
    id("org.springframework.boot") version PluginVersions.springBoot
}

aurora {
    useKotlinDefaults
    features {
        auroraStarters = false
    }
}

dependencies {
    implementation(project(":aurora-spring-boot-webflux-starter"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${Versions.kotlinCoroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:${Versions.kotlinCoroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}")
    implementation("io.github.microutils:kotlin-logging-jvm:${Versions.kotlinLogging}")
}
