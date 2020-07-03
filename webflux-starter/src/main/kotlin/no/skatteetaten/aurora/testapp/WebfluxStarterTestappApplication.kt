package no.skatteetaten.aurora.testapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebfluxStarterTestappApplication

fun main(args: Array<String>) {
    runApplication<WebfluxStarterTestappApplication>(*args)
}
