package org.apache.lucene.index;

import java.io.IOException;
import java.util.Iterator;

public abstract class Fields implements Iterable<String>
{
    public static final Fields[] EMPTY_ARRAY;
    
    protected Fields() {
    }
    
    @Override
    public abstract Iterator<String> iterator();
    
    public abstract Terms terms(final String p0) throws IOException;
    
    public abstract int size();
    
    static {
        EMPTY_ARRAY = new Fields[0];
    }
}
