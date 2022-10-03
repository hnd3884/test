package org.apache.lucene.search.spans;

import org.apache.lucene.util.PriorityQueue;

class SpanPositionQueue extends PriorityQueue<Spans>
{
    SpanPositionQueue(final int maxSize) {
        super(maxSize, false);
    }
    
    @Override
    protected boolean lessThan(final Spans s1, final Spans s2) {
        final int start1 = s1.startPosition();
        final int start2 = s2.startPosition();
        return start1 < start2 || (start1 == start2 && s1.endPosition() < s2.endPosition());
    }
}
