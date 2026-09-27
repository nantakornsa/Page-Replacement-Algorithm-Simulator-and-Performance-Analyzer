package com.simulator.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the outcome of replaying a reference string through an algorithm:
 * the fault/hit counts and a step-by-step trace of frame contents for
 * reporting or visualization.
 */
public class SimulationResult {

    private final String algorithmName;
    private int pageFaults = 0;
    private int pageHits = 0;
    private final List<StepSnapshot> steps = new ArrayList<>();

    public SimulationResult(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public void recordStep(int index, int referencedPage, boolean fault, int evictedPage, List<Integer> frameState) {
        if (fault) {
            pageFaults++;
        } else {
            pageHits++;
        }
        steps.add(new StepSnapshot(index, referencedPage, fault, evictedPage, new ArrayList<>(frameState)));
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public int getPageFaults() {
        return pageFaults;
    }

    public int getPageHits() {
        return pageHits;
    }

    public int getTotalReferences() {
        return pageFaults + pageHits;
    }

    public double getHitRate() {
        return getTotalReferences() == 0 ? 0.0 : (double) pageHits / getTotalReferences();
    }

    public double getFaultRate() {
        return getTotalReferences() == 0 ? 0.0 : (double) pageFaults / getTotalReferences();
    }

    public List<StepSnapshot> getSteps() {
        return steps;
    }

    /** Immutable snapshot of one reference-string step. */
    public static class StepSnapshot {
        public final int index;
        public final int referencedPage;
        public final boolean fault;
        public final int evictedPage; // -1 if nothing was evicted
        public final List<Integer> frameState;

        public StepSnapshot(int index, int referencedPage, boolean fault, int evictedPage, List<Integer> frameState) {
            this.index = index;
            this.referencedPage = referencedPage;
            this.fault = fault;
            this.evictedPage = evictedPage;
            this.frameState = frameState;
        }
    }
}
