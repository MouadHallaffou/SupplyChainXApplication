# Les Filter Chains de Spring Security - Guide Complet

## 📌 Introduction

La **SecurityFilterChain** est le cœur de l'architecture Spring Security. C'est une chaîne de filtres Servlet qui intercepte chaque requête HTTP pour appliquer les mécanismes de sécurité. Comprendre cette architecture est essentiel pour maîtriser Spring Security.

---

## 🏗️ Architecture Globale

### Le Principe de la Chaîne de Filtres

Spring Security s'intègre dans le cycle de vie des Servlets Java EE via le **pattern Chain of Responsibility**. Chaque filtre a une responsabilité spécifique et peut :
- Traiter la requête
- La modifier
- La bloquer
- La passer au filtre suivant

```
Client → DelegatingFilterProxy → FilterChainProxy → Security Filters → Application
```

---

## 🔗 Les Composants Principaux

### 1. DelegatingFilterProxy

**Rôle** : Faire le pont entre le conteneur Servlet et le contexte Spring.

**Problème résolu** : Les filtres Servlet sont gérés par le conteneur web (Tomcat, Jetty), pas par Spring. Le `DelegatingFilterProxy` permet aux filtres Spring d'être intégrés dans la chaîne Servlet standard.

**Fonctionnement** :
```
web.xml ou ServletContext
    ↓
DelegatingFilterProxy (enregistré comme filtre Servlet)
    ↓
Délègue au FilterChainProxy (bean Spring nommé "springSecurityFilterChain")
    ↓
Exécute la SecurityFilterChain
```

**Configuration automatique** : Spring Boot configure automatiquement ce proxy via `SecurityFilterAutoConfiguration`.

---

### 2. FilterChainProxy

**Rôle** : Point d'entrée unique pour toutes les requêtes sécurisées. Il orchestre l'exécution des différentes `SecurityFilterChain`.

**Responsabilités** :
- Sélectionner la bonne `SecurityFilterChain` selon l'URL
- Gérer plusieurs chaînes de filtres pour différents patterns d'URLs
- Nettoyer le `SecurityContext` après la requête
- Intégrer les callbacks de Spring Security

**Exemple de configuration multi-chaînes** :
```java
@Configuration
@EnableWebSecurity
public class MultiChainSecurityConfig {
    
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS));
        return http.build();
    }
    
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .formLogin(Customizer.withDefaults());
        return http.build();
    }
}
```

**Point clé** : L'annotation `@Order` détermine la priorité. La première chaîne correspondante est utilisée.

---

### 3. SecurityFilterChain

**Définition** : Interface représentant une chaîne de filtres de sécurité avec un pattern d'URL.

```java
public interface SecurityFilterChain {
    boolean matches(HttpServletRequest request);
    List<Filter> getFilters();
}
```

**Configuration moderne** :
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .build();
}
```

---

## 🔍 Les Filtres de Sécurité Principaux

### Ordre d'Exécution Standard

Voici l'ordre typique des filtres dans une SecurityFilterChain (environ 15-20 filtres selon la configuration) :

```
1. ForceEagerSessionCreationFilter (si configuré)
2. ChannelProcessingFilter (redirige HTTP → HTTPS)
3. WebAsyncManagerIntegrationFilter
4. SecurityContextPersistenceFilter / SecurityContextHolderFilter
5. HeaderWriterFilter
6. CorsFilter
7. CsrfFilter
8. LogoutFilter
9. OAuth2AuthorizationRequestRedirectFilter (si OAuth2)
10. Saml2WebSsoAuthenticationRequestFilter (si SAML)
11. X509AuthenticationFilter (si authentification par certificat)
12. AbstractPreAuthenticatedProcessingFilter
13. CasAuthenticationFilter (si CAS)
14. OAuth2LoginAuthenticationFilter (si OAuth2)
15. Saml2WebSsoAuthenticationFilter (si SAML)
16. UsernamePasswordAuthenticationFilter (Form Login)
17. DefaultLoginPageGeneratingFilter
18. DefaultLogoutPageGeneratingFilter
19. ConcurrentSessionFilter
20. DigestAuthenticationFilter
21. BearerTokenAuthenticationFilter (OAuth2 Resource Server)
22. BasicAuthenticationFilter (Basic Auth)
23. RequestCacheAwareFilter
24. SecurityContextHolderAwareRequestFilter
25. JaasApiIntegrationFilter
26. RememberMeAuthenticationFilter
27. AnonymousAuthenticationFilter
28. OAuth2AuthorizationCodeGrantFilter (si OAuth2)
29. SessionManagementFilter
30. ExceptionTranslationFilter
31. AuthorizationFilter
```

---

## 🎯 Filtres Essentiels Détaillés

### 1. SecurityContextHolderFilter (anciennement SecurityContextPersistenceFilter)

**Rôle** : Gérer le `SecurityContext` entre les requêtes.

**Fonctionnement** :
```java
// Avant la requête
SecurityContext context = repository.loadContext(request);
SecurityContextHolder.setContext(context);

