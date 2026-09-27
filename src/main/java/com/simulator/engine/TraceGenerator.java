package com.simulator.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Produces reference strings (sequences of page numbers) either by reading
 * a trace file from disk or by generating a synthetic one with a tunable
 * degree of locality of reference.
 */
public class TraceGenerator {

    /**
     * Reads a trace file. Accepts page numbers separated by whitespace,
     * commas, or newlines. Lines starting with '#' are treated as comments.
     */
    public static int[] readFromFile(String path) throws IOException {
        List<Integer> values = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                for (String token : line.split("[,\\s]+")) {
                    if (!token.isEmpty()) {
                        values.add(Integer.parseInt(token.trim()));
                    }
                }
            }
        }
        int[] result = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    /**
     * Generates a synthetic reference string that simulates locality of
     * reference: most accesses stay within a shrinking "working set" window
     * around the previous page, with occasional jumps to a random page.
     *
     * @param length       number of references to generate
     * @param pageSpace    number of distinct virtual pages (0..pageSpace-1)
     * @param localityBias probability (0.0-1.0) that the next reference stays
     *                     close to the current one instead of jumping randomly
     * @param seed         RNG seed for reproducibility
     */
    public static int[] generateLocalityBiased(int length, int pageSpace, double localityBias, long seed) {
        Random random = new Random(seed);
        int[] refs = new int[length];
        int current = random.nextInt(pageSpace);
        int window = Math.max(2, pageSpace / 8);

        for (int i = 0; i < length; i++) {
            if (i > 0 && random.nextDouble() < localityBias) {
                int delta = random.nextInt(window * 2 + 1) - window;
                current = Math.floorMod(current + delta, pageSpace);
            } else {
                current = random.nextInt(pageSpace);
            }
            refs[i] = current;
        }
        return refs;
    }

    /** Generates a purely uniform-random reference string (no locality). */
    public static int[] generateRandom(int length, int pageSpace, long seed) {
        Random random = new Random(seed);
        int[] refs = new int[length];
        for (int i = 0; i < length; i++) {
            refs[i] = random.nextInt(pageSpace);
        }
        return refs;
    }
}
