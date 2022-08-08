package no.skatteetaten.aurora.webflux.testapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class TestApp {

    @Configuration
    public static class TestConfig {

        @Bean
        public WebClient webClient(WebClient.Builder builder) {
            return builder.baseUrl("http://localhost:8080").build();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }
}
