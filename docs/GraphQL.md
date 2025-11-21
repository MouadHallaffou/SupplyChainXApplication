# 🚀 Guide GraphQL - Module Livraison

## 📖 Introduction

Le module **Livraison** de SupplyChainX utilise **GraphQL** au lieu de REST pour offrir une API plus flexible et performante. GraphQL permet aux clients de demander exactement les données dont ils ont besoin, ni plus ni moins.

## 🌐 Accès à l'API GraphQL

### Interface GraphiQL
GraphiQL est une interface interactive pour explorer et tester l'API GraphQL :

- **URL** : http://localhost:8080/graphiql?path=/graphql
- **Fonctionnalités** :
  - 📝 Éditeur de requêtes avec auto-complétion
  - 📚 Documentation automatique du schéma
  - 🔍 Explorateur de types et de champs
  - 📊 Visualisation des résultats en temps réel

### Endpoint API
Pour les intégrations programmatiques :
- **URL** : http://localhost:8080/graphql
- **Méthode** : POST
- **Content-Type** : application/json

## 📋 Schémas Disponibles

Les schémas GraphQL sont définis dans `src/main/resources/graphql/` :

```
graphql/
├── Client.graphqls       # Types et opérations clients
├── ClientOrder.graphqls  # Types et opérations commandes clients
├── Livraison.graphqls    # Types et opérations livraisons
├── Address.graphqls      # Types et opérations adresses
└── schema.graphqls       # Schéma principal
```

## 🎯 Opérations Disponibles

### 👥 Gestion des Clients

#### Query : Lister tous les clients (avec pagination)

```graphql
query getAllClients {
  getAllClients(page: 0, size: 10) {
    totalElements
    totalPages
    number
    size
    content {          
      clientId
      name
      email
      phoneNumber
    }
  }
}
```

**Paramètres :**
- `page` : Numéro de la page (commence à 0)
- `size` : Nombre d'éléments par page

**Réponse :**
- `totalElements` : Nombre total de clients
- `totalPages` : Nombre total de pages
- `number` : Numéro de la page actuelle
- `size` : Taille de la page
- `content` : Liste des clients

#### Mutation : Créer un client

```graphql
mutation CreateClient {
  createClient(input: {
    name: "Entreprise ABC"
    email: "contact@abc.com"
    phoneNumber: "+212612345678"
  }) {
    clientId
    name
    email
    phoneNumber
  }
}
```

#### Query : Obtenir un client par ID

```graphql
query GetClientById {
  getClientById(clientId: 1) {
    clientId
    name
    email
    phoneNumber
    addresses {
      addressId
      street
      city
      country
    }
  }
}
```

### 📍 Gestion des Adresses

#### Mutation : Créer une adresse

```graphql
mutation CreateAdress {
  createAddress(input: {
    city: "Casablanca"
    state: "Grand Casablanca"
    street: "123 Boulevard Mohammed V"
    country: "Maroc"
    zipCode: "20000"
    clientId: 3
  }) {
    addressId
    street
    city
    state
    zipCode
    country
    client {
      name
      email
    }
  }
}
```

**Champs obligatoires :**
- `city` : Ville
- `street` : Rue/adresse
- `country` : Pays
- `zipCode` : Code postal
- `clientId` : ID du client associé

**Champs optionnels :**
- `state` : État/région

### 📦 Gestion des Commandes Clients

#### Query : Lister toutes les commandes

```graphql
query GetAllclientorders {
  getAllClientOrders(page: 0, size: 10) {
    content {
      orderId
      orderNumber
      status
      totalAmount
      createdAt
      updatedAt
      client {
        name
        phoneNumber
        email
      }
      items {
        productId
        productName
        quantity
        unitPrice
      }
    }
    totalPages
    totalElements
  }
}
```

**Statuts possibles :**
- `EN_PREPARATION` : Commande en cours de préparation
- `EN_COURS` : Commande en cours de traitement
- `LIVREE` : Commande livrée
- `ANNULEE` : Commande annulée

#### Mutation : Créer une commande

```graphql
mutation CreateClientOrder {
  createClientOrder(input: {
    clientId: 1
    items: [
      { productId: 1, quantity: 10 }
      { productId: 2, quantity: 5 }
    ]
  }) {
    orderId
    orderNumber
    totalAmount
    status
    items {
      productName
      quantity
      unitPrice
    }
  }
}
```

#### Mutation : Modifier une commande

```graphql
mutation UpdateClientOrder {
  updateClientOrder(
    orderId: 8
    input: {
      clientId: 4
      items: [
        { productId: 1, quantity: 40 }
        { productId: 2, quantity: 30 }
        { productId: 3, quantity: 20 }
      ]
    }
  ) {
    orderId
    orderNumber
    totalAmount
    status
    items {
      productName
      quantity
      unitPrice
    }
  }
}
```

**Note :** La modification d'une commande n'est possible que si son statut est `EN_PREPARATION`.

### 🚚 Gestion des Livraisons

#### Mutation : Créer une livraison

```graphql
mutation CreateLivraison {
  createLivraison(input: {
    clientOrderId: 6
    cost: 100.00
    deliveryDate: "2025-11-10T10:10:22"
    vehicule: "Van Mercedes Sprinter"
    driverName: "Ahmed Bennani"
    status: EN_COURS
  }) {
    livraisonId
    clientOrderId
    cost
    deliveryDate
    driverName
    vehicule
    status
    order {
      orderNumber
      client {
        name
        phoneNumber
      }
    }
  }
}
```

