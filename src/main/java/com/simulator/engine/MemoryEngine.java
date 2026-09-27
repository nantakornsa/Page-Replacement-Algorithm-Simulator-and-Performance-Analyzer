package com.simulator.engine;

import com.simulator.algorithm.PageReplacementAlgorithm;
import com.simulator.algorithm.SimulationResult;
import com.simulator.analyzer.SystemMetrics;

/**
 * Central engine that drives a page-replacement algorithm over a reference
 * string and a fixed number of physical frames, wrapping the run with
 * timing/memory instrumentation via {@link SystemMetrics}.
 */
public class MemoryEngine {

    private final int numFrames;

    public MemoryEngine(int numFrames) {
        if (numFrames <= 0) {
            throw new IllegalArgumentException("numFrames must be positive, got " + numFrames);
        }
        this.numFrames = numFrames;
    }

    public int getNumFrames() {
        return numFrames;
    }

    /**
     * Runs the given algorithm against the reference string and returns the
     * populated {@link SimulationResult} along with wall-clock timing.
     */
    public EngineRun run(PageReplacementAlgorithm algorithm, int[] referenceString) {
        SystemMetrics metrics = new SystemMetrics();
        metrics.start();

        SimulationResult result = algorithm.simulate(referenceString, numFrames);

        metrics.stop();
        return new EngineRun(result, metrics);
    }

    /** Bundles a simulation result together with the metrics captured for that run. */
    public static class EngineRun {
        private final SimulationResult result;
        private final SystemMetrics metrics;

        public EngineRun(SimulationResult result, SystemMetrics metrics) {
            this.result = result;
            this.metrics = metrics;
        }

        public SimulationResult getResult() {
            return result;
        }

        public SystemMetrics getMetrics() {
            return metrics;
        }
    }
}
