pipeline {
    agent any

    environment {
        SONARQUBE = credentials('sonar-token')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'dev', url: 'https://github.com/ton-user/ton-repo.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh '''
                    mvn sonar:sonar \
                      -Dsonar.projectKey=SupplyChainX \
                      -Dsonar.host.url=http://sonarqube:9000 \
                      -Dsonar.login=${SONARQUBE}
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t supplychainx-app .'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker compose up -d'
            }
        }
    }
}
