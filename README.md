# SupplyChainX - Système de Gestion Intégrée de la Supply Chain 📦

![Java](https://img.shields.io/badge/Java-17-orange?style=flat&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?style=flat&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat&logo=mysql)
![GraphQL](https://img.shields.io/badge/GraphQL-E10098?style=flat&logo=graphql)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apache-maven)

## 📋 Table des Matières

- [À Propos](#-à-propos)
- [Architecture](#-architecture)
- [Technologies Utilisées](#-technologies-utilisées)
- [Modules Fonctionnels](#-modules-fonctionnels)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Utilisation](#-utilisation)
- [Tests](#-tests)
- [Structure du Projet](#-structure-du-projet)
- [API Documentation](#-api-documentation)
- [Auteur](#-auteur)

---

## 📖 À Propos

**SupplyChainX** est une application monolithique développée avec **Spring Boot** pour gérer l'ensemble de la chaîne d'approvisionnement, depuis l'achat des matières premières jusqu'à la livraison des produits finis aux clients.

### 🎯 Objectifs

- **Automatiser** les processus métier de la supply chain
- **Centraliser** la gestion des données
- **Assurer** une traçabilité complète des flux
- **Optimiser** la planification et l'ordonnancement

---

## 🏗️ Architecture

Le projet suit une **architecture monolithique multi-couches** basée sur le pattern **MVC** :

```
┌─────────────────────────────────────────────┐
│           Controllers (REST/GraphQL)         │
├─────────────────────────────────────────────┤
│              Services (Business Logic)       │
├─────────────────────────────────────────────┤
│         Repositories (Data Access Layer)     │
├─────────────────────────────────────────────┤
│              Database (MySQL)                │
└─────────────────────────────────────────────┘
```

### Organisation en Packages

L'application est organisée en **services séparés par package** :

- 📦 `service_approvisionnement` - Gestion des fournisseurs et matières premières
- 🏭 `service_production` - Gestion de la production et des ordres
- 🚚 `service_livraison` - Gestion des livraisons (**GraphQL**)
- 👥 `service_user` - Gestion des utilisateurs et rôles

---

## 🛠️ Technologies Utilisées

### Backend
- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Data JPA** - Persistence des données
- **Spring Web** - API REST
- **Spring GraphQL** - API GraphQL (Module Livraison)
- **Hibernate** - ORM
- **MySQL 8.0** - Base de données

### Librairies & Outils
- **Lombok** - Réduction du code boilerplate
- **MapStruct 1.5.5** - Mapping DTO ↔ Entity
- **Hibernate Validator** - Validation des données
- **SpringDoc OpenAPI 2.8.13** - Documentation Swagger
- **Spring Boot DevTools** - Hot reload
- **Spring Boot Actuator** - Monitoring

### Tests
- **JUnit 5** - Framework de tests
- **Mockito** - Mock des dépendances
- **Spring Boot Test** - Tests d'intégration

---

## 📦 Modules Fonctionnels

### 🔵 Module 1 : Approvisionnement

#### Gestion des Fournisseurs
- ✅ CRUD complet des fournisseurs
- ✅ Recherche par nom ou code fournisseur
- ✅ Pagination des résultats
- ✅ Suppression conditionnelle (aucune commande active)

#### Gestion des Matières Premières
- ✅ CRUD des matières premières
- ✅ Suivi des stocks avec seuil critique
- ✅ Alertes de stock minimum
- ✅ Pagination et recherche

#### Gestion des Commandes d'Approvisionnement
- ✅ Création et suivi des commandes
- ✅ États : `EN_ATTENTE`, `EN_COURS`, `RECUE`
- ✅ Modification/suppression conditionnelle
- ✅ Association fournisseur ↔ matières premières

---

### 🔵 Module 2 : Production

#### Gestion des Produits Finis
- ✅ CRUD complet des produits
- ✅ Recherche par nom ou référence
- ✅ Gestion des stocks
- ✅ Suppression conditionnelle

#### Gestion des Ordres de Production
- ✅ Création et suivi des ordres
- ✅ États : `EN_ATTENTE`, `EN_PRODUCTION`, `TERMINE`, `BLOQUE`
- ✅ Planification et ordonnancement
- ✅ Gestion des priorités

#### Bill of Materials (BOM)
- ✅ Définition des nomenclatures
- ✅ Vérification de disponibilité des matières
- ✅ Calcul automatique des besoins
- ✅ Estimation du temps de production

---

### 🔵 Module 3 : Livraison & Distribution (**GraphQL**)

> ⚡ **Ce module utilise GraphQL** au lieu de REST

#### Gestion des Clients
- ✅ CRUD complet via GraphQL
- ✅ Gestion des adresses
- ✅ Recherche et pagination
- ✅ Suppression conditionnelle

#### Gestion des Commandes Clients
- ✅ Création et suivi des commandes
- ✅ États : `EN_PREPARATION`, `EN_ROUTE`, `LIVREE`
- ✅ Association client ↔ produits

#### Gestion des Livraisons
- ✅ Planification des livraisons
- ✅ Affectation véhicule/chauffeur
- ✅ Calcul des coûts
- ✅ Suivi en temps réel

---

### 🔵 Module 4 : Gestion des Utilisateurs

#### Rôles Disponibles

**Approvisionnement :**
- `GESTIONNAIRE_APPROVISIONNEMENT`
- `RESPONSABLE_ACHATS`
- `SUPERVISEUR_LOGISTIQUE`

**Production :**
- `CHEF_PRODUCTION`
- `PLANIFICATEUR`
- `SUPERVISEUR_PRODUCTION`

**Livraison :**
- `GESTIONNAIRE_COMMERCIAL`
- `RESPONSABLE_LOGISTIQUE`
- `SUPERVISEUR_LIVRAISONS`

**Administration :**
- `ADMIN` (accès complet)

---

## ✅ Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- ☕ **Java JDK 17+**
- 🗄️ **MySQL 8.0+**
- 📦 **Maven 3.6+**
- 🔧 **Git**
- 💻 **IntelliJ IDEA** (recommandé) ou un IDE Java

---

## 🚀 Installation

### 1️⃣ Cloner le projet

```bash
git clone https://github.com/MouadHallaffou/SupplyChainXApplication.git
cd SupplyChainXApplication
```

### 2️⃣ Créer la base de données

Connectez-vous à MySQL et exécutez :

```sql
CREATE DATABASE supply_chain_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3️⃣ Configurer l'application

Éditez `src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/supply_chain_db
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe
```

### 4️⃣ Compiler le projet

```bash
mvn clean install
```

### 5️⃣ Lancer l'application

```bash
mvn spring-boot:run
```

L'application sera accessible sur : **http://localhost:8080**

---

## ⚙️ Configuration

### Base de Données

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/supply_chain_db
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### GraphQL

```properties
spring.graphql.graphiql.enabled=true
spring.graphql.graphiql.path=/graphiql
spring.graphql.http.path=/graphql
```

### Serveur

```properties
server.port=8080
spring.application.name=SupplyChainX
```

---

## 🎮 Utilisation

### 🔹 API REST (Modules Approvisionnement, Production, Utilisateurs)

#### Endpoints Disponibles

Les fichiers de test HTTP sont disponibles dans le dossier `api/` :

```
api/
├── approvisionnement/
│   ├── fournisseur.http
│   ├── MPremiere.http
│   └── order.http
├── production/
│   ├── BillOfMaterial.http
│   ├── product.http
│   └── productOrder.http
└── user/
    ├── role.http
    └── user.http
```

#### Utilisation avec IntelliJ HTTP Client

1. Ouvrez un fichier `.http` dans IntelliJ
2. Cliquez sur l'icône ▶️ à côté de la requête
3. Visualisez la réponse directement dans l'IDE

**Exemple** (`api/approvisionnement/fournisseur.http`) :

```http
### Créer un fournisseur
POST http://localhost:8080/api/v1/fournisseurs
Content-Type: application/json

{
  "nom": "Fournisseur ABC",
  "code": "FOUR-001",
  "email": "contact@fournisseur-abc.com",
  "telephone": "+212612345678"
}

### Lister tous les fournisseurs
GET http://localhost:8080/api/v1/fournisseurs?page=0&size=10
```

---

### 🔹 API GraphQL (Module Livraison)

#### GraphiQL Interface

Accédez à l'interface interactive GraphiQL :

🔗 **http://localhost:8080/graphiql**

#### Schémas GraphQL

Les schémas sont définis dans `src/main/resources/graphql/` :

- `Client.graphqls` - Types et requêtes clients
- `ClientOrder.graphqls` - Commandes clients
- `Livraison.graphqls` - Livraisons
- `Address.graphqls` - Adresses
- `schema.graphqls` - Schéma principal

#### Exemples de Requêtes

**Créer un client :**

```graphqli
mutation {
  createClient(input: {
    nom: "Entreprise XYZ"
    email: "contact@xyz.com"
    telephone: "+212698765432"
    address: {
      rue: "123 Avenue Mohammed V"
      ville: "Casablanca"
      codePostal: "20000"
      pays: "Maroc"
    }
  }) {
    id
    nom
    email
    createdAt
  }
}
```

**Lister tous les clients :**

```graphqli
query {
  allClients(page: 0, size: 10) {
    content {
      id
      nom
      email
      telephone
      address {
        ville
        pays
      }
    }
    totalElements
    totalPages
  }
}
```

**Créer une livraison :**

```graphqli
mutation {
  createLivraison(input: {
    commandeClientId: 1
    vehicule: "Camion A-123"
    chauffeur: "Ahmed Bennani"
    datePrevue: "2025-12-01"
    cout: 500.0
  }) {
    id
    statut
    datePrevue
    cout
  }
}
```

---

### 📊 Swagger Documentation

Accédez à la documentation Swagger pour les API REST :

🔗 **http://localhost:8080/swagger-ui.html**

---

## 🧪 Tests

Le projet inclut des **tests unitaires** avec **JUnit 5** et **Mockito**.

### Lancer tous les tests

```bash
mvn test
```

### Tests Disponibles

#### Service Approvisionnement
- ✅ `FournisseurServiceImplTest`
- ✅ `MatierePremiereServiceImplTest`
- ✅ `CommandeFournisseurServiceImplTest`

#### Service Production
- ✅ `ProductServiceImplTest`
- ✅ `ProductOrderServiceImplTest`
- ✅ `BillOfMaterialServiceImplTest`

#### Service Livraison
- ✅ `AddressTestImplTest`

#### Service Utilisateur
- ✅ `UserServiceImplTest`
- ✅ `RoleServiceImplTest`

### Exemple de Test Unitaire

```java
@ExtendWith(MockitoExtension.class)
class FournisseurServiceImplTest {
    
    @Mock
    private FournisseurRepository repository;
    
    @InjectMocks
    private FournisseurServiceImpl service;
    
    @Test
    void testCreateFournisseur() {
        // Arrange
        FournisseurDTO dto = new FournisseurDTO();
        dto.setNom("Test Supplier");
        
        // Act & Assert
        assertNotNull(service.create(dto));
    }
}
```

---

## 📁 Structure du Projet

```
SupplyChainX/
│
├── src/main/java/com/supplychainx/
│   ├── SupplyChainXApplication.java          # Classe principale
│   │
│   ├── exception/                             # Exceptions personnalisées
│   │   ├── BusinessException.java
│   │   └── ResourceNotFoundException.java
│   │
│   ├── handler/                               # Gestionnaires d'exceptions
│   │   ├── GlobalExceptionHandler.java       # REST Exception Handler
│   │   └── GraphQLExceptionHandler.java      # GraphQL Exception Handler
│   │
│   ├── util/                                  # Classes utilitaires
│   │   ├── BaseEntity.java                   # Entité de base
│   │   ├── ValidationUtil.java               # Validations
│   │   ├── PasswordUtil.java                 # Gestion mots de passe
│   │   └── AuthUtil.java                     # Authentification
│   │
│   ├── service_approvisionnement/            # 📦 Module Approvisionnement
│   │   ├── controller/                       # REST Controllers
│   │   ├── service/                          # Business Logic
│   │   ├── repository/                       # Data Access
│   │   ├── model/                            # Entities (JPA)
│   │   ├── dto/                              # Data Transfer Objects
│   │   └── mapper/                           # MapStruct Mappers
│   │
│   ├── service_production/                   # 🏭 Module Production
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── model/
│   │   ├── dto/
│   │   └── mapper/
│   │
│   ├── service_livraison/                    # 🚚 Module Livraison (GraphQL)
│   │   ├── controller/                       # GraphQL Controllers
│   │   ├── service/
│   │   ├── repository/
│   │   ├── model/
│   │   ├── dto/
│   │   └── mapper/
│   │
│   └── service_user/                         # 👥 Module Utilisateur
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── model/
│       ├── dto/
│       └── mapper/
│
├── src/main/resources/
│   ├── application.properties                # Configuration
│   └── graphql/                              # Schémas GraphQL
│       ├── schema.graphqls
│       ├── Client.graphqls
│       ├── ClientOrder.graphqls
│       ├── Livraison.graphqls
│       └── Address.graphqls
│
├── src/test/java/                            # Tests unitaires
│   └── com/supplychainx/service/
│       ├── service_approvisionnement/
│       ├── service_production/
│       ├── service_livraison/
│       └── service_user/
│
├── api/                                       # Fichiers de test HTTP
│   ├── approvisionnement/
│   ├── production/
│   ├── livraison/
│   └── user/
│
├── pom.xml                                    # Dépendances Maven
└── README.md                                  # Ce fichier
```

---

## 📚 API Documentation

### REST Endpoints

#### Approvisionnement

| Méthode | Endpoint                    | Description |
|---------|-----------------------------|-------------|
| GET | `/api/v1/fournisseurs`      | Liste tous les fournisseurs |
| GET | `/api/v1/fournisseurs/{id}` | Récupère un fournisseur |
| POST | `/api/v1/fournisseurs`      | Crée un fournisseur |
| PUT | `/api/v1/fournisseurs/{id}` | Modifie un fournisseur |
| DELETE | `/api/v1/fournisseurs/{id}` | Supprime un fournisseur |

#### Production

| Méthode | Endpoint                | Description |
|---------|-------------------------|-------------|
| GET | `/api/v1/products`      | Liste tous les produits |
| GET | `/api/v1/products/{id}` | Récupère un produit |
| POST | `/api/v1/products`      | Crée un produit |
| PUT | `/api/v1/products/{id}` | Modifie un produit |
| DELETE | `/api/v1/products/{id}` | Supprime un produit |

#### Utilisateurs

| Méthode | Endpoint        | Description |
|---------|-----------------|-------------|
| GET | `/api/v1/users` | Liste tous les utilisateurs |
| POST | `/api/v1/users` | Crée un utilisateur |
| GET | `/api/v1/roles` | Liste tous les rôles |

### GraphQL Queries & Mutations

#### Queries

```graphqli
# Clients
allClients(page: Int, size: Int): ClientPage
clientById(id: ID!): Client

# Commandes
allClientOrders(page: Int, size: Int): ClientOrderPage
clientOrderById(id: ID!): ClientOrder

# Livraisons
allLivraisons(page: Int, size: Int): LivraisonPage
livraisonById(id: ID!): Livraison
```

#### Mutations

```graphqli
# Clients
createClient(input: ClientInput!): Client
updateClient(id: ID!, input: ClientInput!): Client
deleteClient(id: ID!): Boolean

# Commandes
createClientOrder(input: ClientOrderInput!): ClientOrder
updateClientOrder(id: ID!, input: ClientOrderInput!): ClientOrder

# Livraisons
createLivraison(input: LivraisonInput!): Livraison
updateStatutLivraison(id: ID!, statut: StatutLivraison!): Livraison
```

---

## 🎯 Règles de Gestion

### Approvisionnement
- ✅ Une matière première peut avoir plusieurs fournisseurs
- ✅ Une commande est associée à un seul fournisseur
- ✅ Suppression fournisseur impossible si commandes actives
- ✅ Alerte automatique sur stock minimum

### Production
- ✅ Consommation automatique des matières selon BOM
- ✅ Production bloquée si matières insuffisantes
- ✅ Suppression produit impossible si ordres associés
- ✅ Gestion des priorités dans l'ordonnancement

### Livraison
- ✅ Un client peut avoir plusieurs commandes
- ✅ Une commande = une seule livraison
- ✅ Livraison possible uniquement si stock disponible
- ✅ Calcul automatique des coûts

### Utilisateurs
- ✅ Un utilisateur = un seul rôle
- ✅ Permissions définies par rôle
- ✅ Hash sécurisé des mots de passe

---

## 🐛 Résolution des Problèmes

### Erreur de connexion MySQL

```
Access denied for user 'root'@'localhost'
```

**Solution :** Vérifiez les credentials dans `application.properties`

### Port 8080 déjà utilisé

```
Port 8080 is already in use
```

**Solution :** Changez le port dans `application.properties` :
```properties
server.port=8081
```

### GraphiQL ne s'affiche pas

**Solution :** Vérifiez que GraphQL est activé :
```properties
spring.graphql.graphiql.enabled=true
```

---

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. Fork le projet
2. Créez une branche (`git checkout -b feature/AmazingFeature`)
3. Commit vos changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

---

## 📝 Licence

Ce projet est développé dans un cadre éducatif.

---

## 👨‍💻 Auteur

**Développé avec ❤️ par [Votre Nom]**

- 📧 Email : votre.email@example.com
- 🔗 LinkedIn : [Votre Profil](https://linkedin.com)
- 🐙 GitHub : [Votre GitHub](https://github.com)

---

## 🙏 Remerciements

- Spring Boot Team
- GraphQL Java Team
- MapStruct Team
- La communauté Open Source

---

<div align="center">

**⭐ Si ce projet vous a été utile, n'hésitez pas à lui donner une étoile ! ⭐**

</div>

