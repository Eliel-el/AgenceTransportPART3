# 📋 TOUTES LES ROUTES DE SERVICE 3 (AGENCETRANSPORTPART3)

## 🌐 Configuration de base
- **URL de base** : `http://localhost:8080/AgenceTransportPART3/api`
- **Host** : Modifiable dans `application.properties`
- **Format** : JSON
- **Configuration REST** : `JakartaRestConfiguration.java` avec `@ApplicationPath("api")`

---

## 📦 RESERVATIONS - `/api/reservations`

### 1. Lister toutes les réservations
```
GET /api/reservations
```
**Réponse** : 200 OK - Liste complète des réservations

### 2. Récupérer une réservation par ID
```
GET /api/reservations/{id}
```
**Paramètres** : `id` (Long)
**Réponse** : 200 OK ou 404 NOT_FOUND

### 3. Récupérer les réservations par statut
```
GET /api/reservations/status/{status}
```
**Paramètres** : `status` (String) - ex: "PENDING", "CONFIRMED", "CANCELLED"
**Réponse** : 200 OK - Liste filtrée

### 4. Créer une nouvelle réservation
```
POST /api/reservations
Content-Type: application/json

{
  "passager": "string",
  "dateDepart": "2025-02-01",
  "nombrePlaces": 3,
  "status": "PENDING"
}
```
**Réponse** : 201 CREATED ou 400 BAD_REQUEST

### 5. Modifier une réservation
```
PUT /api/reservations/{id}
Content-Type: application/json

{
  "passager": "string",
  "nombrePlaces": 5,
  "status": "PENDING"
}
```
**Paramètres** : `id` (Long)
**Réponse** : 200 OK ou 404 NOT_FOUND

### 6. Confirmer une réservation
```
POST /api/reservations/{id}/confirm?busId={busId}&chauffeurId={chauffeurId}
```
**Paramètres** : 
- `id` (Long) - ID réservation
- `busId` (Long) - Query param
- `chauffeurId` (Long) - Query param
**Réponse** : 200 OK ou 400 BAD_REQUEST

### 7. Annuler une réservation
```
POST /api/reservations/{id}/cancel
```
**Paramètres** : `id` (Long)
**Réponse** : 200 OK

### 8. Supprimer une réservation
```
DELETE /api/reservations/{id}
```
**Paramètres** : `id` (Long)
**Réponse** : 200 OK

---

## 🚗 TRAJETS - `/api/trajets`

### 1. Lister tous les trajets
```
GET /api/trajets
```
**Réponse** : 200 OK - Liste complète des trajets

### 2. Récupérer un trajet par ID
```
GET /api/trajets/{id}
```
**Paramètres** : `id` (Long)
**Réponse** : 200 OK ou 404 NOT_FOUND

### 3. Récupérer les trajets par statut
```
GET /api/trajets/status/{status}
```
**Paramètres** : `status` (String) - ex: "PLANIFIÉ", "EN_COURS", "COMPLÉTÉ"
**Réponse** : 200 OK - Liste filtrée

### 4. Récupérer un trajet par ID de réservation
```
GET /api/trajets/reservation/{reservationId}
```
**Paramètres** : `reservationId` (Long)
**Réponse** : 200 OK ou 404 NOT_FOUND

### 5. Créer un nouveau trajet
```
POST /api/trajets?reservationId={reservationId}&busId={busId}&chauffeurId={chauffeurId}
```
**Paramètres Query** :
- `reservationId` (Long)
- `busId` (Long)
- `chauffeurId` (Long)
**Réponse** : 201 CREATED ou 400 BAD_REQUEST

### 6. Modifier un trajet
```
PUT /api/trajets/{id}
Content-Type: application/json

{
  "depart": "Casablanca",
  "arrivee": "Fès",
  "dateDepart": "2025-02-01",
  "status": "PLANIFIÉ"
}
```
**Paramètres** : `id` (Long)
**Réponse** : 200 OK ou 404 NOT_FOUND

### 7. Assigner un bus à un trajet
```
POST /api/trajets/{id}/assign-bus?busId={busId}
```
**Paramètres** :
- `id` (Long) - ID trajet
- `busId` (Long) - Query param
**Réponse** : 200 OK

### 8. Assigner un chauffeur à un trajet
```
POST /api/trajets/{id}/assign-chauffeur?chauffeurId={chauffeurId}
```
**Paramètres** :
- `id` (Long) - ID trajet
- `chauffeurId` (Long) - Query param
**Réponse** : 200 OK

### 9. Démarrer un trajet
```
POST /api/trajets/{id}/start
```
**Paramètres** : `id` (Long)
**Réponse** : 200 OK

### 10. Complèter un trajet
```
POST /api/trajets/{id}/complete
```
**Paramètres** : `id` (Long)
**Réponse** : 200 OK

### 11. Annuler un trajet
```
POST /api/trajets/{id}/cancel
```
**Paramètres** : `id` (Long)
**Réponse** : 200 OK

### 12. Supprimer un trajet
```
DELETE /api/trajets/{id}
```
**Paramètres** : `id` (Long)
**Réponse** : 200 OK

---

## 📊 REPORTS - `/api/reports`

### 1. Rapport résumé
```
GET /api/reports/summary
```
**Réponse** : 200 OK - Rapport récapitulatif

### 2. Rapport par bus
```
GET /api/reports/by-bus
```
**Réponse** : 200 OK - Rapport groupé par bus

### 3. Rapport par chauffeur
```
GET /api/reports/by-chauffeur
```
**Réponse** : 200 OK - Rapport groupé par chauffeur

