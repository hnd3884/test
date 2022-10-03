package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;

public abstract class SimpleFieldComparator<T> extends FieldComparator<T> implements LeafFieldComparator
{
    protected abstract void doSetNextReader(final LeafReaderContext p0) throws IOException;
    
    @Override
    public final LeafFieldComparator getLeafComparator(final LeafReaderContext context) throws IOException {
        this.doSetNextReader(context);
        return this;
    }
    
    @Override
    public void setScorer(final Scorer scorer) {
    }
}
