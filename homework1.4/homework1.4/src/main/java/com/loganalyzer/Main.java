package com.loganalyzer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point for the Log Analyzer command-line tool.
 *
 * <p>Parses command-line arguments into {@link CliOptions} and delegates
 * the analysis to {@link LogAnalyzer}.</p>
 *
 * <p>Exit codes:
 * <ul>
 *   <li>0 — success</li>
 *   <li>1 — argument parsing error</li>
 *   <li>2 — log file not found</li>
 *   <li>3 — file read error</li>
 *   <li>4 — output file write error</li>
 *   <li>5 — unexpected error</li>
 * </ul>
 * </p>
 */
public final class Main {

    // -----------------------------------------------------------------------
    // Entry point
    // -----------------------------------------------------------------------

    /**
     * Main method. Parses arguments and runs analysis.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        CliOptions options = parseArgs(args);
        validate(options);
        int exitCode = LogAnalyzer.analyze(options);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    // -----------------------------------------------------------------------
    // Argument parser
    // -----------------------------------------------------------------------

    /**
     * Parses the raw command-line arguments into a {@link CliOptions} object.
     *
     * <p>Supports both {@code -f value} and {@code -f=value} forms.</p>
     *
     * @param args the raw argument array
     * @return the parsed options
     */
    private static CliOptions parseArgs(String[] args) {
        CliOptions.Builder builder = CliOptions.builder();
        List<String> errorMessages = new ArrayList<>();
        boolean helpRequested = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.equals("-h") || arg.equals("--help")) {
                builder.showHelp(true);
                helpRequested = true;
                break;
            }

            if (arg.startsWith("--file=")) {
                builder.logFilePath(arg.substring("--file=".length()));
            } else if (arg.equals("--file")) {
                builder.logFilePath(nextArg(args, i++, "--file"));
            } else if (arg.startsWith("-f=")) {
                builder.logFilePath(arg.substring("-f=".length()));
            } else if (arg.equals("-f")) {
                builder.logFilePath(nextArg(args, i++, "-f"));
            }
            // Keyword
            else if (arg.startsWith("--keyword=")) {
                builder.addKeywords(arg.substring("--keyword=".length()));
            } else if (arg.equals("--keyword")) {
                builder.addKeywords(nextArg(args, i++, "--keyword"));
            } else if (arg.startsWith("-k=")) {
                builder.addKeywords(arg.substring("-k=".length()));
            } else if (arg.equals("-k")) {
                builder.addKeywords(nextArg(args, i++, "-k"));
            }
            // Ignore case
            else if (arg.equals("-i") || arg.equals("--ignore-case")) {
                builder.ignoreCase(true);
            }
            // Count only
            else if (arg.equals("-c") || arg.equals("--count")) {
                builder.countOnly(true);
            }
            // Output file
            else if (arg.startsWith("--output=")) {
                builder.outputFile(arg.substring("--output=".length()));
            } else if (arg.equals("--output")) {
                builder.outputFile(nextArg(args, i++, "--output"));
            } else if (arg.startsWith("-o=")) {
                builder.outputFile(arg.substring("-o=".length()));
            } else if (arg.equals("-o")) {
                builder.outputFile(nextArg(args, i++, "-o"));
            }
            // Unknown option
            else {
                errorMessages.add("Unknown option '" + arg + "'");
            }
        }

        if (errorMessages.isEmpty()) {
            return builder.build();
        }

        printErrors(System.err, errorMessages);
        printUsage(System.err);
        System.exit(1);
        // unreachable, but compiler requires it
        throw new IllegalStateException("Should not reach here");
    }

    /**
     * Validates that required options are present.
     *
     * @param options the parsed options
     */
    static void validate(CliOptions options) {
        if (options.getLogFilePath() == null || options.getLogFilePath().isEmpty()) {
            System.err.println("Error: Missing required option --file");
            System.exit(1);
        }
        if (options.getKeywords().isEmpty()) {
            System.err.println("Error: Missing required option --keyword");
            System.exit(1);
        }
    }

    /**
     * Retrieves the next argument from the array, throwing an error if missing.
     *
     * @param args    the argument array
     * @param current the current index
     * @param option  the option name (for error messages)
     * @return the next argument value
     */
    private static String nextArg(String[] args, int current, String option) {
        if (current + 1 >= args.length) {
            System.err.println("Error: Missing value for option " + option);
            System.exit(1);
            throw new IllegalStateException("Should not reach here");
        }
        return args[current + 1];
    }

    // -----------------------------------------------------------------------
    // Output helpers
    // -----------------------------------------------------------------------

    /**
     * Prints error messages to the given stream.
     *
     * @param stream the output stream (typically {@link System#err})
     * @param messages the error messages
     */
    private static void printErrors(PrintStream stream, List<String> messages) {
        for (String msg : messages) {
            stream.println("Error: " + msg);
        }
    }

    /**
     * Prints usage information to the given stream.
     *
     * @param stream the output stream
     */
    private static void printUsage(PrintStream stream) {
        stream.println("Run with --help for usage information.");
    }
}
