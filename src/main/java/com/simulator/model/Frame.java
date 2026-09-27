package com.simulator.model;

/**
 * Represents a physical memory frame that may currently hold a {@link Page}.
 */
public class Frame {

    private final int frameId;
    private Page currentPage;
    private boolean occupied;
    private long lastLoadedTick;

    public Frame(int frameId) {
        this.frameId = frameId;
        this.occupied = false;
    }

    public int getFrameId() {
        return frameId;
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public long getLastLoadedTick() {
        return lastLoadedTick;
    }

    /**
     * Loads a page into this frame, recording the logical time (tick) at which it happened.
     */
    public void load(Page page, long tick) {
        this.currentPage = page;
        this.occupied = true;
        this.lastLoadedTick = tick;
    }

    public void clear() {
        this.currentPage = null;
        this.occupied = false;
    }

    @Override
    public String toString() {
        return "Frame{" + frameId + " -> " + (occupied ? currentPage : "empty") + "}";
    }
}
