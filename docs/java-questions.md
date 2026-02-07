# Guide Complet - Questions Techniques Java/Spring/Docker

## Java SE

### 1. Explication du fonctionnement de la JVM, JDK, JRE

**JVM (Java Virtual Machine)** : Machine virtuelle qui exécute le bytecode Java. Elle traduit le bytecode en instructions machine spécifiques au système d'exploitation. La JVM gère la mémoire (Heap et Stack), le garbage collection et assure l'indépendance de la plateforme.

**JRE (Java Runtime Environment)** : Environnement d'exécution contenant la JVM + les bibliothèques standards Java. Nécessaire pour exécuter des applications Java mais ne permet pas de compiler du code.

**JDK (Java Development Kit)** : Kit de développement contenant le JRE + les outils de développement (compilateur javac, debugger, etc.). Nécessaire pour développer des applications Java.

**Exemple** : Vous écrivez `Main.java` → utilisez `javac` (JDK) pour compiler en `Main.class` → la JVM (dans JRE) exécute le bytecode.

### 2. Pourquoi Java est multiplateforme ou portable ?

Java suit le principe **"Write Once, Run Anywhere"**. Le code source Java est compilé en bytecode intermédiaire (fichiers `.class`) qui n'est pas spécifique à une architecture matérielle. Chaque plateforme possède sa propre JVM qui traduit ce bytecode en instructions natives. Ainsi, le même bytecode s'exécute sur Windows, Linux, Mac sans recompilation.

**Exemple** : Un fichier `App.class` compilé sur Windows s'exécutera sur une JVM Linux sans modification.

### 3. Différence entre Stack et Heap

**Stack (Pile)** :
- Zone mémoire LIFO (Last In First Out)
- Stocke les variables locales et les appels de méthodes
- Mémoire allouée et libérée automatiquement
- Taille limitée (risque de StackOverflowError)
- Accès rapide
- Thread-safe (chaque thread a sa propre stack)

**Heap (Tas)** :
- Zone mémoire pour les objets et leurs variables d'instance
- Partagée entre tous les threads
- Mémoire gérée par le Garbage Collector
- Taille plus grande que la Stack
- Accès plus lent que la Stack
- Risque de OutOfMemoryError

**Exemple** :
```java
public void method() {
    int x = 10; // x stocké dans la Stack
    Person p = new Person(); // référence p dans Stack, objet Person dans Heap
}
```

### 4. Principes de la POO

**Encapsulation** : Regrouper les données et méthodes dans une classe et contrôler l'accès via des modificateurs (private, public, protected).

**Héritage** : Capacité d'une classe à hériter des propriétés et méthodes d'une classe parent.

**Polymorphisme** : Capacité d'un objet à prendre plusieurs formes (surcharge de méthodes, redéfinition, interfaces).

**Abstraction** : Cacher les détails d'implémentation et exposer uniquement les fonctionnalités essentielles via classes abstraites ou interfaces.

**Exemple** :
```java
public class Animal { // Abstraction
    private String nom; // Encapsulation
    public void manger() { } // Polymorphisme
}
public class Chien extends Animal { // Héritage
    @Override
    public void manger() { } // Polymorphisme
}
```

### 5. Différence entre Fail-fast et Fail-safe iterator

**Fail-fast** : Lance une `ConcurrentModificationException` si la collection est modifiée pendant l'itération (sauf via l'iterator lui-même). Fonctionne sur la collection originale. Utilisé par ArrayList, HashMap.

**Fail-safe** : Travaille sur une copie de la collection. Aucune exception levée si modification pendant l'itération. Utilisé par CopyOnWriteArrayList, ConcurrentHashMap.

**Exemple** :
```java
public class Exemple {
    List<String> list = new ArrayList<>();
    public void failFastExample() {
        for(String s : list) {
            list.add("new"); // Lance ConcurrentModificationException
        }
    }
    public void failSafeExample() {
        CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<>(list);
        for(String s : cowList) {
            cowList.add("new"); // Pas d'exception
        }
    }
}
```
### 6. À quoi sert le mot-clé volatile

Le mot-clé `volatile` garantit que les lectures et écritures d'une variable se font directement depuis/vers la mémoire principale et non depuis le cache CPU. Cela assure la visibilité des modifications entre threads mais ne garantit pas l'atomicité des opérations composées.

**Exemple** :
```java
private volatile boolean running = true;

// Thread 1
public void stop() { running = false; }

// Thread 2
public void run() {
    while(running) { } // Voit immédiatement le changement
}
```

### 7. Que signifie immutable object

Un objet immutable est un objet dont l'état ne peut pas être modifié après sa création. Les avantages incluent la thread-safety automatique, la possibilité d'utilisation comme clé dans HashMap et une meilleure prévisibilité du code.

**Exemple** :
```java
public final class Person {
    private final String name;
    private final int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
    // Pas de setters
}
```

### 8. Différence entre protected, private et public

**private** : Accessible uniquement dans la classe elle-même.

**protected** : Accessible dans la classe, les sous-classes et les classes du même package.

**public** : Accessible partout.

**default (package-private)** : Accessible uniquement dans le même package.

**Exemple** :
```java
public class A {
    private int x;    // uniquement dans A
    protected int y;  // A, sous-classes, même package
    public int z;     // partout
    int w;           // même package uniquement
}
```

### 9. À quoi sert le mot synchronized

Le mot-clé `synchronized` garantit qu'un seul thread à la fois peut exécuter un bloc de code ou une méthode. Il utilise un verrou (lock) sur l'objet pour assurer la synchronisation et éviter les problèmes de concurrence.

**Exemple** :
```java
public synchronized void increment() {
    counter++; // Un seul thread à la fois
}

public void method() {
    synchronized(this) {
        // code synchronisé
    }
}
```

### 10. Différence entre StringBuffer et StringBuilder

**StringBuffer** : Thread-safe (synchronisé), plus lent, utilisé dans environnements multi-threads.

**StringBuilder** : Non thread-safe, plus rapide, utilisé dans environnements single-thread.

Les deux sont mutables contrairement à String qui est immutable.

**Exemple** :
```java
StringBuilder sb = new StringBuilder("Hello");
sb.append(" World"); // Rapide, non synchronisé

StringBuffer sbf = new StringBuffer("Hello");
sbf.append(" World"); // Plus lent, thread-safe
```

### 11. Différence entre classe Abstraite et Interface

**Classe Abstraite** :
- Peut contenir méthodes concrètes et abstraites
- Peut avoir des variables d'instance
- Héritage simple uniquement (extends)
- Peut avoir des constructeurs
- Modificateurs d'accès variés

**Interface** :
- Toutes les méthodes sont abstraites par défaut (avant Java 8)
- Variables sont public static final par défaut
- Implémentation multiple possible (implements)
- Pas de constructeurs
- Méthodes public par défaut

**Exemple** :
```java
abstract class Animal {
    protected String nom;
    public Animal(String nom) { this.nom = nom; }
    public abstract void manger();
    public void dormir() { } // méthode concrète
}

interface Volant {
    void voler(); // abstract par défaut
}

class Oiseau extends Animal implements Volant {
    public void manger() { }
    public void voler() { }
}
```

### 12. Différence entre map() et flatMap() dans Stream API

**map()** : Transforme chaque élément du stream en un autre élément. Relation 1:1. Retourne `Stream<R>`.

**flatMap()** : Transforme chaque élément en un stream puis aplatit tous les streams en un seul. Relation 1:N. Retourne `Stream<R>` à partir de `Stream<Stream<R>>`.

**Exemple** :
```java
List<String> list = Arrays.asList("Hello", "World");

// map
list.stream()
    .map(String::toUpperCase) // ["HELLO", "WORLD"]
    
// flatMap
list.stream()
    .flatMap(s -> Arrays.stream(s.split(""))) 
    // ["H","e","l","l","o","W","o","r","l","d"]
```

### 13. Différence entre == et .equals()

**==** : Compare les références mémoire (adresses) pour les objets. Pour les types primitifs, compare les valeurs.

**.equals()** : Compare le contenu logique des objets selon l'implémentation de la méthode equals(). Par défaut (Object), se comporte comme ==.

**Exemple** :
```java
String s1 = new String("test");
String s2 = new String("test");
String s3 = s1;

s1 == s2;        // false (références différentes)
s1.equals(s2);   // true (contenu identique)
s1 == s3;        // true (même référence)

int a = 5, b = 5;
a == b;          // true (valeurs égales)
```

### 14. Différence entre type primitif et classe wrapper

**Type primitif** : Stocke la valeur directement en mémoire. Non objet. Performance optimale. Valeur par défaut (0, false). Stocké dans la Stack.

**Classe Wrapper** : Objet encapsulant un type primitif. Stocké dans le Heap. Peut être null. Offre des méthodes utilitaires. Autoboxing/Unboxing automatique.

**Exemple** :
```java
int x = 5;           // primitif, 4 bytes
Integer y = 5;       // wrapper, objet avec overhead mémoire
Integer z = null;    // possible avec wrapper
// int w = null;     // impossible avec primitif

List<Integer> list = new ArrayList<>(); // Collections nécessitent des objets
// List<int> impossible
```

### 15. Checked Exception vs Unchecked Exception

**Checked Exception** : Exceptions vérifiées à la compilation. Doivent être traitées (try-catch) ou déclarées (throws). Héritent de Exception (sauf RuntimeException). Exemples : IOException, SQLException.

**Unchecked Exception** : Exceptions non vérifiées à la compilation. Héritent de RuntimeException ou Error. Exemples : NullPointerException, ArrayIndexOutOfBoundsException.

