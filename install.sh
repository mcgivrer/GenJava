#!/usr/bin/env bash
#
# GenJava Installation Script
# Installs genj to ~/.local/bin and ~/.local/lib/genj
#

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_NAME="genj-1.0.0.jar"
JAR_SOURCE="${SCRIPT_DIR}/target/${JAR_NAME}"
TEMPLATES_SOURCE="${SCRIPT_DIR}/templates"
TEMPLATES_TARGET="${SCRIPT_DIR}/target/templates"

# Installation directories
INSTALL_BIN="${HOME}/.local/bin"
INSTALL_LIB="${HOME}/.local/lib/genj"
INSTALL_TEMPLATES="${HOME}/.local/share/genj/templates"

echo "=== GenJava Installer ==="
echo ""

# Check if JAR exists
if [[ ! -f "${JAR_SOURCE}" ]]; then
    echo "Error: ${JAR_SOURCE} not found." >&2
    echo "Please run 'mvn package' first." >&2
    exit 1
fi

# Compress templates (one zip per directory)
if [[ -d "${TEMPLATES_SOURCE}" ]]; then
    echo "Compressing templates..."
    mkdir -p "${TEMPLATES_TARGET}"
    
    # Remove old zips
    rm -f "${TEMPLATES_TARGET}"/*.zip
    
    # Create one zip per template directory
    for template_dir in "${TEMPLATES_SOURCE}"/*/; do
        if [[ -d "${template_dir}" ]]; then
            template_name="$(basename "${template_dir}")"
            zip_file="${TEMPLATES_TARGET}/${template_name}.zip"
            echo "  - ${template_name}.zip"
            (cd "${TEMPLATES_SOURCE}" && zip -rq "${zip_file}" "${template_name}")
        fi
    done
    echo ""
fi

# Create directories
echo "Creating directories..."
mkdir -p "${INSTALL_BIN}"
mkdir -p "${INSTALL_LIB}"
mkdir -p "${INSTALL_TEMPLATES}"

# Copy JAR
echo "Copying JAR to ${INSTALL_LIB}..."
cp "${JAR_SOURCE}" "${INSTALL_LIB}/"

# Copy template zips
if [[ -d "${TEMPLATES_TARGET}" ]]; then
    echo "Copying template archives to ${INSTALL_TEMPLATES}..."
    cp "${TEMPLATES_TARGET}"/*.zip "${INSTALL_TEMPLATES}/"
fi

# Create wrapper script
echo "Installing genj command to ${INSTALL_BIN}..."
cat > "${INSTALL_BIN}/genj" << 'WRAPPER_EOF'
#!/usr/bin/env bash
set -euo pipefail

JAR_PATH="${HOME}/.local/lib/genj/genj-1.0.0.jar"
TEMPLATES_DIR="${HOME}/.local/share/genj/templates"

if [[ ! -f "${JAR_PATH}" ]]; then
    echo "Error: GenJava JAR not found at ${JAR_PATH}" >&2
    exit 1
fi

export GENJ_TEMPLATES_DIR="${TEMPLATES_DIR}"
JAVA_OPTS="${JAVA_OPTS:-} --enable-preview"

exec java ${JAVA_OPTS} -jar "${JAR_PATH}" "$@"
WRAPPER_EOF

chmod +x "${INSTALL_BIN}/genj"

echo ""
echo "Installation complete!"
echo ""

# Check if ~/.local/bin is in PATH
if [[ ":${PATH}:" != *":${INSTALL_BIN}:"* ]]; then
    echo "Note: ${INSTALL_BIN} is not in your PATH."
    echo "Add the following to your ~/.bashrc or ~/.zshrc:"
    echo ""
    echo "    export PATH=\"\${HOME}/.local/bin:\${PATH}\""
    echo ""
fi

echo "Usage: genj --help"
