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

        stage('Start Test Database') {
            steps {
                sh '''
                docker run -d --name test-mysql \
                  -e MYSQL_ROOT_PASSWORD=root \
                  -e MYSQL_DATABASE=test_db \
                  -p 3306:3306 \
                  mysql:8.0 \
                  --default-authentication-plugin=mysql_native_password

                # Wait for MySQL to be ready
                sleep 30
                '''
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean verify -Dspring.datasource.url=jdbc:mysql://localhost:3306/test_db'
            }
            post {
                always {
                    sh 'docker stop test-mysql || true'
                    sh 'docker rm test-mysql || true'
                }
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