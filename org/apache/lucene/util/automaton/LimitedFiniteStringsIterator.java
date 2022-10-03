package org.apache.lucene.util.automaton;

import org.apache.lucene.util.IntsRef;

public class LimitedFiniteStringsIterator extends FiniteStringsIterator
{
    private int limit;
    private int count;
    
    public LimitedFiniteStringsIterator(final Automaton a, final int limit) {
        super(a);
        this.limit = Integer.MAX_VALUE;
        this.count = 0;
        if (limit != -1 && limit <= 0) {
            throw new IllegalArgumentException("limit must be -1 (which means no limit), or > 0; got: " + limit);
        }
        this.limit = ((limit > 0) ? limit : Integer.MAX_VALUE);
    }
    
    @Override
    public IntsRef next() {
        if (this.count >= this.limit) {
            return null;
        }
        final IntsRef result = super.next();
        if (result != null) {
            ++this.count;
        }
        return result;
    }
    
    public int size() {
        return this.count;
    }
}
