# Keycloak - Documentation

## Table des Matières
1. [Introduction](#introduction)
2. [Concepts Clés](#concepts-clés)
3. [Architecture](#architecture)
4. [Installation et Configuration](#installation-et-configuration)
5. [Intégration avec Spring Boot](#intégration-avec-spring-boot)
6. [Utilisation dans SupplyChainX](#utilisation-dans-supplychainx)
7. [Bonnes Pratiques](#bonnes-pratiques)

---

## Introduction

**Keycloak** est une solution open-source de gestion d'identité et d'accès (IAM - Identity and Access Management) développée par Red Hat. Il fournit des fonctionnalités complètes pour sécuriser les applications modernes.

### Qu'est-ce que Keycloak ?

Keycloak est un serveur d'authentification et d'autorisation qui offre :

- **Single Sign-On (SSO)** : Une seule connexion pour plusieurs applications
- **Identity Brokering** : Intégration avec des fournisseurs externes (Google, Facebook, LDAP, etc.)
- **User Federation** : Synchronisation avec Active Directory, LDAP
- **Fine-Grained Authorization** : Contrôle d'accès basé sur les rôles (RBAC)
- **Standard Protocols** : OAuth 2.0, OpenID Connect, SAML 2.0

### Pourquoi utiliser Keycloak ?

✅ **Sécurité robuste** : Authentification et autorisation centralisées  
✅ **Prêt à l'emploi** : Interface d'administration complète  
✅ **Multi-tenant** : Gestion de plusieurs domaines (realms)  
✅ **Personnalisable** : Thèmes, extensions, SPI  
✅ **Standards du marché** : Conformité OAuth 2.0 / OpenID Connect  
✅ **Open Source** : Gratuit et communauté active  

---

## Concepts Clés

### 1. Realm (Domaine)

Un **realm** est un espace isolé qui gère un ensemble d'utilisateurs, rôles, clients et configurations.

- Chaque realm a sa propre configuration de sécurité
- Les utilisateurs et rôles d'un realm sont isolés des autres
- Exemple : `supplychainx-realm`, `master` (realm par défaut)

### 2. Client

Un **client** représente une application qui utilise Keycloak pour l'authentification.

Types de clients :
- **Confidential** : Applications serveur (Spring Boot) avec secret client
- **Public** : Applications front-end (React, Angular) sans secret
- **Bearer-only** : APIs REST qui valident uniquement les tokens

### 3. User (Utilisateur)

Un **user** est une entité qui peut s'authentifier dans le système.

Attributs :
- Username, email, nom, prénom
- Credentials (mot de passe, OTP)
- Attributs personnalisés
- Rôles assignés

### 4. Role (Rôle)

Un **role** définit un ensemble de permissions.

Types de rôles :
- **Realm Roles** : Globaux à tout le realm
- **Client Roles** : Spécifiques à un client

Exemples : `ADMIN`, `MANAGER`, `USER`, `PRODUCTION_OPERATOR`

### 5. Group (Groupe)

Un **group** permet de regrouper des utilisateurs et d'assigner des rôles en masse.

Structure hiérarchique possible : `Organization > Department > Team`

### 6. Token

Keycloak utilise des **JWT (JSON Web Token)** pour représenter l'authentification.

Types de tokens :
- **Access Token** : Utilisé pour accéder aux ressources protégées
- **Refresh Token** : Utilisé pour renouveler l'access token
- **ID Token** : Contient les informations de l'utilisateur

---

## Architecture

### Architecture Keycloak

```
┌─────────────────────────────────────────────┐
│          Keycloak Server                     │
│  ┌────────────┐  ┌────────────┐            │
│  │  Realm A   │  │  Realm B   │            │
│  │  (Master)  │  │(SupplyChain)│            │
│  └────────────┘  └────────────┘            │
│         │               │                    │
│    ┌────┴────┬──────────┴────┬────────┐    │
│    │ Users   │   Clients     │ Roles  │    │
│    └─────────┴───────────────┴────────┘    │
└─────────────────────────────────────────────┘
```

### Flux d'authentification OAuth 2.0 / OpenID Connect

```
┌──────────┐                                  ┌──────────┐
│  Client  │                                  │ Keycloak │
│   (Web)  │                                  │  Server  │
└────┬─────┘                                  └────┬─────┘
     │                                              │
     │ 1. Demande d'autorisation                   │
     │─────────────────────────────────────────────>│
     │                                              │
     │ 2. Redirection vers page de connexion       │
     │<─────────────────────────────────────────────│
     │                                              │
     │ 3. Utilisateur entre credentials            │
     │─────────────────────────────────────────────>│
     │                                              │
     │ 4. Code d'autorisation                      │
     │<─────────────────────────────────────────────│
     │                                              │
     │ 5. Échange code contre token                │
     │─────────────────────────────────────────────>│
     │                                              │
     │ 6. Access Token + Refresh Token             │
     │<─────────────────────────────────────────────│
     │                                              │
     
┌──────────┐                                  ┌──────────┐
│  Client  │                                  │   API    │
└────┬─────┘                                  └────┬─────┘
     │                                              │
     │ 7. Requête API avec Access Token            │
     │─────────────────────────────────────────────>│
     │                                              │
     │ 8. Validation token + Réponse               │
     │<─────────────────────────────────────────────│
```

### Types de flux OAuth 2.0

1. **Authorization Code Flow** : Pour applications web avec backend
2. **Implicit Flow** : Pour applications SPA (déprécié)
3. **Client Credentials Flow** : Pour communication service-to-service
4. **Password Flow** : Pour applications de confiance (déconseillé)
5. **Refresh Token Flow** : Pour renouveler les tokens expirés

---

## Installation et Configuration

### Installation avec Docker

```bash
docker run -d \
  --name keycloak \
  -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:23.0 \
  start-dev
```

### Docker Compose

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    container_name: keycloak-db
    environment:
      POSTGRES_DB: keycloak
      POSTGRES_USER: keycloak
      POSTGRES_PASSWORD: password
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - keycloak-network

  keycloak:
    image: quay.io/keycloak/keycloak:23.0
    container_name: keycloak
    environment:
      KC_DB: postgres
      KC_DB_URL: jdbc:postgresql://postgres:5432/keycloak
      KC_DB_USERNAME: keycloak
      KC_DB_PASSWORD: password
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
      KC_HOSTNAME: localhost
      KC_HTTP_ENABLED: true
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    command: start-dev
    networks:
      - keycloak-network

volumes:
  postgres-data:

networks:
  keycloak-network:
    driver: bridge
```

### Configuration Initiale

1. **Accéder à l'interface** : http://localhost:8080
2. **Se connecter** : admin / admin
3. **Créer un nouveau Realm** : `supplychainx-realm`
4. **Créer un Client** : `supplychainx-app`
5. **Créer des Roles** : `ADMIN`, `MANAGER`, `USER`
6. **Créer des Utilisateurs** : Assigner roles et credentials

---

## Intégration avec Spring Boot

### Dépendances Maven

```xml
<dependencies>
    <!-- Spring Boot Starter Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- Spring Boot Starter OAuth2 Resource Server -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>
    
    <!-- Keycloak Spring Boot Starter -->
    <dependency>
        <groupId>org.keycloak</groupId>
        <artifactId>keycloak-spring-boot-starter</artifactId>
        <version>23.0.0</version>
    </dependency>
</dependencies>
```

### Configuration application.yml

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/supplychainx-realm
          jwk-set-uri: http://localhost:8080/realms/supplychainx-realm/protocol/openid-connect/certs

keycloak:
  realm: supplychainx-realm
  auth-server-url: http://localhost:8080
  resource: supplychainx-app
  public-client: false
  credentials:
    secret: your-client-secret
  ssl-required: external
  use-resource-role-mappings: true
  bearer-only: true
```

### Configuration Spring Security

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = 
            new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = 
            new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
            grantedAuthoritiesConverter
        );
        
        return jwtAuthenticationConverter;
    }
}
```

### Sécurisation des Endpoints

```java
@RestController
@RequestMapping("/api/production")
public class ProductController {

    // Accessible uniquement par ADMIN et MANAGER
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // ...
    }

    // Accessible par tous les utilisateurs authentifiés
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        // ...
    }

    // Accessible uniquement par ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        // ...
    }
}
```

### Récupération des informations utilisateur

```java
@Service
public class UserService {

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder
            .getContext()
            .getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getClaimAsString("preferred_username");
        }
        
        return null;
    }

    public List<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder
            .getContext()
            .getAuthentication();
        
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder
            .getContext()
            .getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getClaimAsString("email");
        }
        
        return null;
    }
}
```

---

## Utilisation dans SupplyChainX

### Modèle de Rôles Proposé

```
┌─────────────────────────────────────────────┐
│           SUPPLYCHAINX ROLES                 │
├─────────────────────────────────────────────┤
│ ROLE_ADMIN                                   │
│   ├── Accès complet à toutes les ressources │
│   ├── Gestion des utilisateurs              │
│   └── Configuration système                 │
│                                              │
│ ROLE_MANAGER                                 │
│   ├── Supervision des opérations            │
│   ├── Validation des commandes              │
│   └── Rapports et statistiques              │
│                                              │
│ ROLE_SUPPLY_MANAGER                         │
│   ├── Gestion des fournisseurs              │
│   ├── Gestion des matières premières        │
│   └── Création commandes d'approvisionnement│
│                                              │
│ ROLE_PRODUCTION_MANAGER                     │
│   ├── Gestion des produits                  │
│   ├── Gestion des nomenclatures (BOM)       │
│   └── Planification de production           │
│                                              │
│ ROLE_DELIVERY_MANAGER                       │
│   ├── Gestion des livraisons                │
│   ├── Gestion des clients                   │
│   └── Suivi des commandes clients           │
│                                              │
│ ROLE_OPERATOR                                │
│   ├── Lecture seule sur les ressources      │
│   └── Exécution de tâches basiques          │
└─────────────────────────────────────────────┘
```

### Configuration des Clients Keycloak

**1. Client Backend (supplychainx-api)**
```
Client ID: supplychainx-api
Client Protocol: openid-connect
Access Type: confidential
Service Accounts Enabled: On
Authorization Enabled: On
Valid Redirect URIs: http://localhost:8081/*
```

**2. Client Frontend (supplychainx-web)**
```
Client ID: supplychainx-web
Client Protocol: openid-connect
Access Type: public
Standard Flow Enabled: On
Direct Access Grants Enabled: On
Valid Redirect URIs: http://localhost:3000/*
Web Origins: http://localhost:3000
```

### Exemple de Token JWT

```json
{
  "exp": 1704290400,
  "iat": 1704286800,
  "jti": "a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6",
  "iss": "http://localhost:8080/realms/supplychainx-realm",
  "aud": "account",
  "sub": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "typ": "Bearer",
  "azp": "supplychainx-api",
  "session_state": "abc123",
  "acr": "1",
  "realm_access": {
    "roles": [
      "ADMIN",
      "MANAGER"
    ]
  },
  "resource_access": {
    "supplychainx-api": {
      "roles": [
        "PRODUCTION_MANAGER"
      ]
    }
  },
  "scope": "email profile",
  "email_verified": true,
  "name": "John Doe",
  "preferred_username": "john.doe",
  "given_name": "John",
  "family_name": "Doe",
  "email": "john.doe@supplychainx.com"
}
```

### Tester l'authentification avec Postman

**1. Obtenir un Access Token**

```http
POST http://localhost:8080/realms/supplychainx-realm/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password
&client_id=supplychainx-api
&client_secret=your-client-secret
&username=john.doe
&password=password123
```

**Réponse :**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI...",
  "expires_in": 3600,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI...",
  "token_type": "Bearer",
  "not-before-policy": 0,
  "session_state": "abc123",
  "scope": "email profile"
}
```

