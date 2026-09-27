package com.simulator.model;

import java.util.Objects;

/**
 * Represents a single virtual memory page identified by its page number.
 */
public class Page {

    private final int pageNumber;

    public Page(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Page)) return false;
        Page page = (Page) o;
        return pageNumber == page.pageNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNumber);
    }

    @Override
    public String toString() {
        return "Page{" + pageNumber + "}";
    }
}
