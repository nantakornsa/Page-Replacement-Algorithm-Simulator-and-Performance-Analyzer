package com.simulator.algorithm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Least-Recently-Used page replacement: evicts the page whose most recent
 * access is furthest in the past. Implemented with a {@link LinkedHashMap}
 * in access order so the eldest entry is always the LRU candidate.
 */
public class LruAlgorithm implements PageReplacementAlgorithm {

    @Override
    public String getName() {
        return "LRU";
    }

    @Override
    public SimulationResult simulate(int[] referenceString, int numFrames) {
        SimulationResult result = new SimulationResult(getName());

        // accessOrder=true -> get()/put() move the entry to the end (most recently used)
        LinkedHashMap<Integer, Boolean> resident = new LinkedHashMap<>(numFrames, 0.75f, true);

        for (int i = 0; i < referenceString.length; i++) {
            int page = referenceString[i];
            boolean fault;
            int evicted = -1;

            if (resident.containsKey(page)) {
                resident.get(page); // touch -> marks as most recently used
                fault = false;
            } else {
                fault = true;
                if (resident.size() >= numFrames) {
                    Map.Entry<Integer, Boolean> eldest = resident.entrySet().iterator().next();
                    evicted = eldest.getKey();
                    resident.remove(evicted);
                }
                resident.put(page, Boolean.TRUE);
            }

            result.recordStep(i, page, fault, evicted, new ArrayList<>(resident.keySet()));
        }
        return result;
    }
}
