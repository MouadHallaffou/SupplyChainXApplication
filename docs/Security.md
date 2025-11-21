### Documentation Spring Security (FR)

Spring Security est un framework puissant et personnalisable pour la gestion de la sécurité dans les applications Java. Il fournit des fonctionnalités telles que l'authentification, l'autorisation, la protection contre les attaques CSRF, et bien plus encore.

#### Installation
Pour ajouter Spring Security à votre projet Maven, ajoutez la dépendance suivante dans votre fichier `pom.xml` :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

#### Points essentiels

1. Authentication

Comment Spring Security vérifie qui tu es.

Mécanismes : UserDetails, UserDetailsService, PasswordEncoder, login form, Basic Auth, JWT, OAuth2.

## 2. Authorization

Comment Spring Security décide ce que tu as le droit de faire.

Mécanismes : @PreAuthorize, rôles, authorities, règles HTTP, ACL si niveau avancé.

## 3. SecurityFilterChain

Comment les filtres sont organisés.

Où on définit les règles http.authorizeHttpRequests()

Comment une requête traverse la chaîne.

## 4. The Big Boss : DelegatingFilterProxy

Le proxy installé par Spring Boot qui transfère tout vers FilterChainProxy.

Porte d’entrée de toute la sécurité.

## 5. PasswordEncoder

Importance du hashing.

Pourquoi jamais stocker un password “en clair”.

## 6. Session Management

Session fixation

Timeout

Cookie JSESSIONID

## 7. Stateless vs Stateful

Différence entre session (stateful) et JWT/Token (stateless).

Comment configurer le mode stateless.

## 8. CSRF

Pourquoi c’est activé par défaut en session.

Pourquoi on le désactive pour les API REST + JWT.

## 9. CORS

Autoriser les frontends Angular/React à consommer ton backend.

Configurer CorsConfiguration.

## 10. Exception Handling

AuthenticationEntryPoint

AccessDeniedHandler

Comment renvoyer des erreurs custom (style JSON clean).

## 11. SecurityContext

Où Spring stocke l’utilisateur authentifié.

Comment fonctionne SecurityContextHolder.

## 12. Filters clés à comprendre

UsernamePasswordAuthenticationFilter

JwtAuthenticationFilter (custom)

ExceptionTranslationFilter

FilterSecurityInterceptor

## 13. HTTPS / Certificates

Pourquoi Spring Security doit TOUJOURS tourner derrière HTTPS en prod.

fixe moi le file readme suivant afin de donner une description pour ce service dans j'ai etulise graghql