plugins {
    `java-library`
    kotlin("jvm") version Versions.kotlin
    id("no.skatteetaten.gradle.aurora") version PluginVersions.aurora
}

aurora {
    useLibDefaults
    useJavaDefaults
    useSpringBoot {
        useWebFlux
    }

    features {
        auroraStarters = false
    }
}

dependencies {
    api(platform("org.springframework.cloud:spring-cloud-dependencies:${Versions.springCloud}"))
    api("org.springframework.cloud:spring-cloud-starter-sleuth")

    api("org.springframework.boot:spring-boot-starter-webflux")
    api("no.skatteetaten.aurora.springboot:aurora-spring-boot-base-starter:${Versions.auroraBaseStarter}")
    api("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:${Versions.assertk}")
    testImplementation("com.squareup.okhttp3:mockwebserver:${Versions.mockwebserver}")
    testImplementation("org.testcontainers:testcontainers:${Versions.testcontainers}")
    testImplementation("org.testcontainers:junit-jupiter:${Versions.testcontainers}")
    testImplementation("org.awaitility:awaitility-kotlin:${Versions.awaitility}")
}