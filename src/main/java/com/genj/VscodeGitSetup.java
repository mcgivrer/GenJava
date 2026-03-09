package com.genj;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.URIish;

import java.net.URISyntaxException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * VSCode configuration and Git repository setup.
 */
public final class VscodeGitSetup {

    private VscodeGitSetup() {
        // Utility class
    }

    /**
     * Setup VSCode configuration and initialize Git repository.
     */
    public static void setup(Path destPath, CliConfig cli) throws IOException {
        // Create VSCode configuration
        Log.verbose("Creating VSCode configuration", cli.verbose());
        createVscodeConfig(destPath, cli);

        // Initialize Git repository
        Log.verbose("Initializing Git repository", cli.verbose());
        initGitRepository(destPath, cli);
    }

    /**
     * Create VSCode configuration files.
     */
    private static void createVscodeConfig(Path destPath, CliConfig cli) throws IOException {
        Path vscodeDir = destPath.resolve(".vscode");
        Files.createDirectories(vscodeDir);

        // settings.json
        String settings = """
                {
                  "java.format.settings.url": ".vscode/java-formatter.xml",
                  "java.project.sourcePaths": [
                    "src/main/java",
                    "src/main/resources",
                    "src/test/java",
                    "src/test/resources"
                  ],
                  "java.project.encoding": "warning",
                  "java.project.outputPath": "target/classes"
                }
                """;
        FileUtils.writeText(vscodeDir.resolve("settings.json"), settings);
        Log.success(".vscode/settings.json created");

        // launch.json
        String launch = """
                {
                  "version": "0.2.0",
                  "configurations": [
                    {
                      "type": "java",
                      "name": "Run",
                      "request": "launch",
                      "mainClass": "%s",
                      "projectName": "%s"
                    }
                  ]
                }
                """.formatted(cli.mainclass(), cli.projectName());
        FileUtils.writeText(vscodeDir.resolve("launch.json"), launch);
        Log.success(".vscode/launch.json created");
    }

    /**
     * Initialize Git repository with initial commit.
     */
    private static void initGitRepository(Path destPath, CliConfig cli) throws IOException {
        try {
            // Initialize repository
            Git git = Git.init()
                    .setDirectory(destPath.toFile())
                    .setInitialBranch("main")
                    .call();

            // Configure user
            StoredConfig config = git.getRepository().getConfig();
            config.setString("user", null, "name", cli.author());
            config.setString("user", null, "email", cli.email());
            config.save();

            // Add all files
            git.add()
                    .addFilepattern(".")
                    .call();

            // Initial commit
            git.commit()
                    .setMessage("Create Project " + cli.projectName())
                    .call();

            Log.success("Git repository initialized with initial commit");

            // Setup remote if provided
            if (cli.remoteGit() != null && !cli.remoteGit().isEmpty()) {
                Log.verbose("Configuring remote repository: " + cli.remoteGit(), cli.verbose());
                
                git.remoteAdd()
                        .setName("origin")
                        .setUri(new URIish(cli.remoteGit()))
                        .call();

                try {
                    Iterable<PushResult> results = git.push()
                            .setRemote("origin")
                            .add("main")
                            .call();
                    Log.success("Pushed to remote repository");
                } catch (GitAPIException e) {
                    Log.warning("Failed to push to remote: " + e.getMessage());
                }
            }

            git.close();
        } catch (GitAPIException | URISyntaxException e) {
            throw new IOException("Failed to initialize Git repository: " + e.getMessage(), e);
        }
    }
}