**2. Appeler une API protégée**

```http
GET http://localhost:8081/api/production/products
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI...
```

---

## Bonnes Pratiques

### 1. Sécurité

✅ **Utiliser HTTPS en production** (TLS/SSL)  
✅ **Activer CORS correctement** pour les clients web  
✅ **Définir des durées de token appropriées** (access: 5-15 min, refresh: 30 min - 1h)  
✅ **Stocker les secrets client en toute sécurité** (variables d'environnement)  
✅ **Activer la vérification d'email**  
✅ **Configurer la politique de mot de passe** (longueur min, complexité)  
✅ **Activer 2FA/MFA** pour les comptes sensibles  
✅ **Auditer les événements** (connexions, changements de rôles)  

### 2. Gestion des Tokens

```java
// ❌ MAUVAIS : Stocker le token dans localStorage (vulnérable XSS)
localStorage.setItem('access_token', token);

// ✅ BON : Utiliser httpOnly cookies
// Configuration côté serveur pour définir le cookie sécurisé
```

### 3. Refresh Token Flow

```java
@Service
public class TokenService {
    
    public String refreshAccessToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", "supplychainx-api");
        params.add("client_secret", clientSecret);
        params.add("refresh_token", refreshToken);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        HttpEntity<MultiValueMap<String, String>> request = 
            new HttpEntity<>(params, headers);
        
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
            keycloakTokenUrl,
            request,
            TokenResponse.class
        );
        
        return response.getBody().getAccessToken();
    }
}
```

### 4. Gestion des Erreurs

```java
@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
        AccessDeniedException ex
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(
                "ACCESS_DENIED",
                "Vous n'avez pas les permissions nécessaires"
            ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationError(
        AuthenticationException ex
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(
                "AUTHENTICATION_FAILED",
                "Authentification échouée"
            ));
    }
}
```

### 5. Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void adminCanAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void userCannotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void unauthenticatedUserCannotAccessProtectedEndpoint() 
        throws Exception {
        mockMvc.perform(get("/api/production/products"))
            .andExpect(status().isUnauthorized());
    }
}
```

### 6. Monitoring et Logs

```java
@Aspect
@Component
@Slf4j
public class SecurityAuditAspect {

