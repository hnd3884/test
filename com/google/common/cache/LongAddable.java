package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
interface LongAddable
{
    void increment();
    
    void add(final long p0);
    
    long sum();
}
