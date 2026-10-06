package com.simulator.algorithm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LfuAlgorithmTest {

    @Test
    void countsHitsAndFaults() {
        int[] refs = {1, 2, 1, 3, 1, 2, 4};
        SimulationResult result = new LfuAlgorithm().simulate(refs, 3);

        assertEquals(4, result.getPageFaults());
        assertEquals(3, result.getPageHits());
    }

    @Test
    void evictsLeastFrequentAndBreaksTiesByOldestResident() {
        int[] refs = {1, 2, 3, 1, 2, 4};
        SimulationResult result = new LfuAlgorithm().simulate(refs, 3);

        assertEquals(3, result.getSteps().get(5).evictedPage);
    }

    @Test
    void handlesSingleFrame() {
        SimulationResult result = new LfuAlgorithm().simulate(new int[]{1, 1, 2, 2}, 1);

        assertEquals(2, result.getPageFaults());
        assertEquals(2, result.getPageHits());
    }
}
