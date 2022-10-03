package org.apache.lucene.facet.sortedset;

import org.apache.lucene.index.IndexReader;
import java.util.Map;
import java.io.IOException;
import org.apache.lucene.index.SortedSetDocValues;

public abstract class SortedSetDocValuesReaderState
{
    protected SortedSetDocValuesReaderState() {
    }
    
    public abstract SortedSetDocValues getDocValues() throws IOException;
    
    public abstract String getField();
    
    public abstract OrdRange getOrdRange(final String p0);
    
    public abstract Map<String, OrdRange> getPrefixToOrdRange();
    
    public abstract IndexReader getOrigReader();
    
    public abstract int getSize();
    
    public static final class OrdRange
    {
        public final int start;
        public final int end;
        
        public OrdRange(final int start, final int end) {
            this.start = start;
            this.end = end;
        }
    }
}
