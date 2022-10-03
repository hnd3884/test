package org.apache.lucene.search;

import org.apache.lucene.util.PriorityQueue;

final class PhraseQueue extends PriorityQueue<PhrasePositions>
{
    PhraseQueue(final int size) {
        super(size);
    }
    
    @Override
    protected final boolean lessThan(final PhrasePositions pp1, final PhrasePositions pp2) {
        if (pp1.position != pp2.position) {
            return pp1.position < pp2.position;
        }
        if (pp1.offset == pp2.offset) {
            return pp1.ord < pp2.ord;
        }
        return pp1.offset < pp2.offset;
    }
}
