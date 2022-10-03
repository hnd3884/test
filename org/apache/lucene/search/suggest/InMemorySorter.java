package org.apache.lucene.search.suggest;

import org.apache.lucene.util.BytesRefIterator;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.BytesRef;
import java.util.Comparator;
import org.apache.lucene.util.BytesRefArray;
import org.apache.lucene.search.suggest.fst.BytesRefSorter;

public final class InMemorySorter implements BytesRefSorter
{
    private final BytesRefArray buffer;
    private boolean closed;
    private final Comparator<BytesRef> comparator;
    
    public InMemorySorter(final Comparator<BytesRef> comparator) {
        this.buffer = new BytesRefArray(Counter.newCounter());
        this.closed = false;
        this.comparator = comparator;
    }
    
    @Override
    public void add(final BytesRef utf8) {
        if (this.closed) {
            throw new IllegalStateException();
        }
        this.buffer.append(utf8);
    }
    
    @Override
    public BytesRefIterator iterator() {
        this.closed = true;
        return this.buffer.iterator((Comparator)this.comparator);
    }
    
    @Override
    public Comparator<BytesRef> getComparator() {
        return this.comparator;
    }
}
