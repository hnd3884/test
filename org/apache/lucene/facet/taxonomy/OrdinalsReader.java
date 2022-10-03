package org.apache.lucene.facet.taxonomy;

import org.apache.lucene.util.IntsRef;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;

public abstract class OrdinalsReader
{
    public abstract OrdinalsSegmentReader getReader(final LeafReaderContext p0) throws IOException;
    
    public abstract String getIndexFieldName();
    
    public abstract static class OrdinalsSegmentReader
    {
        public abstract void get(final int p0, final IntsRef p1) throws IOException;
    }
}
