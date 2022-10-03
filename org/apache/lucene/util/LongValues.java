package org.apache.lucene.util;

import org.apache.lucene.index.NumericDocValues;

public abstract class LongValues extends NumericDocValues
{
    public static final LongValues IDENTITY;
    
    public abstract long get(final long p0);
    
    @Override
    public long get(final int idx) {
        return this.get((long)idx);
    }
    
    static {
        IDENTITY = new LongValues() {
            @Override
            public long get(final long index) {
                return index;
            }
        };
    }
}
