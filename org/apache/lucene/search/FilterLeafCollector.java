package org.apache.lucene.search;

import java.io.IOException;

public class FilterLeafCollector implements LeafCollector
{
    protected final LeafCollector in;
    
    public FilterLeafCollector(final LeafCollector in) {
        this.in = in;
    }
    
    @Override
    public void setScorer(final Scorer scorer) throws IOException {
        this.in.setScorer(scorer);
    }
    
    @Override
    public void collect(final int doc) throws IOException {
        this.in.collect(doc);
    }
    
    @Override
    public String toString() {
        String name = this.getClass().getSimpleName();
        if (name.length() == 0) {
            name = "FilterLeafCollector";
        }
        return name + "(" + this.in + ")";
    }
}
