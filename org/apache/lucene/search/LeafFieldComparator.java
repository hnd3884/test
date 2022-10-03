package org.apache.lucene.search;

import java.io.IOException;

public interface LeafFieldComparator
{
    void setBottom(final int p0);
    
    int compareBottom(final int p0) throws IOException;
    
    int compareTop(final int p0) throws IOException;
    
    void copy(final int p0, final int p1) throws IOException;
    
    void setScorer(final Scorer p0);
}
