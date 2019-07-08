#!/usr/bin/env groovy

/**
Jenkinsfile for deploying build projects to AWS S3
*/
import java.util.Date
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

def repoName = "jira-webclient"
def displayName = env.JOB_NAME
def branchName = env.BRANCH_NAME 
def buildString = ''
def start = new Date()
def s3BucketName = "staging.jira-client.terragonbase.com"
def prodS3BucketName = "jiraclient.seamfix.com"
def folderName = "dist/jira-web-client"
def err = null

def isMaster = env.BRANCH_NAME == 'snapshot'

String jobInfoShort = "${env.JOB_NAME} ${env.BUILD_DISPLAY_NAME}"
String jobInfo = "${env.JOB_NAME} ${env.BUILD_DISPLAY_NAME} \n${env.BUILD_URL}"
String buildStatus
String timeSpent

currentBuild.result = "SUCCESS"

try {
    node {
        def buildNumber = env.BUILD_NUMBER
        def workspace = env.WORKSPACE
        def buildUrl = env.BUILD_URL

        env.NODEJS_HOME = "${tool 'Node 10.8.0'}"
        env.PATH = "${env.NODEJS_HOME}/bin:${env.PATH}"

        echo "workspace directory is $workspace"
        echo "build URL is $buildUrl"
        echo "branch name id $branchName"
        echo "build Number is $buildNumber"
        echo "PATH is $env.PATH"

        stage ("Clean Workspace"){
            deleteDir()            
        }

        stage ('Checkout') {
            checkout scm
        }

        if(isMaster){

            stage ("Installing and Building Angular Dependencies"){
                sh '''
                    cat package.json
                    npm set progress=false && npm config set depth 0 && npm cache clean --force
                    npm install @angular/core
                    npm install -g @angular/cli
                    npm install 
                    '''      
            }

            if(isMaster){
                sh "ng build  --prod --aot=false --build-optimizer=false"
                pushToS3(folderName, prodS3BucketName)
            }

            if(isStaging){
                sh "ng build --configuration=staging --aot=false --build-optimizer=false"
                pushToS3(folderName, s3BucketName)
            }
        }
        
    }
} catch (caughtError) {
    err = caughtError
    currentBuild.result = "FAILURE"
} finally {
    
    timeSpent = "\nTime spent: ${timeDiff(start)}"

    if (err) {
        googlechatnotification (url: 'https://chat.googleapis.com/v1/spaces/AAAAuvIDdoQ/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=I7VzE4LUOmrrhTt_jmDoiAJwbAI0z70DqfiJ6eOBvUY%3D', message: '*Error* in build process for `${repoName}` ...'+ err)
        //slackSend (color: 'danger', message: err)
    } else {
        googlechatnotification (url: 'https://chat.googleapis.com/v1/spaces/AAAAuvIDdoQ/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=I7VzE4LUOmrrhTt_jmDoiAJwbAI0z70DqfiJ6eOBvUY%3D', message: '*Success* in build process for `${repoName} ${buildStatus}: ${jobInfo} ${timeSpent}` ...')
        //slackSend (color: 'good', message: "${buildStatus}: ${jobInfo} ${timeSpent}")        
    }
}

def timeDiff(st) {
    def delta = (new Date()).getTime() - st.getTime()
    def seconds = delta.intdiv(1000) % 60
    def minutes = delta.intdiv(60 * 1000) % 60

    return "${minutes} min ${seconds} sec"
}

def pushToS3(folderName, bucketName){
    try {
        def response = sh(returnStdout: true, script: "aws s3 cp $WORKSPACE/${folderName} s3://${bucketName}/ --recursive --include '*'")
        googlechatnotification (url: 'https://chat.googleapis.com/v1/spaces/AAAAuvIDdoQ/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=I7VzE4LUOmrrhTt_jmDoiAJwbAI0z70DqfiJ6eOBvUY%3D', message: '*Nice!.* Artifact published to S3 @ ${bucketName}')
        //slackSend (color: 'good', message: ":fire: Nice!. Artifact published to S3 @ ${bucketName} ")
    } catch (sendErr) {
        googlechatnotification (url: 'https://chat.googleapis.com/v1/spaces/AAAAuvIDdoQ/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=I7VzE4LUOmrrhTt_jmDoiAJwbAI0z70DqfiJ6eOBvUY%3D', message: '*Build Failed!* Artifact published to S3 @ ${bucketName}')
        //slackSend (color: 'danger', message: ":disappointed: ${sendErr}" )
    }
}