    # 🔐 Documentation Spring Security - SupplyChainX

## 📖 Introduction

**Spring Security** est un framework puissant et hautement personnalisable pour la gestion de la sécurité dans les applications Java/Spring Boot. Il fournit des fonctionnalités complètes pour :

- 🔑 **Authentification** - Vérifier l'identité des utilisateurs
- 🛡️ **Autorisation** - Contrôler l'accès aux ressources
- 🔒 **Protection CSRF** - Prévenir les attaques Cross-Site Request Forgery
- 🌐 **CORS** - Gérer les requêtes cross-origin
- 🔐 **Chiffrement** - Protéger les mots de passe et données sensibles

---

## 📦 Installation

### Dépendance Maven

Ajoutez Spring Security à votre projet via `pom.xml` :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### Activation Automatique

Une fois ajoutée, Spring Security est **automatiquement activé** et :
- ✅ Sécurise tous les endpoints par défaut
- ✅ Génère un mot de passe aléatoire au démarrage
- ✅ Active la protection CSRF
- ✅ Configure l'authentification Basic Auth

---

## 🎯 Concepts Fondamentaux

### 1. 🔑 Authentication (Authentification)

**Définition** : Processus de vérification de l'identité d'un utilisateur.

#### Comment Spring Security vérifie qui vous êtes ?

**Composants clés :**

- **UserDetails** : Interface représentant un utilisateur
  ```java
  public interface UserDetails {
      String getUsername();
      String getPassword();
      Collection<? extends GrantedAuthority> getAuthorities();
      boolean isAccountNonExpired();
      boolean isAccountNonLocked();
      boolean isCredentialsNonExpired();
      boolean isEnabled();
  }
  ```

- **UserDetailsService** : Service qui charge les données utilisateur
  ```java
  public interface UserDetailsService {
      UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
  }
  ```

- **PasswordEncoder** : Encodeur de mots de passe
  ```java
  @Bean
  public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }
  ```

#### Mécanismes d'authentification disponibles :

| Mécanisme | Description | Use Case |
|-----------|-------------|----------|
| **Form Login** | Formulaire HTML classique | Applications web traditionnelles |
| **Basic Auth** | Username/Password en Base64 | API simples, tests |
| **JWT** | JSON Web Token | API REST stateless |
| **OAuth2** | Délégation d'authentification | Login Google, GitHub, etc. |
| **LDAP** | Active Directory | Entreprises |

**Exemple d'implémentation UserDetailsService :**

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(user.getRoles())
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
    }
}
```

---

### 2. 🛡️ Authorization (Autorisation)

**Définition** : Processus de détermination des droits d'accès d'un utilisateur authentifié.

#### Comment Spring Security décide ce que vous pouvez faire ?

**Mécanismes d'autorisation :**

#### a) Annotations sur les méthodes

**@PreAuthorize** - Avant l'exécution
```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) {
    userRepository.deleteById(id);
}

@PreAuthorize("hasAuthority('WRITE_PRIVILEGE')")
public void updateProduct(Product product) {
    productRepository.save(product);
}

@PreAuthorize("hasRole('MANAGER') and #username == authentication.name")
public void updateOwnProfile(String username, UserDTO dto) {
    // Seul le manager peut modifier son propre profil
}
```

**@PostAuthorize** - Après l'exécution
```java
@PostAuthorize("returnObject.owner == authentication.name")
public Order getOrder(Long orderId) {
    return orderRepository.findById(orderId);
}
```

**@Secured** - Rôles seulement
```java
@Secured("ROLE_ADMIN")
public void adminOperation() {
    // ...
}
```

#### b) Configuration HTTP

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Endpoints publics
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/graphql", "/graphiql").permitAll()
                
                // Endpoints par rôle
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/fournisseurs/**").hasAnyRole("ADMIN", "GESTIONNAIRE_APPROVISIONNEMENT")
                .requestMatchers("/api/v1/products/**").hasAnyRole("ADMIN", "CHEF_PRODUCTION")
                
                // Tout le reste nécessite une authentification
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
```

#### c) Rôles vs Authorities