**Exemple** :
```java
// Checked - doit être gérée
public void readFile() throws IOException {
    FileReader fr = new FileReader("file.txt");
}

// Unchecked - pas obligatoire de gérer
public void divide(int a, int b) {
    int result = a / b; // peut lancer ArithmeticException
}
```

### 16. À quoi sert le bloc finally

Le bloc `finally` s'exécute toujours, qu'une exception soit levée ou non, et même si un return est présent dans try ou catch. Utilisé pour libérer des ressources (fermeture de fichiers, connexions DB).

**Exemple** :
```java
try {
    // code qui peut lever une exception
    return "A";
} catch(Exception e) {
    return "B";
} finally {
    // S'exécute toujours avant le return
    // Fermeture de ressources
}
```

### 17. À quoi sert la classe Optional

`Optional<T>` est un conteneur pouvant contenir ou non une valeur non-null. Évite les NullPointerException et rend explicite la possibilité d'absence de valeur. Encourage un code plus fonctionnel.

**Exemple** :
```java
Optional<String> opt = Optional.ofNullable(getValue());

// Mauvaise pratique
if(opt.isPresent()) {
    String val = opt.get();
}

// Bonne pratique
opt.ifPresent(val -> System.out.println(val));
String result = opt.orElse("default");
String result2 = opt.orElseThrow(() -> new Exception());
```

### 18. C'est quoi un record

Un `record` (Java 14+) est une classe immuable compacte pour transporter des données. Le compilateur génère automatiquement : constructeur, getters, equals(), hashCode(), toString(). Les champs sont final par défaut.

**Exemple** :
```java
public record Person(String name, int age) { }

// Équivalent à :
public final class Person {
    private final String name;
    private final int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String name() { return name; }
    public int age() { return age; }
    // + equals, hashCode, toString
}
```

### 19. Explication du multithreading

Le multithreading permet l'exécution simultanée de plusieurs threads (fils d'exécution) au sein d'un même processus. Chaque thread partage la mémoire Heap mais possède sa propre Stack. Améliore les performances sur processeurs multi-cœurs et permet la réactivité des applications.

**Exemple** :
```java
// Via Thread
class MyThread extends Thread {
    public void run() {
        System.out.println("Thread running");
    }
}
new MyThread().start();

// Via Runnable
Thread t = new Thread(() -> {
    System.out.println("Thread running");
});
t.start();
```

### 20. À quoi sert la sérialisation

La sérialisation convertit un objet Java en flux d'octets pour le stocker (fichier, DB) ou le transmettre (réseau). La désérialisation reconstruit l'objet à partir du flux. La classe doit implémenter `Serializable`.

**Exemple** :
```java
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private transient String password; // non sérialisé
}

// Sérialisation
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("person.ser"));
oos.writeObject(person);

// Désérialisation
ObjectInputStream ois = new ObjectInputStream(new FileInputStream("person.ser"));
Person p = (Person) ois.readObject();
```

### 21. Différence entre sous-casting (downcasting) et sur-casting (upcasting)

**Upcasting** : Conversion d'un type dérivé vers un type parent. Implicite et toujours sûr. Perte d'accès aux méthodes spécifiques de la sous-classe.

**Downcasting** : Conversion d'un type parent vers un type dérivé. Explicite et peut lever ClassCastException si l'objet n'est pas du bon type.

**Exemple** :
```java
class Animal { }
class Chien extends Animal { 
    void aboyer() { }
}

Chien chien = new Chien();
Animal animal = chien; // Upcasting (implicite)

Animal a = new Chien();
Chien c = (Chien) a; // Downcasting (explicite)
c.aboyer(); // OK

Animal a2 = new Animal();
// Chien c2 = (Chien) a2; // ClassCastException
```

## Java JEE

### 22. Différence entre Cookie et HttpSession

**Cookie** : Petit fichier texte stocké côté client (navigateur). Taille limitée (4KB). Persiste entre les sessions selon expiration. Visible et modifiable par l'utilisateur. Transmis à chaque requête HTTP.

**HttpSession** : Objet stocké côté serveur. Taille non limitée. Détruit à l'expiration du timeout ou fermeture navigateur. Identifié par un JSESSIONID (souvent stocké dans un cookie). Plus sécurisé car données inaccessibles au client.

**Exemple** :
```java
// Cookie
Cookie cookie = new Cookie("username", "john");
cookie.setMaxAge(3600);
response.addCookie(cookie);

// Session
HttpSession session = request.getSession();
session.setAttribute("user", userObject);
```

### 23. Différence entre chargement EAGER et LAZY en JPA

**EAGER** : Les entités associées sont chargées immédiatement avec l'entité principale. Une seule requête SQL (souvent avec JOIN). Peut causer des problèmes de performance si relations complexes.

**LAZY** : Les entités associées sont chargées uniquement quand elles sont accédées. Requête SQL supplémentaire à l'accès. Risque de LazyInitializationException si session fermée. Par défaut pour @OneToMany et @ManyToMany.

**Exemple** :
```java
@Entity
public class Auteur {
    @OneToMany(fetch = FetchType.LAZY) // Par défaut
    private List<Livre> livres;
}

Auteur a = auteurRepo.findById(1); // SELECT auteur
// Pas de chargement des livres
a.getLivres().size(); // SELECT livres (si session active)

@OneToMany(fetch = FetchType.EAGER)
private List<Livre> livres; // Chargés immédiatement
```

### 24. Qu'est-ce que le "N+1 Select Problem" et comment le résoudre

Problème de performance où une requête charge N entités, puis N requêtes supplémentaires chargent leurs associations. Résultat : 1 + N requêtes au lieu d'une seule.

**Solutions** :
- JOIN FETCH en JPQL
- @EntityGraph
- @BatchSize
- Chargement EAGER (avec précaution)

**Exemple** :
```java
// Problème N+1
List<Auteur> auteurs = auteurRepo.findAll(); // 1 requête
for(Auteur a : auteurs) {
    a.getLivres().size(); // N requêtes supplémentaires
}

// Solution avec JOIN FETCH
@Query("SELECT a FROM Auteur a JOIN FETCH a.livres")
List<Auteur> findAllWithLivres(); // 1 seule requête
```

### 25. Différence entre Interceptor et Filter

**Filter** : Composant de l'API Servlet. Intercepte les requêtes HTTP avant qu'elles atteignent les servlets. Utilisé pour logging, authentification, compression. Configuré dans web.xml ou @WebFilter.

**Interceptor** : Composant du framework (Spring MVC). Intercepte les requêtes au niveau du DispatcherServlet, après le Filter mais avant le Controller. Accès au contexte Spring. Méthodes : preHandle, postHandle, afterCompletion.

**Exemple** :
```java
// Filter
@WebFilter("/*")
public class LogFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        // Avant
        chain.doFilter(req, res);
        // Après
    }
}

// Interceptor
public class AuthInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        // Retourne true pour continuer, false pour arrêter
        return true;
    }
}
```

### 26. Différence entre JAR et WAR

**JAR (Java ARchive)** : Archive contenant classes Java, ressources et métadonnées. Utilisé pour bibliothèques ou applications standalone. Exécutable avec `java -jar app.jar`. Contient MANIFEST.MF.

**WAR (Web ARchive)** : Archive spécifique aux applications web. Contient servlets, JSP, HTML, CSS, JS, bibliothèques. Structure standardisée (WEB-INF/). Déployé sur serveur d'application (Tomcat, JBoss). Contient web.xml.

**Exemple structure** :
```
app.jar
├── META-INF/
│   └── MANIFEST.MF
└── com/company/...

app.war
├── WEB-INF/
│   ├── web.xml
│   ├── classes/
│   └── lib/
├── index.html
└── css/
```

### 27. Qu'est-ce que le pattern DAO (Data Access Object)

Pattern de conception séparant la logique d'accès aux données de la logique métier. Le DAO encapsule toutes les opérations CRUD sur une entité. Permet de changer la source de données sans impacter le code métier.

**Exemple** :
```java
public interface UserDao {
    User findById(Long id);
    List<User> findAll();
    void save(User user);
    void delete(Long id);
}

public class UserDaoImpl implements UserDao {
    public User findById(Long id) {
        // JDBC, JPA, etc.
    }
}

// Service utilise le DAO
public class UserService {
    private UserDao userDao;
    
    public User getUser(Long id) {
        return userDao.findById(id);
    }
}
```

### 28. À quoi sert un ORM

**ORM (Object-Relational Mapping)** : Technique mappant les tables relationnelles aux objets Java. Évite l'écriture de SQL manuel. Gère automatiquement les relations, le lazy loading, le caching. Exemples : Hibernate, EclipseLink.

**Avantages** : Productivité accrue, abstraction de la DB, portabilité, gestion automatique des relations.

**Exemple** :
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(name = "username")
    private String username;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}

// Au lieu de : SELECT * FROM users WHERE id = ?
User user = entityManager.find(User.class, 1L);
```

### 29. À quoi sert l'interface EntityManager dans JPA

**EntityManager** : Interface principale de JPA pour interagir avec le contexte de persistance. Gère le cycle de vie des entités (persist, merge, remove, find). Crée des requêtes JPQL/Criteria. Gère les transactions.

**Exemple** :
```java
@PersistenceContext
private EntityManager em;

public void operations() {
    User user = new User();
    em.persist(user);        // INSERT
    
    User found = em.find(User.class, 1L); // SELECT
    found.setName("New");    // UPDATE (en transaction)
    
    em.remove(found);        // DELETE
    
    em.flush();              // Force synchronisation avec DB
    em.clear();              // Détache toutes les entités
}
```

### 30. Différence entre JPA et Hibernate

**JPA (Java Persistence API)** : Spécification standard Java définissant l'ORM. Interface seulement, pas d'implémentation. Annotations et API standardisées.

**Hibernate** : Implémentation de JPA (la plus populaire). Fournit également des fonctionnalités propriétaires au-delà de JPA. Peut être utilisé sans JPA.

**Exemple** :
```java
// JPA standard
@Entity
public class User {
    @Id
    private Long id;
}

