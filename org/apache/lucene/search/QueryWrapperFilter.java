package org.apache.lucene.search;

import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;

@Deprecated
public class QueryWrapperFilter extends Filter
{
    private final Query query;
    
    public QueryWrapperFilter(final Query query) {
        if (query == null) {
            throw new NullPointerException("Query may not be null");
        }
        this.query = query;
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        return new BoostQuery(new ConstantScoreQuery(this.query), 0.0f);
    }
    
    public final Query getQuery() {
        return this.query;
    }
    
    @Override
    public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
        final LeafReaderContext privateContext = context.reader().getContext();
        final Weight weight = new IndexSearcher(privateContext).createNormalizedWeight(this.query, false);
        final DocIdSet set = new DocIdSet() {
            @Override
            public DocIdSetIterator iterator() throws IOException {
                final Scorer s = weight.scorer(privateContext);
                return (s == null) ? null : s.iterator();
            }
            
            @Override
            public long ramBytesUsed() {
                return 0L;
            }
        };
        return BitsFilteredDocIdSet.wrap(set, acceptDocs);
    }
    
    @Override
    public String toString(final String field) {
        return "QueryWrapperFilter(" + this.query.toString(field) + ")";
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && this.query.equals(((QueryWrapperFilter)o).query);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.query.hashCode();
    }
}
