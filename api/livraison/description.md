# 🚚 API GraphQL - Module Livraison

## 📖 Introduction

Ce module utilise **GraphQL** pour gérer les livraisons, offrant une API flexible et performante pour :
- Gérer les clients et leurs adresses
- Créer et suivre les commandes clients
- Planifier et suivre les livraisons

## 🌐 Accès

### Interface GraphiQL (Recommandée)
**URL** : http://localhost:8080/graphiql?path=/graphql

Interface interactive avec :
- ✍️ Éditeur avec auto-complétion
- 📚 Documentation du schéma
- 📊 Visualisation des résultats

### Endpoint API
**URL** : http://localhost:8080/graphql
- **Méthode** : POST
- **Content-Type** : application/json

## 📋 Exemples de Requêtes

### 👥 Clients

#### Lister tous les clients (avec pagination)

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

---

### 📍 Adresses

#### Créer une adresse pour un client

```graphql
mutation CreateAdress {
  createAddress(input: {
    city: "casa"
    state: "ca"
    street: "1111"
    country: "maroc"
    zipCode: "12221"
    clientId: 3
  }) {
    country
    zipCode
  }
}
```

---

### 📦 Commandes Clients

#### Lister toutes les commandes clients

```graphql
query GetAllclientorders {
  getAllClientOrders(page: 0, size: 10) {
    content {
      orderId
      orderNumber
      status
      createdAt
      updatedAt
      client {
        name
        phoneNumber
        email
      }
    }
    totalPages
  }
}
```

#### Modifier une commande client

```graphql
#### Modifier une commande client

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
    items {
      productName
      quantity
    }
  }
}
```

---

### 🚚 Livraisons

#### Créer une livraison

```graphql
mutation CreateLivraison {
  createLivraison(input: {
    clientOrderId: 6
    cost: 100
    deliveryDate: "2025-11-10T10:10:22"
    vehicule: "Van"
    driverName: "John Doe"
    status: EN_COURS
  }) {
    status
    livraisonId
    clientOrderId
    deliveryDate
    driverName
    vehicule
  }
}
```

---

## 📚 Documentation Complémentaire

Pour plus d'exemples et de détails :
- 📖 [Guide GraphQL Complet](../../docs/GraphQL.md)
- 💡 [Exemples Pratiques](../../docs/GraphQL-Examples.md)
- 🤔 [Pourquoi GraphQL ?](../../docs/GraphQL-Why.md)
- 🏠 [README Principal](../../README.md)

---

**SupplyChainX** - Module Livraison avec GraphQL 🚀