### 4. Réservations avec trajets associés
```
GET /api/reports/reservations-with-trajets
```
**Réponse** : 200 OK - Données combinées

---

## 👤 CHAUFFEURS - `/api/chauffeurs`

### 1. Lister tous les chauffeurs
```
GET /api/chauffeurs
```
**Réponse** : 200 OK

### 2. Récupérer un chauffeur par ID
```
GET /api/chauffeurs/{id}
```
**Paramètres** : `id` (Long)
**Réponse** : 200 OK ou 404 NOT_FOUND

### 3. Créer un chauffeur
```
POST /api/chauffeurs
Content-Type: application/json

{
  "nom": "Jean Dupont",
  "permis": "B",
  "telephone": "06-12-34-56-78"
}
```
**Réponse** : 201 CREATED

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
**Paramètres** : `id` (Long)
**Réponse** : 200 OK

### 5. Supprimer un chauffeur
```
DELETE /api/chauffeurs/{id}
```
**Paramètres** : `id` (Long)
**Réponse** : 204 NO_CONTENT

### 6. Chauffeurs disponibles
```
GET /api/chauffeurs/disponibles
```
**Réponse** : 200 OK - Liste chauffeurs disponibles

### 7. Assigner un chauffeur à un trajet
```
POST /api/chauffeurs/assigner
Content-Type: application/json

{
  "chauffeurId": 1,
  "trajetId": 5
}
```
**Réponse** : 200 OK

### 8. Libérer un chauffeur d'un trajet
```
POST /api/chauffeurs/{id}/liberer
```
**Paramètres** : `id` (Long)
**Réponse** : 200 OK

---

## 🔧 SYSTÈME - `/api/jakartaee10`

### 1. Test de connectivité
```
GET /api/jakartaee10
```
**Réponse** : 200 OK - "ping Jakarta EE"

---

## 📝 RÉSUMÉ COMPLET

| Ressource | Méthode | Endpoint | Description |
|-----------|---------|----------|-------------|
| **Réservations** | GET | `/api/reservations` | Lister toutes |
| | GET | `/api/reservations/{id}` | Détails |
| | GET | `/api/reservations/status/{status}` | Par statut |
| | POST | `/api/reservations` | Créer |
| | PUT | `/api/reservations/{id}` | Modifier |
| | POST | `/api/reservations/{id}/confirm` | Confirmer |
| | POST | `/api/reservations/{id}/cancel` | Annuler |
| | DELETE | `/api/reservations/{id}` | Supprimer |
| **Trajets** | GET | `/api/trajets` | Lister tous |
| | GET | `/api/trajets/{id}` | Détails |
| | GET | `/api/trajets/status/{status}` | Par statut |
| | GET | `/api/trajets/reservation/{reservationId}` | Par réservation |
| | POST | `/api/trajets` | Créer |
| | PUT | `/api/trajets/{id}` | Modifier |
| | POST | `/api/trajets/{id}/assign-bus` | Assigner bus |
| | POST | `/api/trajets/{id}/assign-chauffeur` | Assigner chauffeur |
| | POST | `/api/trajets/{id}/start` | Démarrer |
| | POST | `/api/trajets/{id}/complete` | Complèter |
| | POST | `/api/trajets/{id}/cancel` | Annuler |
| | DELETE | `/api/trajets/{id}` | Supprimer |
| **Rapports** | GET | `/api/reports/summary` | Résumé |
| | GET | `/api/reports/by-bus` | Par bus |
| | GET | `/api/reports/by-chauffeur` | Par chauffeur |
| | GET | `/api/reports/reservations-with-trajets` | Réservations + Trajets |
| **Chauffeurs** | GET | `/api/chauffeurs` | Lister tous |
| | GET | `/api/chauffeurs/{id}` | Détails |
| | GET | `/api/chauffeurs/disponibles` | Disponibles |
| | POST | `/api/chauffeurs` | Créer |
| | PUT | `/api/chauffeurs/{id}` | Modifier |
| | DELETE | `/api/chauffeurs/{id}` | Supprimer |
| | POST | `/api/chauffeurs/assigner` | Assigner |
| | POST | `/api/chauffeurs/{id}/liberer` | Libérer |
| **Système** | GET | `/api/jakartaee10` | Ping |

---

## 🔐 Authentification
Aucune authentification actuellement (à implémenter si nécessaire)

## 📤 Types de contenus acceptés
- `application/json` - JSON pour toutes les requêtes

## 📥 Types de réponses
- `application/json` - JSON pour toutes les réponses

## ⏱️ Timeouts
- Chauffeurs : 5000ms
- Bus : 5000ms

---

## 💡 Exemples d'utilisation avec CURL

### Récupérer tous les chauffeurs
```bash
curl -X GET "http://192.168.1.X:8080/AgenceTransportPART3/api/chauffeurs" \
  -H "Content-Type: application/json"
```

### Créer une réservation
```bash
curl -X POST "http://192.168.1.X:8080/AgenceTransportPART3/api/reservations" \
  -H "Content-Type: application/json" \
  -d '{
    "passager": "Ahmed",
    "dateDepart": "2025-02-05",
    "nombrePlaces": 2
  }'
```

### Assigner un chauffeur
```bash
curl -X POST "http://192.168.1.X:8080/AgenceTransportPART3/api/chauffeurs/assigner" \
  -H "Content-Type: application/json" \
  -d '{
    "chauffeurId": 1,
    "trajetId": 5
  }'
```

---

**✅ Document généré le** : 28/01/2026
**📦 Version** : Service 3 PART3
**🔗 À donner à** : Service 2 (PART2) et Service 4 (PART4)
