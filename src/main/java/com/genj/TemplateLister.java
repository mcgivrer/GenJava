package com.genj;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import java.util.zip.*;

/**
 * Template listing and searching functionality.
 */
public final class TemplateLister {

    private static final Path SYSTEM_TEMPLATES = Path.of("/usr/share/genj/templates");
    private static final Path USER_TEMPLATES = Path.of(System.getProperty("user.home"), ".genj");

    private TemplateLister() {
        // Utility class
    }

    /**
     * List all available templates from system and user directories.
     */
    public static void listAvailableTemplates() {
        System.out.println("=== Available Templates ===\n");

        System.out.println("📦 System templates (/usr/share/genj/templates):");
        listTemplatesInDir(SYSTEM_TEMPLATES);

        System.out.println("\n👤 User templates (~/.genj):");
        listTemplatesInDir(USER_TEMPLATES);

        System.out.println("\n💡 Usage: genj -t <template_name_or_path> -d <destination> [options]");
        System.out.println("   Or: genj -t /usr/share/genj/templates/basic-java.zip -d ./out -n MyProject");
    }

    /**
     * Search for templates matching a search term.
     */
    public static void searchTemplates(String searchTerm) {
        String searchLower = searchTerm.toLowerCase();
        boolean resultsFound = false;

        System.out.println("=== Search Results for: '" + searchTerm + "' ===\n");

        System.out.println("📦 System templates (/usr/share/genj/templates):");
        if (searchTemplatesInDir(SYSTEM_TEMPLATES, searchLower)) {
            resultsFound = true;
        }

        System.out.println("\n👤 User templates (~/.genj):");
        if (searchTemplatesInDir(USER_TEMPLATES, searchLower)) {
            resultsFound = true;
        }

        if (!resultsFound) {
            System.out.println("  No templates found matching '" + searchTerm + "'");
        }

        System.out.println("\n💡 Usage: genj -t <template_name_or_path> -d <destination> [options]");
        System.out.println("   Or: genj -t /usr/share/genj/templates/basic-java.zip -d ./out -n MyProject");
    }

    /**
     * List templates in a directory with metadata.
     */
    private static void listTemplatesInDir(Path dir) {
        if (!Files.exists(dir)) {
            System.out.println("  (No templates found - directory does not exist)");
            return;
        }

        try (Stream<Path> stream = Files.list(dir)) {
            List<Path> templates = stream
                    .filter(p -> Files.isDirectory(p) || 
                                 (Files.isRegularFile(p) && p.toString().endsWith(".zip")))
                    .sorted()
                    .toList();

            if (templates.isEmpty()) {
                System.out.println("  (No templates found)");
                return;
            }

            for (Path template : templates) {
                displayTemplateInfo(template);
            }
            System.out.println();
        } catch (IOException e) {
            System.out.println("  Error reading directory: " + e.getMessage());
        }
    }

    /**
     * Search templates in a directory.
     */
    private static boolean searchTemplatesInDir(Path dir, String searchTerm) {
        if (!Files.exists(dir)) {
            return false;
        }

        boolean found = false;

        try (Stream<Path> stream = Files.list(dir)) {
            List<Path> templates = stream
                    .filter(p -> Files.isDirectory(p) || 
                                 (Files.isRegularFile(p) && p.toString().endsWith(".zip")))
                    .sorted()
                    .toList();

            for (Path template : templates) {
                Map<String, Object> metadata = extractMetadata(template);
                String name = template.getFileName().toString();

                // Check if name matches
                boolean nameMatches = name.toLowerCase().contains(searchTerm);

                // Check if any metadata field matches
                boolean metadataMatches = matchesMetadata(metadata, searchTerm);

                if (nameMatches || metadataMatches) {
                    displayTemplateInfo(template, metadata);
                    found = true;
                }
            }
        } catch (IOException e) {
            // Ignore
        }

        return found;
    }

