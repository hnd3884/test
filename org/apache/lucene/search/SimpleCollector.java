package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;

public abstract class SimpleCollector implements Collector, LeafCollector
{
    @Override
    public final LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
        this.doSetNextReader(context);
        return this;
    }
    
    protected void doSetNextReader(final LeafReaderContext context) throws IOException {
    }
    
    @Override
    public void setScorer(final Scorer scorer) throws IOException {
    }
    
    @Override
    public abstract void collect(final int p0) throws IOException;
}
