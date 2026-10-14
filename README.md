# Generateur-Data

Générateur de **jeux de données de test fictifs** (utilisateurs) au format CSV, en Java, sans dépendance.

Il sert à alimenter :
- les **tests de performance** : `CSV Data Set Config` JMeter, feeders Gatling (un compte différent par utilisateur virtuel) ;
- les **campagnes de recette** : création de comptes, formulaires d'inscription, parcours de commande ;
- les **tests automatisés** qui ont besoin de données variées mais maîtrisées.

## Pourquoi des données fictives ?

Utiliser des copies de données de production en test pose des problèmes de **RGPD** et de confidentialité.
Ce générateur ne produit que des données sans lien avec des personnes réelles :

| Donnée | Garantie |
|---|---|
| Email | Domaine `example.com`, réservé aux tests (RFC 2606) – aucun email ne peut partir vers une vraie boîte |
| Téléphone | Plage `06 39 98 xx xx`, réservée par l'ARCEP aux œuvres de fiction |
| Email unique | L'index de ligne garantit l'unicité (indispensable pour des comptes de test en charge) |
| Cohérence | Prénom cohérent avec le genre, code postal cohérent avec la ville |

## Exemple de sortie

```csv
firstname,lastname,email,password,gender,birthdate,phone,street,city,zipcode
Léa,Martin,lea.martin1@example.com,Pass-0c45c028,F,1972-08-18,0639980001,3 Rue de la République,Marseille,13013
Hugo,Martin,hugo.martin2@example.com,Pass-c3185753,H,2001-03-10,0639980002,142 Rue Lafayette,Nantes,44019
```

## Utilisation

Prérequis : Java 21.

```bash
javac GenerateUsers.java
java GenerateUsers                  # utilise config.properties
java GenerateUsers mon-config.properties
```

Avec Docker (le CSV est écrit dans `./output` sur la machine hôte) :

```bash
docker build -t generateur-data .
docker run --rm -v "$PWD/output:/app/output" generateur-data
```

## Configuration

Tout se paramètre dans `config.properties`, sans recompiler :

| Propriété | Défaut | Rôle |
|---|---|---|
| `user.count` | `1000` | Nombre d'utilisateurs |
| `output.file` | `output/users.csv` | Fichier généré |
| `random.seed` | *(vide)* | Renseigné → **même jeu de données à chaque exécution** (tests reproductibles) |
| `email.domain` | `example.com` | Domaine des emails |
| `phone.enabled` / `phone.prefix` | `true` / `063998` | Colonne téléphone |
| `address.enabled` | `true` | Colonnes rue, ville, code postal |
| `birthdate.enabled` / `minYear` / `maxYear` | `true` / `1970` / `2005` | Date de naissance (adultes uniquement) |
| `gender.enabled` | `true` | Colonne genre |
| `password.random` | `true` | Mot de passe aléatoire, sinon `Pass123` |

Les colonnes désactivées disparaissent aussi de l'en-tête du CSV.

## Exemple : feeder Gatling

```java
FeederBuilder<String> users = csv("users.csv").circular();

ScenarioBuilder inscription = scenario("Inscription")
    .feed(users)
    .exec(http("Création de compte")
        .post("/api/register")
        .body(StringBody("""
            {"email": "#{email}", "password": "#{password}", "firstname": "#{firstname}"}
            """)).asJson());
```

Dans JMeter : `CSV Data Set Config` avec `Variable Names` vide (lecture de l'en-tête), puis `${email}`, `${password}`...

## Licence

MIT – voir [LICENSE](LICENSE).
