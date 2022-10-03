package org.apache.lucene.facet;

import org.apache.lucene.util.PriorityQueue;

public class TopOrdAndIntQueue extends PriorityQueue<OrdAndValue>
{
    public TopOrdAndIntQueue(final int topN) {
        super(topN, false);
    }
    
    protected boolean lessThan(final OrdAndValue a, final OrdAndValue b) {
        return a.value < b.value || (a.value <= b.value && a.ord > b.ord);
    }
    
    public static final class OrdAndValue
    {
        public int ord;
        public int value;
    }
}