// Hibernate spécifique
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User { }
```

### 31. Différence entre serveur d'application et serveur web

**Serveur Web** : Gère uniquement les requêtes HTTP. Sert du contenu statique (HTML, CSS, JS) et peut exécuter du contenu dynamique simple (CGI, PHP). Exemples : Apache HTTP Server, Nginx.

**Serveur d'Application** : Serveur web + conteneur pour applications Java EE. Supporte servlets, EJB, JMS, transactions distribuées, etc. Exemples : Tomcat (conteneur servlet), JBoss/WildFly, WebLogic, WebSphere.

**Exemple** :
```
Serveur Web (Apache) → Contenu statique + reverse proxy
Serveur Application (Tomcat) → Servlets + JSP + Spring apps
Serveur Application complet (WildFly) → EJB + JMS + JPA + CDI
```

### 32. C'est quoi une servlet et son rôle

**Servlet** : Classe Java côté serveur traitant les requêtes HTTP et générant des réponses. Gérée par un conteneur servlet (Tomcat). Cycle de vie : init(), service(), destroy(). Base des applications web Java.

**Exemple** :
```java
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.println("<h1>Hello World</h1>");
    }
}
```

### 33. Qu'est-ce que l'Inversion de Contrôle (IoC)

**IoC (Inversion of Control)** : Principe où le contrôle du flux d'exécution est inversé. Au lieu que le code appelle le framework, c'est le framework qui appelle le code. Le conteneur gère la création et la liaison des objets.

**Exemple** :
```java
// Sans IoC - contrôle dans le code
public class UserService {
    private UserDao userDao = new UserDaoImpl(); // Création manuelle
}

// Avec IoC - contrôle au framework
public class UserService {
    private UserDao userDao; // Injecté par le conteneur
    
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }
}
```

### 34. Qu'est-ce que l'injection de dépendance

**Injection de Dépendance** : Implémentation de l'IoC où les dépendances d'un objet sont fournies par un conteneur externe plutôt que créées par l'objet lui-même. Types : constructor injection, setter injection, field injection.

**Exemple** :
```java
@Service
public class UserService {
    private final UserDao userDao;
    
    // Constructor injection (recommandé)
    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }
    
    // Setter injection
    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    
    // Field injection (déconseillé)
    @Autowired
    private UserDao userDao;
}
```

### 35. Différence entre scope singleton et prototype

**Singleton** : Une seule instance du bean pour tout le conteneur Spring. Partagée entre toutes les injections. Scope par défaut. Thread-safe à gérer manuellement si état mutable.

**Prototype** : Nouvelle instance créée à chaque injection ou récupération. Pas de gestion du cycle de destruction par Spring.

**Exemple** :
```java
@Component
@Scope("singleton") // Par défaut
public class SingletonBean {
    // Une seule instance dans l'application
}

@Component
@Scope("prototype")
public class PrototypeBean {
    // Nouvelle instance à chaque @Autowired
}
```

### 36. Cycle de vie d'une servlet

1. **Chargement** : Le conteneur charge la classe servlet
2. **Instantiation** : Création d'une instance (une seule par servlet)
3. **Initialisation** : Appel de `init()` une seule fois
4. **Service** : Appel de `service()` pour chaque requête (doGet, doPost, etc.)
5. **Destruction** : Appel de `destroy()` avant suppression

**Exemple** :
```java
public class MyServlet extends HttpServlet {
    public void init() throws ServletException {
        // Initialisation (connexion DB, etc.)
    }
    
    protected void service(HttpServletRequest req, HttpServletResponse res) {
        // Traitement de chaque requête
    }
    
    public void destroy() {
        // Nettoyage (fermeture ressources)
    }
}
```

### 37. Différence entre forward et sendRedirect

**forward** : Redirection côté serveur. URL ne change pas dans le navigateur. Même requête HTTP. Partage de request/response. Plus rapide. Interne à l'application.

**sendRedirect** : Redirection côté client. Nouvelle URL dans le navigateur. Nouvelle requête HTTP. Perte des attributs request. Peut rediriger vers domaine externe.

**Exemple** :
```java
// Forward (côté serveur)
request.getRequestDispatcher("/page.jsp").forward(request, response);
// URL reste : /servlet, contenu de /page.jsp affiché

// SendRedirect (côté client)
response.sendRedirect("/page.jsp");
// URL devient : /page.jsp, nouvelle requête HTTP
```

### 38. Qu'est-ce qu'un ServletContext

**ServletContext** : Objet représentant l'application web entière. Un seul par application. Permet le partage de données entre servlets. Fournit des informations sur le serveur et l'application. Permet d'accéder aux ressources web.

**Exemple** :
```java
ServletContext context = getServletContext();

// Partager des données entre servlets
context.setAttribute("appName", "MyApp");

// Obtenir des paramètres du contexte (web.xml)
String param = context.getInitParameter("adminEmail");

// Accéder aux ressources
InputStream is = context.getResourceAsStream("/WEB-INF/config.xml");

// Info serveur
String serverInfo = context.getServerInfo();
```

## Spring et Spring Boot

### 39. Différence entre @Component, @Service et @Repository

**@Component** : Annotation générique pour tout bean Spring. Classe détectée par component scanning.

**@Service** : Spécialisation de @Component pour la couche service (logique métier). Sémantique uniquement.

**@Repository** : Spécialisation de @Component pour la couche d'accès aux données. Traduit automatiquement les exceptions de persistance en exceptions Spring DataAccessException.

**Exemple** :
```java
@Component
public class UtilityBean { } // Bean générique

@Service
public class UserService { } // Logique métier

@Repository
public class UserRepository { } // Accès données
```

### 40. À quoi sert l'annotation @Autowired

`@Autowired` effectue l'injection de dépendance automatique par type. Spring recherche un bean compatible dans le conteneur et l'injecte. Peut être utilisé sur constructeur, setter ou champ. Si plusieurs beans du même type existent, utiliser @Qualifier.

**Exemple** :
```java
@Service
public class UserService {
    @Autowired // Field injection
    private UserRepository userRepository;
    
    @Autowired // Constructor injection (recommandé)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Autowired
    @Qualifier("primaryRepo") // Si plusieurs implémentations
    private UserRepository userRepository;
}
```

### 41. Quel est le rôle de la DispatcherServlet

**DispatcherServlet** : Servlet frontal (Front Controller) de Spring MVC. Point d'entrée unique pour toutes les requêtes. Distribue les requêtes aux contrôleurs appropriés via HandlerMapping. Gère le cycle complet : résolution des vues, conversion des données, gestion des exceptions.

**Exemple de flux** :
```
1. Requête HTTP → DispatcherServlet
2. DispatcherServlet → HandlerMapping (trouve le controller)
3. DispatcherServlet → Controller (exécute méthode)
4. Controller → retourne ModelAndView
5. DispatcherServlet → ViewResolver (résout la vue)
6. DispatcherServlet → View (rendu)
7. Réponse HTTP ← DispatcherServlet
```

### 42. Différence entre @Controller et @RestController

**@Controller** : Annotation pour contrôleurs MVC traditionnels. Retourne des vues (JSP, Thymeleaf). Nécessite @ResponseBody sur méthodes pour retourner JSON/XML.

**@RestController** : Combinaison de @Controller + @ResponseBody. Toutes les méthodes retournent automatiquement des données (JSON/XML) et non des vues. Utilisé pour APIs REST.

**Exemple** :
```java
@Controller
public class WebController {
    @GetMapping("/page")
    public String page(Model model) {
        return "page"; // Retourne une vue
    }
    
    @GetMapping("/api/data")
    @ResponseBody // Nécessaire pour retourner JSON
    public User data() {
        return new User();
    }
}

@RestController // @ResponseBody implicite partout
public class ApiController {
    @GetMapping("/api/users")
    public List<User> users() {
        return userService.findAll(); // Retourne JSON automatiquement
    }
}
```

### 43. À quoi sert le fichier application.properties

Fichier de configuration centralisé de Spring Boot. Définit les propriétés de l'application : port serveur, configuration DB, logging, profils, etc. Alternative : application.yml. Supporte les profils (@Profile) via application-{profile}.properties.

**Exemple** :
```properties
# Serveur
server.port=8080
server.servlet.context-path=/api

# Base de données
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=secret

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Logging
logging.level.com.company=DEBUG

# Propriété personnalisée
app.name=MyApplication
```

### 44. Qu'est-ce que la Programmation Orientée Aspect (AOP) dans Spring

**AOP (Aspect-Oriented Programming)** : Paradigme séparant les préoccupations transversales (logging, sécurité, transactions) de la logique métier. Utilise des aspects (advice) appliqués à des points de jonction (joinpoint) via des expressions (pointcut).

**Concepts clés** :
- **Aspect** : Module de préoccupation transversale
- **Advice** : Action (before, after, around)
- **Pointcut** : Expression définissant où appliquer l'advice
- **JoinPoint** : Point d'exécution dans l'application

**Exemple** :
```java
@Aspect
@Component
public class LoggingAspect {
    
