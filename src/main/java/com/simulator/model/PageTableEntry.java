package com.simulator.model;

/**
 * Represents one row of a page table: whether the page is currently resident
 * (valid bit), which frame it occupies, and its reference bit (used by the
 * page-replacement bookkeeping).
 */
public class PageTableEntry {

    private boolean validBit;
    private boolean referenceBit;
    private int frameNumber;

    public PageTableEntry() {
        this.validBit = false;
        this.referenceBit = false;
        this.frameNumber = -1;
    }

    public boolean isValid() {
        return validBit;
    }

    public void setValid(boolean valid) {
        this.validBit = valid;
    }

    public boolean isReferenced() {
        return referenceBit;
    }

    public void setReferenced(boolean referenced) {
        this.referenceBit = referenced;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public void invalidate() {
        this.validBit = false;
        this.referenceBit = false;
        this.frameNumber = -1;
    }

    @Override
    public String toString() {
        return "PTE{valid=" + validBit + ", ref=" + referenceBit + ", frame=" + frameNumber + "}";
    }
}
