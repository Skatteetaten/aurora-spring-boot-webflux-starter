#!/usr/bin/env groovy
env.CI = true

def config = [
    scriptVersion  : 'v7',
    iq: false,
    credentialsId: 'github',
    deployTo: 'maven-central',
    openShiftBuild: false,
    checkstyle : false,
    javaVersion : 11,
    docs: false,
    sonarQube: false,
    pipelineScript : 'https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git',
    pomPath: 'webflux-starter/pom.xml',
    compileProperties: '-pl webflux-starter',
    versionStrategy : [
        [branch: 'master', versionHint: '1']
    ]
]

fileLoader.withGit(config.pipelineScript, config.scriptVersion) {
  jenkinsfile = fileLoader.load('templates/leveransepakke')
}

jenkinsfile.run(config.scriptVersion, config)