    @Before("execution(* com.company.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Avant : " + joinPoint.getSignature());
    }
    
    @Around("@annotation(com.company.Timed)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        System.out.println(joinPoint.getSignature() + " : " + duration + "ms");
        return result;
    }
}
```

### 45. Qu'est-ce qu'un "Starter" Spring Boot

**Starter** : Dépendance Maven/Gradle regroupant un ensemble cohérent de bibliothèques pour une fonctionnalité spécifique. Simplifie la configuration en incluant automatiquement toutes les dépendances nécessaires et l'auto-configuration associée.

**Exemple** :
```xml
<!-- Au lieu de déclarer individuellement -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- Inclut : spring-web, spring-webmvc, tomcat, jackson, etc. -->

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<!-- Inclut : hibernate, spring-data-jpa, jdbc, etc. -->
```

### 46. À quoi sert l'annotation @ControllerAdvice

`@ControllerAdvice` permet de gérer de manière centralisée les exceptions, le binding de données et les attributs du modèle pour plusieurs contrôleurs. Souvent combinée avec @ExceptionHandler pour gérer les exceptions globalement.

**Exemple** :
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse error = new ErrorResponse(500, "Erreur serveur");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    @ModelAttribute("appName")
    public String appName() {
        return "MyApp"; // Disponible dans tous les contrôleurs
    }
}
```

### 47. Comment fonctionne la gestion transactionnelle avec @Transactional