**Rôles** : Préfixés par `ROLE_`
```java
// Configuration
authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

// Utilisation
@PreAuthorize("hasRole('ADMIN')")  // Cherche ROLE_ADMIN
```

**Authorities** : Permissions granulaires
```java
// Configuration
authorities.add(new SimpleGrantedAuthority("READ_PRODUCTS"));
authorities.add(new SimpleGrantedAuthority("WRITE_PRODUCTS"));

// Utilisation
@PreAuthorize("hasAuthority('WRITE_PRODUCTS')")
```

---

### 3. 🔗 SecurityFilterChain

**Définition** : Chaîne de filtres de sécurité qui interceptent et traitent les requêtes HTTP.

#### Comment les filtres sont organisés ?

```
Requête HTTP
    ↓
DelegatingFilterProxy (Servlet Filter)
    ↓
FilterChainProxy (Spring Security)
    ↓
╔════════════════════════════════════╗
║     SecurityFilterChain            ║
╠════════════════════════════════════╣
║ 1. SecurityContextPersistenceFilter║
║ 2. LogoutFilter                    ║
║ 3. UsernamePasswordAuthFilter      ║
║ 4. JwtAuthenticationFilter (custom)║
║ 5. ExceptionTranslationFilter      ║
║ 6. FilterSecurityInterceptor       ║
╚════════════════════════════════════╝
    ↓
Controller
```

#### Configuration de la chaîne

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())  // Désactiver CSRF pour API REST
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Mode stateless
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

#### Comment une requête traverse la chaîne ?

1. **Entrée** : Servlet Filter intercepte la requête
2. **DelegatingFilterProxy** : Délègue à Spring Security
3. **FilterChainProxy** : Distribue aux filtres appropriés
4. **Filtres métier** : Chaque filtre traite un aspect (auth, autorisation, etc.)
5. **Sortie** : Si succès → Controller, sinon → Exception Handler

---

### 4. 🎩 The Big Boss : DelegatingFilterProxy

**Définition** : Proxy installé par Spring Boot qui fait le pont entre le conteneur Servlet et Spring Security.

#### Rôle

```
Tomcat/Jetty (Servlet Container)
        ↓
   DelegatingFilterProxy
        ↓
   FilterChainProxy (Spring Bean)
        ↓
   SecurityFilterChain
```

**Pourquoi c'est important ?**
- ✅ Permet à Spring Security de fonctionner dans un conteneur Servlet
- ✅ Donne accès au contexte Spring (injection de dépendances)
- ✅ Point d'entrée unique pour toute la sécurité

**Configuration automatique :**
```java
// Spring Boot configure automatiquement :
@Bean
public DelegatingFilterProxyRegistrationBean securityFilterChain() {
    return new DelegatingFilterProxyRegistrationBean("springSecurityFilterChain");
}
```

---

### 5. 🔐 PasswordEncoder

**Définition** : Composant responsable du hashing des mots de passe.

#### Importance du hashing

❌ **JAMAIS** stocker un mot de passe en clair :
```java
// MAUVAIS - DANGEREUX !
user.setPassword("password123");
```

✅ **TOUJOURS** hasher les mots de passe :
```java
// BON
String hashedPassword = passwordEncoder.encode("password123");
user.setPassword(hashedPassword);
```

#### Encodeurs disponibles

| Encodeur | Sécurité | Performance | Recommandé |
|----------|----------|-------------|------------|
| **BCryptPasswordEncoder** | ⭐⭐⭐⭐⭐ | Moyen | ✅ Oui |
| **Argon2PasswordEncoder** | ⭐⭐⭐⭐⭐ | Lent | ✅ Oui (si disponible) |
| **Pbkdf2PasswordEncoder** | ⭐⭐⭐⭐ | Moyen | ⚠️ OK |
| **SCryptPasswordEncoder** | ⭐⭐⭐⭐⭐ | Lent | ⚠️ OK |
| **NoOpPasswordEncoder** | ❌ | Rapide | ❌ JAMAIS en prod |

