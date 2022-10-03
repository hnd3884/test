package org.apache.lucene.search.join;

import java.io.IOException;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.search.QueryCache;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Collections;
import java.util.WeakHashMap;
import org.apache.lucene.search.DocIdSet;
import java.util.Map;
import org.apache.lucene.search.Query;

public class QueryBitSetProducer implements BitSetProducer
{
    private final Query query;
    private final Map<Object, DocIdSet> cache;
    
    public QueryBitSetProducer(final Query query) {
        this.cache = Collections.synchronizedMap(new WeakHashMap<Object, DocIdSet>());
        this.query = query;
    }
    
    public Query getQuery() {
        return this.query;
    }
    
    @Override
    public BitSet getBitSet(final LeafReaderContext context) throws IOException {
        final LeafReader reader = context.reader();
        final Object key = reader.getCoreCacheKey();
        DocIdSet docIdSet = this.cache.get(key);
        if (docIdSet == null) {
            final IndexReaderContext topLevelContext = ReaderUtil.getTopLevelContext((IndexReaderContext)context);
            final IndexSearcher searcher = new IndexSearcher(topLevelContext);
            searcher.setQueryCache((QueryCache)null);
            final Weight weight = searcher.createNormalizedWeight(this.query, false);
            final Scorer s = weight.scorer(context);
            if (s == null) {
                docIdSet = DocIdSet.EMPTY;
            }
            else {
                docIdSet = (DocIdSet)new BitDocIdSet(BitSet.of(s.iterator(), context.reader().maxDoc()));
            }
            this.cache.put(key, docIdSet);
        }
        return (docIdSet == DocIdSet.EMPTY) ? null : ((BitDocIdSet)docIdSet).bits();
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + this.query.toString() + ")";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final QueryBitSetProducer other = (QueryBitSetProducer)o;
        return this.query.equals((Object)other.query);
    }
    
    @Override
    public int hashCode() {
        return 31 * this.getClass().hashCode() + this.query.hashCode();
    }
}
