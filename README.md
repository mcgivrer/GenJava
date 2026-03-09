# genj - Java Project Generator

A Java project generator that scaffolds new projects from customizable templates.

This is a full rewrite in Java of the original [`genj`](https://github.com/mcgivrer/genj) tool, initially developed in Rust, bringing the same powerful features with the familiarity of the Java ecosystem.

## Prerequisites

- JDK 25 with preview features enabled
- Maven 3.9+

## Installation

Build and install genj using the provided install script:

```bash
# First, compile the project
mvn clean package

# Then install genj to ~/.local/bin
./install.sh
```

The installer will:
- Copy the JAR to `~/.local/lib/genj/`
- Compress and copy templates to `~/.local/share/genj/templates/`
- Create a `genj` wrapper script in `~/.local/bin/`

> **Note:** Make sure `~/.local/bin` is in your PATH. Add this to your `~/.bashrc` or `~/.zshrc`:
> ```bash
> export PATH="${HOME}/.local/bin:${PATH}"
> ```

## Usage

Once installed, use the `genj` command:

```bash
# List available templates
genj --list

# Search for a template
genj --search game

# Generate a new project
genj \
    -t java-basic-main \
    -n MyProject \
    -k com.example \
    -a "Your Name" \
    -e "your.email@example.com"
```

### Options

| Option | Description | Default |
|--------|-------------|---------|
| `-t, --template` | Template path (ZIP or folder) | - |
| `-d, --destination` | Destination directory | `.` |
| `-n, --project_name` | Project name | `Demo` |
| `-a, --author` | Author name | `Unknown Author` |
| `-e, --email` | Author email | `email@unknown.local` |
| `-v, --project_version` | Project version | `0.0.1` |
| `-j, --java_version` | JDK version | `25` |
| `-f, --java_flavor` | JDK flavor for sdkman | `25-zulu` |
| `-k, --package` | Java package | `com.demo` |
| `-m, --mainclass` | Main class name | `App` |
| `-b, --build` | Build tool (maven/gradle) | `maven` |
| `--maven_version` | Maven version for sdkman | `3.9.5` |
| `--gradle_version` | Gradle version for sdkman | `8.5` |
| `-l, --vendor_name` | Vendor name | `Vendor` |
| `-r, --remote_git` | Remote Git repository URL | - |
| `--verbose` | Verbose mode | `false` |
| `--list` | List available templates | - |
| `-s, --search` | Search templates | - |

## Documentation

- [User Guide](src/main/docs/user-guide-en.md) - Complete guide for using genj
- [Architecture Design](src/main/docs/architecture-design.md) - Technical architecture and design decisions

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository: https://github.com/mcgivrer/genjava.git
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes and commit: `git commit -m "Add my feature"`
4. Push to your fork: `git push origin feature/my-feature`
5. Open a Pull Request

### Building from source

```bash
git clone https://github.com/mcgivrer/genjava.git
cd genjava
mvn clean package
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
