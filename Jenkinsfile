pipeline {
    agent any

    environment {
        DOCKER_BUILDKIT = 1
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    // Use Docker Compose v2 commands, assuming it's installed on the host
                    sh 'docker compose -f docker-compose.yml build'
                }
            }
        }

        stage('Bring Up Services') {
            steps {
                script {
                    // Bringing up the services using docker compose v2
                    sh 'docker compose -f docker-compose.yml up -d'
                }
            }
        }

        stage('Test Services') {
            steps {
                script {
                    // Run tests or health checks on the services
                    sh 'docker compose -f docker-compose.yml ps'
                }
            }
        }

        stage('Teardown') {
            steps {
                script {
                    // Tear down services after testing
                    sh 'docker compose -f docker-compose.yml down'
                }
            }
        }
    }
}
