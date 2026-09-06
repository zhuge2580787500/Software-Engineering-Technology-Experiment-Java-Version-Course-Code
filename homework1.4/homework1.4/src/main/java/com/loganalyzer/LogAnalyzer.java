package com.loganalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Core analysis engine for the log keyword analyzer.
 *
 * <p>Responsible for reading a log file in a streaming fashion,
 * matching lines against one or more keywords, and writing results.</p>
 */
public final class LogAnalyzer {

    private static final int EXIT_SUCCESS = 0;
    private static final int EXIT_FILE_NOT_FOUND = 2;
    private static final int EXIT_READ_ERROR = 3;
    private static final int EXIT_WRITE_ERROR = 4;
    private static final int EXIT_UNEXPECTED = 5;

    private LogAnalyzer() {
        // Utility class — not instantiable
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Executes the log analysis with the given options.
     *
     * <p>Dispatches to the appropriate exit code based on the outcome.</p>
     *
     * @param options the parsed CLI options
     * @return the exit code (0 on success)
     */
    public static int analyze(CliOptions options) {
        if (options.isShowHelp()) {
            printHelp(System.out);
            return EXIT_SUCCESS;
        }

        if (options.getLogFilePath() == null || options.getLogFilePath().isEmpty()) {
            System.err.println("Error: Missing required option --file");
            return 1;
        }

        if (options.getKeywords().isEmpty()) {
            System.err.println("Error: Missing required option --keyword");
            return 1;
        }

        try {
            AnalysisResult result = performAnalysis(options);
            writeResult(options, result);
            return EXIT_SUCCESS;
        } catch (java.nio.file.NoSuchFileException e) {
            System.err.println("Error: Log file not found: " + options.getLogFilePath());
            return EXIT_FILE_NOT_FOUND;
        } catch (IOException e) {
            System.err.println("Error: Failed to read file: " + e.getMessage());
            return EXIT_READ_ERROR;
        } catch (SecurityException e) {
            System.err.println("Error: Failed to read file: " + e.getMessage());
            return EXIT_READ_ERROR;
        } catch (Exception e) {
            System.err.println("Unexpected error:");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return EXIT_UNEXPECTED;
        }
    }

    // -----------------------------------------------------------------------
    // Core logic
    // -----------------------------------------------------------------------

    /**
     * Performs the actual analysis, reading the file and matching lines.
     *
     * @param options the parsed CLI options
     * @return the analysis result
     * @throws IOException if an I/O error occurs during reading
     */
    private static AnalysisResult performAnalysis(CliOptions options) throws IOException {
        String filePath = options.getLogFilePath();
        List<String> keywords = options.getKeywords();
        boolean ignoreCase = options.isIgnoreCase();

        List<String> matchedLines = new ArrayList<>();
        int matchCount = 0;
        int lineNumber = 0;

        Path path = Path.of(filePath);

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (matchesAnyKeyword(line, keywords, ignoreCase)) {
                    matchCount++;
                    if (!options.isCountOnly()) {
                        matchedLines.add(lineNumber + ": " + line);
                    }
                }
            }
        }

        return new AnalysisResult(matchCount, matchedLines);
    }

    /**
     * Checks whether the given line matches any of the keywords.
     *
     * @param line      the log line to test
     * @param keywords  the list of keywords
     * @param ignoreCase {@code true} if matching should be case-insensitive
     * @return {@code true} if the line contains any keyword
     */
    static boolean matchesAnyKeyword(String line,
                                             List<String> keywords,
                                             boolean ignoreCase) {
        for (String keyword : keywords) {
            if (ignoreCase) {
                if (line.toLowerCase().contains(keyword.toLowerCase())) {
                    return true;
                }
            } else {
                if (line.contains(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }

    // -----------------------------------------------------------------------
    // Output
    // -----------------------------------------------------------------------

    /**
     * Writes the analysis result to the appropriate output destination.
     *
     * @param options the parsed CLI options
     * @param result  the analysis result
     * @throws IOException if an I/O error occurs during writing
     */
    private static void writeResult(CliOptions options, AnalysisResult result) throws IOException {
        if (options.getOutputFile() != null) {
            writeToFile(options.getOutputFile(), result, options.isCountOnly());
        } else {
            writeToStdout(result, options.isCountOnly());
        }
    }

    /**
     * Writes the result to standard output.
     *
     * @param result    the analysis result
     * @param countOnly {@code true} to output only the count
     */
    private static void writeToStdout(AnalysisResult result, boolean countOnly) {
        if (countOnly) {
            System.out.println(result.matchCount());
        } else {
            for (String line : result.matchedLines()) {
                System.out.println(line);
            }
        }
    }

    /**
     * Writes the result to a file in UTF-8 encoding.
     *
     * @param filePath  the output file path
     * @param result    the analysis result
     * @param countOnly {@code true} to output only the count
     * @throws IOException if the file cannot be created or written
     */
    private static void writeToFile(String filePath,
                                    AnalysisResult result,
                                    boolean countOnly) throws IOException {
        Path outputPath = Path.of(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(
                outputPath, StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING,
                java.nio.file.StandardOpenOption.WRITE)) {
            if (countOnly) {
                writer.write(Integer.toString(result.matchCount()));
                writer.newLine();
            } else {
                for (String line : result.matchedLines()) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Help text
    // -----------------------------------------------------------------------

    /**
     * Prints the help/usage information to the given output stream.
     *
     * @param out the output stream (e.g. {@link System#out})
     */
    private static void printHelp(java.io.PrintStream out) {
        out.println("Usage: java -jar log-analyzer.jar [options]");
        out.println();
        out.println("Options:");
        out.println("  -f, --file <path>        Path to the log file (required)");
        out.println("  -k, --keyword <words>    Keywords to search, comma-separated (required)");
        out.println("  -i, --ignore-case        Case-insensitive matching (default: false)");
        out.println("  -c, --count              Output match count only (default: false)");
        out.println("  -o, --output <path>      Write results to file (default: stdout)");
        out.println("  -h, --help               Display this help message");
        out.println();
        out.println("Example:");
        out.println("  java -jar log-analyzer.jar -f app.log -k ERROR,WARN -i -c");
    }

    // -----------------------------------------------------------------------
    // Immutable result record
    // -----------------------------------------------------------------------

    /**
     * Holds the result of a log analysis.
     *
     * @param matchCount  total number of matching lines
     * @param matchedLines the matched lines formatted as "lineNo: content"
     */
    private record AnalysisResult(int matchCount, List<String> matchedLines) {
        AnalysisResult {
            matchedLines = Collections.unmodifiableList(new ArrayList<>(matchedLines));
        }
    }
}
