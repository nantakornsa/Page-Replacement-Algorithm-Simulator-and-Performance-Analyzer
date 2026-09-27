package com.simulator.analyzer;

/**
 * Lightweight wall-clock and JVM heap usage instrumentation around a
 * simulation run. Not a substitute for a proper profiler, but enough to
 * give a rough relative sense of algorithm cost.
 */
public class SystemMetrics {

    private long startNanos;
    private long endNanos;
    private long usedMemoryBeforeBytes;
    private long usedMemoryAfterBytes;

    public void start() {
        Runtime runtime = Runtime.getRuntime();
        usedMemoryBeforeBytes = runtime.totalMemory() - runtime.freeMemory();
        startNanos = System.nanoTime();
    }

    public void stop() {
        endNanos = System.nanoTime();
        Runtime runtime = Runtime.getRuntime();
        usedMemoryAfterBytes = runtime.totalMemory() - runtime.freeMemory();
    }

    public long getElapsedNanos() {
        return endNanos - startNanos;
    }

    public double getElapsedMillis() {
        return getElapsedNanos() / 1_000_000.0;
    }

    /** Approximate delta in JVM heap usage observed during the run (can be negative due to GC). */
    public long getMemoryDeltaBytes() {
        return usedMemoryAfterBytes - usedMemoryBeforeBytes;
    }

    public double getMemoryDeltaKb() {
        return getMemoryDeltaBytes() / 1024.0;
    }
}
