package org.antlr.v4.runtime.misc;

import java.util.List;

public interface IntSet
{
    void add(final int p0);
    
    IntSet addAll(final IntSet p0);
    
    IntSet and(final IntSet p0);
    
    IntSet complement(final IntSet p0);
    
    IntSet or(final IntSet p0);
    
    IntSet subtract(final IntSet p0);
    
    int size();
    
    boolean isNil();
    
    boolean equals(final Object p0);
    
    int getSingleElement();
    
    boolean contains(final int p0);
    
    void remove(final int p0);
    
    List<Integer> toList();
    
    String toString();
}
