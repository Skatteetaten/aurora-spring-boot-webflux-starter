#!/usr/bin/env groovy
env.CI = true

def version = 'v7'
fileLoader.withGit('https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git', version) {
    jenkinsfile = fileLoader.load('templates/leveransepakke')
}

def overrides = [
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