// Traiter la requête via la chaîne

// Après la requête
repository.saveContext(SecurityContextHolder.getContext(), request, response);
SecurityContextHolder.clearContext();
```

**Stateful (sessions)** :
- Utilise `HttpSessionSecurityContextRepository`
- Stocke le contexte dans la session HTTP (JSESSIONID)

**Stateless (APIs REST)** :
- Utilise `NullSecurityContextRepository`
- Ne persiste rien entre les requêtes

---

### 2. BasicAuthenticationFilter

**Rôle** : Intercepter et traiter les headers `Authorization: Basic`.

**Processus** :
```
1. Détecte le header "Authorization: Basic base64(username:password)"
2. Décode le Base64
3. Extrait username et password
4. Crée un UsernamePasswordAuthenticationToken (non authentifié)
5. Délègue à l'AuthenticationManager
6. Si succès → place l'Authentication dans le SecurityContext
7. Si échec → retourne 401 Unauthorized avec WWW-Authenticate header
```

**Code simplifié** :
```java
String header = request.getHeader("Authorization");
if (header != null && header.startsWith("Basic ")) {
    String base64Token = header.substring(6);
    String token = new String(Base64.decode(base64Token));
    String[] credentials = token.split(":", 2);
    
    UsernamePasswordAuthenticationToken authRequest = 
        new UsernamePasswordAuthenticationToken(credentials[0], credentials[1]);
    
    Authentication authResult = authenticationManager.authenticate(authRequest);
    SecurityContextHolder.getContext().setAuthentication(authResult);
}
```

---

### 3. UsernamePasswordAuthenticationFilter

**Rôle** : Traiter les soumissions de formulaire de login (Form Login).

**Déclenchement** : POST sur `/login` (par défaut) avec paramètres `username` et `password`.

**Processus** :
```
1. Intercepte POST /login
2. Extrait username et password des paramètres
3. Crée UsernamePasswordAuthenticationToken
4. Authentifie via AuthenticationManager
5. Si succès :
   - Crée une session (JSESSIONID)
   - Stocke l'Authentication dans le SecurityContext
   - Redirige vers la page de succès
6. Si échec :
   - Redirige vers /login?error
```

**Configuration** :
```java
http.formLogin(form -> form
    .loginPage("/custom-login")           // Page personnalisée
    .loginProcessingUrl("/perform_login") // URL de traitement
    .usernameParameter("email")           // Paramètre custom
    .passwordParameter("pass")            // Paramètre custom
    .defaultSuccessUrl("/dashboard")      // Redirection succès
    .failureUrl("/login?error=true")      // Redirection échec
);
```

---

### 4. CsrfFilter

**Rôle** : Protéger contre les attaques Cross-Site Request Forgery.

**Fonctionnement** :
```
1. Pour toute requête modifiante (POST, PUT, DELETE, PATCH) :
   - Vérifie la présence d'un token CSRF
   - Compare avec le token stocké en session
2. Si valide → continue
3. Si invalide ou absent → rejette avec 403 Forbidden
```

**Quand le désactiver** :
- APIs REST stateless (pas de cookies de session)
- Authentification par tokens (JWT, OAuth2)

**Configuration** :
```java
// Désactiver complètement (APIs REST)
http.csrf(csrf -> csrf.disable());

// Désactiver pour certains endpoints
http.csrf(csrf -> csrf
    .ignoringRequestMatchers("/api/**")
);

// Personnaliser le repository
http.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
);
```

---

### 5. CorsFilter

**Rôle** : Gérer les Cross-Origin Resource Sharing pour les applications frontend/backend séparées.

**Configuration** :
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}

// Dans la SecurityFilterChain
http.cors(Customizer.withDefaults());
```

---

### 6. ExceptionTranslationFilter

**Rôle** : Convertir les exceptions de sécurité en réponses HTTP appropriées.

