# Aurora Spring Boot WebFlux Starter

A Spring Boot starter for WebFlux related functionality.
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
spring.zipkin.enabled = true
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
- `Klientid` set from the environment variable AURORA_KLIENTID or uses application name with version as fallback (using the `spring.application.name` property and `APP_VERSION` env). The `User-Agent` header will also be set to the same value.

## Using WebClient with mvc-starter

It is possible to use WebClient (from the webflux-starter) even if the project is based on the mvc-starter.
You can do this by adding both starters to the classpath, the webflux-starter will check if the auto configuration from the mvc-starter is on the classpath.
If it is, it will only enable the WebClient customizer. All other beans are not initialized, and should therefore not create a conflict with the mvc-starter.
Remember that the webclient property must be enabled for the customizer to be included:
```properties
aurora.webflux.header.webclient.interceptor.enabled = true
```

## Project structure

* *aurora-spring-boot-webflux-starter*, the starter code 
* *buildSrc*, sets common versions for the subprojects 
* *testapp-java*, java test application 
* *testapp-kotlin*, kotlin test application

In the aurora-spring-boot-webflux repository there are two test applications, one for java and one for kotlin.
The test applications are organized in separate subprojects, testapp-java and testapp-kotlin. 
These projects are created to test the starter, for example to verify that Korrelasjonsid is added properly and that the correct dependencies are on the classpath.
Both testapps contain a main class that will start a simple application that can be reached on http://localhost:8080