package org.apache.lucene.search.highlight;

public class PositionSpan
{
    int start;
    int end;
    
    public PositionSpan(final int start, final int end) {
        this.start = start;
        this.end = end;
    }
}
