package com.simulator.algorithm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptAlgorithmTest {

    // Classic textbook reference string, 3 frames -> 9 faults for OPT (the theoretical minimum).
    private static final int[] CLASSIC_REFS = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1};

    @Test
    void classicReferenceStringWithThreeFrames() {
        OptAlgorithm algo = new OptAlgorithm();
        SimulationResult result = algo.simulate(CLASSIC_REFS, 3);

        assertEquals(9, result.getPageFaults());
        assertEquals(11, result.getPageHits());
    }

    @Test
    void optIsNeverWorseThanFifoOrLru() {
        OptAlgorithm opt = new OptAlgorithm();
        FifoAlgorithm fifo = new FifoAlgorithm();
        LruAlgorithm lru = new LruAlgorithm();

        SimulationResult optResult = opt.simulate(CLASSIC_REFS, 3);
        SimulationResult fifoResult = fifo.simulate(CLASSIC_REFS, 3);
        SimulationResult lruResult = lru.simulate(CLASSIC_REFS, 3);

        assertTrue(optResult.getPageFaults() <= fifoResult.getPageFaults());
        assertTrue(optResult.getPageFaults() <= lruResult.getPageFaults());
    }

    @Test
    void evictsPageUsedFurthestInFuture() {
        OptAlgorithm algo = new OptAlgorithm();
        // With frames {1,2,3} full, page 2 is used again at index 5 while 1 and 3
        // are used at indices 4 and 6 respectively... construct a clear case instead:
        // Resident {1,2,3}; next reference is 4. Future uses: 1 -> never again, 2 -> idx 5, 3 -> idx 4.
        int[] refs = {1, 2, 3, 4, 3, 2};
        SimulationResult result = algo.simulate(refs, 3);

        SimulationResult.StepSnapshot faultStep = result.getSteps().get(3); // reference to page 4
        assertEquals(1, faultStep.evictedPage, "page 1 is never referenced again, so it's the optimal victim");
    }
}
