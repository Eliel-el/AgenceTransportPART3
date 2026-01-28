# Guide d'intégration du Service de Chauffeurs

## 📋 Aperçu

Ce service permet à votre application de :
- Communiquer avec le service de chauffeurs sur une machine distante
- Gérer facilement l'adresse IP et le port de la machine distante
- Effectuer des opérations CRUD sur les chauffeurs
- Assigner et libérer les chauffeurs des trajets

## 🔧 Configuration

### Modification de l'adresse IP du service distant

#### Option 1 : Fichier de configuration (Recommandé)
Modifiez le fichier `src/main/resources/application.properties` :

```properties
# Service 2 - Gestion des Chauffeurs
chauffeur.service.url=http://VOTRE_IP:8080/AgenceTransportPART2/api/chauffeurs

# Service 3 - Gestion des Bus
bus.service.url=http://VOTRE_IP:9090/AgenceTransportPART4/api/bus
```

**Exemple :**
```properties
# Si le service est sur 192.168.1.100
chauffeur.service.url=http://192.168.1.100:8080/AgenceTransportPART2/api/chauffeurs
```

#### Option 2 : Modification dynamique (À l'exécution)
Vous pouvez modifier l'adresse IP sans redémarrer l'application :

```java
@Inject
private ServiceConfig serviceConfig;

// Modifier l'URL du service
serviceConfig.setChauffeurServiceUrl("http://192.168.1.100:8080/AgenceTransportPART2/api/chauffeurs");
```

## 📡 Endpoints disponibles

### 1. Liste tous les chauffeurs
```
GET /api/chauffeurs
```

**Réponse :**
```json
[
  {
    "id": 1,
    "nom": "Jean Dupont",
    "permis": "B",
    "disponible": true
  },
  {
    "id": 2,
    "nom": "Marie Martin",
    "permis": "D",
    "disponible": false
  }
]
```

### 2. Détails d'un chauffeur
```
GET /api/chauffeurs/{id}
```

**Réponse :**
```json
{
  "id": 1,
  "nom": "Jean Dupont",
  "permis": "B",
  "disponible": true,
  "telephone": "06-12-34-56-78"
}
```

### 3. Créer un nouveau chauffeur
```
POST /api/chauffeurs
Content-Type: application/json

{
  "nom": "Paul Bernard",
  "permis": "D",
  "telephone": "06-87-65-43-21"
}
```

**Réponse :**
```json
{
  "id": 3,
  "nom": "Paul Bernard",
  "permis": "D",
  "telephone": "06-87-65-43-21",
  "disponible": true
}
```

### 4. Modifier un chauffeur
```
PUT /api/chauffeurs/{id}
Content-Type: application/json

{
  "nom": "Jean Dupont",
  "permis": "B",
  "telephone": "06-99-88-77-66",
  "disponible": true
}
```

### 5. Supprimer un chauffeur
```
DELETE /api/chauffeurs/{id}
```

**Réponse :** Status 204 (No Content)

### 6. Chauffeurs disponibles
```
GET /api/chauffeurs/disponibles
```

**Réponse :**
```json
[
  {
    "id": 1,
    "nom": "Jean Dupont",
    "disponible": true
  },
  {
    "id": 3,
    "nom": "Paul Bernard",
    "disponible": true
  }
]
```

### 7. Assigner un chauffeur à un trajet
```
POST /api/chauffeurs/assigner
Content-Type: application/json

{
  "chauffeurId": 1,
  "trajetId": 5
}
```

**Réponse :**
```json
{
  "message": "Chauffeur assigned successfully"
}
```

### 8. Libérer un chauffeur d'un trajet
```
POST /api/chauffeurs/{id}/liberer
```

**Réponse :**
```json
{
  "message": "Chauffeur released successfully"
}
```

## 🏗️ Architecture

### Classes créées

1. **ServiceConfig** (`com.jakarta.udb.agencetransportpart3.config.ServiceConfig`)
   - Gère la configuration des URLs des services externes
   - Permet la modification dynamique des adresses IP
   - Charge les valeurs depuis `application.properties`

2. **ChauffeurBean** (`com.jakarta.udb.agencetransportpart3.bean.ChauffeurBean`)
   - Bean de service pour la logique métier
   - Communique avec le service distant via REST
   - Gère les erreurs de communication

3. **ChauffeurResource** (`com.jakarta.udb.agencetransportpart3.api.ChauffeurResource`)
   - REST API resource
   - Expose les endpoints pour les opérations sur les chauffeurs
   - Valide les requêtes et formatte les réponses

## 🔌 Integration avec votre application

### 1. Injection de la ressource
```java
@Inject
private ChauffeurResource chauffeurResource;
```

### 2. Utilisation dans d'autres services
```java
@Inject
private ChauffeurBean chauffeurBean;

// Récupérer un chauffeur
LocalChauffeur chauffeur = chauffeurBean.getChauffeurById(1);

// Assigner à un trajet
boolean assigned = chauffeurBean.assignChauffeurToTrajet(1, 5);
```

## 🛠️ Modification des paramètres

### Changer l'adresse IP du service de chauffeurs
```properties
# AVANT
chauffeur.service.url=http://localhost:8080/AgenceTransportPART2/api/chauffeurs

# APRÈS (nouvelle machine)
chauffeur.service.url=http://192.168.0.50:8080/AgenceTransportPART2/api/chauffeurs
```

### Changer le timeout de communication
```properties
# Timeout en millisecondes
chauffeur.service.timeout=5000
bus.service.timeout=5000
```

## 📝 Logs

L'application génère des logs pour :
- Configuration chargée
- Chauffeurs récupérés
- Erreurs de communication
- Modifications d'URLs dynamiques

**Consultez les logs du serveur pour le diagnostic :**
```
INFO: Configuration loaded successfully
INFO: Retrieved 5 chauffeurs from external service
INFO: Chauffeur service URL updated to: http://192.168.1.100:8080/...
```

## ⚠️ Gestion des erreurs

Tous les endpoints retournent des codes HTTP standard :
- `200 OK` : Succès
- `201 CREATED` : Création réussie
- `204 NO CONTENT` : Suppression réussie
- `400 BAD REQUEST` : Données invalides
- `404 NOT FOUND` : Ressource non trouvée
- `500 INTERNAL_SERVER_ERROR` : Erreur serveur

**Exemple d'erreur :**
```json
{
  "error": "Chauffeur not found"
}
```

## 🚀 Déploiement

1. Assurez-vous que le fichier `application.properties` est dans le classpath
2. Configurez l'adresse IP correcte avant le déploiement
3. Vérifiez la connectivité réseau vers la machine distante
4. Les services doivent être en cours d'exécution sur la machine distante

## 📌 Checklist de configuration

- [ ] Adresse IP du Service 2 (Chauffeurs) correcte
- [ ] Port du Service 2 (8080) accessible
- [ ] Adresse IP du Service 3 (Bus) correcte
- [ ] Port du Service 3 (9090) accessible
- [ ] Firewall autorise les connexions
- [ ] Application.properties chargé correctement
- [ ] Logs affichent "Configuration loaded successfully"