**Gestion des exceptions** :
- `AuthenticationException` → 401 Unauthorized (ou redirection vers login)
- `AccessDeniedException` → 403 Forbidden (ou page d'erreur)

**Processus** :
```
Try {
    // Filtres suivants (y compris AuthorizationFilter)
}
Catch (AuthenticationException) {
    → Déclencher AuthenticationEntryPoint
    → Basic Auth : envoie WWW-Authenticate header
    → Form Login : redirige vers /login
}
Catch (AccessDeniedException) {
    → Si anonyme : déclenche AuthenticationEntryPoint
    → Sinon : déclenche AccessDeniedHandler (403)
}
```

---

### 7. AuthorizationFilter

**Rôle** : Vérifier les autorisations d'accès aux ressources (dernier filtre de la chaîne).

**Fonctionnement** :
```
1. Récupère l'Authentication du SecurityContext
2. Évalue les règles d'autorisation (requestMatchers)
3. Si autorisé → passe au Controller
4. Si refusé → lance AccessDeniedException
```

**Configuration des règles** :
```java
http.authorizeHttpRequests(auth -> auth
    // Accès public
    .requestMatchers("/", "/public/**").permitAll()
    
    // Par rôle
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/supplies/**").hasRole("GESTIONNAIRE_APPROVISIONNEMENT")
    
    // Par authority
    .requestMatchers("/api/orders/**").hasAuthority("ORDER_WRITE")
    
    // Par méthode HTTP
    .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
    
    // Expressions SpEL
    .requestMatchers("/api/users/{id}/**").access(
        new WebExpressionAuthorizationManager("@userSecurity.checkUserId(authentication, #id)")
    )
    
    // Authentification requise pour tout le reste
    .anyRequest().authenticated()
);
```

---

### 8. LogoutFilter

**Rôle** : Gérer la déconnexion des utilisateurs.

**Actions par défaut** :
```
1. Invalide la session HTTP
2. Nettoie le SecurityContext
3. Supprime les cookies (JSESSIONID, remember-me)
4. Redirige vers /login?logout
```

**Configuration** :
```java
http.logout(logout -> logout
    .logoutUrl("/perform_logout")
    .logoutSuccessUrl("/login?logout")
    .invalidateHttpSession(true)
    .deleteCookies("JSESSIONID", "remember-me")
    .addLogoutHandler((request, response, auth) -> {
        // Logique personnalisée (ex: audit)
    })
);
```

---

### 9. AnonymousAuthenticationFilter

**Rôle** : Créer une authentification anonyme si aucune authentification n'existe.

**Pourquoi ?** : Permet d'avoir toujours un objet `Authentication` dans le `SecurityContext`, même pour les utilisateurs non connectés.

**Authentication anonyme** :
```java
AnonymousAuthenticationToken(
    "anonymousUser",           // Principal
    "anonymousUser",           // Credentials
    [ROLE_ANONYMOUS]           // Authorities
)
```

**Utilité** :
```java
// Permet des règles comme
.requestMatchers("/public").hasRole("ANONYMOUS")

// Ou vérifier si l'utilisateur est anonyme
if (authentication instanceof AnonymousAuthenticationToken) {
    // Utilisateur non connecté
}
```

---

### 10. SessionManagementFilter

**Rôle** : Gérer les sessions et protéger contre les attaques de session.

**Protections** :
- **Session Fixation** : Régénère le JSESSIONID après login
- **Concurrent Sessions** : Limite le nombre de sessions par utilisateur
- **Invalid Session** : Détecte et gère les sessions invalides

**Configuration** :
```java
http.sessionManagement(session -> session
    // Politique de création
    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
    // ou ALWAYS, NEVER, STATELESS
    
    // Protection session fixation
    .sessionFixation().newSession()
    
    // Contrôle des sessions concurrentes
    .maximumSessions(1)
    .maxSessionsPreventsLogin(true) // Bloquer nouvelles sessions
    .expiredUrl("/session-expired")
);
```

---

## 🔐 Flux Complet d'une Requête Sécurisée

### Scénario 1 : Basic Auth sur API REST

```
GET /api/supplies
Authorization: Basic dXNlcjpwYXNz

    ↓
[1] DelegatingFilterProxy
    ↓
[2] FilterChainProxy (sélectionne la SecurityFilterChain)
    ↓
[3] SecurityContextHolderFilter
    - Crée un SecurityContext vide
    - Le place dans SecurityContextHolder
    ↓
[4] HeaderWriterFilter (ajoute headers de sécurité)
    ↓
[5] CorsFilter (vérifie CORS si configuré)
    ↓
[6] LogoutFilter (pas de /logout, continue)
    ↓
[7] BasicAuthenticationFilter
    - Détecte le header Authorization: Basic
    - Décode : "user:pass"
    - Crée UsernamePasswordAuthenticationToken(user, pass)
    - Appelle AuthenticationManager
        → AuthenticationProvider
            → UserDetailsService.loadUserByUsername("user")
            → PasswordEncoder.matches("pass", storedHash)
        → Retourne Authentication authentifiée
    - Place Authentication dans SecurityContext
    ↓
[8] RequestCacheAwareFilter
    ↓
[9] SecurityContextHolderAwareRequestFilter
    ↓
[10] AnonymousAuthenticationFilter (skip, déjà authentifié)
    ↓
[11] SessionManagementFilter (skip en STATELESS)
    ↓
[12] ExceptionTranslationFilter (try/catch autour des suivants)
    ↓
[13] AuthorizationFilter
    - Récupère Authentication du SecurityContext
    - Vérifie si user a le rôle requis pour /api/supplies
    - SI OUI → continue vers le Controller
    - SI NON → lance AccessDeniedException
        → ExceptionTranslationFilter la capture
        → Retourne 403 Forbidden
    ↓
[14] Controller traite la requête
    ↓
[15] Retour via les filtres (dans l'ordre inverse)
    ↓
[16] SecurityContextHolderFilter
    - NullSecurityContextRepository.saveContext() (ne fait rien en stateless)
    - SecurityContextHolder.clearContext()
    ↓
Response au client
```

---

### Scénario 2 : Form Login avec Session

```
POST /login
username=john&password=secret

    ↓
[1-6] Filtres initiaux (comme au-dessus)
    ↓
[7] UsernamePasswordAuthenticationFilter
    - Détecte POST /login
    - Extrait username et password
    - Crée UsernamePasswordAuthenticationToken(john, secret)
    - Appelle AuthenticationManager (même processus)
    - SI SUCCÈS :
        → Crée une session HTTP
        → Génère JSESSIONID
        → Stocke Authentication dans HttpSession
        → Redirige vers /dashboard
    - SI ÉCHEC :
        → Redirige vers /login?error
    ↓
Response avec Set-Cookie: JSESSIONID=xxx
```

**Requête suivante avec session** :
```
GET /dashboard
Cookie: JSESSIONID=xxx

    ↓
[3] SecurityContextHolderFilter
    - HttpSessionSecurityContextRepository.loadContext()
    - Récupère l'Authentication depuis la session
    - La place dans SecurityContextHolder
    ↓
[... autres filtres ...]
    ↓
[13] AuthorizationFilter
    - Authentication déjà présente
    - Vérifie les autorisations
    - Continue vers le Controller
```

---

## 🛠️ Personnalisation des Filtres

### Ajouter un Filtre Personnalisé

```java
public class CustomHeaderAuthenticationFilter extends OncePerRequestFilter {
    
    private final AuthenticationManager authenticationManager;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                     HttpServletResponse response, 
                                     FilterChain filterChain) 
            throws ServletException, IOException {
        
        String token = request.getHeader("X-API-KEY");
        
        if (token != null) {
            try {
                // Créer un token d'authentification personnalisé
                PreAuthenticatedAuthenticationToken authRequest = 
                    new PreAuthenticatedAuthenticationToken(token, null);
                
                // Authentifier
                Authentication authResult = 
                    authenticationManager.authenticate(authRequest);
                
                // Placer dans le contexte
                SecurityContextHolder.getContext().setAuthentication(authResult);
                
            } catch (AuthenticationException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

**Enregistrer le filtre** :
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http, 
                                        AuthenticationManager authManager) throws Exception {
    CustomHeaderAuthenticationFilter customFilter = 
        new CustomHeaderAuthenticationFilter(authManager);
    
    return http
        // Ajouter AVANT BasicAuthenticationFilter
        .addFilterBefore(customFilter, BasicAuthenticationFilter.class)
        
        // Ou APRÈS un filtre
        .addFilterAfter(customFilter, SecurityContextHolderFilter.class)
        
        // Ou À LA PLACE d'un filtre
        .addFilterAt(customFilter, UsernamePasswordAuthenticationFilter.class)
        
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .build();
}
```

---

## 📊 Debugging et Monitoring

### Activer les Logs Détaillés

```properties
# application.properties
logging.level.org.springframework.security=TRACE
logging.level.org.springframework.security.web.FilterChainProxy=DEBUG
```

**Sortie typique** :
```
DEBUG o.s.security.web.FilterChainProxy : Securing GET /api/supplies
DEBUG o.s.security.web.FilterChainProxy : /api/supplies at position 1 of 15 in additional filter chain; firing Filter: 'SecurityContextHolderFilter'
DEBUG o.s.security.web.FilterChainProxy : /api/supplies at position 2 of 15 in additional filter chain; firing Filter: 'HeaderWriterFilter'
...
DEBUG o.s.security.web.FilterChainProxy : /api/supplies at position 7 of 15 in additional filter chain; firing Filter: 'BasicAuthenticationFilter'
DEBUG o.s.s.w.a.www.BasicAuthenticationFilter : Found username 'john' in Basic Authorization header
...
```

---

### Endpoint Actuator pour Monitoring

```properties
# Activer l'endpoint des filtres
management.endpoints.web.exposure.include=security
```

Accès : `GET /actuator/mappings` pour voir tous les mappings de sécurité.

---

## 🎓 Bonnes Pratiques

### 1. Ordre des Filtres
✅ Respecter l'ordre standard de Spring Security
✅ Placer les filtres d'authentification avant AuthorizationFilter
✅ Utiliser `addFilterBefore/After` prudemment

### 2. Performance
✅ Utiliser `securityMatcher()` pour limiter le scope des chaînes
✅ Éviter les opérations lourdes dans les filtres
✅ Mettre en cache les résultats d'authentification quand possible

### 3. Stateless vs Stateful
✅ APIs REST → STATELESS + Basic Auth ou JWT
✅ Applications web classiques → STATEFUL + Form Login
✅ Ne pas mélanger les deux approches sans raison

### 4. Sécurité
✅ Toujours utiliser HTTPS avec Basic Auth
✅ Désactiver CSRF uniquement pour les APIs stateless
✅ Configurer CORS strictement (pas de `*` en production)
✅ Logger les échecs d'authentification pour la détection d'intrusion

---

## 📚 Résumé pour SupplyChainX

Pour votre projet de chaîne d'approvisionnement avec Basic Auth :

```java
@Configuration
@EnableWebSecurity
public class SupplyChainSecurityConfig {
    
    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/api/**")
            
            // Filtres actifs :
            // 1. SecurityContextHolderFilter (crée contexte vide)
            // 2. BasicAuthenticationFilter (authentifie via header)
            // 3. AuthorizationFilter (vérifie les rôles)
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/supplies/**")
                    .hasRole("GESTIONNAIRE_APPROVISIONNEMENT")
                .requestMatchers("/api/orders/**")
                    .hasRole("RESPONSABLE_ACHATS")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
            .build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public UserDetailsService users(PasswordEncoder encoder) {
        UserDetails gestionnaire = User.builder()
            .username("gestionnaire")
            .password(encoder.encode("password"))
            .roles("GESTIONNAIRE_APPROVISIONNEMENT")
            .build();
            
        UserDetails responsable = User.builder()
            .username("responsable")
            .password(encoder.encode("password"))
            .roles("RESPONSABLE_ACHATS")
            .build();
            
        return new InMemoryUserDetailsManager(gestionnaire, responsable);
    }
}
```

**Flux pour une requête** :
```
GET /api/supplies/12345
Authorization: Basic Z2VzdGlvbm5haXJlOnBhc3N3b3Jk

→ BasicAuthenticationFilter décode et authentifie
→ AuthorizationFilter vérifie GESTIONNAIRE_APPROVISIONNEMENT
→ Controller traite la requête
→ Réponse 200 OK (ou 403 si mauvais rôle)
```

---

## 🎯 Conclusion

La **SecurityFilterChain** est un mécanisme puissant mais complexe. Retenez :

1. **DelegatingFilterProxy** → pont Servlet/Spring
2. **FilterChainProxy** → orchestrateur de chaînes multiples
3. **SecurityFilterChain** → liste ordonnée de filtres avec pattern d'URL
4. **~15-20 filtres** s'exécutent dans un ordre précis
5. **Chaque filtre** a une responsabilité unique (authentification, autorisation, CSRF, etc.)
6. **L'ordre est critique** : contexte → authentification → autorisation

Maîtriser cette architecture vous permettra de comprendre, débugger et personnaliser efficacement Spring Security ! 🔐