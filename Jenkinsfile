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
                // Build and run tests, but don't fail the build on test failures
                sh '''
                mvn clean test -Dspring.datasource.url=jdbc:h2:mem:testdb \
                               -Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect \
                               -Dmaven.test.failure.ignore=true
                '''
            }
        }

        stage('Package') {
            steps {
                // Package without running tests again
                sh 'mvn package -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh '''
                    mvn sonar:sonar \
                      -Dsonar.projectKey=SupplyChainX \
                      -Dsonar.host.url=http://sonarqube:9000 \
                      -Dsonar.login=${SONARQUBE} \
                      -Dsonar.coverage.exclusions=**/test/** \
                      -Dsonar.test.failure.ignore=true
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
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

    post {
        always {
            // Archive test results even if some tests failed
            junit 'target/surefire-reports/*.xml'
        }
    }
}