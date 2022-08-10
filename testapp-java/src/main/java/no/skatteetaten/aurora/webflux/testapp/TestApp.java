package no.skatteetaten.aurora.webflux.testapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@SuppressWarnings("checkstyle:FinalClass")
@SpringBootApplication
public class TestApp {

    private TestApp() {
    }

    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }

    @Configuration
    public static class TestConfig {

        @Bean
        public WebClient webClient(WebClient.Builder builder) {
            return builder.baseUrl("http://localhost:8080").build();
        }
    }
}
