#!/usr/bin/env groovy
env.CI = true

def version = 'v7'
fileLoader.withGit('https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git', version) {
    jenkinsfile = fileLoader.load('templates/leveransepakke')
}

def overrides = [
    credentialsId: 'github',
    javaVersion: "11",
    docs: false,
    sonarQube: false,
    openShiftBuild: false,
    versionStrategy: [
            [ branch: 'master', versionHint: '1']
    ],
    iqOrganizationName: "Team AOS",
    compilePropertiesIq: "-x test",
    chatRoom: "#aos-notifications",
    pomPath: 'webflux-starter/pom.xml',
    compileProperties: '-pl webflux-starter',
    deployProperties: '-pl webflux-starter ',
]

jenkinsfile.run(version, overrides)
