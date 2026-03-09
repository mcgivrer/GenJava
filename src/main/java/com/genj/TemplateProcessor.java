package com.genj;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import java.util.zip.*;

/**
 * Template processor for genj.
 * Handles extraction and processing of template directories and ZIP files.
 */
public final class TemplateProcessor {

    private TemplateProcessor() {
        // Utility class
    }

    /**
     * Process a template (directory or ZIP file) and copy to destination.
     *
     * @param templatePath Path to the template
     * @param destPath     Destination path
     * @param replacements Map of placeholders to their values
     * @param verbose      Enable verbose logging
     */
    public static void processTemplate(Path templatePath, Path destPath,
                                        Map<String, String> replacements, boolean verbose) throws IOException {
        if (Files.isRegularFile(templatePath)) {
            Log.verbose("Template detected as ZIP file", verbose);
            extractZipWithReplace(templatePath, destPath, replacements, verbose);
        } else if (Files.isDirectory(templatePath)) {
            Log.verbose("Template detected as directory", verbose);
            copyDirWithReplace(templatePath, destPath, replacements, verbose);
        } else {
            throw new IOException("Template path not found: " + templatePath);
        }
    }

    /**
     * Replace package placeholder in path, converting dots to directory separators.
     */
    private static Path replacePackageInPath(String pathStr, Map<String, String> replacements, String packageVal) {
        String[] parts = pathStr.split("/");
        List<String> resultParts = new ArrayList<>();

        for (String part : parts) {
            if (part.isEmpty()) continue;
            
            if (part.equals("${PACKAGE}")) {
                // Convert package (e.g., com.example) to path (e.g., com/example)
                Collections.addAll(resultParts, packageVal.split("\\."));
            } else {
                // Apply other replacements
                String replaced = part;
                for (var entry : replacements.entrySet()) {
                    replaced = replaced.replace(entry.getKey(), entry.getValue());
                }
                resultParts.add(replaced);
            }
        }

        if (resultParts.isEmpty()) {
            return Path.of("");
        }

        return Path.of(resultParts.getFirst(), resultParts.stream().skip(1).toArray(String[]::new));
    }

    /**
     * Extract a ZIP file, applying replacements to text files.
     */
    private static void extractZipWithReplace(Path zipPath, Path destPath,
                                               Map<String, String> replacements, boolean verbose) throws IOException {
        Log.verbose("Opening ZIP file: " + zipPath, verbose);

        String packageVal = replacements.getOrDefault("${PACKAGE}", "");

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipPath))) {
            // First pass: collect all entry names to detect common prefix
            List<String> entryNames = new ArrayList<>();
            try (ZipInputStream tempZis = new ZipInputStream(Files.newInputStream(zipPath))) {
                ZipEntry entry;
                while ((entry = tempZis.getNextEntry()) != null) {
                    entryNames.add(entry.getName());
                }
            }

            // Detect common root prefix
            String commonPrefix = detectCommonPrefix(entryNames);
            if (commonPrefix != null && !commonPrefix.isEmpty()) {
                Log.verbose("Detected common root prefix: " + commonPrefix, verbose);
            }

            // Second pass: extract files
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String rawName = entry.getName();

                // Strip common prefix
                String relativePath = rawName;
                if (commonPrefix != null && !commonPrefix.isEmpty() && rawName.startsWith(commonPrefix)) {
                    relativePath = rawName.substring(commonPrefix.length());
                }

                if (relativePath.isEmpty()) {
                    continue;
                }

                Path outPath = replacePackageInPath(relativePath, replacements, packageVal);
                Path fullPath = destPath.resolve(outPath);

                if (entry.isDirectory()) {
                    Files.createDirectories(fullPath);
                    Log.verbose("Created directory: " + fullPath, verbose);
                    continue;
                }

                // Read entry content
                byte[] bytes = zis.readAllBytes();

                // Create parent directories
                FileUtils.createParentDirs(fullPath);

                // Check if text file
                if (!FileUtils.isTextBytes(bytes)) {
                    FileUtils.writeBytes(fullPath, bytes);
                    Log.verbose("Copied binary file: " + fullPath, verbose);
                    continue;
                }

                // Apply replacements to text content
                String content = new String(bytes, StandardCharsets.UTF_8);
                String replaced = applyReplacements(content, replacements);
                FileUtils.writeText(fullPath, replaced);
                Log.verbose("Extracted and replaced: " + rawName, verbose);
            }
        }
    }

    /**
     * Copy a directory, applying replacements to text files.
     */
    private static void copyDirWithReplace(Path srcDir, Path destDir,
                                            Map<String, String> replacements, boolean verbose) throws IOException {
        Log.verbose("Scanning source directory: " + srcDir, verbose);

        String packageVal = replacements.getOrDefault("${PACKAGE}", "");

        try (Stream<Path> walk = Files.walk(srcDir)) {
            for (Path path : walk.toList()) {
                Path relativePath = srcDir.relativize(path);
                String relStr = relativePath.toString().replace("\\", "/");
                Path newPath = replacePackageInPath(relStr, replacements, packageVal);
                Path fullDestPath = destDir.resolve(newPath);

                if (Files.isDirectory(path)) {
                    Files.createDirectories(fullDestPath);
                    Log.verbose("Created directory: " + fullDestPath, verbose);
                    continue;
                }

                if (Files.isRegularFile(path)) {
                    FileUtils.createParentDirs(fullDestPath);

                    if (FileUtils.isTextFile(path)) {
                        String content = FileUtils.readText(path);
                        String replaced = applyReplacements(content, replacements);
                        FileUtils.writeText(fullDestPath, replaced);
                        Log.verbose("Copied and replaced: " + fullDestPath, verbose);
                    } else {
                        FileUtils.copyFile(path, fullDestPath);
                        Log.verbose("Copied binary file: " + fullDestPath, verbose);
                    }
                }
            }
        }

        Log.verbose("Template copy complete", verbose);
    }

    /**
     * Apply all replacements to a string.
     */
    private static String applyReplacements(String content, Map<String, String> replacements) {
        String result = content;
        for (var entry : replacements.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Detect common prefix in ZIP entries (e.g., when ZIP has a root folder).
     */
    private static String detectCommonPrefix(List<String> names) {
        if (names.isEmpty()) {
            return null;
        }

        // Find entries with '/' and extract the first segment
        Set<String> prefixes = new HashSet<>();
        for (String name : names) {
            int idx = name.indexOf('/');
            if (idx > 0) {
                prefixes.add(name.substring(0, idx + 1));
            }
        }

        // If all entries start with the same prefix, return it
        if (prefixes.size() == 1) {
            String prefix = prefixes.iterator().next();
            boolean allMatch = names.stream()
                    .allMatch(n -> n.startsWith(prefix) || n.equals(prefix.substring(0, prefix.length() - 1)));
            if (allMatch) {
                return prefix;
            }
        }

        return null;
    }
}
