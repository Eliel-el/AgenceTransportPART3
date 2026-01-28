# 🔧 OÙ MODIFIER LES IP DES DEUX AUTRES SERVICES

## 📍 Fichier à modifier :
```
src/main/resources/application.properties
```

---

## 🎯 Modification des adresses IP

### Service 2 - Gestion des Chauffeurs (PART2)

**AVANT** :
```properties
chauffeur.service.url=http://localhost:8080/AgenceTransportPART2/api/chauffeurs
```

**APRÈS** (avec l'IP de la machine du Service 2) :
```properties
chauffeur.service.url=http://192.168.1.100:8080/AgenceTransportPART2/api/chauffeurs
```

**Explications** :
- `192.168.1.100` = IP de la machine du Service 2
- `8080` = Port Glassfish du Service 2
- `/AgenceTransportPART2/api/chauffeurs` = Chemin exact du service

---

### Service 4 - Gestion des Bus (PART4)

**AVANT** :
```properties
bus.service.url=http://localhost:9090/AgenceTransportPART4/api/bus
```

**APRÈS** (avec l'IP de la machine du Service 4) :
```properties
bus.service.url=http://192.168.1.50:9090/AgenceTransportPART4/api/bus
```

**Explications** :
- `192.168.1.50` = IP de la machine du Service 4
- `9090` = Port Glassfish du Service 4
- `/AgenceTransportPART4/api/bus` = Chemin exact du service

---

## 📋 Exemple complet du fichier application.properties

```properties
# Configuration des services externes
# Service 2 - Gestion des Chauffeurs (MODIFIER CETTE IP)
chauffeur.service.url=http://192.168.1.100:8080/AgenceTransportPART2/api/chauffeurs

# Service 4 - Gestion des Bus (MODIFIER CETTE IP)
bus.service.url=http://192.168.1.50:9090/AgenceTransportPART4/api/bus

# Configuration d'accès (optionnel)
chauffeur.service.timeout=5000
bus.service.timeout=5000
```

---

## 🔍 Où trouver les IPs ?

### Pour le Service 2 (Chauffeurs)
1. Sur la machine qui héberge le Service 2
2. Ouvrir un terminal/PowerShell
3. Exécuter : `ipconfig` (Windows)
4. Chercher "IPv4 Address" = c'est l'IP à mettre

### Pour le Service 4 (Bus)
1. Sur la machine qui héberge le Service 4
2. Ouvrir un terminal/PowerShell
3. Exécuter : `ipconfig` (Windows)
4. Chercher "IPv4 Address" = c'est l'IP à mettre

---

## ✅ Checklist

- [ ] IP du Service 2 trouvée
- [ ] IP du Service 4 trouvée
- [ ] Fichier `application.properties` modifié
- [ ] Les deux services sont en cours d'exécution
- [ ] Firewall autorise les connexions entre machines
- [ ] Application redémarrée

---

## 🚀 Après modification

1. **Redémarrer** GlassFish de Service 3
2. **Attendre** le chargement de l'application
3. **Vérifier** les logs pour voir "Configuration loaded successfully"
4. **Tester** les endpoints avec les IPs correctes

---

## 💡 Exemple pour tester

```bash
# Remplacer 192.168.1.X par l'IP réelle de votre Service 3
curl -X GET "http://192.168.1.X:8080/AgenceTransportPART3/api/chauffeurs" \
  -H "Content-Type: application/json"
```

Si ça marche = les IPs des deux autres services sont correctes ! ✅
