pipeline {
    agent any
    tools{
        maven 'Maven-3.9.9'
        jdk 'JDK-17'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/MouadHallaffou/SupplyChainX-CI-CD.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh '''
                mvn clean test -Dspring.datasource.url=jdbc:h2:mem:testdb \
                               -Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect \
                               -Dmaven.test.failure.ignore=true
                '''
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh '''
                    if command -v docker &> /dev/null; then
                        echo "Building Docker image..."
                        docker build -t supplychainx-app .
                    else
                        echo "Docker n'est pas disponible, skip du build Docker"
                    fi
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh '''
                    if command -v docker &> /dev/null && [ -f "docker-compose.yml" ]; then
                        echo "Deploying with Docker Compose..."
                        docker compose up -d
                    else
                        echo "Docker ou docker-compose.yml non disponible, skip du déploiement"
                    fi
                    '''
                }
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
        success {
            archiveArtifacts 'target/*.jar'
        }
    }
}