`@Transactional` démarre une transaction au début de la méthode et la commit à la fin (ou rollback en cas d'exception). Utilise l'AOP pour entourer la méthode d'un proxy. Par défaut, rollback uniquement sur RuntimeException et Error.

**Propriétés** :
- **propagation** : Comportement si transaction existe déjà
- **isolation** : Niveau d'isolation (READ_COMMITTED, etc.)
- **readOnly** : Optimisation pour lectures seules
- **rollbackFor** : Exceptions déclenchant rollback

**Exemple** :
```java
@Service
public class UserService {
    
    @Transactional
    public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
        accountRepo.debit(fromId, amount);
        accountRepo.credit(toId, amount);
        // Commit automatique si pas d'exception
        // Rollback si RuntimeException
    }
    
    @Transactional(
        propagation = Propagation.REQUIRES_NEW,
        isolation = Isolation.SERIALIZABLE,
        rollbackFor = Exception.class
    )
    public void criticalOperation() { }
}
```

### 48. Quelles sont les CRUD offertes par CrudRepository

**CrudRepository** fournit les opérations CRUD de base :
- **save(S entity)** : INSERT ou UPDATE
- **saveAll(Iterable entities)** : Sauvegarde multiple
- **findById(ID id)** : SELECT par clé primaire
- **existsById(ID id)** : Vérification existence
- **findAll()** : SELECT tous
- **findAllById(Iterable ids)** : SELECT par liste d'IDs
- **count()** : COUNT
- **deleteById(ID id)** : DELETE par ID
- **delete(T entity)** : DELETE par entité
- **deleteAll()** : DELETE tous

**Exemple** :
```java
public interface UserRepository extends CrudRepository<User, Long> {
    // Méthodes héritées automatiquement
}

// Utilisation
userRepo.save(user);
Optional<User> found = userRepo.findById(1L);
userRepo.deleteById(1L);
long count = userRepo.count();
```

### 49. Différence entre save(), saveAndFlush() et persist()

**save()** : Méthode Spring Data JPA. Appelle persist() (nouveau) ou merge() (existant) selon l'état de l'entité. Retourne l'entité sauvegardée. Pas de flush immédiat.

**saveAndFlush()** : Comme save() mais force un flush() immédiat vers la DB. Utile pour obtenir l'ID généré ou détecter les violations de contraintes.

**persist()** : Méthode EntityManager JPA. Uniquement pour nouvelles entités. Ne retourne rien (void). Lance exception si entité déjà existante.

**Exemple** :
```java
// save() - flush différé
User user = new User("John");
userRepo.save(user); // En mémoire, pas encore en DB
// user.getId() peut être null selon la stratégie

// saveAndFlush() - flush immédiat
User user2 = new User("Jane");
userRepo.saveAndFlush(user2); // INSERT immédiat
// user2.getId() est garanti d'être set

// persist() - EntityManager
User user3 = new User("Bob");
entityManager.persist(user3); // Uniquement si nouveau
```

### 50. Différence entre MapStruct et ModelMapper

**MapStruct** : Générateur de code de mapping à la compilation. Génère le code Java de mapping via annotation processor. Très performant (pas de réflexion). Type-safe (erreurs à la compilation). Configuration via interfaces et annotations.

**ModelMapper** : Bibliothèque de mapping par réflexion à l'exécution. Configuration fluide par code. Plus flexible mais moins performant. Détection automatique des mappings.

**Exemple** :
```java
// MapStruct (compilation)
@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    @Mapping(source = "fullName", target = "name")
    UserDto toDto(User user);
}
UserDto dto = UserMapper.INSTANCE.toDto(user);

// ModelMapper (runtime)
ModelMapper mapper = new ModelMapper();
UserDto dto = mapper.map(user, UserDto.class);
```

### 51. Quel est l'intérêt d'utilisation des DTOs

**DTO (Data Transfer Object)** : Objet transportant des données entre couches ou systèmes.

**Avantages** :
- Découplage entre couches (entité JPA ≠ API)
- Sécurité (ne pas exposer toutes les propriétés)
- Performance (éviter lazy loading non souhaité)
- Versioning API (plusieurs DTOs pour une entité)
- Validation spécifique par use case

**Exemple** :
```java
@Entity
public class User {
    private Long id;
    private String username;
    private String password; // Ne pas exposer
    private String email;
    private List<Order> orders; // Éviter lazy loading
}

public class UserDto {
    private Long id;
    private String username;
    private String email;
    // Pas de password ni orders
}

@RestController
public class UserController {
    @GetMapping("/users/{id}")
    public UserDto getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return userMapper.toDto(user); // Conversion DTO
    }
}
```

### 52. Différence entre ServletContext et ApplicationContext

**ServletContext** : Contexte de l'application web au niveau Servlet. Un par application web. Fourni par le conteneur servlet. Stocke les paramètres et attributs de l'application web.

**ApplicationContext** : Contexte Spring. Conteneur IoC gérant les beans Spring. Hérite de BeanFactory avec fonctionnalités supplémentaires (i18n, événements, AOP). Plusieurs types : WebApplicationContext, AnnotationConfigApplicationContext.

**Relation** : Le WebApplicationContext de Spring est généralement stocké comme attribut du ServletContext.

**Exemple** :
```java
// ServletContext
ServletContext servletContext = request.getServletContext();
servletContext.setAttribute("appName", "MyApp");

// ApplicationContext (Spring)
@Autowired
private ApplicationContext applicationContext;

UserService service = applicationContext.getBean(UserService.class);
String[] beanNames = applicationContext.getBeanDefinitionNames();
```

### 53. Différence entre BeanFactory et ApplicationContext

**BeanFactory** : Interface de base du conteneur Spring. Lazy loading des beans. Fonctionnalités minimales (création, récupération beans).

**ApplicationContext** : Extension de BeanFactory. Eager loading par défaut. Fonctionnalités avancées : gestion événements, i18n, intégration AOP, chargement de ressources, ApplicationContext hiérarchiques.

**Exemple** :
```java
// BeanFactory (rarement utilisé directement)
BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
UserService service = (UserService) factory.getBean("userService");

// ApplicationContext (standard)
ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
UserService service = context.getBean(UserService.class);

// Fonctionnalités d'ApplicationContext
context.publishEvent(new CustomEvent());
String message = context.getMessage("welcome", null, Locale.FRENCH);
```

### 54. Quels sont les types d'injection de dépendance

**Constructor Injection** : Injection via constructeur. Recommandée. Permet final fields. Facilite les tests. Dépendances obligatoires évidentes.

**Setter Injection** : Injection via setters. Dépendances optionnelles. Permet réinjection.

**Field Injection** : Injection directe sur champ via @Autowired. Simple mais déconseillée (difficile à tester, couplage fort, dépendances cachées).

**Exemple** :
```java
@Service
public class UserService {
    
    // Constructor injection (recommandé)
    private final UserRepository userRepo;
    
    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }
    
    // Setter injection
    private EmailService emailService;
    
    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
    
    // Field injection (déconseillé)
    @Autowired
    private LogService logService;
}
```

### 55. Qu'est-ce que Spring Core

**Spring Core** : Module fondamental du framework Spring. Fournit les fonctionnalités de base : conteneur IoC (BeanFactory, ApplicationContext), injection de dépendances, gestion du cycle de vie des beans, configuration (XML, annotations, Java config).

**Composants principaux** :
- BeanFactory et ApplicationContext
- Dependency Injection
- Bean lifecycle management
- Resource loading
- Event handling

**Exemple** :
```java
// Configuration Java (Spring Core)
@Configuration
public class AppConfig {
    @Bean
    public UserService userService() {
        return new UserService(userRepository());
    }
    
    @Bean
    public UserRepository userRepository() {
        return new UserRepositoryImpl();
    }
}

// Démarrage
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
UserService service = context.getBean(UserService.class);
```

### 56. Différence entre @Controller et @RestController (déjà répondu au #42)

Voir question 42 ci-dessus.

## Sécurité dans Spring

### 57. Différence conceptuelle entre Authentication et Authorization

**Authentication (Authentification)** : Processus de vérification de l'identité d'un utilisateur. Répond à "Qui êtes-vous ?". Vérifie les credentials (username/password, token, certificat).

**Authorization (Autorisation)** : Processus de vérification des permissions d'un utilisateur authentifié. Répond à "Que pouvez-vous faire ?". Vérifie les droits d'accès aux ressources.

**Exemple** :
```java
// Authentication
User user = authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(username, password)
);

// Authorization
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { }

@GetMapping("/admin")
public String adminPage() {
    // Vérifie si l'utilisateur a le rôle ADMIN
}
```

### 58. Qu'est-ce qu'un Principal dans le contexte de la sécurité Java

**Principal** : Interface représentant l'identité de l'utilisateur actuellement authentifié. Contient généralement le nom d'utilisateur ou identifiant unique. Accessible via SecurityContext.

**Exemple** :
```java
@GetMapping("/me")
public String currentUser(Principal principal) {
    return "User: " + principal.getName();
}

// Ou via SecurityContext
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName(); // Principal
Object principal = auth.getPrincipal(); // UserDetails généralement
```

### 59. Rôle du SecurityContextHolder et du SecurityContext

**SecurityContextHolder** : Classe utilitaire stockant le SecurityContext. Utilise ThreadLocal par défaut (un contexte par thread). Point d'accès global au contexte de sécurité.

**SecurityContext** : Interface contenant l'objet Authentication de l'utilisateur courant. Stocke l'état d'authentification.

**Exemple** :
```java
// Récupération
SecurityContext context = SecurityContextHolder.getContext();
Authentication authentication = context.getAuthentication();
String username = authentication.getName();
Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

// Définition (après authentification)
SecurityContextHolder.getContext().setAuthentication(authentication);

// Nettoyage
SecurityContextHolder.clearContext();
```

### 60. Qu'est-ce qu'une GrantedAuthority et comment est-elle liée à un utilisateur

**GrantedAuthority** : Interface représentant un privilège ou permission accordé à un principal. Généralement un rôle (ROLE_USER, ROLE_ADMIN) ou une permission granulaire (READ_PRODUCTS, DELETE_USERS). Retournée par `Authentication.getAuthorities()`.

**Exemple** :
```java
public class User implements UserDetails {
    private List<GrantedAuthority> authorities;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}

// Création
GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");

// Vérification
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public void adminMethod() { }

@PreAuthorize("hasAnyAuthority('READ_USERS', 'WRITE_USERS')")
public void userMethod() { }
```

### 61. Qu'est-ce que la DelegatingFilterProxy

**DelegatingFilterProxy** : Filtre Servlet standard qui délègue au bean Spring nommé "springSecurityFilterChain". Pont entre le conteneur Servlet et le contexte Spring. Permet à Spring Security d'utiliser des beans Spring dans la chaîne de filtres.

**Fonctionnement** :
1. Enregistré dans web.xml ou via Java config
2. Reçoit les requêtes HTTP
3. Délègue au FilterChainProxy de Spring
4. Retourne la réponse

**Exemple** :
```xml
<!-- web.xml -->
<filter>
    <filter-name>springSecurityFilterChain</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>
<filter-mapping>
    <filter-name>springSecurityFilterChain</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

### 62. Concept de FilterChainProxy

**FilterChainProxy** : Bean Spring gérant une liste ordonnée de Security Filters. Détermine quels filtres appliquer pour chaque requête selon l'URL pattern. Permet plusieurs chaînes de filtres pour différents chemins.

**Filtres typiques** : SecurityContextPersistenceFilter, UsernamePasswordAuthenticationFilter, ExceptionTranslationFilter, FilterSecurityInterceptor.

**Exemple** :
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin();
        return http.build();
    }
}
```

### 63. Pourquoi Spring Security est basé sur une "chaîne de filtres"

Spring Security utilise une chaîne de filtres car chaque filtre a une responsabilité unique et les filtres peuvent être composés, réordonnés ou remplacés facilement. Permet séparation des préoccupations (authentification, autorisation, CSRF, session, etc.) et extensibilité.

**Avantages** :
- Modularité (chaque filtre = une responsabilité)
- Réutilisabilité
- Configuration flexible
- Intégration naturelle avec l'architecture Servlet

**Exemple de chaîne** :
```
1. SecurityContextPersistenceFilter (charge contexte)
2. LogoutFilter (gère déconnexion)
3. UsernamePasswordAuthenticationFilter (authentification)
4. DefaultLoginPageGeneratingFilter (génère page login)
5. BasicAuthenticationFilter (HTTP Basic)
6. RequestCacheAwareFilter (cache requêtes)
7. SecurityContextHolderAwareRequestFilter (wrapping requête)
8. AnonymousAuthenticationFilter (utilisateur anonyme)
9. SessionManagementFilter (gestion session)
10. ExceptionTranslationFilter (gère exceptions sécurité)
11. FilterSecurityInterceptor (décisions autorisation finale)
```

### 64. Rôle de l'AuthenticationManager

**AuthenticationManager** : Interface centrale pour l'authentification. Méthode principale : `authenticate(Authentication)`. Retourne un Authentication complet si succès, lance AuthenticationException sinon. L'implémentation standard est ProviderManager qui délègue à plusieurs AuthenticationProvider.

**Exemple** :
```java
public interface AuthenticationManager {
    Authentication authenticate(Authentication authentication) 
        throws AuthenticationException;
}

// Utilisation
@Autowired
private AuthenticationManager authManager;

public String login(String username, String password) {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password)
    );
    SecurityContextHolder.getContext().setAuthentication(auth);
    return "Success";
}
```

### 65. Qu'est-ce qu'un AuthenticationProvider et pourquoi peut-il y en avoir plusieurs

**AuthenticationProvider** : Interface effectuant l'authentification réelle pour un type spécifique d'Authentication. Chaque provider gère un mécanisme d'authentification (DB, LDAP, OAuth, etc.). Le ProviderManager itère sur les providers jusqu'à trouver celui qui supporte l'Authentication.

**Plusieurs providers** : Permet supporter différents mécanismes d'authentification simultanément (username/password + token + certificat).

**Exemple** :
```java
public class CustomAuthenticationProvider implements AuthenticationProvider {
    
    @Override
    public Authentication authenticate(Authentication authentication) 
            throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        // Logique d'authentification personnalisée
        if (isValid(username, password)) {
            return new UsernamePasswordAuthenticationToken(
                username, password, getAuthorities(username)
            );
        }
        throw new BadCredentialsException("Invalid credentials");
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

// Configuration
@Bean
public AuthenticationManager authManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder builder = 
        http.getSharedObject(AuthenticationManagerBuilder.class);
    builder.authenticationProvider(customAuthProvider);
    builder.authenticationProvider(ldapAuthProvider);
    return builder.build();
}
```

### 66. Rôle de l'interface UserDetailsService

**UserDetailsService** : Interface chargeant les données utilisateur. Méthode unique : `loadUserByUsername(String username)`. Retourne un UserDetails contenant username, password, authorities. Utilisée par DaoAuthenticationProvider.

**Exemple** :
```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepo;
    
    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList()))
            .build();
    }
}
```

### 67. Mécanisme du CSRF (Cross-Site Request Forgery)

**CSRF** : Attaque forçant un utilisateur authentifié à exécuter une action non désirée sur une application web. L'attaquant exploite la session active de la victime.

**Protection Spring** : Génère un token CSRF unique par session, vérifie ce token sur chaque requête modifiante (POST, PUT, DELETE). Le token doit être inclus dans les formulaires ou headers.

**Exemple** :
```java
// Configuration
http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

// HTML (Thymeleaf)
<form th:action="@{/submit}" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
</form>

// JavaScript
fetch('/api/data', {
    method: 'POST',
    headers: {
        'X-CSRF-TOKEN': csrfToken
    }
});

// Désactiver CSRF (APIs stateless)
http.csrf().disable();
```

### 68. Principe stateful et stateless

**Stateful** : Le serveur maintient l'état de session (données utilisateur) entre les requêtes. Utilise sessions HTTP (cookies JSESSIONID). Nécessite stockage serveur. Non scalable horizontalement sans session replication.

**Stateless** : Le serveur ne maintient aucun état. Chaque requête contient toutes les informations nécessaires (JWT token). Scalable. Utilisé pour APIs REST.

**Exemple** :
```java
// Stateful (session)
http.sessionManagement()
    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
// Session stockée serveur

// Stateless (JWT)
http.sessionManagement()
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
// Pas de session, token dans chaque requête
```

### 69. Que signifie Bearer Token

**Bearer Token** : Type de token d'accès envoyé dans l'header HTTP `Authorization`. "Bearer" signifie "porteur" - quiconque possède le token peut l'utiliser. Généralement un JWT. Format standard OAuth 2.0.

**Exemple** :
```http
GET /api/users
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

// Spring Security
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
    http.oauth2ResourceServer()
        .jwt();
    return http.build();
}
```

### 70. Trois parties composant un token JWT

**Header** : Métadonnées sur le token (algorithme de signature, type de token).

**Payload** : Claims (données) - sub (subject), iat (issued at), exp (expiration), custom claims.

**Signature** : Hash cryptographique du header et payload avec une clé secrète. Garantit l'intégrité.

**Format** : `header.payload.signature` (Base64URL encoded)

**Exemple** :
```json
// Header
{
  "alg": "HS256",
  "typ": "JWT"
}

// Payload
{
  "sub": "user123",
  "name": "John Doe",
  "iat": 1516239022,
  "exp": 1516242622,
  "roles": ["USER", "ADMIN"]
}

// Signature
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)

// JWT final
eyJhbGc...base64(header).eyJzdWI...base64(payload).SflKxw...signature
```

### 71. Que signifie l'encodage et est-il réversible

**Encodage** : Transformation de données dans un format différent pour transmission ou stockage. Réversible (décodage possible). Ne fournit aucune sécurité. Exemples : Base64, URL encoding, UTF-8.

**Réversible** : Oui, complètement. Pas de clé nécessaire.

**Exemple** :
```java
// Base64 encoding
String original = "Hello World";
String encoded = Base64.getEncoder().encodeToString(original.getBytes());
// encoded = "SGVsbG8gV29ybGQ="

// Base64 decoding (réversible)
byte[] decoded = Base64.getDecoder().decode(encoded);
String original2 = new String(decoded);
// original2 = "Hello World"
```

### 72. Différence entre algorithme de signature symétrique et asymétrique

**Symétrique** : Une seule clé secrète pour signer et vérifier. Plus rapide. Moins sécurisé pour distribution (partage de clé). Exemples : HMAC-SHA256, HMAC-SHA512. Utilisé pour JWT dans applications internes.

**Asymétrique** : Paire de clés (privée pour signer, publique pour vérifier). Plus lent. Plus sécurisé pour distribution publique. Exemples : RSA, ECDSA. Utilisé pour JWT dans applications distribuées.

**Exemple** :
```java
// Symétrique (HS256)
String jwt = Jwts.builder()
    .setSubject("user")
    .signWith(SignatureAlgorithm.HS256, "mySecretKey")
    .compact();

// Asymétrique (RS256)
KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
String jwt = Jwts.builder()
    .setSubject("user")
    .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
    .compact();

// Vérification avec clé publique
Jwts.parserBuilder()
    .setSigningKey(keyPair.getPublic())
    .build()
    .parseClaimsJws(jwt);
```

### 73. À quoi sert le hashage

**Hashage** : Fonction à sens unique transformant des données en empreinte de taille fixe. Irréversible (impossible de retrouver l'original). Déterministe (même input = même hash). Utilisé pour stocker passwords, vérifier intégrité. Exemples : SHA-256, BCrypt, Argon2.

**Propriétés** :
- Irréversible
- Résistance aux collisions
- Effet avalanche (petit changement = hash très différent)

**Exemple** :
```java
// Hashage de password avec BCrypt
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String password = "myPassword123";
String hashed = encoder.encode(password);
// hashed = "$2a$10$N9qo8uLO..."

// Vérification
boolean matches = encoder.matches("myPassword123", hashed); // true
boolean matches2 = encoder.matches("wrongPassword", hashed); // false

// SHA-256 (pour intégrité, pas passwords)
MessageDigest digest = MessageDigest.getInstance("SHA-256");
byte[] hash = digest.digest("data".getBytes());
String hex = DatatypeConverter.printHexBinary(hash);
```

### 74. Pourquoi on fait l'encodage

**Raisons** :
- **Compatibilité** : Transmission de données binaires sur protocoles texte (Base64 en email, JSON)
- **Format** : Conversion entre charsets (UTF-8, ASCII)
- **URL-safe** : Encoder caractères spéciaux dans URLs
- **Interopérabilité** : Format standard entre systèmes

**Pas pour la sécurité** : L'encodage est réversible.

**Exemple** :
```java
// URL encoding (caractères spéciaux)
String url = "Hello World & Special=Chars";
String encoded = URLEncoder.encode(url, "UTF-8");
// encoded = "Hello+World+%26+Special%3DChars"

// Base64 (données binaires en texte)
byte[] bytes = {0x48, 0x65, 0x6C, 0x6C, 0x6F};
String base64 = Base64.getEncoder().encodeToString(bytes);
// base64 = "SGVsbG8="
```

### 75. À quoi sert le chiffrement

**Chiffrement** : Transformation réversible de données pour les rendre illisibles sans la clé de déchiffrement. Protège la confidentialité. Symétrique (AES) ou asymétrique (RSA).

**Différence avec encodage** : Nécessite une clé secrète. Sécurise les données.

**Différence avec hashage** : Réversible avec la clé.

**Exemple** :
```java
// Chiffrement symétrique AES
SecretKey key = KeyGenerator.getInstance("AES").generateKey();
Cipher cipher = Cipher.getInstance("AES");

// Chiffrement
cipher.init(Cipher.ENCRYPT_MODE, key);
byte[] encrypted = cipher.doFinal("Secret Data".getBytes());

// Déchiffrement (avec la clé)
cipher.init(Cipher.DECRYPT_MODE, key);
byte[] decrypted = cipher.doFinal(encrypted);
String original = new String(decrypted); // "Secret Data"
```

### 76. Rôle d'un Refresh Token par rapport à un Access Token

**Access Token** : Token de courte durée (15 min - 1h) pour accéder aux ressources protégées. Contient les permissions. Risque limité si compromis.

**Refresh Token** : Token de longue durée (jours/semaines) pour obtenir de nouveaux access tokens. Stocké sécurisé. Utilisé uniquement avec le serveur d'autorisation. Peut être révoqué.

**Flux** :
1. Login → Access Token + Refresh Token
2. Utiliser Access Token pour API
3. Access Token expire → Utiliser Refresh Token pour nouveau Access Token
4. Répéter jusqu'à expiration Refresh Token

**Exemple** :
```java
// Génération
String accessToken = generateJWT(user, 15); // 15 minutes
String refreshToken = generateJWT(user, 10080); // 7 jours

// Refresh endpoint
@PostMapping("/refresh")
public TokenResponse refresh(@RequestBody RefreshRequest request) {
    if (isValidRefreshToken(request.getRefreshToken())) {
        String newAccessToken = generateJWT(user, 15);
        return new TokenResponse(newAccessToken);
    }
    throw new UnauthorizedException();
}
```

### 77. Pourquoi adopter la solution de sécurité stateful et stateless

**Stateful adopté quand** :
- Application monolithique traditionnelle
- Sessions complexes avec beaucoup d'état
- Révocation immédiate nécessaire
- Intégration avec systèmes legacy

**Stateless adopté quand** :
- Architecture microservices
- APIs RESTful
- Scalabilité horizontale nécessaire
- Mobile/SPA applications
- Services distribués

**Exemple comparatif** :
```java
// Stateful - session serveur
@GetMapping("/profile")
public User getProfile(HttpSession session) {
    return (User) session.getAttribute("user");
}

// Stateless - JWT
@GetMapping("/profile")
public User getProfile(@RequestHeader("Authorization") String token) {
    Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    return userService.findById(claims.getSubject());
}
```

### 78. Pourquoi utiliser un OncePerRequestFilter pour valider le JWT

**OncePerRequestFilter** : Garantit qu'un filtre s'exécute exactement une fois par requête, même en cas de forward/include. Important pour JWT car évite validations multiples et problèmes de performance.

**Sans OncePerRequestFilter** : Risque d'exécution multiple lors de dispatching interne.

**Exemple** :
```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String token = extractToken(request);
        
        if (token != null && validateToken(token)) {
            String username = getUsernameFromToken(token);
            UsernamePasswordAuthenticationToken auth = 
                new UsernamePasswordAuthenticationToken(
                    username, null, getAuthorities(token)
                );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### 79. Qu'est-ce qu'un Identity Provider (IdP)

**Identity Provider** : Service gérant l'identité et l'authentification des utilisateurs. Fournit un point d'authentification centralisé. Délivre des tokens/assertions après authentification. Exemples : Keycloak, Okta, Auth0, Azure AD, Google Identity.

**Responsabilités** :
- Authentification utilisateurs
- Gestion des identités
- Émission de tokens
- SSO (Single Sign-On)
- Gestion des sessions

**Exemple flux** :
```
1. Application → Redirige vers IdP
2. Utilisateur → Authentifie sur IdP
3. IdP → Retourne token à l'application
4. Application → Valide token et autorise accès
```

### 80. Notion de Single Sign-On (SSO)

**SSO** : Mécanisme permettant à un utilisateur de s'authentifier une seule fois pour accéder à plusieurs applications. Améliore l'expérience utilisateur. Centralise la gestion des identités. Réduit la fatigue des mots de passe.

**Types** :
- Enterprise SSO (SAML)
- Web SSO (OAuth 2.0, OpenID Connect)
- Federated SSO

**Exemple** :
```
1. User accède App1 → Redirigé vers IdP → Login
2. IdP crée session SSO
3. User accède App2 → Redirigé vers IdP → Déjà authentifié
4. IdP retourne token à App2 → Accès direct sans nouveau login
```

### 81. Différence entre OAuth2 et OpenID Connect (OIDC)

**OAuth 2.0** : Framework d'autorisation. Permet à une application d'accéder aux ressources au nom de l'utilisateur. Délivre des access tokens. Ne définit pas l'authentification. Utilisé pour délégation d'accès.

**OpenID Connect (OIDC)** : Couche d'authentification au-dessus d'OAuth 2.0. Délivre un ID Token (JWT) en plus de l'access token. Fournit des informations sur l'utilisateur (/userinfo endpoint). Standard pour l'authentification.

**Exemple** :
```java
// OAuth 2.0 - Authorization
// "Cette app peut-elle accéder à mes photos Google Drive ?"
// → Access Token pour Google Drive API

// OIDC - Authentication + Authorization
// "Qui est cet utilisateur ?"
// → ID Token (contient sub, email, name)
// → Access Token
// → Refresh Token

// ID Token JWT
{
  "iss": "https://accounts.google.com",
  "sub": "110169484474386276334",
  "email": "user@example.com",
  "name": "John Doe",
  "picture": "https://..."
}
```

### 82. Qu'est-ce que l'ID Token par rapport à l'Access Token

**ID Token** : JWT contenant les informations d'identité de l'utilisateur (claims). Prouve qui est l'utilisateur. Destiné à l'application cliente. Court durée de vie. Format standardisé OIDC.

**Access Token** : Token d'autorisation pour accéder aux ressources protégées. Peut être opaque ou JWT. Destiné à l'API/Resource Server. Contient les scopes/permissions. Format non standardisé.

**Exemple** :
```java
// ID Token (OIDC)
{
  "iss": "https://idp.example.com",
  "sub": "user123",
  "aud": "clientApp",
  "exp": 1516239022,
  "iat": 1516235422,
  "email": "user@example.com",
  "name": "John Doe",
  "email_verified": true
}

// Access Token (contenu peut varier)
{
  "sub": "user123",
  "scope": "read:users write:users",
  "client_id": "app123",
  "exp": 1516239022
}

// Utilisation
// ID Token → Identifier l'utilisateur dans l'app
// Access Token → Appeler l'API
GET /api/users
Authorization: Bearer <access_token>
```

### 83. Flux Authorization Code Flow - Pourquoi le plus sécurisé

**Authorization Code Flow** : Flux OAuth 2.0 en deux étapes. Client reçoit un code d'autorisation, puis l'échange contre un token via back-channel sécurisé.

**Pourquoi le plus sécurisé** :
- Access Token jamais exposé au navigateur
- Code échangé via back-channel (serveur à serveur)
- Support de PKCE (Proof Key for Code Exchange)
- Client authentication sur token endpoint
- Refresh tokens sécurisés

**Exemple flux** :
```
1. User → Click "Login"
2. App → Redirect vers IdP avec client_id, redirect_uri, scope
   https://idp.com/authorize?client_id=app123&redirect_uri=https://app.com/callback&response_type=code&scope=openid

3. User → Login sur IdP
4. IdP → Redirect vers App avec code
   https://app.com/callback?code=AUTH_CODE_123

5. App Backend → POST vers IdP Token Endpoint
   POST https://idp.com/token
   code=AUTH_CODE_123
   client_id=app123
   client_secret=secret
   grant_type=authorization_code

6. IdP → Retourne tokens
   {
     "access_token": "eyJhbG...",
     "id_token": "eyJhbG...",
     "refresh_token": "eyJhbG...",
     "token_type": "Bearer"
   }
```

### 84. Différence entre Realm Role et Client Role

**Realm Role** : Rôle global applicable à tout le realm (tenant). Partagé entre toutes les applications du realm. Exemples : admin, user, manager.

**Client Role** : Rôle spécifique à une application cliente. Isolé aux permissions de cette application. Exemples : app1-admin, app1-viewer.

**Exemple Keycloak** :
```java
// Realm Roles (globaux)
- admin (accès administration générale)
- user (accès utilisateur standard)

// Client Roles (spécifiques à "app-backend")
- app-backend/read-data
- app-backend/write-data
- app-backend/delete-data

// Mapping JWT
{
  "realm_access": {
    "roles": ["admin", "user"]
  },
  "resource_access": {
    "app-backend": {
      "roles": ["read-data", "write-data"]
    }
  }
}

// Vérification Spring
@PreAuthorize("hasRole('admin')") // Realm role
@PreAuthorize("hasAuthority('app-backend/read-data')") // Client role
```

### 85. Qu'est-ce que l'Issuer URI dans la configuration Spring Boot

**Issuer URI** : URL de base de l'Identity Provider émettant les tokens JWT. Utilisé pour découvrir automatiquement les endpoints (authorization, token, jwks, userinfo). Spring Boot l'utilise pour configurer OAuth2/OIDC.

**Exemple** :
```properties
# application.properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://idp.example.com/realms/myrealm

# Spring télécharge automatiquement depuis :
# https://idp.example.com/realms/myrealm/.well-known/openid-configuration

# Configuration découverte :
# - authorization_endpoint
# - token_endpoint
# - jwks_uri (clés publiques pour valider JWT)
# - userinfo_endpoint
```

```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer()
            .jwt(); // Utilise issuer-uri automatiquement
        return http.build();
    }
}
```

### 86. Qu'est-ce qu'un Realm

**Realm** : Espace isolé dans un Identity Provider gérant un ensemble d'utilisateurs, rôles, clients et configurations. Équivalent à un tenant ou domaine. Permet multi-tenancy. Chaque realm a ses propres données et configurations.

**Exemple Keycloak** :
```
Keycloak Instance
├── Realm: company-dev
│   ├── Users: dev-users
│   ├── Clients: app-dev
│   └── Roles: dev-roles
├── Realm: company-prod
│   ├── Users: prod-users
│   ├── Clients: app-prod
│   └── Roles: prod-roles
└── Realm: customer-portal
    ├── Users: customers
    ├── Clients: portal-app
    └── Roles: customer-roles

