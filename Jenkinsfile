#!/usr/bin/env groovy
def config = [
    scriptVersion  : 'v7',
    credentialsId: 'github',
    javaVersion: "11",
    jacoco: false,
    docs: false,
    sonarQube: false,
    openShiftBuild: false,
    versionStrategy: [
            [ branch: 'master', versionHint: '1']
    ],
    iqOrganizationName: "Team AOS",
    compilePropertiesIq: "-x test",
    chatRoom: "#aos-notifications",
    compileGoal: ':aurora-spring-boot-webflux-starter:build',
    deployGoal: ':aurora-spring-boot-webflux-starter:upload -x test',
]

fileLoader.withGit(config.pipelineScript, config.scriptVersion) {
  jenkinsfile = fileLoader.load('templates/leveransepakke')
}
jenkinsfile.gradle(config.scriptVersion, config)
