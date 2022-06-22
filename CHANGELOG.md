# Change Log

Alle viktige endringer i dette prosjektet vil bli dokumentert i denne filen.

Formatet i denne filen er basert på [Keep a Changelog](http://keepachangelog.com/)
og prosjektet følger [Semantic Versioning](http://semver.org/).

## [1.4.12] - 2022-06-22
- Fixed bug where trace data was included in zipkin requests

## [1.4.10] - 2022-06-15
- Set trace tag prefix to `skatteetaten`
- General tags are now set with AuroraSpanHandler

## [1.4.9] - 2022-06-10
- Updated to use spring properties instead of environment variables directly

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
