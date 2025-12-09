pipeline {
    agent any

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker compose -f docker-compose.yml build'
            }
        }

        stage('Bring Up Services') {
            steps {
                sh 'docker compose -f docker-compose.yml up -d'
            }
        }

        stage('Test Services') {
            steps {
                sh 'docker compose -f docker-compose.yml ps'
            }
        }

        stage('Teardown') {
            steps {
                sh 'docker compose -f docker-compose.yml down'
            }
        }
    }
}