    @Around("@annotation(org.springframework.security.access.prepost.PreAuthorize)")
    public Object auditSecurityAccess(ProceedingJoinPoint joinPoint) 
        throws Throwable {
        String username = getCurrentUsername();
        String method = joinPoint.getSignature().getName();
        
        log.info("User {} attempting to access method {}", username, method);
        
        try {
            Object result = joinPoint.proceed();
            log.info("User {} successfully accessed method {}", username, method);
            return result;
        } catch (AccessDeniedException e) {
            log.warn("User {} denied access to method {}", username, method);
            throw e;
        }
    }
}
```

---

## Configuration Avancée

### Social Login (Google, Facebook)

1. **Créer une application OAuth dans Google Cloud Console**
2. **Dans Keycloak : Identity Providers > Add Provider > Google**
3. **Configurer Client ID et Client Secret**
4. **Mapper les attributs utilisateur**

### User Federation avec LDAP

```
User Federation > Add provider > ldap
Connection URL: ldap://ldap.example.com
Bind DN: cn=admin,dc=example,dc=com
Bind Credential: password
Users DN: ou=users,dc=example,dc=com
```

### Custom Theme

```
themes/
  supplychainx/
    login/
      theme.properties
      resources/
        css/
          login.css
        img/
          logo.png
```

### Event Listeners

Configuration pour envoyer des événements à des systèmes externes (Kafka, webhooks).

---

## Commandes Utiles

### API Admin Keycloak

```bash
# Obtenir un token admin
curl -X POST http://localhost:8080/realms/master/protocol/openid-connect/token \
  -d "client_id=admin-cli" \
  -d "username=admin" \
  -d "password=admin" \
  -d "grant_type=password"

