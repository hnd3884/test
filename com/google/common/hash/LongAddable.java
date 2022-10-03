package com.google.common.hash;

@ElementTypesAreNonnullByDefault
interface LongAddable
{
    void increment();
    
    void add(final long p0);
    
    long sum();
}