    /**
     * Check if metadata matches search term.
     */
    private static boolean matchesMetadata(Map<String, Object> metadata, String searchTerm) {
        if (metadata.isEmpty()) {
            return false;
        }

        // Check string fields
        for (String key : List.of("description", "language", "author", "version", "contact", "license")) {
            Object value = metadata.get(key);
            if (value instanceof String str && str.toLowerCase().contains(searchTerm)) {
                return true;
            }
        }

        // Check tags
        Object tags = metadata.get("tags");
        if (tags instanceof List<?> tagList) {
            for (Object tag : tagList) {
                if (tag instanceof String str && str.toLowerCase().contains(searchTerm)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Display template information.
     */
    private static void displayTemplateInfo(Path template) {
        displayTemplateInfo(template, extractMetadata(template));
    }

    /**
     * Display template information with pre-extracted metadata.
     */
    private static void displayTemplateInfo(Path template, Map<String, Object> metadata) {
        String name = template.getFileName().toString();
        if (Files.isDirectory(template)) {
            name += "/";
        }

        System.out.println("\n  📋 Template: " + name);

        if (metadata.isEmpty()) {
            System.out.println("     (No metadata available)");
            return;
        }

        printField(metadata, "description", "Description");
        printField(metadata, "language", "Language");
        printField(metadata, "version", "Version");
        printField(metadata, "author", "Author");
        printField(metadata, "contact", "Contact");
        printField(metadata, "license", "License");

        Object tags = metadata.get("tags");
        if (tags instanceof List<?> tagList && !tagList.isEmpty()) {
            String tagStr = tagList.stream()
                    .filter(t -> t instanceof String)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            if (!tagStr.isEmpty()) {
                System.out.println("     " + Log.bold("Tags:") + " " + tagStr);
            }
        }

        printField(metadata, "created_at", "Created");
    }

    /**
     * Print a metadata field if present.
     */
    private static void printField(Map<String, Object> metadata, String key, String label) {
        Object value = metadata.get(key);
        if (value instanceof String str && !str.isEmpty()) {
            System.out.println("     " + Log.bold(label + ":") + " " + str);
        }
    }

    /**
     * Extract metadata from a template (.template file).
     */
    private static Map<String, Object> extractMetadata(Path template) {
        try {
            if (Files.isDirectory(template)) {
                Path templateFile = template.resolve(".template");
                if (Files.exists(templateFile)) {
                    return parseJsonMetadata(Files.readString(templateFile));
                }
            } else if (template.toString().endsWith(".zip")) {
                try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(template))) {
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        if (entry.getName().equals(".template") || entry.getName().endsWith("/.template")) {
                            String content = new String(zis.readAllBytes());
                            return parseJsonMetadata(content);
                        }
                    }
                }
            }
        } catch (IOException e) {
            // Ignore
        }
        return Collections.emptyMap();
    }

    /**
     * Simple JSON parser for template metadata.
     * Since we're using only JDK, implement a basic parser.
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseJsonMetadata(String json) {
        Map<String, Object> result = new HashMap<>();
        
        // Very basic JSON parsing - handles simple key-value pairs and arrays
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            return result;
        }
        
        json = json.substring(1, json.length() - 1).trim();
        
        int depth = 0;
        int start = 0;
        List<String> pairs = new ArrayList<>();
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{' || c == '[') depth++;
            else if (c == '}' || c == ']') depth--;
            else if (c == ',' && depth == 0) {
                pairs.add(json.substring(start, i).trim());
                start = i + 1;
            }
        }
        if (start < json.length()) {
            pairs.add(json.substring(start).trim());
        }
        
        for (String pair : pairs) {
            int colonIdx = pair.indexOf(':');
            if (colonIdx == -1) continue;
            
            String key = pair.substring(0, colonIdx).trim();
            String value = pair.substring(colonIdx + 1).trim();
            
            // Remove quotes from key
            if (key.startsWith("\"") && key.endsWith("\"")) {
                key = key.substring(1, key.length() - 1);
            }
            
            // Parse value
            if (value.startsWith("[")) {
                // Array
                List<String> list = new ArrayList<>();
                value = value.substring(1, value.length() - 1).trim();
                if (!value.isEmpty()) {
                    for (String item : value.split(",")) {
                        item = item.trim();
                        if (item.startsWith("\"") && item.endsWith("\"")) {
                            item = item.substring(1, item.length() - 1);
                        }
                        list.add(item);
                    }
                }
                result.put(key, list);
            } else if (value.startsWith("\"") && value.endsWith("\"")) {
                result.put(key, value.substring(1, value.length() - 1));
            } else {
                result.put(key, value);
            }
        }
        
        return result;
    }
}