# URLs différentes par realm
https://idp.com/realms/company-dev/protocol/openid-connect/token
https://idp.com/realms/company-prod/protocol/openid-connect/token
```

### 87. Rôle de la signature de token

**Signature de token** : Garantit l'intégrité et l'authenticité du token. Prouve que le token a été émis par l'émetteur légitime et n'a pas été modifié. Calculée avec algorithme cryptographique (HMAC, RSA).

**Vérification** :
1. Récupérer header et payload du JWT
2. Recalculer la signature avec la clé
3. Comparer avec la signature du token
4. Si identique → Token valide
5. Si différent → Token falsifié

**Exemple** :
```java
// Création avec signature
String jwt = Jwts.builder()
    .setSubject("user123")
    .setExpiration(new Date(System.currentTimeMillis() + 3600000))
    .signWith(SignatureAlgorithm.HS256, "secretKey")
    .compact();

// Vérification
try {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey("secretKey")
        .build()
        .parseClaimsJws(jwt)
        .getBody();
    // Signature valide
} catch (SignatureException e) {
    // Signature invalide - token falsifié
}
```

### 88. Explication du mécanisme de rotation

**Token Rotation** : Remplacement automatique des tokens à leur utilisation. Principalement pour refresh tokens. Améliore la sécurité en limitant la fenêtre d'exploitation si un token est compromis.

**Refresh Token Rotation** :
1. Client utilise refresh token pour obtenir nouvel access token
2. Serveur invalide l'ancien refresh token
3. Serveur retourne nouveau access token + nouveau refresh token
4. Si l'ancien refresh token est réutilisé → détection de compromission

**Exemple** :
```java
@PostMapping("/refresh")
public TokenResponse refreshToken(@RequestBody String oldRefreshToken) {
    // Vérifier validité
    if (!isValid(oldRefreshToken)) {
        throw new InvalidTokenException();
    }
    
    // Invalider l'ancien refresh token
    revokeToken(oldRefreshToken);
    
    // Détecter réutilisation (signe de vol)
    if (isAlreadyRevoked(oldRefreshToken)) {
        // Révoquer tous les tokens de cet utilisateur
        revokeAllUserTokens(getUserFromToken(oldRefreshToken));
        throw new SecurityException("Token reuse detected");
    }
    
    // Générer nouveaux tokens
    String newAccessToken = generateAccessToken(user);
    String newRefreshToken = generateRefreshToken(user);
    
    return new TokenResponse(newAccessToken, newRefreshToken);
}
```

## Docker

### 89. Différence entre Machine Virtuelle et Conteneur Docker

**Machine Virtuelle** :
- Virtualise le hardware complet
- Contient un OS complet (kernel)
- Lourde (plusieurs GB)
- Lente à démarrer (minutes)
- Isolation forte (hyperviseur)
- Consommation ressources élevée

**Conteneur Docker** :
- Virtualise uniquement l'espace utilisateur
- Partage le kernel de l'hôte
- Léger (quelques MB)
- Démarrage rapide (secondes)
- Isolation processus (namespaces, cgroups)
- Consommation ressources faible

**Exemple** :
```
VM:
├── Hardware
├── Hypervisor (VMware, VirtualBox)
├── VM1: Ubuntu + Kernel + App1 (2GB)
├── VM2: CentOS + Kernel + App2 (2GB)
└── VM3: Debian + Kernel + App3 (2GB)