#### Configuration recommandée

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);  // Force de 12 (par défaut: 10)
    }
}
```

#### Utilisation

```java
@Service
public class UserService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public void createUser(String username, String rawPassword) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));  // Hash
        userRepository.save(user);
    }
    
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);  // Vérification
    }
}
```

**Exemple de hash BCrypt :**
```
Input:  "password123"
Output: "$2a$12$KIXxNj3dHZXjgE.6qg5K3e0vJ9yZhQ0M1L2nU8P4w5x7R9C1E3K5M"
        └─┘ └┘ └──────────────┘ └─────────────────────────────────────┘
         │   │        │                      │
      Algo Version  Salt                  Hash
```

---

### 6. 🕐 Session Management

**Définition** : Gestion des sessions utilisateur côté serveur.

#### Concepts clés

**a) Session Fixation**

Attaque où un hacker réutilise un ID de session existant.

**Protection :**
```java
http.sessionManagement(session -> session
    .sessionFixation().newSession()  // Crée une nouvelle session après login
);
```

**b) Session Timeout**

Durée avant expiration de la session.

```properties
# application.properties
server.servlet.session.timeout=30m
```

**c) Cookie JSESSIONID**

Cookie contenant l'ID de session.

```java
http.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // Crée si nécessaire
);
```

#### Stratégies de création de session

| Stratégie | Description | Use Case |
|-----------|-------------|----------|
| **ALWAYS** | Crée toujours une session | Applications web traditionnelles |
| **IF_REQUIRED** | Crée si nécessaire (défaut) | Mixte |
| **NEVER** | N'en crée jamais, mais utilise si existe | API avec fallback |
| **STATELESS** | Aucune session | API REST pure, JWT |

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Pour API REST
        .maximumSessions(1)  // Limite à 1 session par utilisateur
        .maxSessionsPreventsLogin(true)  // Bloque nouveau login si max atteint
    );
    
    return http.build();
}
```

---

### 7. 🔄 Stateless vs Stateful

**Définition** : Modes de gestion de l'état utilisateur.

#### Comparaison

| Aspect | Stateful (Session) | Stateless (JWT) |
|--------|-------------------|-----------------|
| **Stockage** | Serveur (mémoire/DB) | Client (token) |
| **Scalabilité** | ⚠️ Limitée | ✅ Excellente |
| **Session** | Cookie JSESSIONID | Header Authorization |
| **Révocation** | ✅ Facile | ⚠️ Compliqué |
| **Sécurité** | Serveur contrôle | Client possède le token |
| **Use Case** | App web monolithique | Microservices, SPA |

#### Configuration Stateful (Session)

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        )
        .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
    
    return http.build();
}
```

#### Configuration Stateless (JWT)

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())  // Pas besoin de CSRF avec JWT
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Aucune session
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

**Flux JWT :**
```
1. Login → POST /api/v1/auth/login
   ↓
2. Serveur valide credentials
   ↓
3. Génère JWT : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ↓
4. Client stocke le token (localStorage/sessionStorage)
   ↓
5. Requêtes suivantes : 
   Header: Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ↓
6. Serveur vérifie et décode le token
```

---

### 8. 🛡️ CSRF (Cross-Site Request Forgery)

**Définition** : Attaque où un site malveillant force l'utilisateur à exécuter des actions non désirées sur un site où il est authentifié.

#### Pourquoi activé par défaut ?

Spring Security **active CSRF par défaut** pour protéger les applications web utilisant des sessions.

**Comment ça marche ?**
1. Serveur génère un token CSRF unique
2. Token envoyé au client (cookie ou formulaire caché)
3. Client renvoie le token à chaque requête mutante (POST, PUT, DELETE)
4. Serveur vérifie que le token est valide

```html
<!-- Formulaire avec token CSRF -->
<form action="/api/v1/products" method="POST">
    <input type="hidden" name="_csrf" value="4bfd1575-3ad1-4d21-96c7-4ef2d9f86721"/>
    <!-- autres champs -->
