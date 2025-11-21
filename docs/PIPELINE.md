# 🔄 Pipeline CI/CD - Documentation Détaillée

## Vue d'Ensemble du Pipeline Jenkins

Le pipeline CI/CD de SupplyChainX est composé de **6 étapes principales** qui automatisent le processus de build, test, package, containerisation et déploiement.

## 📊 Diagramme du Pipeline

```
┌─────────────────────────────────────────────────────────────────────┐
│                        JENKINS PIPELINE                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  1. Checkout SCM                                                     │
│      ↓                                                               │
│  2. Build & Test (Maven + JUnit + JaCoCo)                           │
│      ↓                                                               │
│  3. Package (JAR)                                                    │
│      ↓                                                               │
│  4. Build Docker Image                                               │
│      ↓                                                               │
│  5. Push to DockerHub                                                │
│      ↓                                                               │
│  6. Deploy (Docker Container)                                        │
│                                                                       │
│  Post Actions:                                                       │
│  - Archive JUnit Reports                                             │
│  - Archive JAR Artifacts                                             │
│  - Notifications                                                     │
│                                                                       │
└─────────────────────────────────────────────────────────────────────┘
```

## 🔍 Détails des Étapes

### Stage 1: Checkout
- **Durée moyenne**: 2-3 secondes
- **Action**: Clone le repository depuis GitHub (branche master)
- **Output**: Code source prêt pour le build

### Stage 2: Build & Test
- **Durée moyenne**: 1-2 minutes
- **Actions**:
    - Compilation du code Java
    - Exécution de 177 tests (unitaires + intégration)
    - Génération du rapport JaCoCo
- **Base de données**: H2 en mémoire pour les tests
- **Output**: Classes compilées + Rapports de tests

### Stage 3: Package
- **Durée moyenne**: 30-45 secondes
- **Action**: Création du fichier JAR exécutable
- **Output**: `SupplyChainX-0.0.1-SNAPSHOT.jar`

### Stage 4: Build Docker Image
- **Durée moyenne**: 1-2 minutes
- **Action**: Construction de l'image Docker
- **Image**: `supplychainx-app:latest`
- **Base**: OpenJDK 17

### Stage 5: Push to DockerHub
- **Durée moyenne**: 20-30 secondes
- **Action**: Upload de l'image vers DockerHub
- **Registry**: `mouadhallaffou/supplychainx-app:latest`

### Stage 6: Deploy
- **Durée moyenne**: 30-40 secondes
- **Actions**:
    - Arrêt du conteneur existant
    - Démarrage du nouveau conteneur
    - Configuration du réseau Docker
    - Vérification du déploiement
- **Port**: 8080
- **Network**: supplychain-network

## 📈 Métriques du Pipeline

| Métrique | Valeur |
|----------|--------|
| **Durée totale** | ~3-4 minutes |
| **Taux de succès** | 95%+ |
| **Tests exécutés** | 177 |
| **Couverture de code** | 94% |
| **Taille de l'image Docker** | ~300 MB |

## 🔔 Actions Post-Pipeline

### En cas de succès ✅
- Archive des artifacts (JAR)
- Archive des rapports de tests (JUnit XML)
- Message de confirmation
- Notification (si configurée)

### En cas d'échec ❌
- Archive des rapports de tests
- Logs détaillés
- Notification d'erreur (si configurée)

## 🛠️ Configuration Requise

### Outils Jenkins
- Maven 3.9.9
- JDK 17
- Docker
- Git

### Credentials
- `dockerhub-token`: Token d'authentification DockerHub

### Plugins Jenkins
- Pipeline
- Git
- Maven Integration
- Docker Pipeline
- JUnit

## 📊 Rapports Générés

1. **JUnit Test Reports**: `target/surefire-reports/*.xml`
2. **JaCoCo Coverage**: `target/site/jacoco/index.html`
3. **JAR Artifact**: `target/SupplyChainX-0.0.1-SNAPSHOT.jar`
4. **Build Logs**: Disponibles dans Jenkins

## 🚀 Exécution Manuelle

Pour exécuter le pipeline manuellement:

1. Se connecter à Jenkins: `http://localhost:8080`
2. Sélectionner le job "SupplyChainX"
3. Cliquer sur "Build Now"
4. Suivre la progression dans la vue Pipeline

## 🔧 Dépannage

### Problème: Tests qui échouent
```bash
# Vérifier les logs des tests
cat target/surefire-reports/*.txt

# Exécuter les tests localement
mvn clean test
```

### Problème: Docker build échoue
```bash
# Vérifier les logs Docker
docker logs supplychainx-app

# Reconstruire manuellement
docker build -t supplychainx-app .
```

### Problème: Déploiement échoue
```bash
# Vérifier les conteneurs en cours
docker ps -a

# Vérifier les logs
docker logs supplychainx-app

# Vérifier le réseau
docker network inspect supplychain-network
```

## 📸 Captures d'Écran

Les captures d'écran des exécutions du pipeline sont disponibles dans le README principal.