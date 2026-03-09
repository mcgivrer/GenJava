package com.genj;

/**
 * Logging utilities for genj.
 * Provides colored console output.
 */
public final class Log {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private Log() {
        // Utility class
    }

    /**
     * Log a verbose message (only if verbose mode is enabled).
     */
    public static void verbose(String msg, boolean verbose) {
        if (verbose) {
            System.out.println("[VERBOSE] " + msg);
        }
    }

    /**
     * Log an info message.
     */
    public static void info(String msg) {
        System.out.println("[INFO] " + msg);
    }

    /**
     * Log a success message with a checkmark.
     */
    public static void success(String msg) {
        System.out.println(ANSI_GREEN + "[✓]" + ANSI_RESET + " " + msg);
    }

    /**
     * Log a warning message.
     */
    public static void warning(String msg) {
        System.err.println(ANSI_YELLOW + "[⚠]" + ANSI_RESET + " " + msg);
    }

    /**
     * Print bold text.
     */
    public static String bold(String text) {
        return ANSI_BOLD + text + ANSI_RESET;
    }
}
