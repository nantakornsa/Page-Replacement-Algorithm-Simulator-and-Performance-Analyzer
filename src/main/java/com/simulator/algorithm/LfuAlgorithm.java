package com.simulator.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Least-Frequently-Used replacement. On a tie, evicts the resident page that
 * entered memory earliest (FIFO tie-breaker), making results deterministic.
 */
public class LfuAlgorithm implements PageReplacementAlgorithm {

    @Override
    public String getName() {
        return "LFU";
    }

    @Override
    public SimulationResult simulate(int[] referenceString, int numFrames) {
        if (numFrames <= 0) {
            throw new IllegalArgumentException("numFrames must be positive");
        }
        SimulationResult result = new SimulationResult(getName());
        List<Integer> resident = new ArrayList<>();
        Map<Integer, Integer> frequency = new HashMap<>();

        for (int i = 0; i < referenceString.length; i++) {
            int page = referenceString[i];
            boolean fault = !resident.contains(page);
            int evicted = -1;

            if (fault) {
                if (resident.size() == numFrames) {
                    int victimIndex = 0;
                    for (int j = 1; j < resident.size(); j++) {
                        if (frequency.get(resident.get(j)) < frequency.get(resident.get(victimIndex))) {
                            victimIndex = j;
                        }
                    }
                    evicted = resident.remove(victimIndex);
                    frequency.remove(evicted);
                }
                resident.add(page);
                frequency.put(page, 1);
            } else {
                frequency.put(page, frequency.get(page) + 1);
            }

            result.recordStep(i, page, fault, evicted, resident);
        }
        return result;
    }
}
