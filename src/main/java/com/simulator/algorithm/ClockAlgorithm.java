package com.simulator.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Clock (Second-Chance) page replacement: an efficient approximation of LRU.
 * Frames are arranged in a circular buffer with a "hand" pointer and a
 * reference bit per frame. On a fault, the hand scans forward; any frame
 * with reference bit 1 is given a second chance (bit cleared) and skipped,
 * the first frame found with bit 0 is evicted.
 */
public class ClockAlgorithm implements PageReplacementAlgorithm {

    @Override
    public String getName() {
        return "CLOCK";
    }

    @Override
    public SimulationResult simulate(int[] referenceString, int numFrames) {
        SimulationResult result = new SimulationResult(getName());

        int[] frames = new int[numFrames];
        boolean[] referenceBit = new boolean[numFrames];
        boolean[] occupied = new boolean[numFrames];
        int hand = 0;
        int filled = 0;

        for (int i = 0; i < referenceString.length; i++) {
            int page = referenceString[i];
            boolean fault;
            int evicted = -1;

            int slot = indexOf(frames, occupied, page);
            if (slot >= 0) {
                fault = false;
                referenceBit[slot] = true;
            } else {
                fault = true;
                if (filled < numFrames) {
                    // Still room: place in the next free slot.
                    frames[filled] = page;
                    occupied[filled] = true;
                    referenceBit[filled] = true;
                    filled++;
                } else {
                    // Sweep the clock hand until we find a victim (ref bit == 0).
                    while (referenceBit[hand]) {
                        referenceBit[hand] = false; // give second chance
                        hand = (hand + 1) % numFrames;
                    }
                    evicted = frames[hand];
                    frames[hand] = page;
                    referenceBit[hand] = true;
                    hand = (hand + 1) % numFrames;
                }
            }

            result.recordStep(i, page, fault, evicted, snapshot(frames, occupied));
        }
        return result;
    }

    private int indexOf(int[] frames, boolean[] occupied, int page) {
        for (int i = 0; i < frames.length; i++) {
            if (occupied[i] && frames[i] == page) {
                return i;
            }
        }
        return -1;
    }

    private List<Integer> snapshot(int[] frames, boolean[] occupied) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < frames.length; i++) {
            if (occupied[i]) {
                list.add(frames[i]);
            }
        }
        return list;
    }
}
