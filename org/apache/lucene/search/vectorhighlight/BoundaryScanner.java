package org.apache.lucene.search.vectorhighlight;

public interface BoundaryScanner
{
    int findStartOffset(final StringBuilder p0, final int p1);
    
    int findEndOffset(final StringBuilder p0, final int p1);
}
