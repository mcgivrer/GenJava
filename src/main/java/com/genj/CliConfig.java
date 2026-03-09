package com.genj;

/**
 * CLI configuration record for genj - Java Project Generator.
 * Uses JDK 25 record pattern for immutable configuration.
 *
 * @param template        Path to the template (ZIP or folder)
 * @param destination     Destination directory
 * @param projectName     Name of the project
 * @param author          Author name
 * @param email           Author email
 * @param projectVersion  Project version
 * @param java            JDK version
 * @param javaFlavor      JDK flavor for sdkman
 * @param packageName     Java package name
 * @param mainclass       Main class name
 * @param buildTool       Build tool (maven or gradle)
 * @param mavenVersion    Maven version for sdkman
 * @param gradleVersion   Gradle version for sdkman
 * @param vendorName      Vendor name
 * @param remoteGit       Remote Git repository URL
 * @param verbose         Enable verbose output
 * @param list            List available templates
 * @param search          Search term for templates
 */
public record CliConfig(
        String template,
        String destination,
        String projectName,
        String author,
        String email,
        String projectVersion,
        String java,
        String javaFlavor,
        String packageName,
        String mainclass,
        String buildTool,
        String mavenVersion,
        String gradleVersion,
        String vendorName,
        String remoteGit,
        boolean verbose,
        boolean list,
        String search
) {

    /**
     * Builder for CliConfig with default values.
     */
    public static class Builder {
        private String template = null;
        private String destination = null;
        private String projectName = "Demo";
        private String author = "Unknown Author";
        private String email = "email@unknown.local";
        private String projectVersion = "0.0.1";
        private String java = "25";
        private String javaFlavor = "25-zulu";
        private String packageName = "com.demo";
        private String mainclass = "App";
        private String buildTool = "maven";
        private String mavenVersion = "3.9.5";
        private String gradleVersion = "8.5";
        private String vendorName = "Vendor";
        private String remoteGit = null;
        private boolean verbose = false;
        private boolean list = false;
        private String search = null;

        public Builder template(String template) {
            this.template = template;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder projectName(String projectName) {
            this.projectName = projectName;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder projectVersion(String projectVersion) {
            this.projectVersion = projectVersion;
            return this;
        }

        public Builder java(String java) {
            this.java = java;
            return this;
        }

        public Builder javaFlavor(String javaFlavor) {
            this.javaFlavor = javaFlavor;
            return this;
        }

        public Builder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder mainclass(String mainclass) {
            this.mainclass = mainclass;
            return this;
        }

        public Builder buildTool(String buildTool) {
            this.buildTool = buildTool;
            return this;
        }

        public Builder mavenVersion(String mavenVersion) {
            this.mavenVersion = mavenVersion;
            return this;
        }

        public Builder gradleVersion(String gradleVersion) {
            this.gradleVersion = gradleVersion;
            return this;
        }

        public Builder vendorName(String vendorName) {
            this.vendorName = vendorName;
            return this;
        }

        public Builder remoteGit(String remoteGit) {
            this.remoteGit = remoteGit;
            return this;
        }

        public Builder verbose(boolean verbose) {
            this.verbose = verbose;
            return this;
        }

        public Builder list(boolean list) {
            this.list = list;
            return this;
        }

        public Builder search(String search) {
            this.search = search;
            return this;
        }

        public CliConfig build() {
            return new CliConfig(
                    template, destination, projectName, author, email,
                    projectVersion, java, javaFlavor, packageName, mainclass,
                    buildTool, mavenVersion, gradleVersion, vendorName,
                    remoteGit, verbose, list, search
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
