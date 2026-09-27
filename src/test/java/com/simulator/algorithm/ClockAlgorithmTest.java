package com.simulator.algorithm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClockAlgorithmTest {

    private static final int[] CLASSIC_REFS = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1};

    @Test
    void classicReferenceStringProducesReasonableFaultCount() {
        ClockAlgorithm algo = new ClockAlgorithm();
        SimulationResult result = algo.simulate(CLASSIC_REFS, 3);

        // Clock approximates LRU: bounded between OPT's minimum and FIFO's count for this trace.
        assertTrue(result.getPageFaults() >= 9);
        assertTrue(result.getPageFaults() <= 15);
        assertEquals(20, result.getTotalReferences());
    }

    @Test
    void allDistinctPagesAlwaysFault() {
        ClockAlgorithm algo = new ClockAlgorithm();
        int[] refs = {1, 2, 3, 4, 5};
        SimulationResult result = algo.simulate(refs, 3);

        assertEquals(5, result.getPageFaults());
    }

    @Test
    void secondChanceSweepsFullCircleWhenAllBitsAreSet() {
        ClockAlgorithm algo = new ClockAlgorithm();
        // Fill frames with 1,2,3 (each gets ref bit set on load). Re-reference 1
        // (ref bit already set, stays set). Bring in 4: every resident page still
        // has its ref bit set, so the hand sweeps a full circle clearing all three
        // bits and lands back on slot 0 (page 1), which becomes the victim.
        int[] refs = {1, 2, 3, 1, 4};
        SimulationResult result = algo.simulate(refs, 3);

        SimulationResult.StepSnapshot lastStep = result.getSteps().get(4);
        assertEquals(1, lastStep.evictedPage);
    }

    @Test
    void secondChanceProtectsRecentlyReferencedPage() {
        ClockAlgorithm algo = new ClockAlgorithm();
        // Fill frames 1,2,3, then let bits 1 and 3's bits get cleared by an
        // intervening fault (page 4 evicts page 1's slot, sweeping the hand and
        // clearing 2's and 3's bits along the way except the one it re-sets).
        // Then re-reference page 2 so only its bit is set again, and bring in 5:
        // the hand should skip page 2 (protected) and evict page 3 instead.
        int[] refs = {1, 2, 3, 4, 2, 5};
        SimulationResult result = algo.simulate(refs, 3);

        SimulationResult.StepSnapshot lastStep = result.getSteps().get(5);
        assertEquals(3, lastStep.evictedPage);
    }

    @Test
    void repeatedAccessDoesNotFault() {
        ClockAlgorithm algo = new ClockAlgorithm();
        int[] refs = {5, 5, 5};
        SimulationResult result = algo.simulate(refs, 1);

        assertEquals(1, result.getPageFaults());
        assertEquals(2, result.getPageHits());
    }
}
