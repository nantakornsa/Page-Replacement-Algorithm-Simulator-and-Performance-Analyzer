package com.simulator.algorithm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FifoAlgorithmTest {

    // Classic textbook reference string (Silberschatz et al.), 3 frames -> 15 faults for FIFO.
    private static final int[] CLASSIC_REFS = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1};

    @Test
    void classicReferenceStringWithThreeFrames() {
        FifoAlgorithm algo = new FifoAlgorithm();
        SimulationResult result = algo.simulate(CLASSIC_REFS, 3);

        assertEquals(15, result.getPageFaults());
        assertEquals(5, result.getPageHits());
        assertEquals(20, result.getTotalReferences());
    }

    @Test
    void allDistinctPagesAlwaysFault() {
        FifoAlgorithm algo = new FifoAlgorithm();
        int[] refs = {1, 2, 3, 4, 5};
        SimulationResult result = algo.simulate(refs, 3);

        assertEquals(5, result.getPageFaults());
        assertEquals(0, result.getPageHits());
    }

    @Test
    void repeatedSinglePageNeverFaultsAfterFirst() {
        FifoAlgorithm algo = new FifoAlgorithm();
        int[] refs = {1, 1, 1, 1};
        SimulationResult result = algo.simulate(refs, 2);

        assertEquals(1, result.getPageFaults());
        assertEquals(3, result.getPageHits());
    }

    @Test
    void evictsOldestFirst() {
        FifoAlgorithm algo = new FifoAlgorithm();
        int[] refs = {1, 2, 3, 4}; // with 3 frames, page 1 should be evicted when 4 arrives
        SimulationResult result = algo.simulate(refs, 3);

        SimulationResult.StepSnapshot lastStep = result.getSteps().get(3);
        assertEquals(1, lastStep.evictedPage);
    }
}
