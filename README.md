# TP 3 — Spring Cloud Config (Use case GitHub)

Spring Boot 2.7.16 · Spring Cloud 2021.0.8 · Java 8+ · H2

```
TP3-Spring-Cloud-Config/
├── config-server-repo/          -> à pousser sur GitHub (mcommerce-config-repo)
│   └── microservice-produits.properties
├── config.server/               -> Spring Cloud Config Server (port 9101)
└── microservice-produits/       -> MS Produits (port 9001, reçu du config server)
```

## 1. Pousser le repo de config sur GitHub
1. Créer le repo GitHub **mcommerce-config-repo** (vide, public).
2. Dans `config-server-repo/` :
```bash
git init
git checkout -b master
git add .
git commit -m "first commit microservice-produits.properties"
git remote add origin https://github.com/<TON_USER_GITHUB>/mcommerce-config-repo.git
git push -u origin master
```
Mot de passe demandé = **token GitHub** (https://github.com/settings/tokens), pas le mot de passe du compte.

## 2. Lancer le Config Server
1. Dans `config.server/src/main/resources/application.properties`, remplacer `<TON_USER_GITHUB>`.
2. Eclipse : *Import > Existing Maven Project*, puis *Run As > Spring Boot App* (ou `mvn spring-boot:run`).
3. Tester :
   - http://localhost:9101/microservice-produits/default
   - http://localhost:9101/microservice-produits/default/master
   → JSON avec `mes-configs.limitDeProduits: "3"`.

## 3. Lancer le Microservice Produits (config server démarré avant)
- Log attendu : `Fetching config from server at : http://localhost:9101`
- http://localhost:9001/Produits → **3 produits** seulement (sur 8 en base)
- http://localhost:9001/h2-console (JDBC URL : `jdbc:h2:mem:testdb`, user `sa`, mdp vide)

## 4. Tests CRUD (Postman)
| Méthode | URL | Body (JSON) |
|---|---|---|
| GET | `/Produits` | — |
| GET | `/Produits/2` | — |
| POST | `/Produits` | `{"titre":"Lampe","description":"Test","image":"x.png","prix":50}` |
| PUT | `/Produits/2` | `{"titre":"Cheval modifié","description":"...","image":"...","prix":400}` |
| DELETE | `/Produits/2` | — |

## 5. Bonus : changer la config à chaud (@RefreshScope)
1. Modifier sur GitHub `mes-configs.limitDeProduits=5`, commit.
2. `POST http://localhost:9001/actuator/refresh` (Postman, body vide).
3. `GET /Produits` → 5 produits, **sans redémarrer** le MS.

## Corrections apportées par rapport à l'énoncé
- artifactIds coupés dans le PDF corrigés (`spring-boot-starter-data-jpa`, `spring-boot-configuration-processor`, `spring-cloud-starter-config`, `spring-cloud-dependencies`, `spring-boot-maven-plugin`).
- Dépendance `spring-cloud-starter-netflix-eureka-server` retirée du MS Produits (elle transformerait le MS en serveur Eureka ; Eureka n'est pas l'objet de ce TP).
- `management.endpoints.web.expose` → clé correcte `management.endpoints.web.exposure.include`.
- `subList(0, limit)` protégé par `Math.min` (évite IndexOutOfBoundsException si limite > nb produits).
- Ajout POST / PUT / DELETE (CRUD demandé dans le use case).
- `schema.sql` crée aussi `hibernate_sequence` (nécessaire pour les POST avec `ddl-auto=none`).
