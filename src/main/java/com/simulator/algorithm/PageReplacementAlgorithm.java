package com.simulator.algorithm;

/**
 * Strategy-pattern interface implemented by every page replacement policy
 * (FIFO, LRU, OPT, LFU). Each implementation replays a full reference
 * string against a fixed number of frames and returns a {@link SimulationResult}.
 */
public interface PageReplacementAlgorithm {

    /** Human readable name, e.g. "FIFO", "LRU", "OPT", "LFU". */
    String getName();

    /**
     * Runs the algorithm over the given reference string.
     *
     * @param referenceString the sequence of page numbers accessed by the process
     * @param numFrames       number of available physical frames
     * @return a populated {@link SimulationResult}
     */
    SimulationResult simulate(int[] referenceString, int numFrames);
}
