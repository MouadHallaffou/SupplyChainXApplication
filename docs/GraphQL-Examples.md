# 🎨 GraphQL - Exemples Visuels

## 📱 Interface GraphiQL

GraphiQL est une interface web interactive qui vous permet de :
- ✍️ Écrire et exécuter des requêtes GraphQL
- 📖 Explorer la documentation du schéma
- 🔍 Utiliser l'auto-complétion intelligente
- 📊 Visualiser les résultats en temps réel

### Accès
🔗 **http://localhost:8080/graphiql?path=/graphql**

---

## 🎯 Cas d'Usage Pratiques

### Scénario 1 : Création d'un nouveau client

**Étape 1 - Créer le client**
```graphql
mutation CreateClient {
  createClient(input: {
    name: "Café Central"
    email: "contact@cafecentral.ma"
    phoneNumber: "+212522334455"
  }) {
    clientId
    name
    email
    phoneNumber
  }
}
```

**Résultat :**
```json
{
  "data": {
    "createClient": {
      "clientId": "15",
      "name": "Café Central",
      "email": "contact@cafecentral.ma",
      "phoneNumber": "+212522334455"
    }
  }
}
```

**Étape 2 - Ajouter l'adresse**
```graphql
mutation CreateAddress {
  createAddress(input: {
    street: "45 Rue de la Liberté"
    city: "Casablanca"
    state: "Grand Casablanca"
    zipCode: "20250"
    country: "Maroc"
    clientId: 15
  }) {
    addressId
    street
    city
    country
    client {
      name
    }
  }
}
```

**Résultat :**
```json
{
  "data": {
    "createAddress": {
      "addressId": "42",
      "street": "45 Rue de la Liberté",
      "city": "Casablanca",
      "country": "Maroc",
      "client": {
        "name": "Café Central"
      }
    }
  }
}
```

---

### Scénario 2 : Passage d'une commande complète

**Étape 1 - Consulter les produits disponibles** (via REST)
```http
GET http://localhost:8080/api/v1/products
```

**Étape 2 - Créer la commande avec GraphQL**
```graphql
mutation CreateOrder {
  createClientOrder(input: {
    clientId: 15
    items: [
      { productId: 1, quantity: 50 }   # 50 cafés arabica
      { productId: 3, quantity: 30 }   # 30 thés verts
      { productId: 7, quantity: 20 }   # 20 chocolats chauds
    ]
  }) {
    orderId
    orderNumber
    totalAmount
    status
    createdAt
    client {
      name
      email
    }
    items {
      productId
      productName
      quantity
      unitPrice
    }
  }
}
```

**Résultat :**
```json
{
  "data": {
    "createClientOrder": {
      "orderId": "89",
      "orderNumber": "ORD-2025-00089",
      "totalAmount": 4250.00,
      "status": "EN_PREPARATION",
      "createdAt": "2025-11-21T14:30:00",
      "client": {
        "name": "Café Central",
        "email": "contact@cafecentral.ma"
      },
      "items": [
        {
          "productId": "1",
          "productName": "Café Arabica Premium",
          "quantity": 50,
          "unitPrice": 45.00
        },
        {
          "productId": "3",
          "productName": "Thé Vert Bio",
          "quantity": 30,
          "unitPrice": 35.00
        },
        {
          "productId": "7",
          "productName": "Chocolat Chaud Deluxe",
          "quantity": 20,
          "unitPrice": 55.00
        }
      ]
    }
  }
}
```

---

### Scénario 3 : Organisation de la livraison

**Étape 1 - Vérifier les détails de la commande**
```graphql
query GetOrder {
  getClientOrderById(orderId: 89) {
    orderNumber
    status
    totalAmount
    client {
      name
      phoneNumber
      addresses {
        street
        city
        zipCode
      }
    }
    items {
      productName
      quantity
    }
  }
}
```

**Résultat :**
```json
{
  "data": {
    "getClientOrderById": {
      "orderNumber": "ORD-2025-00089",
      "status": "EN_PREPARATION",
      "totalAmount": 4250.00,
      "client": {
        "name": "Café Central",
        "phoneNumber": "+212522334455",
        "addresses": [
          {
            "street": "45 Rue de la Liberté",
            "city": "Casablanca",
            "zipCode": "20250"
          }
        ]
      },
      "items": [
        { "productName": "Café Arabica Premium", "quantity": 50 },
        { "productName": "Thé Vert Bio", "quantity": 30 },
        { "productName": "Chocolat Chaud Deluxe", "quantity": 20 }
      ]
    }
  }
}
```

**Étape 2 - Planifier la livraison**
```graphql
mutation CreateDelivery {
  createLivraison(input: {
    clientOrderId: 89
    deliveryDate: "2025-11-22T09:00:00"
    vehicule: "Camionnette Renault Master"
    driverName: "Hassan Alaoui"
    cost: 150.00
    status: EN_COURS
  }) {
    livraisonId
    deliveryDate
    driverName
    vehicule
    cost
    status
    order {
      orderNumber
      client {
        name
        addresses {
          city
        }
      }
    }
  }
}
```

**Résultat :**
```json
{
  "data": {
    "createLivraison": {
      "livraisonId": "67",
      "deliveryDate": "2025-11-22T09:00:00",
      "driverName": "Hassan Alaoui",
      "vehicule": "Camionnette Renault Master",
      "cost": 150.00,
      "status": "EN_COURS",
      "order": {
        "orderNumber": "ORD-2025-00089",
        "client": {
          "name": "Café Central",
          "addresses": [
            {
              "city": "Casablanca"
            }
          ]
        }
      }
    }
  }
}
```