</form>
```

#### Pourquoi désactiver pour API REST + JWT ?

✅ **Raisons de désactiver :**
- JWT est envoyé dans le header, pas dans un cookie
- Pas de session côté serveur
- CORS + JWT suffisent pour la sécurité
- Simplifie l'implémentation

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable());  // Désactiver pour API REST stateless
    return http.build();
}
```

❌ **Quand NE PAS désactiver :**
- Application web avec formulaires
- Utilisation de sessions (cookies)
- Pas de JWT/token

---

### 9. 🌐 CORS (Cross-Origin Resource Sharing)

**Définition** : Mécanisme permettant à des applications frontend (Angular, React, Vue) d'accéder à votre API backend sur un domaine différent.

#### Problème sans CORS

```
Frontend: http://localhost:4200 (Angular)
    ↓ (requête)
Backend:  http://localhost:8080 (Spring Boot)
    ↓
❌ ERREUR: CORS policy blocked
```

#### Configuration CORS

**a) Configuration globale**

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origines autorisées
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",      // Angular dev
            "http://localhost:3000",      // React dev
            "https://supplychainx.com"    // Production
        ));
        
        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        
        // Headers autorisés
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With"
        ));
        
        // Expose headers au client
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Disposition"
        ));
        
        // Autoriser les credentials (cookies)
        configuration.setAllowCredentials(true);
        
        // Durée de cache des preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
```

**b) Intégration avec SecurityFilterChain**

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable());
    
    return http.build();
}
```

**c) Annotation sur un Controller**

```java
@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "http://localhost:4200")  // Spécifique à ce controller
public class ProductController {
    // ...
}
```

#### Pour GraphQL

```java
@Configuration
public class GraphQLConfig {
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/graphql")
                    .allowedOrigins("http://localhost:4200")
                    .allowedMethods("POST", "GET")
                    .allowCredentials(true);
            }
        };
    }
}
```

---

### 10. ⚠️ Exception Handling

**Définition** : Gestion centralisée des erreurs de sécurité.

#### Composants clés

**a) AuthenticationEntryPoint**

Gérer les tentatives d'accès non authentifiées.

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        Map<String, Object> error = new HashMap<>();
        error.put("status", 401);
        error.put("error", "Unauthorized");
        error.put("message", "Authentication required");
        error.put("path", request.getRequestURI());
        error.put("timestamp", LocalDateTime.now());
        
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(error));
    }
}
```

**b) AccessDeniedHandler**

Gérer les accès refusés (utilisateur authentifié mais sans permissions).

```java
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    
    @Override
    public void handle(HttpServletRequest request,
                      HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException {
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        Map<String, Object> error = new HashMap<>();
        error.put("status", 403);
        error.put("error", "Forbidden");
        error.put("message", "You don't have permission to access this resource");
        error.put("path", request.getRequestURI());
        error.put("timestamp", LocalDateTime.now());
        
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(error));
    }
}
```

**c) Configuration**

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler)
        );
    
    return http.build();
}
```

#### Réponse JSON personnalisée

**Exemple de réponse d'erreur :**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired JWT token",
  "path": "/api/v1/products",
  "timestamp": "2025-11-21T14:30:00"
}
```

---

### 11. 🗄️ SecurityContext

**Définition** : Conteneur qui stocke les informations d'authentification de l'utilisateur courant.

#### Comment fonctionne SecurityContextHolder ?

```java
// Structure
SecurityContextHolder
    └── SecurityContext
            └── Authentication
                    ├── Principal (UserDetails)
                    ├── Credentials (password)
                    └── Authorities (roles/permissions)
```

#### Stratégies de stockage

| Stratégie | Description | Use Case |
|-----------|-------------|----------|
| **MODE_THREADLOCAL** | ThreadLocal (défaut) | Applications web classiques |
| **MODE_INHERITABLETHREADLOCAL** | Threads enfants héritent | Async/parallel processing |
| **MODE_GLOBAL** | Variable statique globale | Applications desktop |

#### Utilisation

**a) Récupérer l'utilisateur connecté**

```java
@RestController
public class UserController {
    
