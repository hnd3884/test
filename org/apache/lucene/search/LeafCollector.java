package org.apache.lucene.search;

import java.io.IOException;

public interface LeafCollector
{
    void setScorer(final Scorer p0) throws IOException;
    
    void collect(final int p0) throws IOException;
}