**Étape 3 - Mettre à jour le statut de livraison**
```graphql
mutation UpdateDeliveryStatus {
  updateLivraisonStatus(
    livraisonId: 67
    status: LIVREE
  ) {
    livraisonId
    status
    deliveryDate
    order {
      orderNumber
    }
  }
}
```

**Résultat :**
```json
{
  "data": {
    "updateLivraisonStatus": {
      "livraisonId": "67",
      "status": "LIVREE",
      "deliveryDate": "2025-11-22T09:00:00",
      "order": {
        "orderNumber": "ORD-2025-00089"
      }
    }
  }
}
```

---

### Scénario 4 : Tableau de bord - Vue d'ensemble

**Requête complexe combinant plusieurs données**
```graphql
query Dashboard {
  # Statistiques clients
  clientStats: getAllClients(page: 0, size: 1) {
    totalElements
  }
  
  # Commandes récentes
  recentOrders: getAllClientOrders(page: 0, size: 5) {
    content {
      orderId
      orderNumber
      status
      totalAmount
      createdAt
      client {
        name
      }
    }
  }
  
  # Livraisons en cours
  activeLivraisons: getAllLivraisons(status: EN_COURS) {
    livraisonId
    driverName
    vehicule
    deliveryDate
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

**Résultat :**
```json
{
  "data": {
    "clientStats": {
      "totalElements": 127
    },
    "recentOrders": {
      "content": [
        {
          "orderId": "89",
          "orderNumber": "ORD-2025-00089",
          "status": "EN_PREPARATION",
          "totalAmount": 4250.00,
          "createdAt": "2025-11-21T14:30:00",
          "client": {
            "name": "Café Central"
          }
        },
        {
          "orderId": "88",
          "orderNumber": "ORD-2025-00088",
          "status": "LIVREE",
          "totalAmount": 2800.00,
          "createdAt": "2025-11-21T11:15:00",
          "client": {
            "name": "Restaurant Le Gourmet"
          }
        }
      ]
    },
    "activeLivraisons": [
      {
        "livraisonId": "67",
        "driverName": "Hassan Alaoui",
        "vehicule": "Camionnette Renault Master",
        "deliveryDate": "2025-11-22T09:00:00",
        "order": {
          "orderNumber": "ORD-2025-00089",
          "client": {
            "name": "Café Central",
            "phoneNumber": "+212522334455"
          }
        }
      },
      {
        "livraisonId": "65",
        "driverName": "Fatima Zahra",
        "vehicule": "Van Mercedes Sprinter",
        "deliveryDate": "2025-11-21T16:30:00",
        "order": {
          "orderNumber": "ORD-2025-00085",
          "client": {
            "name": "Hôtel Atlas",
            "phoneNumber": "+212523445566"
          }
        }
      }
    ]
  }
}
```

---

## 🔄 Modification vs REST

### Avec REST (nécessite plusieurs appels)
```http
# 1. Obtenir le client
GET http://localhost:8080/api/v1/clients/15

# 2. Obtenir ses adresses
GET http://localhost:8080/api/v1/clients/15/addresses

# 3. Obtenir ses commandes
GET http://localhost:8080/api/v1/clients/15/orders

# 4. Obtenir les détails d'une commande
GET http://localhost:8080/api/v1/orders/89

# Total: 4 requêtes HTTP
```

### Avec GraphQL (une seule requête)
```graphql
query GetCompleteClientInfo {
  getClientById(clientId: 15) {
    clientId
    name
    email
    phoneNumber
    addresses {
      street
      city
      country
    }
    orders {
      content {
        orderId
        orderNumber
        totalAmount
        status
        items {
          productName
          quantity
        }
      }
    }
  }
}

# Total: 1 requête HTTP
```

---

## 💡 Conseils d'Utilisation

### 1. Utilisez des Fragments pour la Réutilisation
```graphql
fragment ClientBasicInfo on Client {
  clientId
  name
  email
  phoneNumber
}

fragment OrderBasicInfo on ClientOrder {
  orderId
  orderNumber
  status
  totalAmount
}

query GetClientWithOrders {
  getClientById(clientId: 15) {
    ...ClientBasicInfo
    orders {
      content {
        ...OrderBasicInfo
      }
    }
  }
}
```

### 2. Utilisez des Variables pour la Sécurité
❌ **Mauvais** (injection possible) :
```graphql
query {
  getClientById(clientId: ${userInput})
}
```

✅ **Bon** (sécurisé) :
```graphql
query GetClient($clientId: ID!) {
  getClientById(clientId: $clientId) {
    name
    email
  }
}

# Variables (séparées) :
{
  "clientId": "15"
}
```

### 3. Limitez la Profondeur des Requêtes
❌ **Évitez** (trop profond) :
```graphql
query {
  getClientById(clientId: 15) {
    orders {
      content {
        items {
          product {
            billOfMaterials {
              matieresPremières {
                fournisseur {
                  orders {
                    # Trop profond !
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
```

✅ **Préférez** (raisonnable) :
```graphql
query {
  getClientById(clientId: 15) {
    name
    orders(page: 0, size: 10) {
      content {
        orderNumber
        totalAmount
      }
    }
  }
}
```

---

## 🎓 Pour Aller Plus Loin

1. **Explorez le Schéma** dans GraphiQL
   - Cliquez sur "Docs" dans l'interface
   - Parcourez les types disponibles
   - Découvrez les champs et arguments

2. **Testez l'Auto-complétion**
   - Appuyez sur `Ctrl + Espace` dans l'éditeur
   - GraphiQL suggère les champs disponibles

3. **Consultez la Documentation Complète**
   - [Guide GraphQL](GraphQL.md)
   - [Exemples d'API](../api/livraison/description.md)
   - [README Principal](../README.md)

---

**SupplyChainX** - Gestion Moderne avec GraphQL 🚀

