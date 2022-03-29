plugins {
    id("no.skatteetaten.gradle.aurora") version "4.4.12"
    id("org.springframework.boot") version "2.6.5"
}

aurora {
    useKotlinDefaults
    features {
        auroraStarters = false
    }
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.spring.io/milestone")
    }
}

val implementation by configurations

dependencies {
    implementation(project(":aurora-spring-boot-webflux-starter"))

    implementation("org.springframework.boot:spring-boot-starter:2.6.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
}
