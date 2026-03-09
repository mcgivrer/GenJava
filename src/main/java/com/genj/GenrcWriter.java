package com.genj;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Generates the .genrc configuration file.
 */
public final class GenrcWriter {

    private static final String VERSION = "1.0.0";

    private GenrcWriter() {
        // Utility class
    }

    /**
     * Write the .genrc file to the destination directory.
     */
    public static void write(Path destPath, CliConfig cli) throws IOException {
        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        
        // Build JSON manually to avoid external dependencies
        String genrc = """
                {
                  "project_name": "%s",
                  "author": "%s",
                  "email": "%s",
                  "project_version": "%s",
                  "package": "%s",
                  "mainclass": "%s",
                  "java_version": "%s",
                  "java_flavor": "%s",
                  "build_tool": "%s",
                  "maven_version": "%s",
                  "gradle_version": "%s",
                  "vendor_name": "%s",
                  "template": %s,
                  "remote_git_repository": %s,
                  "created_at": "%s",
                  "generated_with": {
                    "cmd": "genj",
                    "version": "%s"
                  }
                }
                """.formatted(
                escapeJson(cli.projectName()),
                escapeJson(cli.author()),
                escapeJson(cli.email()),
                escapeJson(cli.projectVersion()),
                escapeJson(cli.packageName()),
                escapeJson(cli.mainclass()),
                escapeJson(cli.java()),
                escapeJson(cli.javaFlavor()),
                escapeJson(cli.buildTool()),
                escapeJson(cli.mavenVersion()),
                escapeJson(cli.gradleVersion()),
                escapeJson(cli.vendorName()),
                cli.template() != null ? "\"" + escapeJson(cli.template()) + "\"" : "null",
                cli.remoteGit() != null ? "\"" + escapeJson(cli.remoteGit()) + "\"" : "null",
                timestamp,
                VERSION
        );

        Path genrcPath = destPath.resolve(".genrc");
        FileUtils.writeText(genrcPath, genrc);
    }

    /**
     * Escape special characters for JSON string.
     */
    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
