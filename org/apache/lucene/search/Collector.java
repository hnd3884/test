package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;

public interface Collector
{
    LeafCollector getLeafCollector(final LeafReaderContext p0) throws IOException;
    
    boolean needsScores();
}
