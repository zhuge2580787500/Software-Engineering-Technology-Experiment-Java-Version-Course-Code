package com.loganalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * Test helper that exposes package-private methods of {@link LogAnalyzer}
 * so they can be verified in unit tests without reflection.
 */
final class LogAnalyzerTestHelper {

    private LogAnalyzerTestHelper() {
        // Not instantiable
    }

    /**
     * Delegates to {@link LogAnalyzer}'s package-private matching logic.
     */
    static boolean matchesAnyKeyword(String line,
                                     List<String> keywords,
                                     boolean ignoreCase) {
        return LogAnalyzer.matchesAnyKeyword(line, keywords, ignoreCase);
    }

    /**
     * Counts the number of matching lines in a file.
     *
     * @param filePath   path to the log file
     * @param keywords   list of keywords
     * @param ignoreCase whether to ignore case
     * @return the number of matching lines
     */
    static int countMatches(String filePath,
                            List<String> keywords,
                            boolean ignoreCase) {
        int count = 0;
        Path path = Path.of(filePath);
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (matchesAnyKeyword(line, keywords, ignoreCase)) {
                    count++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return count;
    }
}