    @GetMapping("/api/v1/me")
    public UserDTO getCurrentUser() {
        // Méthode 1 : Via SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        // Méthode 2 : Via annotation @AuthenticationPrincipal
        return userService.getUserByUsername(username);
    }
    
    @GetMapping("/api/v1/profile")
    public UserDTO getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserByUsername(userDetails.getUsername());
    }
}
```

**b) Définir manuellement l'authentification**

```java
public void authenticateUser(String username, String password) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    
    UsernamePasswordAuthenticationToken authentication = 
        new UsernamePasswordAuthenticationToken(
            userDetails, 
            password, 
            userDetails.getAuthorities()
        );
    
    SecurityContextHolder.getContext().setAuthentication(authentication);
}
```

**c) Vérifier si l'utilisateur est authentifié**

```java
public boolean isUserAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null 
        && authentication.isAuthenticated() 
        && !(authentication instanceof AnonymousAuthenticationToken);
}
```

**d) Nettoyer le contexte**

```java
@PostMapping("/api/v1/auth/logout")
public void logout() {
    SecurityContextHolder.clearContext();  // Important pour éviter les fuites
}
```

---

### 12. 🔍 Filtres Clés à Comprendre

#### Architecture des filtres

```
Requête HTTP
    ↓
╔══════════════════════════════════════════╗
║  1. SecurityContextPersistenceFilter      ║  Restaure le contexte de sécurité
╠══════════════════════════════════════════╣
║  2. HeaderWriterFilter                    ║  Ajoute headers de sécurité
╠══════════════════════════════════════════╣
║  3. CsrfFilter                            ║  Valide le token CSRF
╠══════════════════════════════════════════╣
║  4. LogoutFilter                          ║  Gère la déconnexion
╠══════════════════════════════════════════╣
║  5. UsernamePasswordAuthenticationFilter  ║  Authentification form-based
╠══════════════════════════════════════════╣
║  6. JwtAuthenticationFilter (custom)      ║  Authentification JWT
╠══════════════════════════════════════════╣
║  7. BasicAuthenticationFilter             ║  Authentification Basic
╠══════════════════════════════════════════╣
║  8. RequestCacheAwareFilter               ║  Cache des requêtes
╠══════════════════════════════════════════╣
║  9. SecurityContextHolderAwareFilter      ║  Intégration Servlet API
╠══════════════════════════════════════════╣
║  10. AnonymousAuthenticationFilter        ║  Utilisateurs anonymes
╠══════════════════════════════════════════╣
║  11. SessionManagementFilter              ║  Gestion des sessions
╠══════════════════════════════════════════╣
║  12. ExceptionTranslationFilter           ║  Traduction des exceptions
╠══════════════════════════════════════════╣
║  13. FilterSecurityInterceptor            ║  Décisions d'autorisation
╚══════════════════════════════════════════╝
    ↓
Controller
```

#### Filtres détaillés

**a) UsernamePasswordAuthenticationFilter**

Gère l'authentification via formulaire.

```java
// Déclenché automatiquement sur POST /login
// avec paramètres "username" et "password"
```

**b) JwtAuthenticationFilter (custom)**

Filtre personnalisé pour JWT.

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        
        // 1. Extraire le token du header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String jwt = authHeader.substring(7);
        
        // 2. Extraire le username du token
        String username = jwtService.extractUsername(jwt);
        
        // 3. Si username valide et pas déjà authentifié
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 4. Charger l'utilisateur
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 5. Valider le token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                
                // 6. Créer l'authentification
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 7. Définir dans le contexte
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 8. Continuer la chaîne
        filterChain.doFilter(request, response);
    }
}
```

**c) ExceptionTranslationFilter**

Convertit les exceptions de sécurité en réponses HTTP appropriées.

**d) FilterSecurityInterceptor**

Dernier filtre - prend les décisions d'autorisation finales.

---

### 13. 🔒 HTTPS / Certificates

**Définition** : Protocole sécurisé pour chiffrer les communications HTTP.

#### Pourquoi HTTPS est OBLIGATOIRE en production ?

❌ **Sans HTTPS (HTTP) :**
- Mots de passe transmis en clair
- Tokens JWT visibles sur le réseau
- Attaques Man-in-the-Middle faciles
- Cookies volables

