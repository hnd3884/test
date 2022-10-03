package org.apache.lucene.search.highlight;

import org.apache.lucene.util.PriorityQueue;

class FragmentQueue extends PriorityQueue<TextFragment>
{
    public FragmentQueue(final int size) {
        super(size);
    }
    
    public final boolean lessThan(final TextFragment fragA, final TextFragment fragB) {
        if (fragA.getScore() == fragB.getScore()) {
            return fragA.fragNum > fragB.fragNum;
        }
        return fragA.getScore() < fragB.getScore();
    }
}
