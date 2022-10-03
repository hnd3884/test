package org.apache.lucene.search;

import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.Bits;
import java.io.IOException;
import org.apache.lucene.util.Accountable;

public abstract class DocIdSet implements Accountable
{
    public static final DocIdSet EMPTY;
    
    public abstract DocIdSetIterator iterator() throws IOException;
    
    public Bits bits() throws IOException {
        return null;
    }
    
    public boolean isCacheable() {
        return false;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    static {
        EMPTY = new DocIdSet() {
            @Override
            public DocIdSetIterator iterator() {
                return DocIdSetIterator.empty();
            }
            
            @Override
            public boolean isCacheable() {
                return true;
            }
            
            @Override
            public Bits bits() {
                return null;
            }
            
            @Override
            public long ramBytesUsed() {
                return 0L;
            }
        };
    }
}
