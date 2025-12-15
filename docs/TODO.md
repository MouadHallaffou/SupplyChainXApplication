# 📋 TODO - SupplyChainXApplication

## 🎯 Contexte du Projet

Ce repository rassemble le code source complet du projet SupplyChainX depuis le début. Il est structuré en trois parties principales :

### 1️⃣ **Backend Spring Boot**
- Implémentation des fonctionnalités CRUD avec logique métier
- Architecture REST et GraphQL (module Livraison)
- Gestion des entités : Users, Roles, Production, Livraison, Approvisionnement
- tests unitaires using JUnit et Mockito
- https://github.com/MouadHallaffou/SupplyChainXApplication/tree/dev-5

### 2️⃣ **Tests & CI/CD** (Repository séparé)
- Tests unitaires et d'intégration
- Pipeline CI/CD avec Jenkins et GitHub Actions 
- https://github.com/MouadHallaffou/SupplyChainX-CI-CD
- https://github.com/MouadHallaffou/SupplyChainX-github-actions

### 3️⃣ **Sécurité Spring Security**
- Basic Authentication
- Autorisation avec `@PreAuthorize` dans les controllers
- Service `UserDetailsService` personnalisé

---

## 🚀 Prochaines Étapes

### ✅ **1. Amélioration de la Logique Métier**

#### 🏭 **Production**
- Gestion des heures de production
- Validation des capacités de production
- Calcul automatique des délais

#### 🚚 **Livraison**
- Gestion des dates de livraison
- Validation des adresses
- Tracking des statuts

#### 📦 **Approvisionnement**
- **Gestion des quantités** : Mise à jour automatique des stocks
- **Gestion des états** : `EN_COURS`, `LIVREE`, `ANNULEE`
- **Règles métier** :
    - ❌ Si commande `ANNULEE` → Remettre les quantités en stock automatiquement
    - ✅ Si commande `LIVREE` → Déduire les quantités du stock automatiquement
    - 🚫 Si commande `LIVREE` → Interdire l'annulation

---

### 🔐 **2. Sécurité avec `@PreAuthorize`**

Ajouter les annotations `@PreAuthorize` dans **tous les controllers** avec gestion des rôles :

#### **Exemples de configuration** :

```java
// UserController.java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable Long id) { ... }

@PreAuthorize("hasAnyRole('ADMIN', 'PLANIFICATEUR')")
@GetMapping
public ResponseEntity<List<UserDTO>> getAllUsers() { ... }

// ProductionController.java
@PreAuthorize("hasAnyRole('ADMIN', 'PLANIFICATEUR')")
@PostMapping
public ResponseEntity<ProductionDTO> createProduction(@RequestBody ProductionRequestDTO dto) { ... }

// ApprovisionnementController.java
@PreAuthorize("hasRole('ADMIN')")
@PutMapping("/{id}/cancel")
public ResponseEntity<Void> cancelOrder(@PathVariable Long id) { ... }
```

#### **Rôles à gérer** :
- `ADMIN`
- `PLANIFICATEUR`
- `...`
---
### 🛡️ **3. Tests de Sécurité avec SecurityFilterChain **

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests(auth -> auth
                    // Users endpoints
                    .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasAnyRole("ADMIN", "PLANIFICATEUR")
                    .requestMatchers(HttpMethod.POST, "/api/v1/users/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")

                    // Production endpoints
                    .requestMatchers("/api/v1/production/**").hasAnyRole("ADMIN", "PLANIFICATEUR")

                    // Approvisionnement endpoints
                    .requestMatchers(HttpMethod.GET, "/api/v1/approvisionnement/**").authenticated()
                    .requestMatchers("/api/v1/approvisionnement/**").hasRole("ADMIN")

                    // Autres endpoints
                    .anyRequest().authenticated()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(Customizer.withDefaults())
            .userDetailsService(customUserDetailsService);

    return http.build();
}
```

---
## 🧪 **4. Tests Unitaires et d'Intégration**
````java
@Test
@WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
void testCreateRole_Success() throws Exception {
    RoleRequestDTO role = new RoleRequestDTO("ADMIN");
    
    mockMvc.perform(post("/api/v1/roles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(role)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.name").value("ADMIN"));
}

@Test
@WithMockUser(username = "planif@gmail.com", roles = {"PLANIFICATEUR"})
void testDeleteUser_Forbidden() throws Exception {
    mockMvc.perform(delete("/api/v1/users/1"))
        .andExpect(status().isForbidden());
}
````

````java
@Test
void testGetUsers_WithBasicAuth() throws Exception {
    mockMvc.perform(get("/api/v1/users")
            .header("Authorization", "Basic " + 
                Base64.getEncoder().encodeToString("admin:password".getBytes())))
        .andExpect(status().isOk());
}
````

- add endponit to postman using swagger docs with ai structuration!!
- doc jwt security and implementation steps 
- def stetful and stateless session management with spring security
- what is keycloak and how to integrate it with spring security
- implement oauth2 with spring security and document steps