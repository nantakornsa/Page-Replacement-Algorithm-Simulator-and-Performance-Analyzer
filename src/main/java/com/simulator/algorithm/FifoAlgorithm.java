package com.simulator.algorithm;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * First-In-First-Out page replacement: evicts the page that has been
 * resident the longest, regardless of how recently it was used.
 */
public class FifoAlgorithm implements PageReplacementAlgorithm {

    @Override
    public String getName() {
        return "FIFO";
    }

    @Override
    public SimulationResult simulate(int[] referenceString, int numFrames) {
        SimulationResult result = new SimulationResult(getName());
        Deque<Integer> queue = new ArrayDeque<>();
        Set<Integer> resident = new HashSet<>();

        for (int i = 0; i < referenceString.length; i++) {
            int page = referenceString[i];
            boolean fault;
            int evicted = -1;

            if (resident.contains(page)) {
                fault = false;
            } else {
                fault = true;
                if (resident.size() >= numFrames) {
                    evicted = queue.poll();
                    resident.remove(evicted);
                }
                queue.offer(page);
                resident.add(page);
            }

            result.recordStep(i, page, fault, evicted, new ArrayList<>(queue));
        }
        return result;
    }
}