✅ **Avec HTTPS :**
- Chiffrement bout-en-bout
- Intégrité des données
- Authentification du serveur
- Confiance des utilisateurs

#### Configuration HTTPS dans Spring Boot

**a) Générer un certificat auto-signé (DEV uniquement)**

```bash
keytool -genkeypair -alias supplychainx -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 365 \
  -storepass password
```

**b) Configuration application.properties**

```properties
# HTTPS Configuration
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=supplychainx

# Forcer HTTPS
server.ssl.enabled-protocols=TLSv1.2,TLSv1.3
```

**c) Rediriger HTTP vers HTTPS**

```java
@Configuration
public class HttpsConfig {
    
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }
    
    private Connector redirectConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
}
```

**d) Production : Utiliser Let's Encrypt ou un certificat commercial**

```bash
# Let's Encrypt avec Certbot
sudo certbot certonly --standalone -d supplychainx.com
```

#### Headers de sécurité HTTPS

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.headers(headers -> headers
        .httpStrictTransportSecurity(hsts -> hsts
            .includeSubDomains(true)
            .maxAgeInSeconds(31536000)  // 1 an
        )
        .contentSecurityPolicy(csp -> csp
            .policyDirectives("default-src 'self'; script-src 'self'")
        )
        .frameOptions(frame -> frame.deny())
        .xssProtection(xss -> xss.block(true))
    );
    
    return http.build();
}
```

---

## 🎯 Best Practices

### 1. ✅ Sécurité des Mots de Passe

```java
// ✅ BON
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}

// ❌ MAUVAIS
user.setPassword("plaintext");  // JAMAIS !
```

### 2. ✅ Validation des Inputs

```java
@PostMapping("/api/v1/users")
public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO dto) {
    // @Valid déclenche la validation Bean Validation
    return ResponseEntity.ok(userService.create(dto));
}
```

### 3. ✅ Principe du Moindre Privilège

```java
// Donnez le minimum de permissions nécessaires
@PreAuthorize("hasAuthority('READ_PRODUCTS')")  // Spécifique
// au lieu de
@PreAuthorize("hasRole('ADMIN')")  // Trop large
```

### 4. ✅ Logout Proper

```java
@PostMapping("/api/v1/auth/logout")
public void logout(HttpServletRequest request) {
    SecurityContextHolder.clearContext();
    HttpSession session = request.getSession(false);
    if (session != null) {
        session.invalidate();
    }
}
```

### 5. ✅ Rate Limiting

```java
// Protéger contre les attaques brute-force
@Bean
public RateLimiter rateLimiter() {
    return RateLimiter.create(10.0);  // 10 requêtes/seconde max
}
```

### 6. ✅ Auditing

```java
@EntityListeners(AuditingEntityListener.class)
@Entity
public class User {
    @CreatedBy
    private String createdBy;
    
    @LastModifiedBy
    private String lastModifiedBy;
    
