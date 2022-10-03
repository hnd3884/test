package com.adventnet.ds.query;

import java.io.Serializable;

public class Range implements Serializable, Cloneable
{
    private static final long serialVersionUID = 8207834695608535417L;
    private int startIndex;
    private int numOfObjects;
    public static final int LAST = -99;
    
    public Range(final int startIndex, final int numOfObjects) {
        this.startIndex = startIndex;
        this.numOfObjects = numOfObjects;
    }
    
    public int getStartIndex() {
        return this.startIndex;
    }
    
    public int getNumberOfObjects() {
        return this.numOfObjects;
    }
    
    @Override
    public String toString() {
        return "StartIndex=" + this.startIndex + ":Number of objects=" + this.numOfObjects;
    }
    
    public Object clone() {
        return this;
    }
    
    @Override
    public int hashCode() {
        return this.startIndex + this.numOfObjects;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Range)) {
            return false;
        }
        final Range range = (Range)obj;
        return this.startIndex == range.startIndex && this.numOfObjects == range.numOfObjects;
    }
}
