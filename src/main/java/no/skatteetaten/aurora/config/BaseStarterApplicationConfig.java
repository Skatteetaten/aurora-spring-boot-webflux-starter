package no.skatteetaten.aurora.config;

import java.io.IOException;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@PropertySource(
    value = "classpath:aurora-openshift-spring-boot-starter.yml",
    factory = BaseStarterApplicationConfig.YamlPropertyLoaderFactory.class
)
public class BaseStarterApplicationConfig {
    static class YamlPropertyLoaderFactory extends DefaultPropertySourceFactory {
        @Override
        public org.springframework.core.env.PropertySource<?> createPropertySource(
            String name,
            EncodedResource resource
        ) throws IOException {
            return new YamlPropertySourceLoader()
                .load(resource.getResource().getFilename(), resource.getResource())
                .get(0);
        }
    }
}
