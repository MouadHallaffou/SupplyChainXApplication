# ELK Stack - Documentation

## Table des Matières
1. [Introduction](#introduction)
2. [Composants de l'ELK Stack](#composants-de-lelk-stack)
3. [Architecture](#architecture)
4. [Installation et Configuration](#installation-et-configuration)
5. [Utilisation dans SupplyChainX](#utilisation-dans-supplychainx)
6. [Bonnes Pratiques](#bonnes-pratiques)

---

## Introduction

**ELK Stack** est une suite d'outils open-source développée par Elastic qui permet de centraliser, analyser et visualiser les logs et données en temps réel. L'acronyme ELK représente :

- **E** - Elasticsearch
- **L** - Logstash
- **K** - Kibana

Depuis 2015, Elastic a ajouté **Beats** à la stack, ce qui l'a renommée en **Elastic Stack**, mais le terme ELK reste couramment utilisé.

### Pourquoi utiliser ELK Stack ?

- **Centralisation des logs** : Collecte les logs de multiples sources
- **Analyse en temps réel** : Traitement et analyse instantanés
- **Visualisation intuitive** : Dashboards interactifs et personnalisables
- **Scalabilité** : Gère de grands volumes de données
- **Recherche performante** : Indexation et recherche full-text rapide

---

## Composants de l'ELK Stack

### 1. Elasticsearch

**Elasticsearch** est un moteur de recherche et d'analyse distribué basé sur Apache Lucene.

#### Caractéristiques principales :
- **Moteur de recherche NoSQL** orienté documents
- **Architecture distribuée** avec système de sharding
- **Indexation en temps réel**
- **RESTful API** pour toutes les opérations
- **Recherche full-text** avec scoring de pertinence

#### Fonctionnement :
```json
{
  "index": "supplychainx-logs",
  "type": "_doc",
  "body": {
    "timestamp": "2026-01-02T10:30:00",
    "level": "ERROR",
    "service": "service-production",
    "message": "Failed to process order",
    "user": "admin@supplychainx.com"
  }
}
```

### 2. Logstash

**Logstash** est un pipeline de traitement de données qui ingère, transforme et envoie les données vers Elasticsearch.

#### Pipeline Logstash :
1. **Input** : Collecte des données (fichiers, bases de données, API, etc.)
2. **Filter** : Transformation et enrichissement des données
3. **Output** : Envoi vers Elasticsearch ou autres destinations

#### Exemple de configuration :
```ruby
input {
  file {
    path => "/var/log/supplychainx/*.log"
    type => "application"
  }
}

filter {
  grok {
    match => { "message" => "%{TIMESTAMP_ISO8601:timestamp} %{LOGLEVEL:level} %{GREEDYDATA:message}" }
  }
  
  date {
    match => [ "timestamp", "ISO8601" ]
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "supplychainx-%{+YYYY.MM.dd}"
  }
}
```

### 3. Kibana

**Kibana** est l'interface de visualisation qui permet d'explorer et visualiser les données stockées dans Elasticsearch.

#### Fonctionnalités :
- **Discover** : Exploration interactive des données
- **Visualize** : Création de graphiques et visualisations
- **Dashboard** : Tableaux de bord personnalisés
- **Canvas** : Présentations infographiques
- **Machine Learning** : Détection d'anomalies
- **Alerting** : Notifications sur événements

### 4. Beats (Bonus)

**Beats** sont des agents légers qui envoient des données directement vers Elasticsearch ou Logstash.

Types de Beats :
- **Filebeat** : Logs de fichiers
- **Metricbeat** : Métriques système et services
- **Packetbeat** : Trafic réseau
- **Auditbeat** : Audit de sécurité
- **Heartbeat** : Monitoring de disponibilité

---

## Architecture

### Architecture Simple
```
Application (Spring Boot)
         ↓
    Logstash (Pipeline)
         ↓
   Elasticsearch (Indexation)
         ↓
     Kibana (Visualisation)
```

### Architecture Distribuée
```
Multiple Applications
    ↓    ↓    ↓
   Filebeat (Agent léger)
         ↓
    Logstash (Agrégation)
         ↓
Elasticsearch Cluster (3+ nodes)
         ↓
   Kibana + Load Balancer
```

---

## Installation et Configuration

### Avec Docker Compose

```yaml
version: '3.8'

services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
      - xpack.security.enabled=false
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - elasticsearch-data:/usr/share/elasticsearch/data
    networks:
      - elk

  logstash:
    image: docker.elastic.co/logstash/logstash:8.11.0
    container_name: logstash
    volumes:
      - ./logstash.conf:/usr/share/logstash/pipeline/logstash.conf
    ports:
      - "5000:5000"
      - "9600:9600"
    depends_on:
      - elasticsearch
    networks:
      - elk

  kibana:
    image: docker.elastic.co/kibana/kibana:8.11.0
    container_name: kibana
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
    ports:
      - "5601:5601"
    depends_on:
      - elasticsearch
    networks:
      - elk

volumes:
  elasticsearch-data:

networks:
  elk:
    driver: bridge
```

### Configuration Spring Boot (logback-spring.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg traceId=%X{traceId} spanId=%X{spanId}%n</pattern>
        </encoder>
    </appender>

    <!-- Logstash Appender for sending logs to Logstash-->
    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <!-- Logstash host and port -->
        <!-- localhost:5044 is for local development-->
        <!-- In production, this should point to the Logstash service address -->
        <destination>localhost:5044</destination>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>

    <root level="info">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="LOGSTASH"/>
    </root>

</configuration>
```

---

## Utilisation dans SupplyChainX

### 1. Tracking des Opérations Métier

**Scénarios à logger :**
- Création de commandes d'approvisionnement
- Gestion des livraisons
- Production de produits
- Actions utilisateur critiques

### 2. Monitoring de Performance

**Métriques à surveiller :**
- Temps de réponse des APIs
- Utilisation des ressources (CPU, mémoire)
- Taux d'erreurs par service
- Nombre de requêtes par endpoint

### 3. Sécurité et Audit

**Événements de sécurité :**
- Tentatives de connexion (réussies/échouées)
- Modifications de rôles et permissions
- Accès non autorisés
- Changements de configuration

### 4. Exemple de Queries Kibana

**Recherche d'erreurs dans le service production :**
```
level:ERROR AND service:service-production
```

**Analyse des commandes par utilisateur :**
```
action:"CREATE_ORDER" AND timestamp:[now-7d TO now]
```

**Alertes sur échecs d'authentification :**
```
event.type:authentication AND event.outcome:failure
```

---

## Bonnes Pratiques

### 1. Structuration des Logs

✅ **BON** - Log structuré JSON :
```json
{
  "timestamp": "2026-01-02T10:30:00Z",
  "level": "ERROR",
  "service": "production",
  "action": "CREATE_PRODUCT",
  "user_id": "123",
  "error": "Validation failed",
  "trace_id": "abc-123-xyz"
}
```

❌ **MAUVAIS** - Log non structuré :
```
ERROR: Something went wrong in production
```

### 2. Niveaux de Logs

- **TRACE** : Informations de debug très détaillées
- **DEBUG** : Informations utiles pour le développement
- **INFO** : Événements informatifs (état normal)
- **WARN** : Situations anormales mais gérées
- **ERROR** : Erreurs nécessitant attention
- **FATAL** : Erreurs critiques arrêtant l'application

### 3. Index Management

- **Index Lifecycle Management (ILM)** : Automatiser la rotation des index
- **Naming Convention** : `supplychainx-{service}-{date}`
- **Retention Policy** : Définir la durée de conservation (ex: 30 jours)

### 4. Performance

- **Bulk Indexing** : Envoyer les logs par batch
- **Async Logging** : Utiliser des appenders asynchrones
- **Resource Limits** : Définir heap size approprié pour Elasticsearch
- **Shard Sizing** : Ne pas créer trop de petits shards

### 5. Sécurité

- **Activer TLS/SSL** pour les communications
- **Authentification** avec X-Pack Security
- **Role-Based Access Control (RBAC)**
- **Masquer les données sensibles** (mots de passe, tokens)

### 6. Dashboards Recommandés

1. **Application Overview**
   - Nombre total de logs par niveau
   - Distribution des erreurs par service
   - Timeline des événements

2. **Performance Monitoring**
   - Temps de réponse moyen par endpoint
   - Throughput (requêtes/seconde)
   - Taux d'erreurs

3. **Security Dashboard**
   - Tentatives de connexion
   - Événements de sécurité
   - Accès par utilisateur

4. **Business Metrics**
   - Nombre de commandes créées
   - Volume de production
   - Livraisons effectuées

---

## Commandes Utiles

### Elasticsearch

```bash
# Vérifier la santé du cluster
curl -X GET "localhost:9200/_cluster/health?pretty"

# Lister les index
curl -X GET "localhost:9200/_cat/indices?v"

# Rechercher dans un index
curl -X GET "localhost:9200/supplychainx-2026.01.02/_search?pretty" -H 'Content-Type: application/json' -d'
{
  "query": {
    "match": { "level": "ERROR" }
  }
}'

# Supprimer un index
curl -X DELETE "localhost:9200/supplychainx-2026.01.01"
```

### Logstash

```bash
# Tester la configuration
bin/logstash --config.test_and_exit -f logstash.conf

# Recharger la configuration
curl -X POST "localhost:9600/_reload"

# Vérifier les pipelines actifs
curl -X GET "localhost:9600/_node/pipelines?pretty"
```

---

## Ressources

- **Documentation officielle** : https://www.elastic.co/guide/
- **Elastic Stack** : https://www.elastic.co/elastic-stack
- **Community Forums** : https://discuss.elastic.co/
- **GitHub** : https://github.com/elastic

---

## Conclusion

L'ELK Stack est un outil puissant pour la gestion centralisée des logs dans les applications modernes. Pour SupplyChainX, il permet :

✅ Une visibilité complète sur l'activité de tous les services  
✅ Un diagnostic rapide des problèmes en production  
✅ Une conformité aux exigences d'audit et de sécurité  
✅ Une analyse business basée sur les données réelles  

**Prochaines étapes** : Implémenter l'ILM, créer des dashboards métier personnalisés, configurer des alertes automatiques.

