package com.loganalyzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CliOptions} argument parsing.
 */
class CliOptionsTest {

    // ---------------------------------------------------------------
    // Main.parseArgs is private; test via LogAnalyzer.analyze which
    // uses it internally, or test CliOptions.Builder directly.
    // ---------------------------------------------------------------

    @Test
    void builder_setsAllFields() {
        CliOptions options = CliOptions.builder()
                .logFilePath("/var/log/app.log")
                .addKeywords("ERROR,WARN")
                .ignoreCase(true)
                .countOnly(true)
                .outputFile("/tmp/result.txt")
                .showHelp(false)
                .build();

        assertEquals("/var/log/app.log", options.getLogFilePath());
        assertEquals(List.of("ERROR", "WARN"), options.getKeywords());
        assertTrue(options.isIgnoreCase());
        assertTrue(options.isCountOnly());
        assertEquals("/tmp/result.txt", options.getOutputFile());
        assertFalse(options.isShowHelp());
    }

    @Test
    void builder_defaultValues() {
        CliOptions options = CliOptions.builder().build();

        assertNull(options.getLogFilePath());
        assertTrue(options.getKeywords().isEmpty());
        assertFalse(options.isIgnoreCase());
        assertFalse(options.isCountOnly());
        assertNull(options.getOutputFile());
        assertFalse(options.isShowHelp());
    }

    @Test
    void builder_keywordsAreUnmodifiable() {
        CliOptions options = CliOptions.builder()
                .addKeywords("ERROR")
                .build();

        List<String> keywords = options.getKeywords();
        assertThrows(UnsupportedOperationException.class,
                () -> keywords.add("WARN"));
    }

    @Test
    void builder_keywordsSplitByComma() {
        CliOptions options = CliOptions.builder()
                .addKeywords("ERROR, WARN, INFO")
                .build();

        assertEquals(List.of("ERROR", "WARN", "INFO"), options.getKeywords());
    }
}
