Pour tester votre API directement dans le navigateur, voici les étapes et les URLs spécifiques à votre projet :

1. Tester les requêtes GET (Lecture)
C'est le plus simple, vous pouvez taper l'URL directement dans la barre d'adresse de votre navigateur.

Toutes les réservations : http://localhost:8080/AgenceTransportPART3/resources/reservations
Tous les trajets : http://localhost:8080/AgenceTransportPART3/resources/trajets
Rapport de synthèse : http://localhost:8080/AgenceTransportPART3/resources/reports/summary
Réservations avec détails des trajets : http://localhost:8080/AgenceTransportPART3/resources/reports/reservations-with-trajets
2. Tester les requêtes POST, PUT, DELETE (Écriture)
Le navigateur ne peut pas faire ces requêtes via la barre d'adresse. Voici deux méthodes :

A. Utiliser la console du navigateur (Sans extension)
Ouvrez votre navigateur sur n'importe quelle page.
Appuyez sur F12 pour ouvrir les Outils de développement.
Allez dans l'onglet Console.
Copiez-collez ce code pour tester la création d'une réservation :
javascript
fetch('http://localhost:8080/AgenceTransportPART3/resources/reservations', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
        nomClient: "Jean Dupont",
        dateReservation: "2026-01-15T10:00:00",
        lieuDepart: "Paris",
        lieuArrivee: "Lyon",
        status: "EN_ATTENTE"
    })
}).then(res => res.json()).then(console.log);
B. Utiliser une extension (Recommandé)
Pour un test plus confortable, je vous recommande d'installer une extension comme :

Talend API Tester ou Postman (disponibles sur Chrome/Edge/Firefox).
Elles permettent de configurer facilement le corps du message (JSON), les headers et de voir les codes de retour (200 OK, 201 Created, etc.).
Résumé des chemins (Endpoints) :
Base URL : http://localhost:8080/AgenceTransportPART3/resources
Réservations : /reservations (GET, POST), /reservations/{id} (GET, PUT, DELETE)
Trajets : /trajets (GET, POST), /trajets/{id} (GET, PUT, DELETE)
Rapports : /reports/summary, /reports/by-bus, /reports/by-chauffeur
Note : Assurez-vous que votre serveur GlassFish est bien démarré et que le projet est déployé.