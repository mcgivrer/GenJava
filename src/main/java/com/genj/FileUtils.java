package com.genj;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;

/**
 * File system utilities for genj.
 * Handles file operations like reading, writing, and detecting binary files.
 */
public final class FileUtils {

    private static final int BUFFER_SIZE = 8192;

    private FileUtils() {
        // Utility class
    }

    /**
     * Check if the given byte array represents text content.
     * Returns false if it contains null bytes or is not valid UTF-8.
     */
    public static boolean isTextBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return true;
        }
        // Check for null bytes (common in binary files)
        for (byte b : bytes) {
            if (b == 0) {
                return false;
            }
        }
        // Try to decode as UTF-8
        try {
            new String(bytes, StandardCharsets.UTF_8);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if a file is a text file by reading the first few KB.
     */
    public static boolean isTextFile(Path path) throws IOException {
        if (!Files.exists(path) || Files.isDirectory(path)) {
            return false;
        }
        
        byte[] buffer = new byte[BUFFER_SIZE];
        try (InputStream is = Files.newInputStream(path)) {
            int n = is.read(buffer);
            if (n <= 0) {
                return true; // Empty file is considered text
            }
            return isTextBytes(Arrays.copyOf(buffer, n));
        }
    }

    /**
     * Create parent directories if they don't exist.
     */
    public static void createParentDirs(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    /**
     * Write bytes to a file, creating parent directories if needed.
     */
    public static void writeBytes(Path path, byte[] data) throws IOException {
        createParentDirs(path);
        Files.write(path, data);
    }

    /**
     * Write text to a file, creating parent directories if needed.
     */
    public static void writeText(Path path, String content) throws IOException {
        createParentDirs(path);
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    /**
     * Read file content as string.
     */
    public static String readText(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    /**
     * Copy a file, creating parent directories if needed.
     */
    public static void copyFile(Path source, Path destination) throws IOException {
        createParentDirs(destination);
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }
}
