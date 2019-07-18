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
				archiveArtifacts artifacts: 'login/target/*.jar', fingerprint: true
				archiveArtifacts artifacts: 'kanban/target/*.jar', fingerprint: true
				archiveArtifacts artifacts: 'get-issues/target/*.jar', fingerprint: true
				archiveArtifacts artifacts: 'changelog/target/*.jar', fingerprint: true
				archiveArtifacts artifacts: 'projects/target/*.jar', fingerprint: true
				archiveArtifacts artifacts: 'sprints/target/*.jar', fingerprint: true
				archiveArtifacts artifacts: 'issue-keys/target/*.jar', fingerprint: true
				
				}
		}
		

	}
}
