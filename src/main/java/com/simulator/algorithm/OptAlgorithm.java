package com.simulator.algorithm;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Optimal (a.k.a. Belady's / MIN) page replacement: evicts the resident page
 * whose next use lies furthest in the future (or is never used again). This
 * requires full knowledge of the reference string ahead of time and serves
 * as the theoretical lower bound on page faults for any online algorithm.
 */
public class OptAlgorithm implements PageReplacementAlgorithm {

    @Override
    public String getName() {
        return "OPT";
    }

    @Override
    public SimulationResult simulate(int[] referenceString, int numFrames) {
        SimulationResult result = new SimulationResult(getName());
        // LinkedHashSet just to keep a stable, deterministic iteration order for display.
        Set<Integer> resident = new LinkedHashSet<>();

        for (int i = 0; i < referenceString.length; i++) {
            int page = referenceString[i];
            boolean fault;
            int evicted = -1;

            if (resident.contains(page)) {
                fault = false;
            } else {
                fault = true;
                if (resident.size() >= numFrames) {
                    evicted = findVictim(resident, referenceString, i + 1);
                    resident.remove(evicted);
                }
                resident.add(page);
            }

            result.recordStep(i, page, fault, evicted, new ArrayList<>(resident));
        }
        return result;
    }

    /**
     * Among the currently resident pages, finds the one whose next
     * occurrence in {@code referenceString[fromIndex..]} is furthest away
     * (or absent entirely, which wins outright).
     */
    private int findVictim(Set<Integer> resident, int[] referenceString, int fromIndex) {
        int victim = -1;
        int furthestNextUse = -1;

        for (int candidate : resident) {
            int nextUse = Integer.MAX_VALUE;
            for (int j = fromIndex; j < referenceString.length; j++) {
                if (referenceString[j] == candidate) {
                    nextUse = j;
                    break;
                }
            }
            if (nextUse == Integer.MAX_VALUE) {
                // Never used again -> optimal victim, stop searching further.
                return candidate;
            }
            if (nextUse > furthestNextUse) {
                furthestNextUse = nextUse;
                victim = candidate;
            }
        }
        return victim;
    }
}
