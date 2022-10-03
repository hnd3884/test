package org.apache.lucene.search;

import org.apache.lucene.util.Accountables;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.lucene.index.IndexReader;
import java.io.IOException;
import org.apache.lucene.util.RoaringDocIdSet;
import org.apache.lucene.index.LeafReader;
import java.util.Objects;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.Map;
import org.apache.lucene.util.Accountable;

@Deprecated
public class CachingWrapperQuery extends Query implements Accountable, Cloneable
{
    private Query query;
    private final QueryCachingPolicy policy;
    private final Map<Object, DocIdSet> cache;
    int hitCount;
    int missCount;
    
    public CachingWrapperQuery(final Query query, final QueryCachingPolicy policy) {
        this.cache = Collections.synchronizedMap(new WeakHashMap<Object, DocIdSet>());
        this.query = Objects.requireNonNull(query, "Query must not be null");
        this.policy = Objects.requireNonNull(policy, "QueryCachingPolicy must not be null");
    }
    
    public CachingWrapperQuery(final Query query) {
        this(query, QueryCachingPolicy.CacheOnLargeSegments.DEFAULT);
    }
    
    public Query getQuery() {
        return this.query;
    }
    
    protected DocIdSet cacheImpl(final DocIdSetIterator iterator, final LeafReader reader) throws IOException {
        return new RoaringDocIdSet.Builder(reader.maxDoc()).add(iterator).build();
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final Query rewritten = this.query.rewrite(reader);
        if (this.query == rewritten) {
            return super.rewrite(reader);
        }
        final CachingWrapperQuery clone = (CachingWrapperQuery)this.clone();
        clone.query = rewritten;
        return clone;
    }
    
    @Override
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final Weight weight = this.query.createWeight(searcher, needsScores);
        if (needsScores) {
            return weight;
        }
        return new ConstantScoreWeight(weight.getQuery()) {
            final AtomicBoolean used = new AtomicBoolean(false);
            
            @Override
            public void extractTerms(final Set<Term> terms) {
                weight.extractTerms(terms);
            }
            
            @Override
            public Scorer scorer(final LeafReaderContext context) throws IOException {
                if (this.used.compareAndSet(false, true)) {
                    CachingWrapperQuery.this.policy.onUse(this.getQuery());
                }
                final LeafReader reader = context.reader();
                final Object key = reader.getCoreCacheKey();
                DocIdSet docIdSet = CachingWrapperQuery.this.cache.get(key);
                if (docIdSet != null) {
                    final CachingWrapperQuery this$0 = CachingWrapperQuery.this;
                    ++this$0.hitCount;
                }
                else {
                    if (!CachingWrapperQuery.this.policy.shouldCache(CachingWrapperQuery.this.query, context)) {
                        return weight.scorer(context);
                    }
                    final CachingWrapperQuery this$2 = CachingWrapperQuery.this;
                    ++this$2.missCount;
                    final Scorer scorer = weight.scorer(context);
                    if (scorer == null) {
                        docIdSet = DocIdSet.EMPTY;
                    }
                    else {
                        docIdSet = CachingWrapperQuery.this.cacheImpl(scorer.iterator(), context.reader());
                    }
                    CachingWrapperQuery.this.cache.put(key, docIdSet);
                }
                assert docIdSet != null;
                if (docIdSet == DocIdSet.EMPTY) {
                    return null;
                }
                final DocIdSetIterator disi = docIdSet.iterator();
                if (disi == null) {
                    return null;
                }
                return new ConstantScoreScorer(this, 0.0f, disi);
            }
        };
    }
    
    @Override
    public String toString(final String field) {
        return this.getClass().getSimpleName() + "(" + this.query.toString(field) + ")";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final CachingWrapperQuery other = (CachingWrapperQuery)o;
        return this.query.equals(other.query);
    }
    
    @Override
    public int hashCode() {
        return this.query.hashCode() ^ super.hashCode();
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
