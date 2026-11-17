# SEG3502 — Laboratoire 9 — Code de départ

Ce projet contient l'API REST de conversion de température à sécuriser pour le laboratoire 9.

Le code de départ **ne contient pas encore Spring Security**. Le travail demandé consiste à ajouter
l'authentification **In Memory** et à exiger **Basic Authentication** pour toutes les requêtes de l'API.

## Démarrage

Le projet utilise Java 21.

Sous macOS/Linux :

```bash
./gradlew bootRun
```

Sous Windows :

```text
gradlew.bat bootRun
```

Exemples de points de terminaison déjà disponibles :

```text
GET http://localhost:8080/temperature-converter/celsius-fahrenheit/50
GET http://localhost:8080/temperature-converter/fahrenheit-celsius/122
```

## Travail demandé

Conformément aux consignes du laboratoire :

- ajouter Spring Security au projet;
- configurer une authentification en mémoire;
- créer au minimum :
  - `user1` avec le mot de passe `pass1`;
  - `user2` avec le mot de passe `pass2`;
- exiger l'authentification Basic pour toutes les requêtes à l'API;
- tester l'API avec Postman, Insomnia, Curl ou un outil similaire.

Aucune interface utilisateur n'est demandée pour ce livrable.

## Solution — Sécurité (In Memory + Basic Authentication)

Modifications apportées :

- `build.gradle.kts` : ajout de `spring-boot-starter-security` (et `spring-security-test` pour les tests).
- `src/main/kotlin/seg3x02/tempconverterapi/security/SecurityConfig.kt` :
  - `InMemoryUserDetailsManager` avec `user1`/`pass1` et `user2`/`pass2` (mots de passe hachés avec BCrypt);
  - `anyRequest().authenticated()` : toutes les requêtes exigent une authentification;
  - `httpBasic()` activé, formulaire de connexion désactivé, CSRF désactivé (API REST);
  - politique de session `STATELESS` : les identifiants doivent accompagner **chaque** requête.
- `src/test/kotlin/seg3x02/tempconverterapi/SecurityTests.kt` : tests MockMvc (401 sans identifiants
  ou avec un mauvais mot de passe, 200 avec `user1` et `user2`).

### Tests avec Curl

```bash
# Sans identifiants -> 401 Unauthorized
curl -i http://localhost:8080/temperature-converter/celsius-fahrenheit/50

# Mauvais mot de passe -> 401 Unauthorized
curl -i -u user1:mauvais http://localhost:8080/temperature-converter/celsius-fahrenheit/50

# user1 -> 200 OK, corps : 122.0
curl -i -u user1:pass1 http://localhost:8080/temperature-converter/celsius-fahrenheit/50

# user2 -> 200 OK, corps : 50.0
curl -i -u user2:pass2 http://localhost:8080/temperature-converter/fahrenheit-celsius/122
```

Dans Postman / Insomnia : onglet **Auth** → **Basic Auth**, saisir le nom d'utilisateur et le mot de passe.
L'en-tête envoyé est `Authorization: Basic dXNlcjE6cGFzczE=` (base64 de `user1:pass1`).

Lancer les tests automatisés : `./gradlew test`
