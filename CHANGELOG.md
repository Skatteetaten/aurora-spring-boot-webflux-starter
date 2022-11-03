# Change Log

Alle viktige endringer i dette prosjektet vil bli dokumentert i denne filen.

Formatet i denne filen er basert på [Keep a Changelog](http://keepachangelog.com/)
og prosjektet følger [Semantic Versioning](http://semver.org/).

## [1.5.4] - 2022-11-03
- Oppdatert til spring boot 2.7.5
- Oppdatert til aurora-gradle-plugin 4.5.9, aurora-spring-boot-base-starter 1.4.4

## [1.5.3] - 2022-10-13
- Feilhåndtering på webclient sender, for å unngå stack traces når vi ikke får sendt trace data

## [1.5.2] - 2022-09-20
- Oppdatert aurora-gradle-plugin og base-starter versjon

## [1.5.1] - 2022-08-22
- Støtter å bruke WebClient (AuroraWebClientCustomizer) når mvc-starter er på classpath

## [1.5.0] - 2022-08-11
- Oppdatert til spring boot 2.7.2
- Oppdatert til aurora-gradle-plugin 4.5.1
- Fikset feil som gjorde at kotlin stdlib kom med selv i java prosjekter

## [1.4.14] - 2022-07-07
- Kotlin skal kun være tilgjengelig på test scope

## [1.4.13] - 2022-06-22
- Fikset bug som gjorde at zipkin requests ble med på trace

## [1.4.10] - 2022-06-15
- Sette trace tag prefix til `skatteetaten`
- Generelle tagger er nå satt med AuroraSpanHandler

## [1.4.9] - 2022-06-10
- Oppdatert til å bruke spring properties istedet for environment variables

## [1.4.6] - 2022-06-09
- Setter X-Scope-OrgID header til affiliation for zipkin requests

## [1.4.5] - 2022-06-03
- Lagt til støtte for basic auth header på zipkin requests

## [1.4.3] - 2022-05-20

### Changed

- Oppgraderte versjon av aurora-spring-boot-base-starter til versjon 1.3.12.
- Oppgraderte versjon av aurora-gradle-plugin til versjon 4.4.22 (som innebærer SpringBoot til versjon 2.6.8.)

## [1.4.1] - 2022-05-04

### Changed

- Oppgraderte til SpringBoot til versjon 2.6.7.
- Oppgraderte versjon av aurora-spring-boot-base-starter til versjon 1.3.10.
- Oppgraderte versjon av spring-cloud-dependencies tilm versjon 2021.0.2

## [1.4.0] - 2022-04-05

### Added

- La til støtte for bruk av AURORA_KLIENTID miljøvariabelen i WebClientCustomizer.

## [1.3.6] - 2022-04-01

### Changed

- Oppgraderte til SpringBoot versjon 2.6.6. Oppgraderte versjon av aurora-spring-boot-base-starter versjon 1.3.9.

## [1.3.5] - 2022-03-29

### Changed

- Oppgraderte til SpringBoot versjon 2.6.5. Oppgraderte versjon av aurora-spring-boot-base-starter versjon 1.3.7. Slettet
ZipkinIntegrationTest, slo av javadoc check, oppgraderte maven-dependency-plugin.

### Added

- CHANGELOG.md for å dokumentere endringer.
