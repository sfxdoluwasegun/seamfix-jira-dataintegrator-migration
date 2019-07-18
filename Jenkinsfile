pipeline {
	agent any
	tools {
		maven 'mvn339'
		jdk 'jdk8'
	}

	stages {
		stage('Build') {
			steps {
				echo 'Running build'
				sh 'mvn clean package -U'
				archiveArtifacts artifacts: 'target/*', fingerprint: true
				}
		}
		

	}
}