# Lister les utilisateurs d'un realm
curl -X GET http://localhost:8080/admin/realms/supplychainx-realm/users \
  -H "Authorization: Bearer $TOKEN"

# Créer un utilisateur
curl -X POST http://localhost:8080/admin/realms/supplychainx-realm/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "enabled": true,
    "emailVerified": true
  }'
```

---

## Dépannage

### Problème : Token invalide

**Cause** : Token expiré, signature invalide, ou mauvaise configuration JWK  
**Solution** : Vérifier `issuer-uri` et `jwk-set-uri` dans application.yml

### Problème : CORS

**Cause** : Origines non autorisées  
**Solution** : Configurer Web Origins dans le client Keycloak

### Problème : Rôles non mappés

**Cause** : JwtAuthenticationConverter mal configuré  
**Solution** : Vérifier le claim name et le prefix des autorités

---

## Ressources

- **Documentation officielle** : https://www.keycloak.org/documentation
- **GitHub** : https://github.com/keycloak/keycloak
- **Docker Hub** : https://quay.io/repository/keycloak/keycloak
- **Stack Overflow** : Tag `keycloak`
- **Keycloak Blog** : https://www.keycloak.org/blog

---

## Conclusion

Keycloak est une solution complète et robuste pour gérer l'authentification et l'autorisation dans SupplyChainX. Son intégration avec Spring Boot est simple et offre :

✅ **Sécurité enterprise-grade** avec standards OAuth 2.0 / OpenID Connect  
✅ **Gestion centralisée** des utilisateurs et rôles  
✅ **Single Sign-On (SSO)** pour une expérience utilisateur fluide  
✅ **Extensibilité** avec SPI et thèmes personnalisés  
✅ **Audit et conformité** avec logging des événements  

**Prochaines étapes** : Configurer les rôles métier, intégrer avec le frontend, activer 2FA, configurer les politiques de mot de passe.

