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
                mvn clean test -Dspring.datasource.url=jdbc:h2:mem:testdb -Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect -Dmaven.test.failure.ignore=true
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
                sh '''
                docker build -t supplychainx-app .
                '''
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh '''
                    docker network ls | grep supplychain-network || docker network create supplychain-network
                    '''

                    def mysqlRunning = sh(
                        script: 'docker ps --filter name=supplychain-mysql --format "{{.Names}}"',
                        returnStdout: true
                    ).trim()

                    if (!mysqlRunning) {
                        sh '''
                        docker run -d --name supplychain-mysql --network supplychain-network -p 3307:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=supply_chain_db -v mysql_data:/var/lib/mysql mysql:8.0 --default-authentication-plugin=mysql_native_password
                        '''
                        sleep 30
                    }

                    sh '''
                    docker stop supplychainx-app || true
                    docker rm supplychainx-app || true

                    docker run -d --name supplychainx-app --network supplychain-network -p 8080:8080 -e SPRING_DATASOURCE_URL="jdbc:mysql://supplychain-mysql:3306/supply_chain_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&autoReconnect=true" -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=root -e SPRING_JPA_HIBERNATE_DDL_AUTO=update supplychainx-app:latest

                    sleep 20
                    docker ps --format "table {{.Names}}\\t{{.Status}}\\t{{.Ports}}"
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
            echo '🎉 PIPELINE RÉUSSIE!'
        }
    }
}