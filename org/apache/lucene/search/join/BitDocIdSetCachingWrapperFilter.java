package org.apache.lucene.search.join;

import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.index.LeafReader;
import java.util.Collections;
import java.util.WeakHashMap;
import org.apache.lucene.search.DocIdSet;
import java.util.Map;
import org.apache.lucene.search.Filter;

@Deprecated
public class BitDocIdSetCachingWrapperFilter extends BitDocIdSetFilter
{
    private final Filter filter;
    private final Map<Object, DocIdSet> cache;
    
    public BitDocIdSetCachingWrapperFilter(final Filter filter) {
        this.cache = Collections.synchronizedMap(new WeakHashMap<Object, DocIdSet>());
        this.filter = filter;
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    private BitDocIdSet docIdSetToCache(final DocIdSet docIdSet, final LeafReader reader) throws IOException {
        final DocIdSetIterator it = docIdSet.iterator();
        if (it == null) {
            return null;
        }
        final BitDocIdSet.Builder builder = new BitDocIdSet.Builder(reader.maxDoc());
        builder.or(it);
        return builder.build();
    }
    
    @Override
    public BitDocIdSet getDocIdSet(final LeafReaderContext context) throws IOException {
        final LeafReader reader = context.reader();
        final Object key = reader.getCoreCacheKey();
        DocIdSet docIdSet = this.cache.get(key);
        if (docIdSet == null) {
            docIdSet = this.filter.getDocIdSet(context, (Bits)null);
            docIdSet = (DocIdSet)this.docIdSetToCache(docIdSet, reader);
            if (docIdSet == null) {
                docIdSet = DocIdSet.EMPTY;
            }
            this.cache.put(key, docIdSet);
        }
        return (docIdSet == DocIdSet.EMPTY) ? null : ((BitDocIdSet)docIdSet);
    }
    
    public String toString(final String field) {
        return this.getClass().getSimpleName() + "(" + this.filter.toString(field) + ")";
    }
    
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final BitDocIdSetCachingWrapperFilter other = (BitDocIdSetCachingWrapperFilter)o;
        return this.filter.equals((Object)other.filter);
    }
    
    public int hashCode() {
        return 31 * super.hashCode() + this.filter.hashCode();
    }
}
