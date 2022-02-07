# Aurora Spring Boot WebFlux Starter

A Spring Boot (2.6.3) starter for WebFlux related functionality.
This starter is has a dependency on the [base-starter](https://github.com/Skatteetaten/aurora-spring-boot-base-starter).

## How to use
Include the starter as a dependency

```xml
<dependency>
  <groupId>no.skatteetaten.aurora.springboot</groupId>
  <artifactId>aurora-spring-boot-webflux-starter</artifactId>
  <version>${aurora.starters.version}</version>
</dependency>
```

## Features

### Register the Aurora Header Web Filter

The starter will register the Aurora Header Web Filter. The registration can be disabled with the property
```properties
aurora.webflux.header.filter.enabled = false
```

[Spring Sleuth](https://spring.io/projects/spring-cloud-sleuth) is included by the base starter.
It is a distributed tracing solution for Spring Boot apps. Spring Sleuth will generate its own IDs, however it can be useful to see how these IDs related to the `Korrelasjonsid` header.

By enabling the filter `Korrelasjonsid` set will be included in the information sent to Zipkin as a tag.
If `Korrelasjonsid` is not set, this tag will simply be skipped.

`Korrelasjonsid`, `Meldingsid` and `Klientid` is set on MDC if they are present in the incoming request.

Spring Sleuth is by default disabled for local development and enabled in OpenShift.
You can override this by setting the following property:

```properties
spring.zipkin.enabled = false
```

### WebClient interceptor

The WebClient interceptor will add the `Korrelasjonsid`, `Meldingsid` and `Klientid` headers to requests sent from the WebClient instance.
To use this functionality enabled it using the property, as shown below. It is disabled by default.
Inject it as a normal Spring bean using the `WebClient.Builder`, where you can also add you own customization.

```properties
aurora.webflux.header.webclient.interceptor.enabled = true
```

The headers set are based on these values:
- `Korrelasjonsid` taken from the incoming Request header. If not found, it will generate a new ID.
- `Meldingsid` will always generate a new ID.
- `Klientid` set from the application name (using the `spring.application.name` property). The `User-Agent` header will also be set to the same value.