    @CreatedDate
    private LocalDateTime createdDate;
    
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
```

---

## 📚 Ressources

- 📖 [Documentation Officielle Spring Security](https://spring.io/projects/spring-security)
- 📖 [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
- 📖 [Baeldung Spring Security](https://www.baeldung.com/security-spring)
- 📖 [OWASP Top 10](https://owasp.org/www-project-top-ten/)

---

## 🔐 Intégration Keycloak dans SupplyChainX

### Vue d'Ensemble

**Keycloak** est utilisé dans SupplyChainX comme serveur d'authentification et d'autorisation centralisé. Il offre une solution complète d'Identity and Access Management (IAM) basée sur les standards **OAuth 2.0** et **OpenID Connect**.

### Pourquoi Keycloak ?

| Avantage | Description |
|----------|-------------|
| 🔒 **Sécurité Renforcée** | Authentification multi-facteurs, protection contre les attaques |
| 🎯 **Centralisé** | Un seul point de gestion pour tous les utilisateurs |
| 🚀 **Standards** | OAuth 2.0, OpenID Connect, SAML 2.0 |
| 🔄 **SSO** | Single Sign-On entre applications |
| 📊 **Administration** | Console d'administration complète |
| 🔌 **Intégration** | Compatible avec LDAP, Active Directory, etc. |

### Architecture avec Keycloak

```
┌─────────────────┐
│   Frontend      │
│   Application   │
└────────┬────────┘
         │ 1. Demande authentification
         ▼
┌─────────────────┐
│    Keycloak     │◄───── 2. Login utilisateur
│   Auth Server   │
└────────┬────────┘
         │ 3. Retourne JWT Token
         ▼
┌─────────────────┐
│  SupplyChainX   │
│   Backend API   │◄───── 4. Requête API avec token
│  (Spring Boot)  │
└─────────────────┘
         │ 5. Valide le token
         ▼
    ✅ Accès autorisé
```

### Configuration dans SupplyChainX

#### 1. Dépendances Maven

```xml
<!-- Spring Security OAuth2 Resource Server -->
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
```

#### 2. Configuration application.yml

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          # URL de l'émetteur du token JWT
          issuer-uri: http://localhost:8180/realms/supplychainx
          # URL des certificats publics pour valider les signatures
          jwk-set-uri: http://localhost:8180/realms/supplychainx/protocol/openid-connect/certs

keycloak:
  # Nom du realm Keycloak
  realm: supplychainx
  # URL du serveur Keycloak
  auth-server-url: http://localhost:8180
  # ID du client Keycloak
  resource: supplychainx-client
  # Credentials du client
  credentials:
    secret: ${KEYCLOAK_CLIENT_SECRET}
  # Mode bearer-only (pour les API)
  bearer-only: true
  # SSL requis (external = uniquement pour les connexions externes)
  ssl-required: external
```

#### 3. Configuration Spring Security

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Endpoints publics
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // Module Approvisionnement
                .requestMatchers("/api/v1/fournisseurs/**")
                    .hasAnyRole("GESTIONNAIRE_APPROVISIONNEMENT", "RESPONSABLE_ACHATS", "ADMIN")
                .requestMatchers("/api/v1/matieres-premieres/**")
                    .hasAnyRole("GESTIONNAIRE_APPROVISIONNEMENT", "ADMIN")
                
                // Module Production
                .requestMatchers("/api/v1/produits/**")
                    .hasAnyRole("CHEF_PRODUCTION", "PLANIFICATEUR", "ADMIN")
                .requestMatchers("/api/v1/ordres-production/**")
                    .hasAnyRole("CHEF_PRODUCTION", "PLANIFICATEUR", "ADMIN")
                
                // Module Livraison (GraphQL)
                .requestMatchers("/graphql/**", "/graphiql/**")
                    .hasAnyRole("GESTIONNAIRE_COMMERCIAL", "RESPONSABLE_LOGISTIQUE", "ADMIN")
                
