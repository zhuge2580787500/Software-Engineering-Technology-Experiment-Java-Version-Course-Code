package com.loganalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable holder for parsed command-line options.
 *
 * <p>Encapsulates all parameters accepted by the log analyzer tool.
 * Once constructed, the instance cannot be modified.</p>
 */
public final class CliOptions {

    private final String logFilePath;
    private final List<String> keywords;
    private final boolean ignoreCase;
    private final boolean countOnly;
    private final String outputFile;
    private final boolean showHelp;

    /**
     * Private constructor. Use {@link #builder()} to create instances.
     */
    private CliOptions(String logFilePath,
                       List<String> keywords,
                       boolean ignoreCase,
                       boolean countOnly,
                       String outputFile,
                       boolean showHelp) {
        this.logFilePath = logFilePath;
        this.keywords = Collections.unmodifiableList(new ArrayList<>(keywords));
        this.ignoreCase = ignoreCase;
        this.countOnly = countOnly;
        this.outputFile = outputFile;
        this.showHelp = showHelp;
    }

    /**
     * Returns a new {@link Builder}.
     *
     * @return a fresh builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return the path to the log file, never {@code null}
     */
    public String getLogFilePath() {
        return logFilePath;
    }

    /**
     * @return an unmodifiable list of keywords, never {@code null} (may be empty)
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * @return {@code true} if case-insensitive matching is enabled
     */
    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    /**
     * @return {@code true} if only the match count should be output
     */
    public boolean isCountOnly() {
        return countOnly;
    }

    /**
     * @return the output file path, or {@code null} if output goes to stdout
     */
    public String getOutputFile() {
        return outputFile;
    }

    /**
     * @return {@code true} if help should be displayed
     */
    public boolean isShowHelp() {
        return showHelp;
    }

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------

    /**
     * Fluent builder for {@link CliOptions}.
     */
    public static final class Builder {
        private String logFilePath;
        private final List<String> keywords = new ArrayList<>();
        private boolean ignoreCase;
        private boolean countOnly;
        private String outputFile;
        private boolean showHelp;

        Builder() {
        }

        /**
         * Sets the log file path.
         *
         * @param path the file path
         * @return this builder
         */
        public Builder logFilePath(String path) {
            this.logFilePath = path;
            return this;
        }

        /**
         * Adds one or more keywords (comma-separated) to the keyword list.
         *
         * @param commaSeparated comma-separated keywords
         * @return this builder
         */
        public Builder addKeywords(String commaSeparated) {
            if (commaSeparated != null && !commaSeparated.isEmpty()) {
                for (String kw : commaSeparated.split(",")) {
                    String trimmed = kw.trim();
                    if (!trimmed.isEmpty()) {
                        keywords.add(trimmed);
                    }
                }
            }
            return this;
        }

        /**
         * Adds a single keyword to the keyword list.
         *
         * @param keyword the keyword to add
         * @return this builder
         */
        public Builder addKeyword(String keyword) {
            this.keywords.add(keyword);
            return this;
        }

        /**
         * Sets whether to ignore case during matching.
         *
         * @param ignoreCase {@code true} to ignore case
         * @return this builder
         */
        public Builder ignoreCase(boolean ignoreCase) {
            this.ignoreCase = ignoreCase;
            return this;
        }

        /**
         * Sets whether to output only the match count.
         *
         * @param countOnly {@code true} to count only
         * @return this builder
         */
        public Builder countOnly(boolean countOnly) {
            this.countOnly = countOnly;
            return this;
        }

        /**
         * Sets the output file path.
         *
         * @param outputFile the output file path, or {@code null} for stdout
         * @return this builder
         */
        public Builder outputFile(String outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        /**
         * Sets whether to display help.
         *
         * @param showHelp {@code true} to show help
         * @return this builder
         */
        public Builder showHelp(boolean showHelp) {
            this.showHelp = showHelp;
            return this;
        }

        /**
         * Builds an immutable {@link CliOptions} instance.
         *
         * @return the built options object
         */
        public CliOptions build() {
            return new CliOptions(
                    logFilePath,
                    keywords,
                    ignoreCase,
                    countOnly,
                    outputFile,
                    showHelp
            );
        }
    }
}
