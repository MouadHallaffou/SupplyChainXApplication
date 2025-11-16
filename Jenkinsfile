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
                sh '''
                echo "🔨 Construction de l'image Docker..."
                docker build -t supplychainx-app .
                echo "✅ Image Docker construite avec succès"
                '''
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                echo "🚀 Déploiement de l'application..."
                docker-compose up -d app

                echo "📊 Vérification des conteneurs..."
                sleep 10
                docker ps --filter "name=supplychain"
                '''
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
        success {
            echo '🎉 PIPELINE RÉUSSIE!'
            archiveArtifacts 'target/*.jar'

            sh '''
            echo "✅ Application déployée avec succès"
            echo "🌐 URL: http://localhost:8080"
            echo "📦 Image: supplychainx-app"
            echo "🐳 Conteneurs en cours:"
            docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
            '''
        }
    }
}