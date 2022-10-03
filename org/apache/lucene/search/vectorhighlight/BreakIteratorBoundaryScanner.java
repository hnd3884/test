package org.apache.lucene.search.vectorhighlight;

import java.text.BreakIterator;

public class BreakIteratorBoundaryScanner implements BoundaryScanner
{
    final BreakIterator bi;
    
    public BreakIteratorBoundaryScanner(final BreakIterator bi) {
        this.bi = bi;
    }
    
    @Override
    public int findStartOffset(final StringBuilder buffer, final int start) {
        if (start > buffer.length() || start < 1) {
            return start;
        }
        this.bi.setText(buffer.substring(0, start));
        this.bi.last();
        return this.bi.previous();
    }
    
    @Override
    public int findEndOffset(final StringBuilder buffer, final int start) {
        if (start > buffer.length() || start < 0) {
            return start;
        }
        this.bi.setText(buffer.substring(start));
        return this.bi.next() + start;
    }
}
