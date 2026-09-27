package com.simulator.algorithm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LruAlgorithmTest {

    // Classic textbook reference string, 3 frames -> 12 faults for LRU.
    private static final int[] CLASSIC_REFS = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1};

    @Test
    void classicReferenceStringWithThreeFrames() {
        LruAlgorithm algo = new LruAlgorithm();
        SimulationResult result = algo.simulate(CLASSIC_REFS, 3);

        assertEquals(12, result.getPageFaults());
        assertEquals(8, result.getPageHits());
    }

    @Test
    void evictsLeastRecentlyUsedNotOldest() {
        LruAlgorithm algo = new LruAlgorithm();
        // Access 1,2,3 then touch 1 again (making 2 the LRU), then bring in 4.
        int[] refs = {1, 2, 3, 1, 4};
        SimulationResult result = algo.simulate(refs, 3);

        SimulationResult.StepSnapshot lastStep = result.getSteps().get(4);
        assertEquals(2, lastStep.evictedPage, "page 2 should be evicted because it's least recently used");
    }

    @Test
    void repeatedAccessDoesNotFault() {
        LruAlgorithm algo = new LruAlgorithm();
        int[] refs = {5, 5, 5};
        SimulationResult result = algo.simulate(refs, 1);

        assertEquals(1, result.getPageFaults());
        assertEquals(2, result.getPageHits());
    }
}