**Champs obligatoires :**
- `clientOrderId` : ID de la commande client
- `deliveryDate` : Date de livraison prévue (format ISO 8601)
- `vehicule` : Véhicule utilisé
- `driverName` : Nom du chauffeur
- `status` : Statut de la livraison

**Champs optionnels :**
- `cost` : Coût de la livraison

**Statuts de livraison :**
- `EN_ATTENTE` : En attente d'affectation
- `EN_COURS` : En cours de livraison
- `LIVREE` : Livraison effectuée
- `RETOURNEE` : Colis retourné

#### Query : Obtenir les détails d'une livraison

```graphql
query GetLivraisonById {
  getLivraisonById(livraisonId: 1) {
    livraisonId
    cost
    deliveryDate
    driverName
    vehicule
    status
    order {
      orderNumber
      totalAmount
      client {
        name
        email
        phoneNumber
        addresses {
          street
          city
          zipCode
          country
        }
      }
      items {
        productName
        quantity
      }
    }
  }
}
```

#### Mutation : Mettre à jour le statut d'une livraison

```graphql
mutation UpdateLivraisonStatus {
  updateLivraisonStatus(
    livraisonId: 1
    status: LIVREE
  ) {
    livraisonId
    status
    deliveryDate
  }
}
```

## 🔧 Utilisation Programmatique

### Exemple avec cURL

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query { getAllClients(page: 0, size: 10) { content { clientId name email } } }"
  }'
```

### Exemple avec JavaScript (Fetch API)

```javascript
const query = `
  query getAllClients {
    getAllClients(page: 0, size: 10) {
      content {
        clientId
        name
        email
        phoneNumber
      }
    }
  }
`;

fetch('http://localhost:8080/graphql', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({ query })
})
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

### Exemple avec Java (Spring WebClient)

```java
import org.springframework.web.reactive.function.client.WebClient;

WebClient client = WebClient.create("http://localhost:8080/graphql");

String query = """
    query {
        getAllClients(page: 0, size: 10) {
            content {
                clientId
                name
                email
            }
        }
    }
    """;

String response = client.post()
    .bodyValue(Map.of("query", query))
    .retrieve()
    .bodyToMono(String.class)
    .block();
```

## 📊 Requêtes Complexes

### Requête avec variables

```graphql
query GetClientOrders($clientId: ID!, $page: Int!, $size: Int!) {
  getClientById(clientId: $clientId) {
    name
    email
    orders(page: $page, size: $size) {
      content {
        orderId
        orderNumber
        status
        totalAmount
        items {
          productName
          quantity
        }
      }
    }
  }
}
```

**Variables :**
```json
{
  "clientId": "1",
  "page": 0,
  "size": 10
}
```

### Requête avec fragments

```graphql
fragment ClientInfo on Client {
  clientId
  name
  email
  phoneNumber
}

fragment OrderInfo on ClientOrder {
  orderId
  orderNumber
  status
  totalAmount
}

query GetClientWithOrders {
  getClientById(clientId: 1) {
    ...ClientInfo
    orders {
      content {
        ...OrderInfo
      }
    }
  }
}
```

## 🎯 Avantages de GraphQL

### 1. Requêtes Précises
Récupérez uniquement les données dont vous avez besoin :

```graphql
# Seulement le nom et l'email
query {
  getAllClients(page: 0, size: 10) {
    content {
      name
      email
    }
  }
}
```

### 2. Pas de Sur-récupération (Over-fetching)
Contrairement à REST où un endpoint retourne tous les champs, GraphQL permet de sélectionner précisément les données.

### 3. Pas de Sous-récupération (Under-fetching)
Une seule requête pour récupérer des données liées :

```graphql
query {
  getClientById(clientId: 1) {
    name
    email
    addresses {
      city
      country
    }
    orders {
      content {
        orderNumber
        items {
          productName
          quantity
        }
      }
    }
  }
}
```

### 4. Introspection
Le schéma est auto-documenté et peut être interrogé :

```graphql
query {
  __schema {
    types {
      name
      description
    }
  }
}
```

## 🔍 Debugging & Erreurs

### Erreurs Communes

#### 1. Validation Error
```json
{
  "errors": [
    {
      "message": "Validation error",
      "path": ["createClient"],
      "extensions": {
        "classification": "ValidationError"
      }
    }
  ]
}
```

**Solution :** Vérifiez que tous les champs obligatoires sont fournis et valides.

#### 2. Not Found
```json
{
  "errors": [
    {
      "message": "Client not found with id: 999",
      "path": ["getClientById"]
    }
  ]
}
```

**Solution :** Vérifiez que l'ID existe dans la base de données.

## 📚 Ressources

- **Documentation GraphQL officielle** : https://graphql.org/
- **Spring for GraphQL** : https://spring.io/projects/spring-graphql
- **GraphiQL** : Interface de test intégrée
- **Fichier d'exemples** : `api/livraison/description.md`

## 🤝 Support

Pour plus d'informations ou en cas de problème :
- Consultez la documentation complète dans `README.md`
- Ouvrez une issue sur GitHub
- Contactez l'équipe de développement

---

**SupplyChainX** - Gestion Intégrée de la Supply Chain avec GraphQL 🚀