                // Module Utilisateurs (Admin uniquement)
                .requestMatchers("/api/v1/users/**", "/api/v1/roles/**")
                    .hasRole("ADMIN")
                
                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().authenticated()
            )
            // Configuration OAuth2 Resource Server avec JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            // Désactiver CSRF pour les API REST
            .csrf(csrf -> csrf.disable())
            // Configuration CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    /**
     * Convertit les rôles du JWT en authorities Spring Security
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        
        // Préfixe "ROLE_" pour les rôles Spring Security
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        
        // Nom du claim dans le JWT contenant les rôles
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        
        return jwtAuthenticationConverter;
    }

    /**
     * Configuration CORS pour permettre les requêtes cross-origin
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### Rôles et Permissions

#### Rôles Définis dans Keycloak

| Rôle | Module | Permissions |
|------|--------|-------------|
| `ADMIN` | Tous | Accès complet à toutes les fonctionnalités |
| `GESTIONNAIRE_APPROVISIONNEMENT` | Approvisionnement | CRUD fournisseurs, matières premières, commandes |
| `RESPONSABLE_ACHATS` | Approvisionnement | Validation et gestion des commandes d'achat |
| `SUPERVISEUR_LOGISTIQUE` | Approvisionnement | Supervision de la logistique |
| `CHEF_PRODUCTION` | Production | CRUD produits, ordres de production, BOM |
| `PLANIFICATEUR` | Production | Planification et ordonnancement de la production |
| `SUPERVISEUR_PRODUCTION` | Production | Supervision de la production |
| `GESTIONNAIRE_COMMERCIAL` | Livraison | Gestion des clients et commandes (GraphQL) |
| `RESPONSABLE_LOGISTIQUE` | Livraison | Coordination et suivi des livraisons |
| `SUPERVISEUR_LIVRAISONS` | Livraison | Supervision des livraisons |

### Flux d'Authentification

#### 1. Obtenir un Token

```bash
curl -X POST http://localhost:8180/realms/supplychainx/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=supplychainx-client" \
  -d "client_secret=YOUR_CLIENT_SECRET" \
  -d "username=admin" \
  -d "password=admin123" \
  -d "grant_type=password"
```

**Réponse** :
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer"
}
```

#### 2. Structure du JWT Token

```json
{
  "header": {
    "alg": "RS256",
    "typ": "JWT",
    "kid": "..."
  },
  "payload": {
    "exp": 1735841234,
    "iat": 1735840934,
    "jti": "...",
    "iss": "http://localhost:8180/realms/supplychainx",
    "sub": "user-uuid",
    "typ": "Bearer",
    "azp": "supplychainx-client",
    "preferred_username": "admin",
    "email": "admin@supplychainx.com",
    "roles": [
      "ADMIN",
      "GESTIONNAIRE_APPROVISIONNEMENT"
    ],
    "scope": "profile email"
  }
}
```

#### 3. Utiliser le Token dans les Requêtes

```bash
# API REST
curl -X GET http://localhost:8080/api/v1/fournisseurs \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."

# API GraphQL
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{"query": "{ getAllClients { content { name } } }"}'
```

### Validation du Token

Spring Security valide automatiquement le token JWT :

1. **Vérification de la signature** : Utilise les certificats publics de Keycloak (JWK Set)
2. **Vérification de l'expiration** : Vérifie que le token n'est pas expiré (`exp` claim)
3. **Vérification de l'émetteur** : Vérifie que l'`iss` correspond à `issuer-uri`
4. **Extraction des rôles** : Extrait les rôles du claim `roles`
5. **Création du SecurityContext** : Crée un `Authentication` object avec les rôles

### Gestion des Erreurs

#### 401 Unauthorized - Token Manquant ou Invalide

```json
{
  "timestamp": "2026-01-02T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/v1/fournisseurs"
}
```

**Solutions** :
- Vérifier que le header `Authorization: Bearer <token>` est présent
- Obtenir un nouveau token si expiré

#### 403 Forbidden - Permissions Insuffisantes

```json
{
  "timestamp": "2026-01-02T10:30:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/v1/users"
}
```

**Solutions** :
- Vérifier que l'utilisateur a le rôle requis
- Contacter l'administrateur pour obtenir les permissions

### Tests de Sécurité

#### Test Unitaire avec Mock JWT

```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminAccess() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTIONNAIRE_APPROVISIONNEMENT")
    void testGestionnaireAccess() throws Exception {
        mockMvc.perform(get("/api/v1/fournisseurs"))
            .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/fournisseurs"))
            .andExpect(status().isUnauthorized());
    }
}
```

### Ressources Keycloak

- 📖 **Guide de Configuration** : [docs/Keycloak-Setup.md](Keycloak-Setup.md)
- 🚀 **Exemples d'API** : [api/Keycloak-API-Examples.md](../api/Keycloak-API-Examples.md)
- 🌐 **Documentation Officielle** : https://www.keycloak.org/documentation
- 🔗 **Console Admin** : http://localhost:8180

---

## 📚 Ressources Supplémentaires

- [README Principal](../README.md)
- [Guide GraphQL](GraphQL.md)
- [Pipeline CI/CD](PIPELINE.md)

---

**SupplyChainX** - Sécurité Robuste avec Spring Security 🔐

