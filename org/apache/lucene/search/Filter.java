package org.apache.lucene.search;

import org.apache.lucene.index.IndexReader;
import java.io.IOException;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;

@Deprecated
public abstract class Filter extends Query
{
    public abstract DocIdSet getDocIdSet(final LeafReaderContext p0, final Bits p1) throws IOException;
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        return FilteredQuery.RANDOM_ACCESS_FILTER_STRATEGY.rewrite(this);
    }
}
