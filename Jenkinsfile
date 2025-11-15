pipeline {
    agent any
    tools{
        maven 'Maven-3.9.9'
        jdk 'JDK-17'
    }
    environment {
        SONARQUBE = credentials('sonar-token')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/MouadHallaffou/SupplyChainX-CI-CD.git'
            }
        }

        stage('Build & Test') {
            steps {
                // Use H2 database for tests instead of MySQL
                sh 'mvn clean verify -Dspring.datasource.url=jdbc:h2:mem:testdb -Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect'
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
                script {
                    // Only build Docker image if Docker is available
                    try {
                        sh 'docker --version'
                        sh 'docker build -t supplychainx-app .'
                    } catch (Exception e) {
                        echo "Docker not available, skipping Docker build"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    try {
                        sh 'docker compose up -d'
                    } catch (Exception e) {
                        echo "Docker not available, skipping deployment"
                    }
                }
            }
        }
    }
}