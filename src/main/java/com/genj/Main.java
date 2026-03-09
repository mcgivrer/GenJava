package com.genj;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Main class for genj - Java Project Generator.
 */
public class Main {

    private static final String VERSION = "1.0.0";

    public static void main(String[] args) {
        CliConfig cli = CliParser.parse(args);

        // Handle --list option
        if (cli.list()) {
            TemplateLister.listAvailableTemplates();
            return;
        }

        // Handle --search option
        if (cli.search() != null) {
            TemplateLister.searchTemplates(cli.search());
            return;
        }

        // Validate required options for generation
        if (cli.template() == null) {
            System.err.println("Error: --template is required (unless using --list or --search)");
            System.err.println("Use 'genj --list' to see available templates");
            System.err.println("Use 'genj --search <term>' to search for templates");
            System.exit(1);
        }

        try {
            run(cli);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            if (cli.verbose()) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    /**
     * Main execution logic for project generation.
     */
    private static void run(CliConfig cli) throws IOException {
        if (cli.verbose()) {
            System.out.println("=== genj - Java Project Generator ===");
            System.out.println("Version: " + VERSION);
            System.out.println("Verbose mode enabled");
            System.out.println();
        }

        // Resolve template path
        Path templatePath = resolveTemplatePath(cli.template());
        Path destPath = resolveDestinationPath(cli.destination()).resolve(cli.projectName());

        Log.verbose("Template: " + templatePath, cli.verbose());
        Log.verbose("Destination path will be: " + destPath, cli.verbose());

        // Build replacements map
        Map<String, String> replacements = buildReplacements(cli);

        // Process template
        Log.info("Reading template from: " + templatePath);
        TemplateProcessor.processTemplate(templatePath, destPath, replacements, cli.verbose());

        // Generate build files
        generateBuildFiles(destPath, cli);

        // Generate .sdkmanrc
        generateSdkmanrc(destPath, cli);

        // Generate .genrc
        Log.verbose("Generating .genrc", cli.verbose());
        GenrcWriter.write(destPath, cli);
        Log.success(".genrc configuration file generated");

        // Setup VSCode and Git
        Log.info("Configuring VSCode and Git repository...");
        try {
            VscodeGitSetup.setup(destPath, cli);
        } catch (IOException e) {
            Log.warning("Error during VSCode/Git configuration: " + e.getMessage());
        }

        Log.success("Java project '" + cli.projectName() + "' generated successfully in " + destPath);

        if (cli.verbose()) {
            System.out.println();
            System.out.println("=== Generation Summary ===");
            System.out.println("Project Name: " + cli.projectName());
            System.out.println("Package: " + cli.packageName());
            System.out.println("Build Tool: " + cli.buildTool());
            System.out.println("Java Version: " + cli.java());
            System.out.println("Location: " + destPath);
        }
    }

    /**
     * Resolve template path from CLI option.
     */
    private static Path resolveTemplatePath(String template) throws IOException {
        Path path = Path.of(template);
        if (Files.exists(path)) {
            return path;
        }

        // Try system templates
        Path systemPath = Path.of("/usr/share/genj/templates", template);
        if (Files.exists(systemPath)) {
            return systemPath;
        }

        // Try system templates with .zip extension
        Path systemZipPath = Path.of("/usr/share/genj/templates", template + ".zip");
        if (Files.exists(systemZipPath)) {
            return systemZipPath;
        }

        // Try user templates
        Path userPath = Path.of(System.getProperty("user.home"), ".genj", template);
        if (Files.exists(userPath)) {
            return userPath;
        }

        // Try user templates with .zip extension
        Path userZipPath = Path.of(System.getProperty("user.home"), ".genj", template + ".zip");
        if (Files.exists(userZipPath)) {
            return userZipPath;
        }

        throw new IOException("Template not found: " + template);
    }

    /**
     * Resolve destination path from CLI option.
     */
    private static Path resolveDestinationPath(String destination) {
        return destination != null ? Path.of(destination) : Path.of(".");
    }

    /**
     * Build the replacements map for template processing.
     */
    private static Map<String, String> buildReplacements(CliConfig cli) {
        Map<String, String> replacements = new LinkedHashMap<>();
        replacements.put("${PROJECT_NAME}", cli.projectName());
        replacements.put("${AUTHOR_NAME}", cli.author());
        replacements.put("${AUTHOR_EMAIL}", cli.email());
        replacements.put("${PROJECT_VERSION}", cli.projectVersion());
        replacements.put("${PACKAGE}", cli.packageName());
        replacements.put("${JAVA}", cli.java());
        replacements.put("${VENDOR_NAME}", cli.vendorName());
        replacements.put("${MAINCLASS}", cli.mainclass());
        replacements.put("${PROJECT_YEAR}", String.valueOf(Year.now().getValue()));
        return replacements;
    }

    /**
     * Generate build tool files (pom.xml or build.gradle).
     */
    private static void generateBuildFiles(Path destPath, CliConfig cli) throws IOException {
        String buildTool = cli.buildTool().toLowerCase();

        if (!buildTool.equals("maven") && !buildTool.equals("gradle")) {
            Log.warning("Unsupported build tool: " + buildTool + " (possible values: maven, gradle)");
            System.exit(1);
        }

        Log.info("Using build tool: " + buildTool);

        if (buildTool.equals("maven")) {
            Log.verbose("Generating pom.xml", cli.verbose());
            String pomContent = """
                    <project xmlns="http://maven.apache.org/POM/4.0.0">
                      <modelVersion>4.0.0</modelVersion>
                      <groupId>%s</groupId>
                      <artifactId>%s</artifactId>
                      <version>%s</version>
                      <properties>
                        <maven.compiler.target>%s</maven.compiler.target>
                        <maven.compiler.source>%s</maven.compiler.source>
                      </properties>
                      <build>
                        <plugins>
                          <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-jar-plugin</artifactId>
                            <version>3.4.1</version>
                            <configuration>
                              <archive>
                                <manifest>
                                  <mainClass>%s.%s</mainClass>
                                </manifest>
                              </archive>
                            </configuration>
                          </plugin>
                        </plugins>
                      </build>
                    </project>
                    """.formatted(
                    cli.packageName(),
                    cli.projectName(),
                    cli.projectVersion(),
                    cli.java(),
                    cli.java(),
                    cli.packageName(),
                    cli.mainclass()
            );
            FileUtils.writeText(destPath.resolve("pom.xml"), pomContent);
            Log.success("pom.xml generated");
        } else {
            Log.verbose("Generating build.gradle", cli.verbose());
            String gradleContent = """
                    plugins {
                      id 'java'
                    }
                    group '%s'
                    version '%s'
                    repositories {
                      mavenCentral()
                    }
                    """.formatted(cli.packageName(), cli.projectVersion());
            FileUtils.writeText(destPath.resolve("build.gradle"), gradleContent);
            Log.success("build.gradle generated");
        }
    }

    /**
     * Generate .sdkmanrc file.
     */
    private static void generateSdkmanrc(Path destPath, CliConfig cli) throws IOException {
        Log.verbose("Generating .sdkmanrc", cli.verbose());
        
        StringBuilder content = new StringBuilder();
        content.append("java=").append(cli.javaFlavor()).append("\n");
        
        if (cli.buildTool().equalsIgnoreCase("maven")) {
            content.append("maven=").append(cli.mavenVersion()).append("\n");
        } else {
            content.append("gradle=").append(cli.gradleVersion()).append("\n");
        }
        
        FileUtils.writeText(destPath.resolve(".sdkmanrc"), content.toString());
        Log.success(".sdkmanrc generated");
    }
}