Docker:
├── Hardware
├── OS Kernel (partagé)
├── Docker Engine
├── Container1: App1 + libs (50MB)
├── Container2: App2 + libs (60MB)
└── Container3: App3 + libs (40MB)
```

### 90. À quoi sert le Docker Engine

**Docker Engine** : Runtime Docker gérant les conteneurs. Composé de trois parties : Docker Daemon (dockerd), REST API, Docker CLI.

**Responsabilités** :
- Création et gestion des conteneurs
- Gestion des images
- Gestion des volumes et réseaux
- Orchestration de base

**Exemple** :
```bash
# Docker CLI → REST API → Docker Daemon

# Daemon gère les conteneurs
dockerd &

# CLI communique avec le daemon
docker run nginx
docker ps
docker stop container_id
```

### 91. Qu'est-ce que le Docker Hub (ou Registry)

**Docker Hub** : Registry public officiel d'images Docker. Stocke et distribue des images. Permet versioning via tags. Alternative : registries privés (Harbor, GitLab Container Registry, AWS ECR).

**Exemple** :
```bash
# Pull depuis Docker Hub
docker pull nginx:latest
docker pull mysql:8.0

# Push vers Docker Hub
docker tag myapp:latest username/myapp:1.0
docker push username/myapp:1.0

# Registry privé
docker pull registry.company.com/myapp:latest
```

### 92. Différence entre RUN, CMD et ENTRYPOINT

**RUN** : Exécute une commande pendant la construction de l'image (build time). Crée un nouveau layer. Utilisé pour installer packages, copier fichiers.

**CMD** : Définit la commande par défaut exécutée au démarrage du conteneur (runtime). Peut être surchargée. Une seule CMD effective.

**ENTRYPOINT** : Définit l'exécutable principal du conteneur. Ne peut pas être surchargée facilement. CMD devient des arguments pour ENTRYPOINT.

**Exemple** :
```dockerfile
# RUN - Build time
FROM ubuntu:20.04
RUN apt-get update
RUN apt-get install -y nginx
# Crée des layers dans l'image

# CMD - Runtime, peut être surchargée
CMD ["nginx", "-g", "daemon off;"]

