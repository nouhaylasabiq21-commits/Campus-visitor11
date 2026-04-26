cat > /mnt/user-data/outputs/README.md << 'EOF'
# 📱 CampusVisitor Android — ENS Marrakech

Application mobile Android native (Java) pour la gestion des visiteurs du campus de l'École Normale Supérieure de Marrakech.

---

## 📋 Description

Cette application Android est la partie mobile du système de gestion des visiteurs du campus ENS Marrakech. Elle permet aux agents de sécurité, hôtes et visiteurs d'accéder aux fonctionnalités essentielles du système depuis leur téléphone Android.

---

## ✨ Fonctionnalités

### Côté Agent / Hôte
- 🔐 Connexion sécurisée avec sélection du profil (Admin, Agent, Hôte, Visiteur)
- 📋 Voir toutes les demandes de visite avec filtres par statut
- ✅ Approuver ou refuser les demandes en attente
- 🚪 Check-in — Enregistrer l'entrée d'un visiteur
- 🚶 Check-out — Enregistrer la sortie d'un visiteur
- 👥 Consulter la liste des visiteurs et des hôtes
- 📊 Tableau de bord avec statistiques en temps réel
- 📜 Journal des événements (CHECK_IN / CHECK_OUT)

### Côté Visiteur
- 📝 Soumettre une demande de visite
- 👁️ Voir le statut de sa demande
- 📅 Consulter les informations du rendez-vous

### Authentification
- 🔑 Connexion JWT sécurisée
- 📧 Mot de passe oublié par email
- 💾 Session persistante via SharedPreferences

---

## 🛠️ Technologies utilisées

| Technologie | Version | Usage |
|---|---|---|
| Java | JDK 8+ | Langage principal |
| Android SDK | API 24+ | Plateforme mobile |
| Volley | 1.2.1 | Requêtes HTTP |
| Material Design | 1.11.0 | Interface utilisateur |
| Navigation Component | 2.7.6 | Navigation entre fragments |
| RecyclerView | 1.3.2 | Listes dynamiques |
| CardView | 1.0.0 | Cartes d'affichage |
| SwipeRefreshLayout | 1.1.0 | Actualisation par glissement |
| Gson | 2.10.1 | Sérialisation JSON |

---

## 📁 Structure du projet

```
app/src/main/java/com/ens/campusvisitor/
├── api/
│   ├── VolleyClient.java          # Singleton Volley - gestion des requêtes
│   └── ApiManager.java            # Centralisation de tous les appels API
├── ui/
│   ├── auth/
│   │   ├── LoginActivity.java     # Page de connexion multi-profil
│   │   └── ForgotPasswordActivity.java  # Mot de passe oublié
│   ├── dashboard/
│   │   └── DashboardFragment.java # Tableau de bord avec statistiques
│   ├── visits/
│   │   ├── VisitsFragment.java    # Liste et gestion des visites
│   │   └── VisitAdapter.java      # Adaptateur RecyclerView des visites
│   ├── visitors/
│   │   ├── VisitorsFragment.java  # Liste des visiteurs
│   │   └── PersonAdapter.java     # Adaptateur réutilisable (visiteurs/hôtes)
│   ├── hosts/
│   │   └── HostsFragment.java     # Liste des hôtes
│   ├── checkin/
│   │   └── CheckInFragment.java   # Interface check-in / check-out
│   ├── logs/
│   │   ├── LogsFragment.java      # Journal des événements
│   │   └── LogAdapter.java        # Adaptateur des logs
│   └── MainActivity.java          # Activité principale avec navigation
└── utils/
    └── SessionManager.java        # Gestion de la session JWT
```

---

## ⚙️ Configuration

### Prérequis
- Android Studio Hedgehog ou plus récent
- JDK 8 minimum
- Android SDK API 24 minimum (Android 7.0)
- Le backend `campus-visitor-api` doit être en cours d'exécution

### Installation

**1 — Clonez ou ouvrez le projet dans Android Studio**
```
File → Open → sélectionnez le dossier CampusVisitor
```

**2 — Configurez l'URL du backend**

Ouvrez `api/VolleyClient.java` et modifiez `BASE_URL` :

```java
// Sur émulateur Android
public static final String BASE_URL = "http://10.0.2.2:3000/";

// Sur vrai téléphone (remplacez par l'IP de votre PC)
public static final String BASE_URL = "http://192.168.1.X:3000/";
```

Pour trouver votre IP sur Windows :
```powershell
ipconfig
# Cherchez IPv4 Address
```

**3 — Synchronisez Gradle**
```
File → Sync Project with Gradle Files
```

**4 — Lancez l'application**
- Connectez votre téléphone Android en USB
- Activez le débogage USB sur votre téléphone
- Cliquez sur ▶ Run

---

## 🔌 Routes API utilisées

| Méthode | Endpoint | Description |
|---|---|---|
| POST | /api/auth/login | Connexion |
| POST | /api/auth/forgot-password | Mot de passe oublié |
| GET | /api/visits/dashboard/stats | Statistiques dashboard |
| GET | /api/visits?status= | Liste des visites |
| POST | /api/visits | Créer une visite |
| PATCH | /api/visits/:id/status | Modifier le statut |
| POST | /api/visits/:id/check-in | Check-in |
| POST | /api/visits/:id/check-out | Check-out |
| GET | /api/visitors?search= | Liste des visiteurs |
| POST | /api/visitors | Créer un visiteur |
| DELETE | /api/visitors/:id | Supprimer un visiteur |
| GET | /api/hosts?search= | Liste des hôtes |
| POST | /api/hosts | Créer un hôte |
| DELETE | /api/hosts/:id | Supprimer un hôte |
| GET | /api/logs | Journal des événements |

---

## 👤 Comptes de test

| Profil | Email | Mot de passe | Table |
|---|---|---|---|
| Admin | nouhaylasabiq21@gmail.com| admin12345 | hosts |
| Agent | agent@ens.ma | agent123 | visitors |
| Hôte | hote@ens.ma | hote123 | hosts |
| Visiteur | visiteur@test.com | visiteur123 | visitors |

---

## 🔒 Sécurité

- Authentification par **JWT (JSON Web Token)**
- Token stocké dans **SharedPreferences** de manière sécurisée
- Toutes les requêtes API incluent le header `Authorization: Bearer TOKEN`
- Redirection automatique vers le login si le token expire
- Contrôle d'accès différencié selon le rôle de l'utilisateur

---







