package com.genj;

import java.util.ArrayList;
import java.util.List;

/**
 * Command line argument parser for genj.
 * Pure JDK implementation without external dependencies.
 */
public final class CliParser {

    private static final String VERSION = "1.0.0";

    private static final String HELP_TEXT = """
            genj - Java Project Generator
            Version: %s
            
            This tool generates a Java project based on the specified template files.
            It creates the necessary directory structure, copies the templates, replaces
            placeholders in the templates with the provided values, and generates additional
            files such as pom.xml/build.gradle, .sdkmanrc, and README.md.
            
            USAGE:
                genj [OPTIONS]
            
            OPTIONS:
                -t, --template <PATH>         Path to the template (ZIP or folder)
                -d, --destination <PATH>      Destination directory (default: current directory)
                -n, --project_name <NAME>     Project name (default: Demo)
                -a, --author <NAME>           Author name (default: Unknown Author)
                -e, --email <EMAIL>           Author email (default: email@unknown.local)
                -v, --project_version <VER>   Project version (default: 0.0.1)
                -j, --java_version <VER>      JDK version (default: 25)
                -f, --java_flavor <FLAVOR>    JDK flavor for sdkman (default: 25-zulu)
                -k, --package <PKG>           Java package name (default: com.demo)
                -m, --mainclass <CLASS>       Main class name (default: App)
                -b, --build <TOOL>            Build tool: maven or gradle (default: maven)
                    --maven_version <VER>     Maven version for sdkman (default: 3.9.5)
                    --gradle_version <VER>    Gradle version for sdkman (default: 8.5)
                -l, --vendor_name <NAME>      Vendor name (default: Vendor)
                -r, --remote_git <URL>        Remote git repository URL
                    --verbose                 Enable verbose output
                    --list                    List available templates
                -s, --search <TERM>           Search for templates by name or metadata
                -h, --help                    Show this help message
                    --version                 Show version information
            
            EXAMPLES:
                genj --list
                genj -t /path/to/template -n MyProject -k com.example
                genj -t java-basic-main -n Demo -b gradle
            """.formatted(VERSION);

    private CliParser() {
        // Utility class
    }

    /**
     * Parse command line arguments and return a CliConfig.
     *
     * @param args Command line arguments
     * @return Parsed CliConfig
     */
    public static CliConfig parse(String[] args) {
        var builder = CliConfig.builder();
        List<String> argList = new ArrayList<>(List.of(args));

        int i = 0;
        while (i < argList.size()) {
            String arg = argList.get(i);

            switch (arg) {
                case "-h", "--help" -> {
                    System.out.println(HELP_TEXT);
                    System.exit(0);
                }
                case "--version" -> {
                    System.out.println("genj version " + VERSION);
                    System.exit(0);
                }
                case "--verbose" -> {
                    builder.verbose(true);
                    i++;
                }
                case "--list" -> {
                    builder.list(true);
                    i++;
                }
                case "-t", "--template" -> {
                    builder.template(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "-d", "--destination" -> {
                    builder.destination(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "-n", "--project_name" -> {
                    builder.projectName(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "-a", "--author" -> {
                    builder.author(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "-e", "--email" -> {
                    builder.email(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "-v", "--project_version" -> {
                    builder.projectVersion(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "-j", "--java_version" -> {
                    builder.java(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "-f", "--java_flavor" -> {
                    builder.javaFlavor(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "-k", "--package" -> {
                    builder.packageName(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "-m", "--mainclass" -> {
                    builder.mainclass(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "-b", "--build" -> {
                    builder.buildTool(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "--maven_version" -> {
                    builder.mavenVersion(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "--gradle_version" -> {
                    builder.gradleVersion(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "-l", "--vendor_name" -> {
                    builder.vendorName(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "-r", "--remote_git_repository" -> {
                    builder.remoteGit(getNextArg(argList, i, arg));
                    i += 2;
                }
                case "-s", "--search" -> {
                    builder.search(getNextArg(argList, i, arg));
                    i += 2;
                }
                default -> {
                    System.err.println("Unknown option: " + arg);
                    System.err.println("Use --help for usage information");
                    System.exit(1);
                    i++;
                }
            }
        }

        return builder.build();
    }

    private static String getNextArg(List<String> args, int currentIndex, String optionName) {
        if (currentIndex + 1 >= args.size()) {
            System.err.println("Error: Option " + optionName + " requires a value");
            System.exit(1);
        }
        return args.get(currentIndex + 1);
    }
}