# Démarrage
docker run myimage
# Exécute: nginx -g daemon off;

docker run myimage echo "hello"
# CMD surchargée, exécute: echo "hello"

# ENTRYPOINT - Runtime, exécutable fixe
ENTRYPOINT ["nginx"]
CMD ["-g", "daemon off;"]

docker run myimage
# Exécute: nginx -g daemon off;

docker run myimage -v
# Exécute: nginx -v (CMD remplacée par -v)
```

### 93. À quoi sert Docker Compose

**Docker Compose** : Outil définissant et gérant des applications multi-conteneurs. Utilise un fichier YAML (docker-compose.yml). Simplifie le déploiement de stacks complètes (app + DB + cache + etc.). Gère les réseaux et volumes automatiquement.

**Exemple** :
```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    build: ./app
    ports:
      - "8080:8080"
    environment:
      - DB_HOST=database
    depends_on:
      - database
      - redis
    networks:
      - backend

  database:
    image: postgres:14
    environment:
      - POSTGRES_PASSWORD=secret
    volumes:
      - db-data:/var/lib/postgresql/data
    networks:
      - backend

  redis:
    image: redis:7
    networks:
      - backend

volumes:
  db-data:

networks:
  backend:
```

```bash
# Commandes
docker-compose up -d        # Démarrer tous les services
docker-compose down         # Arrêter et supprimer
docker-compose ps           # Lister services
docker-compose logs app     # Voir logs d'un service
docker-compose scale app=3  # Scaler un service
```

### 94. Commandes Docker

**Images** :
```bash
docker images              # Lister images
docker pull nginx:latest   # Télécharger image
docker build -t myapp .    # Construire image
docker rmi image_id        # Supprimer image
docker tag source target   # Tagger image
```

**Conteneurs** :
```bash
docker run -d -p 8080:80 nginx          # Créer et démarrer
docker ps                                # Lister conteneurs actifs
docker ps -a                             # Tous les conteneurs
docker stop container_id                 # Arrêter
docker start container_id                # Démarrer
docker restart container_id              # Redémarrer
docker rm container_id                   # Supprimer
docker exec -it container_id /bin/bash   # Entrer dans conteneur
docker logs container_id                 # Voir logs
docker inspect container_id              # Détails conteneur
```

**Système** :
```bash
docker system prune        # Nettoyer ressources inutilisées
docker volume ls           # Lister volumes
docker network ls          # Lister réseaux
docker stats               # Statistiques ressources
```

### 95. Est-il possible de construire une image d'après un conteneur

**Oui** : Commande `docker commit`. Crée une nouvelle image à partir de l'état actuel d'un conteneur. Utile pour sauvegarder des modifications, mais déconseillé pour production (préférer Dockerfile pour reproductibilité).

**Exemple** :
```bash
# Démarrer conteneur
docker run -it ubuntu:20.04 /bin/bash

# À l'intérieur, modifier le conteneur
apt-get update
apt-get install -y nginx
exit

# Créer image depuis le conteneur
docker commit container_id myimage:v1

# Ou avec métadonnées
docker commit -m "Added nginx" -a "Author Name" container_id myimage:v1

# Vérifier
docker images
# myimage  v1  ...

# Utiliser la nouvelle image
docker run myimage:v1
```

## Testing

### 96. Différence entre Test Unitaire et Test d'Intégration

**Test Unitaire** :
- Teste une unité isolée (méthode, classe)
- Dépendances mockées
- Très rapide (millisecondes)
- Pas d'I/O (DB, réseau, fichiers)
- Exécuté fréquemment
- Framework : JUnit, TestNG

**Test d'Intégration** :
- Teste l'intégration de plusieurs composants
- Dépendances réelles ou embedded
- Plus lent (secondes/minutes)
- Utilise DB, API, fichiers
- Exécuté moins fréquemment
- Framework : Spring Test, Testcontainers

**Exemple** :
```java
// Test Unitaire
@Test
public void testCalculateTotal() {
    OrderService service = new OrderService();
    BigDecimal total = service.calculateTotal(items);
    assertEquals(new BigDecimal("100.00"), total);
}

// Test d'Intégration
@SpringBootTest
@AutoConfigureTestDatabase
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;
    
    @Test
    public void testCreateAndFindUser() {
        User user = userService.create(new User("John"));
        User found = userService.findById(user.getId());
        assertNotNull(found);
        assertEquals("John", found.getName());
    }
}
```

### 97. Qu'est-ce qu'un Mock et pourquoi l'utiliser

**Mock** : Objet simulant le comportement d'une dépendance réelle. Permet d'isoler le code testé. Contrôle total sur les retours et comportements. Vérification des interactions.

**Pourquoi** :
- Isolation des tests unitaires
- Tester sans dépendances externes
- Contrôler les cas limites
- Tester l'utilisation des dépendances
- Rapidité d'exécution

**Exemple** :
```java
// Avec Mockito
@Test
public void testGetUser() {
    // Créer mock
    UserRepository mockRepo = mock(UserRepository.class);
    UserService service = new UserService(mockRepo);
    
    // Définir comportement
    User user = new User(1L, "John");
    when(mockRepo.findById(1L)).thenReturn(Optional.of(user));
    
    // Tester
    User result = service.getUser(1L);
    
    // Vérifications
    assertEquals("John", result.getName());
    verify(mockRepo).findById(1L); // Vérifier l'appel
    verify(mockRepo, times(1)).findById(anyLong());
}
```

### 98. Différence entre Mock et Spy

**Mock** : Objet complètement simulé. Aucun comportement réel. Tous les appels doivent être définis avec when(). Par défaut retourne null/0/false.

**Spy** : Wrapper autour d'un objet réel. Comportement réel par défaut. Possibilité de stubber certaines méthodes. Permet vérifier interactions sur objet réel.

**Exemple** :
```java
// Mock - comportement complètement simulé
List<String> mockList = mock(ArrayList.class);
mockList.add("item");
assertEquals(0, mockList.size()); // Mock retourne 0 par défaut
when(mockList.size()).thenReturn(5);
assertEquals(5, mockList.size());

// Spy - comportement réel par défaut
List<String> spyList = spy(new ArrayList<>());
spyList.add("item");
assertEquals(1, spyList.size()); // Comportement réel
verify(spyList).add("item");

// Stubber une méthode du spy
doReturn(100).when(spyList).size();
assertEquals(100, spyList.size());
```
### 99. La difference entre Junit et TestNG?
**JUnit** : Framework de test unitaire populaire en Java. Utilise des annotations comme @Test, @BeforeEach, @AfterEach. Simple et léger. Intégré dans la plupart des IDEs. Supporte les assertions via Assert class.
**TestNG** : Framework de test plus avancé. Supporte les tests unitaires, d'intégration et fonctionnels. Utilise des annotations similaires (@Test, @BeforeMethod, @AfterMethod) mais avec plus de fonctionnalités (groupes, dépendances, data providers). Plus flexible pour les tests complexes.
**Exemple JUnit** :
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class CalculatorTest {
    @Test
    public void testAdd() {
        Calculator calc = new Calculator();
        assertEquals(5, calc.add(2, 3));
    }
}
```
**Exemple TestNG** :
```java
import org.testng.annotations.Test;
import static org.testng.Assert.*;
public class CalculatorTest {
    @Test
    public void testAdd() {
        Calculator calc = new Calculator();
        assertEquals(calc.add(2, 3), 5);
    }
}
``` 
### 100. Qu'est-ce qu'un Test Double ?
**Test Double** : Terme générique pour désigner tout objet utilisé pour remplacer une dépendance dans les tests. Inclut plusieurs types : Mocks, Stubs, Spies, Fakes.
**Types de Test Doubles** :
- **Mock** : Objet simulé avec comportement contrôlé et vérification des interactions.
- **Stub** : Objet fournissant des réponses prédéfinies aux appels, sans logique de vérification.
- **Spy** : Wrapper autour d'un objet réel, permettant de vérifier les interactions tout en conservant le comportement réel.
- **Fake** : Implémentation simplifiée d'une dépendance réelle, souvent utilisée pour des tests plus complets.
**Exemple** :
```java
// Stub example
public class UserService {
    private UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}   
// In test
@Test
public void testGetUserById() {
    UserRepository stubRepo = mock(UserRepository.class);
    when(stubRepo.findById(1L)).thenReturn(Optional.of(new User(1L, "John")));
    UserService userService = new UserService(stubRepo);
    User user = userService.getUserById(1L);
    assertEquals("John", user.getName());
}
```
### 101. Qu elle est role de Sonarqube ? 
**SonarQube** : Outil de gestion de la qualité du code. Analyse statique pour détecter bugs, vulnérabilités, code smells, et mesurer la couverture des tests. Fournit des rapports détaillés et des tableaux de bord. Aide à maintenir un code propre et sécurisé.
**Fonctionnalités** :
- Analyse statique de code
- Détection de bugs et vulnérabilités
- Mesure de la couverture des tests
- Suivi de la dette technique
- Intégration avec CI/CD
**Exemple d'utilisation** :
```bash# Analyser un projet Java avec Maven
mvn sonar:sonar \
  -Dsonar.projectKey=my-project \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=your_sonar_token
```
### 102. Qu elle est role de Jacoco ? 
**JaCoCo** : Outil de couverture de code pour Java. Mesure le pourcentage de code exécuté lors des tests unitaires. Génère des rapports détaillés (HTML, XML, CSV) pour identifier les parties non testées du code.
**Fonctionnalités** :
- Mesure de la couverture de code (ligne, branche, méthode)
- Rapports détaillés
- Intégration avec Maven, Gradle, Ant
- Support pour les tests unitaires et d'intégration

--