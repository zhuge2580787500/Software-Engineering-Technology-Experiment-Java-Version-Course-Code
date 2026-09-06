package com.loganalyzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LogAnalyzer} keyword matching logic and
 * large-file streaming.
 */
class LogAnalyzerTest {

    // ---------------------------------------------------------------
    // Keyword matching — case sensitive
    // ---------------------------------------------------------------

    @Test
    void match_caseSensitive_found() {
        String line = "2024-01-01 ERROR Something went wrong";
        assertTrue(LogAnalyzerTestHelper.matchesAnyKeyword(
                line, List.of("ERROR"), false));
    }

    @Test
    void match_caseSensitive_notFound() {
        String line = "2024-01-01 error Something went wrong";
        assertFalse(LogAnalyzerTestHelper.matchesAnyKeyword(
                line, List.of("ERROR"), false));
    }

    // ---------------------------------------------------------------
    // Keyword matching — case insensitive
    // ---------------------------------------------------------------

    @Test
    void match_caseInsensitive_found() {
        String line = "2024-01-01 error Something went wrong";
        assertTrue(LogAnalyzerTestHelper.matchesAnyKeyword(
                line, List.of("ERROR"), true));
    }

    // ---------------------------------------------------------------
    // Multiple keywords — OR logic
    // ---------------------------------------------------------------

    @Test
    void match_multipleKeywords_anyMatch() {
        assertTrue(LogAnalyzerTestHelper.matchesAnyKeyword(
                "Level: ERROR", List.of("ERROR", "WARN"), false));
        assertTrue(LogAnalyzerTestHelper.matchesAnyKeyword(
                "Level: WARN", List.of("ERROR", "WARN"), false));
        assertFalse(LogAnalyzerTestHelper.matchesAnyKeyword(
                "Level: INFO", List.of("ERROR", "WARN"), false));
    }

    // ---------------------------------------------------------------
    // Substring matching (not regex / whole-word)
    // ---------------------------------------------------------------

    @Test
    void match_substring() {
        assertTrue(LogAnalyzerTestHelper.matchesAnyKeyword(
                "java.lang.NullPointerException", List.of("NullPointer"), false));
        assertTrue(LogAnalyzerTestHelper.matchesAnyKeyword(
                "connection timeout", List.of("time"), false));
    }

    // ---------------------------------------------------------------
    // Empty keyword list
    // ---------------------------------------------------------------

    @Test
    void match_emptyKeywords_noMatch() {
        assertFalse(LogAnalyzerTestHelper.matchesAnyKeyword(
                "anything", List.of(), false));
    }

    // ---------------------------------------------------------------
    // Streaming large file
    // ---------------------------------------------------------------

    @Test
    void analyze_largeFile_streaming(@TempDir Path tempDir) throws IOException {
        int totalLines = 10_000;
        int errorLines = 250;

        Path logFile = tempDir.resolve("large.log");
        try (var writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8)) {
            for (int i = 1; i <= totalLines; i++) {
                if (i % (totalLines / errorLines) == 0) {
                    writer.write(i + ": ERROR some message");
                } else {
                    writer.write(i + ": INFO normal message");
                }
                writer.newLine();
            }
        }

        CliOptions options = CliOptions.builder()
                .logFilePath(logFile.toString())
                .addKeywords("ERROR")
                .build();

        int exitCode = LogAnalyzer.analyze(options);
        assertEquals(0, exitCode);
    }

    // ---------------------------------------------------------------
    // File not found
    // ---------------------------------------------------------------

    @Test
    void analyze_fileNotFound(@TempDir Path tempDir) {
        CliOptions options = CliOptions.builder()
                .logFilePath(tempDir.resolve("nonexistent.log").toString())
                .addKeywords("ERROR")
                .build();

        int exitCode = LogAnalyzer.analyze(options);
        assertEquals(2, exitCode);
    }

    // ---------------------------------------------------------------
    // Help flag
    // ---------------------------------------------------------------

    @Test
    void analyze_helpFlag() {
        CliOptions options = CliOptions.builder().showHelp(true).build();
        int exitCode = LogAnalyzer.analyze(options);
        assertEquals(0, exitCode);
    }

    // ---------------------------------------------------------------
    // Count only mode
    // ---------------------------------------------------------------

    @Test
    void analyze_countOnly(@TempDir Path tempDir) throws IOException {
        Path logFile = tempDir.resolve("test.log");
        Files.writeString(logFile,
                "INFO start\nERROR first\nINFO middle\nERROR second\nINFO end\n",
                StandardCharsets.UTF_8);

        // We can't easily capture stdout, so test via LogAnalyzerTestHelper
        int count = LogAnalyzerTestHelper.countMatches(
                logFile.toString(), List.of("ERROR"), false);
        assertEquals(2, count);
    }
}
