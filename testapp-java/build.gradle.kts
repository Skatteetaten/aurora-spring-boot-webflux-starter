plugins {
    id("java")
    id("no.skatteetaten.gradle.aurora") version PluginVersions.aurora
    id("org.springframework.boot") version PluginVersions.springBoot
}

aurora {
    useJavaDefaults
    features {
        auroraStarters = false
    }
}

dependencies {
    implementation(project(":aurora-spring-boot-webflux-starter"))

    implementation("org.springframework.boot:spring-boot-starter")
}
