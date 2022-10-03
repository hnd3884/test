package org.apache.lucene.search;

import org.apache.lucene.util.Accountables;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.RoaringDocIdSet;
import java.io.IOException;
import org.apache.lucene.index.LeafReader;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.Map;
import org.apache.lucene.util.Accountable;

@Deprecated
public class CachingWrapperFilter extends Filter implements Accountable
{
    private final Filter filter;
    private final FilterCachingPolicy policy;
    private final Map<Object, DocIdSet> cache;
    int hitCount;
    int missCount;
    
    public CachingWrapperFilter(final Filter filter, final FilterCachingPolicy policy) {
        this.cache = Collections.synchronizedMap(new WeakHashMap<Object, DocIdSet>());
        this.filter = filter;
        this.policy = policy;
    }
    
    public CachingWrapperFilter(final Filter filter) {
        this(filter, FilterCachingPolicy.CacheOnLargeSegments.DEFAULT);
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    protected DocIdSet docIdSetToCache(final DocIdSet docIdSet, final LeafReader reader) throws IOException {
        if (docIdSet == null || docIdSet.isCacheable()) {
            return docIdSet;
        }
        final DocIdSetIterator it = docIdSet.iterator();
        if (it == null) {
            return null;
        }
        return this.cacheImpl(it, reader);
    }
    
    protected DocIdSet cacheImpl(final DocIdSetIterator iterator, final LeafReader reader) throws IOException {
        return new RoaringDocIdSet.Builder(reader.maxDoc()).add(iterator).build();
    }
    
    @Override
    public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
        final LeafReader reader = context.reader();
        final Object key = reader.getCoreCacheKey();
        DocIdSet docIdSet = this.cache.get(key);
        if (docIdSet != null) {
            ++this.hitCount;
        }
        else {
            docIdSet = this.filter.getDocIdSet(context, null);
            if (this.policy.shouldCache(this.filter, context, docIdSet)) {
                ++this.missCount;
                docIdSet = this.docIdSetToCache(docIdSet, reader);
                if (docIdSet == null) {
                    docIdSet = DocIdSet.EMPTY;
                }
                assert docIdSet.isCacheable();
                this.cache.put(key, docIdSet);
            }
        }
        return (docIdSet == DocIdSet.EMPTY) ? null : BitsFilteredDocIdSet.wrap(docIdSet, acceptDocs);
    }
    
    @Override
    public String toString(final String field) {
        return this.getClass().getSimpleName() + "(" + this.filter.toString(field) + ")";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final CachingWrapperFilter other = (CachingWrapperFilter)o;
        return this.filter.equals(other.filter);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.filter.hashCode();
    }
    
    @Override
    public long ramBytesUsed() {
        final List<DocIdSet> docIdSets;
        synchronized (this.cache) {
            docIdSets = new ArrayList<DocIdSet>(this.cache.values());
        }
        long total = 0L;
        for (final DocIdSet dis : docIdSets) {
            total += dis.ramBytesUsed();
        }
        return total;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        synchronized (this.cache) {
            return Accountables.namedAccountables("segment", this.cache);
        }
    }
}
