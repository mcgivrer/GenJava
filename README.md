# genj - Java Project Generator (Version Java)

Réimplémentation en Java du générateur de projets Java `genj` initialement écrit en Rust.

## Prérequis

- JDK 25 avec les fonctionnalités preview activées
- Maven 3.9+

## Compilation

```bash
cd java-version
mvn clean package
```

## Utilisation

```bash
# Lister les templates disponibles
java --enable-preview -jar target/genj-1.0.0.jar --list

# Rechercher un template
java --enable-preview -jar target/genj-1.0.0.jar --search game

# Générer un projet
java --enable-preview -jar target/genj-1.0.0.jar \
    -t /chemin/vers/template \
    -n MonProjet \
    -k com.example \
    -a "Mon Nom" \
    -e "email@example.com"
```

## Options

| Option | Description | Défaut |
|--------|-------------|--------|
| `-t, --template` | Chemin vers le template (ZIP ou dossier) | - |
| `-d, --destination` | Répertoire de destination | `.` |
| `-n, --project_name` | Nom du projet | `Demo` |
| `-a, --author` | Nom de l'auteur | `Unknown Author` |
| `-e, --email` | Email de l'auteur | `email@unknown.local` |
| `-v, --project_version` | Version du projet | `0.0.1` |
| `-j, --java_version` | Version JDK | `25` |
| `-f, --java_flavor` | Flavor JDK pour sdkman | `25-zulu` |
| `-k, --package` | Package Java | `com.demo` |
| `-m, --mainclass` | Classe principale | `App` |
| `-b, --build` | Outil de build (maven/gradle) | `maven` |
| `--maven_version` | Version Maven pour sdkman | `3.9.5` |
| `--gradle_version` | Version Gradle pour sdkman | `8.5` |
| `-l, --vendor_name` | Nom du vendeur | `Vendor` |
| `-r, --remote_git` | URL du dépôt Git distant | - |
| `--verbose` | Mode verbeux | `false` |
| `--list` | Lister les templates | - |
| `-s, --search` | Rechercher des templates | - |

## Architecture

Le projet utilise uniquement les fonctionnalités du JDK 25 :
- **Records** pour les structures de données immuables (`CliConfig`)
- **Text Blocks** pour les templates de chaînes multi-lignes
- **Pattern Matching** dans les switch expressions
- **Stream API** pour le traitement des collections

### Structure du code

```
src/main/java/com/genj/
├── Main.java           # Point d'entrée principal
├── CliConfig.java      # Record de configuration CLI
├── CliParser.java      # Parseur d'arguments en ligne de commande
├── FileUtils.java      # Utilitaires de manipulation de fichiers
├── GenrcWriter.java    # Générateur du fichier .genrc
├── Log.java            # Utilitaires de logging
├── TemplateLister.java # Listage et recherche de templates
├── TemplateProcessor.java # Traitement des templates
└── VscodeGitSetup.java # Configuration VSCode et Git
```

## Dépendances

- **JGit** : Pour les opérations Git (initialisation, commit, remote)

## Différences avec la version Rust

Cette version Java reproduit fidèlement les fonctionnalités de la version Rust :
- Génération de projets à partir de templates (dossiers ou ZIP)
- Remplacement des placeholders (`${PROJECT_NAME}`, `${PACKAGE}`, etc.)
- Génération de `pom.xml` ou `build.gradle`
- Génération de `.sdkmanrc`
- Génération de `.genrc`
- Configuration VSCode
- Initialisation du dépôt Git avec commit initial
