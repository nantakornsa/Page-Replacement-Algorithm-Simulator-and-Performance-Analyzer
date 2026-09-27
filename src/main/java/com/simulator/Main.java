package com.simulator;

import com.simulator.algorithm.ClockAlgorithm;
import com.simulator.algorithm.FifoAlgorithm;
import com.simulator.algorithm.LruAlgorithm;
import com.simulator.algorithm.OptAlgorithm;
import com.simulator.algorithm.PageReplacementAlgorithm;
import com.simulator.analyzer.PerformanceAnalyzer;
import com.simulator.engine.MemoryEngine;
import com.simulator.engine.MemoryEngine.EngineRun;
import com.simulator.engine.TraceGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Command-line entry point for the page replacement simulator.
 *
 * Usage examples:
 *   java -jar page-replacement-simulator.jar --frames 3 --algo fifo,lru,opt,clock \
 *        --trace traces/sample.txt
 *
 *   java -jar page-replacement-simulator.jar --frames 4 --algo all \
 *        --generate 30 --pages 10 --seed 42
 */
public class Main {

    public static void main(String[] args) {
        try {
            CliOptions options = CliOptions.parse(args);
            run(options);
        } catch (CliOptions.CliException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println();
            System.err.println(CliOptions.usage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to read trace file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void run(CliOptions options) throws IOException {
        int[] referenceString;
        if (options.tracePath != null) {
            referenceString = TraceGenerator.readFromFile(options.tracePath);
            System.out.println("Loaded trace from " + options.tracePath + " (" + referenceString.length + " references)");
        } else {
            referenceString = TraceGenerator.generateLocalityBiased(
                    options.generateLength, options.pageSpace, options.localityBias, options.seed);
            System.out.println("Generated synthetic trace: length=" + options.generateLength
                    + " pageSpace=" + options.pageSpace + " seed=" + options.seed);
        }

        System.out.println("Reference string: " + Arrays.toString(referenceString));
        System.out.println("Frames available: " + options.numFrames);
        System.out.println();

        MemoryEngine engine = new MemoryEngine(options.numFrames);
        PerformanceAnalyzer analyzer = new PerformanceAnalyzer();

        Map<String, PageReplacementAlgorithm> registry = buildRegistry();
        List<PageReplacementAlgorithm> selected = resolveSelection(options.algorithms, registry);

        List<EngineRun> runs = new ArrayList<>();
        for (PageReplacementAlgorithm algo : selected) {
            EngineRun run = engine.run(algo, referenceString);
            runs.add(run);

            System.out.println("=== " + algo.getName() + " ===");
            if (options.verbose) {
                System.out.println(analyzer.renderTrace(run.getResult()));
            }
            System.out.println(analyzer.summarize(run).replace("frames=?", "frames=" + options.numFrames));
            System.out.println();
        }

        if (runs.size() > 1) {
            System.out.println("=== Comparison ===");
            System.out.println(analyzer.compare(runs));
        }
    }

    private static Map<String, PageReplacementAlgorithm> buildRegistry() {
        Map<String, PageReplacementAlgorithm> registry = new LinkedHashMap<>();
        registry.put("fifo", new FifoAlgorithm());
        registry.put("lru", new LruAlgorithm());
        registry.put("opt", new OptAlgorithm());
        registry.put("clock", new ClockAlgorithm());
        return registry;
    }

    private static List<PageReplacementAlgorithm> resolveSelection(List<String> requested,
                                                                     Map<String, PageReplacementAlgorithm> registry) {
        List<PageReplacementAlgorithm> selected = new ArrayList<>();
        if (requested.size() == 1 && requested.get(0).equalsIgnoreCase("all")) {
            selected.addAll(registry.values());
            return selected;
        }
        for (String name : requested) {
            PageReplacementAlgorithm algo = registry.get(name.toLowerCase());
            if (algo == null) {
                throw new CliOptions.CliException("Unknown algorithm: " + name
                        + " (expected one of fifo, lru, opt, clock, all)");
            }
            selected.add(algo);
        }
        return selected;
    }

    /** Parses and validates command-line arguments. */
    static class CliOptions {
        int numFrames = 3;
        String tracePath = null;
        List<String> algorithms = List.of("all");
        int generateLength = 20;
        int pageSpace = 10;
        double localityBias = 0.7;
        long seed = 42L;
        boolean verbose = false;

        static CliOptions parse(String[] args) {
            CliOptions opts = new CliOptions();
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                switch (arg) {
                    case "--frames":
                        opts.numFrames = Integer.parseInt(requireValue(args, ++i, "--frames"));
                        break;
                    case "--trace":
                        opts.tracePath = requireValue(args, ++i, "--trace");
                        break;
                    case "--algo":
                        opts.algorithms = Arrays.asList(requireValue(args, ++i, "--algo").split(","));
                        break;
                    case "--generate":
                        opts.generateLength = Integer.parseInt(requireValue(args, ++i, "--generate"));
                        break;
                    case "--pages":
                        opts.pageSpace = Integer.parseInt(requireValue(args, ++i, "--pages"));
                        break;
                    case "--locality":
                        opts.localityBias = Double.parseDouble(requireValue(args, ++i, "--locality"));
                        break;
                    case "--seed":
                        opts.seed = Long.parseLong(requireValue(args, ++i, "--seed"));
                        break;
                    case "--verbose":
                        opts.verbose = true;
                        break;
                    case "--help":
                    case "-h":
                        System.out.println(usage());
                        System.exit(0);
                        break;
                    default:
                        throw new CliException("Unrecognized argument: " + arg);
                }
            }
            if (opts.numFrames <= 0) {
                throw new CliException("--frames must be a positive integer");
            }
            return opts;
        }

        private static String requireValue(String[] args, int index, String flag) {
            if (index >= args.length) {
                throw new CliException("Missing value for " + flag);
            }
            return args[index];
        }

        static String usage() {
            return """
                    Page Replacement Simulator

                    Usage:
                      java -jar page-replacement-simulator.jar [options]

                    Options:
                      --frames N        Number of physical frames (default: 3)
                      --trace FILE      Path to a trace file of page numbers (overrides --generate)
                      --algo LIST       Comma-separated: fifo,lru,opt,clock or "all" (default: all)
                      --generate N      Length of a synthetic reference string to generate (default: 20)
                      --pages N         Distinct virtual page numbers for generation (default: 10)
                      --locality D      Locality bias 0.0-1.0 for generation (default: 0.7)
                      --seed N          RNG seed for reproducible generation (default: 42)
                      --verbose         Print the full step-by-step trace for each algorithm
                      --help            Show this message

                    Examples:
                      java -jar page-replacement-simulator.jar --frames 3 --trace traces/sample.txt --algo all
                      java -jar page-replacement-simulator.jar --frames 4 --generate 30 --pages 8 --verbose
                    """;
        }

        static class CliException extends RuntimeException {
            CliException(String message) {
                super(message);
            }
        }
    }
}
