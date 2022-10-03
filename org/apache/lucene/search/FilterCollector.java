package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;

public class FilterCollector implements Collector
{
    protected final Collector in;
    
    public FilterCollector(final Collector in) {
        this.in = in;
    }
    
    @Override
    public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
        return this.in.getLeafCollector(context);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + this.in + ")";
    }
    
    @Override
    public boolean needsScores() {
        return this.in.needsScores();
    }
}
