#!/usr/bin/env groovy

pipeline {
    agent {
        docker {
            image 'alvrme/alpine-android:android-28'
            args '-v /var/www:/var/www'
        }
    }
    environment {
        FLAVOR = build_flavor(env.BRANCH_NAME)
    }
    stages {
        stage('Build') {
            steps {
                sh "./jenkins/build.sh ${FLAVOR}"
            }
        }
        stage('Test') {
            steps {
                sh "gradle test"
            }
        }
        stage('Delivery') {
            steps {
                //sh "./jenkins/deliver.sh ${FLAVOR} ${env.BRANCH_NAME}"
                archiveArtifacts artifacts: "app/build/outputs/apk/${FLAVOR}/*.apk", fingerprint: true
            }
        }
        stage('Cleanup') {
            steps {
                sh 'gradle clean'
            }
        }
    }
    post {
        failure {
            notifyFailed()
        }
        success {
            notifySuccessed()
        }
    }
}

def build_flavor(branch_name) {
    if (branch_name ==~ /r[.0-9]+/ || branch_name == 'master') {
        return 'release'
    }
    return 'debug'
}

def notifySuccessed() {
}

def notifyFailed() {

}