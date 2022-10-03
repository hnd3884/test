package org.apache.lucene.search.spell;

import java.util.Comparator;
import org.apache.lucene.util.PriorityQueue;

public final class SuggestWordQueue extends PriorityQueue<SuggestWord>
{
    public static final Comparator<SuggestWord> DEFAULT_COMPARATOR;
    private Comparator<SuggestWord> comparator;
    
    public SuggestWordQueue(final int size) {
        super(size);
        this.comparator = SuggestWordQueue.DEFAULT_COMPARATOR;
    }
    
    public SuggestWordQueue(final int size, final Comparator<SuggestWord> comparator) {
        super(size);
        this.comparator = comparator;
    }
    
    protected final boolean lessThan(final SuggestWord wa, final SuggestWord wb) {
        final int val = this.comparator.compare(wa, wb);
        return val < 0;
    }
    
    static {
        DEFAULT_COMPARATOR = new SuggestWordScoreComparator();
    }
}
