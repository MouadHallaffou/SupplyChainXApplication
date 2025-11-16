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
                # Arrêt et suppression du conteneur précédent si existant
                echo "🚀 Arrêt de l'application existante..."
                docker stop supplychainx-app || true
                docker rm supplychainx-app || true

                # Option A: S'assurer que le réseau "sonarnet" (déclaré dans docker-compose.yml) existe.
                # -> C'est la meilleure pratique si MySQL et l'app doivent etre sur le meme réseau.
                echo "🔧 Vérification/creation du réseau sonarnet si nécessaire..."
                docker network inspect sonarnet > /dev/null 2>&1 || docker network create sonarnet

                # Construire l'image (déjà fait dans stage précédent mais on s'assure ici)
                echo "🔨 (Re)construction image si besoin..."
                docker build -t supplychainx-app . || true

                # Déploiement: utiliser le réseau 'sonarnet' pour que le conteneur puisse joindre mysql-db
                echo "🚀 Déploiement de la nouvelle application sur le réseau 'sonarnet'..."
                docker run -d \
                  --name supplychainx-app \
                  --network sonarnet \                       # <- ici : NETWORK corrected to sonarnet
                  -p 8080:8080 \
                  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/supply_chain_db \
                  -e SPRING_DATASOURCE_USERNAME=root \
                  -e SPRING_DATASOURCE_PASSWORD=root \
                  supplychainx-app:latest

                # Attendre un petit peu et lister les conteneurs (contrôle basique)
                echo "📊 Vérification du déploiement..."
                sleep 15
                docker ps --filter name=supplychainx-app
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
            echo "🐳 État des conteneurs:"
            docker-compose ps
            '''
        }
    }
}