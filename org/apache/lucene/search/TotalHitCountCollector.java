package org.apache.lucene.search;

public class TotalHitCountCollector extends SimpleCollector
{
    private int totalHits;
    
    public int getTotalHits() {
        return this.totalHits;
    }
    
    @Override
    public void collect(final int doc) {
        ++this.totalHits;
    }
    
    @Override
    public boolean needsScores() {
        return false;
    }
}
