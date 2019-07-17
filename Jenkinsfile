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
				archiveArtifacts artifacts: 'login/target/*.jar', 'get-issues/target/*.jar', 'changelog/target/*.jar', 'projects/target/*.jar',  'sprints/target/*.jar', 'issues-keys/target/*.jar', 'kanban/target/*.jar', fingerprint: true
			}
		}
		

	}
}
