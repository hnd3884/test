package org.apache.lucene.search;

import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.ReaderUtil;
import java.util.concurrent.ExecutionException;
import org.apache.lucene.util.ThreadInterruptedException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.lucene.index.Term;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import org.apache.lucene.index.StoredFieldVisitor;
import java.io.IOException;
import org.apache.lucene.document.Document;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import org.apache.lucene.index.LeafReaderContext;
import java.util.List;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.similarities.Similarity;

public class IndexSearcher
{
    private static final Similarity NON_SCORING_SIMILARITY;
    private static QueryCache DEFAULT_QUERY_CACHE;
    private static QueryCachingPolicy DEFAULT_CACHING_POLICY;
    final IndexReader reader;
    protected final IndexReaderContext readerContext;
    protected final List<LeafReaderContext> leafContexts;
    protected final LeafSlice[] leafSlices;
    private final ExecutorService executor;
    private static final Similarity defaultSimilarity;
    private QueryCache queryCache;
    private QueryCachingPolicy queryCachingPolicy;
    private Similarity similarity;
    
    public static Similarity getDefaultSimilarity() {
        return IndexSearcher.defaultSimilarity;
    }
    
    public static QueryCache getDefaultQueryCache() {
        return IndexSearcher.DEFAULT_QUERY_CACHE;
    }
    
    public static void setDefaultQueryCache(final QueryCache defaultQueryCache) {
        IndexSearcher.DEFAULT_QUERY_CACHE = defaultQueryCache;
    }
    
    public static QueryCachingPolicy getDefaultQueryCachingPolicy() {
        return IndexSearcher.DEFAULT_CACHING_POLICY;
    }
    
    public static void setDefaultQueryCachingPolicy(final QueryCachingPolicy defaultQueryCachingPolicy) {
        IndexSearcher.DEFAULT_CACHING_POLICY = defaultQueryCachingPolicy;
    }
    
    public IndexSearcher(final IndexReader r) {
        this(r, null);
    }
    
    public IndexSearcher(final IndexReader r, final ExecutorService executor) {
        this(r.getContext(), executor);
    }
    
    public IndexSearcher(final IndexReaderContext context, final ExecutorService executor) {
        this.queryCache = IndexSearcher.DEFAULT_QUERY_CACHE;
        this.queryCachingPolicy = IndexSearcher.DEFAULT_CACHING_POLICY;
        this.similarity = IndexSearcher.defaultSimilarity;
        assert context.isTopLevel : "IndexSearcher's ReaderContext must be topLevel for reader" + context.reader();
        this.reader = context.reader();
        this.executor = executor;
        this.readerContext = context;
        this.leafContexts = context.leaves();
        this.leafSlices = (LeafSlice[])((executor == null) ? null : this.slices(this.leafContexts));
    }
    
    public IndexSearcher(final IndexReaderContext context) {
        this(context, null);
    }
    
    public void setQueryCache(final QueryCache queryCache) {
        this.queryCache = queryCache;
    }
    
    public QueryCache getQueryCache() {
        return this.queryCache;
    }
    
    public void setQueryCachingPolicy(final QueryCachingPolicy queryCachingPolicy) {
        this.queryCachingPolicy = Objects.requireNonNull(queryCachingPolicy);
    }
    
    public QueryCachingPolicy getQueryCachingPolicy() {
        return this.queryCachingPolicy;
    }
    
    protected LeafSlice[] slices(final List<LeafReaderContext> leaves) {
        final LeafSlice[] slices = new LeafSlice[leaves.size()];
        for (int i = 0; i < slices.length; ++i) {
            slices[i] = new LeafSlice(new LeafReaderContext[] { leaves.get(i) });
        }
        return slices;
    }
    
    public IndexReader getIndexReader() {
        return this.reader;
    }
    
    public Document doc(final int docID) throws IOException {
        return this.reader.document(docID);
    }
    
    public void doc(final int docID, final StoredFieldVisitor fieldVisitor) throws IOException {
        this.reader.document(docID, fieldVisitor);
    }
    
    public Document doc(final int docID, final Set<String> fieldsToLoad) throws IOException {
        return this.reader.document(docID, fieldsToLoad);
    }
    
    public void setSimilarity(final Similarity similarity) {
        this.similarity = similarity;
    }
    
    public Similarity getSimilarity(final boolean needsScores) {
        return needsScores ? this.similarity : IndexSearcher.NON_SCORING_SIMILARITY;
    }
    
    @Deprecated
    protected Query wrapFilter(final Query query, final Filter filter) {
        return (filter == null) ? query : new FilteredQuery(query, filter);
    }
    
    public int count(Query query) throws IOException {
        for (query = this.rewrite(query); query instanceof ConstantScoreQuery; query = ((ConstantScoreQuery)query).getQuery()) {}
        if (query instanceof MatchAllDocsQuery) {
            return this.reader.numDocs();
        }
        if (query instanceof TermQuery && !this.reader.hasDeletions()) {
            final Term term = ((TermQuery)query).getTerm();
            int count = 0;
            for (final LeafReaderContext leaf : this.reader.leaves()) {
                count += leaf.reader().docFreq(term);
            }
            return count;
        }
        final CollectorManager<TotalHitCountCollector, Integer> collectorManager = new CollectorManager<TotalHitCountCollector, Integer>() {
            @Override
            public TotalHitCountCollector newCollector() throws IOException {
                return new TotalHitCountCollector();
            }
            
            @Override
            public Integer reduce(final Collection<TotalHitCountCollector> collectors) throws IOException {
                int total = 0;
                for (final TotalHitCountCollector collector : collectors) {
                    total += collector.getTotalHits();
                }
                return total;
            }
        };
        return this.search(query, collectorManager);
    }
    
    public TopDocs searchAfter(final ScoreDoc after, final Query query, int numHits) throws IOException {
        final int limit = Math.max(1, this.reader.maxDoc());
        if (after != null && after.doc >= limit) {
            throw new IllegalArgumentException("after.doc exceeds the number of documents in the reader: after.doc=" + after.doc + " limit=" + limit);
        }
        numHits = Math.min(numHits, limit);
        final int cappedNumHits = Math.min(numHits, limit);
        final CollectorManager<TopScoreDocCollector, TopDocs> manager = new CollectorManager<TopScoreDocCollector, TopDocs>() {
            @Override
            public TopScoreDocCollector newCollector() throws IOException {
                return TopScoreDocCollector.create(cappedNumHits, after);
            }
            
            @Override
            public TopDocs reduce(final Collection<TopScoreDocCollector> collectors) throws IOException {
                final TopDocs[] topDocs = new TopDocs[collectors.size()];
                int i = 0;
                for (final TopScoreDocCollector collector : collectors) {
                    topDocs[i++] = collector.topDocs();
                }
                return TopDocs.merge(cappedNumHits, topDocs);
            }
        };
        return this.search(query, manager);
    }
    
    @Deprecated
    public final TopDocs searchAfter(final ScoreDoc after, final Query query, final Filter filter, final int n) throws IOException {
        return this.searchAfter(after, this.wrapFilter(query, filter), n);
    }
    
    public TopDocs search(final Query query, final int n) throws IOException {
        return this.searchAfter(null, query, n);
    }
    
    @Deprecated
    public final TopDocs search(final Query query, final Filter filter, final int n) throws IOException {
        return this.search(this.wrapFilter(query, filter), n);
    }
    
    @Deprecated
    public final void search(final Query query, final Filter filter, final Collector results) throws IOException {
        this.search(this.wrapFilter(query, filter), results);
    }
    
    public void search(final Query query, final Collector results) throws IOException {
        this.search(this.leafContexts, this.createNormalizedWeight(query, results.needsScores()), results);
    }
    
    @Deprecated
    public final TopFieldDocs search(final Query query, final Filter filter, final int n, final Sort sort) throws IOException {
        return this.search(query, filter, n, sort, false, false);
    }
    
    @Deprecated
    public final TopFieldDocs search(final Query query, final Filter filter, final int n, final Sort sort, final boolean doDocScores, final boolean doMaxScore) throws IOException {
        return this.searchAfter(null, query, filter, n, sort, doDocScores, doMaxScore);
    }
    
    public final TopFieldDocs search(final Query query, final int n, final Sort sort, final boolean doDocScores, final boolean doMaxScore) throws IOException {
        return this.searchAfter(null, query, n, sort, doDocScores, doMaxScore);
    }
    
    @Deprecated
    public final TopFieldDocs searchAfter(final ScoreDoc after, final Query query, final Filter filter, final int n, final Sort sort) throws IOException {
        return this.searchAfter(after, query, filter, n, sort, false, false);
    }
    
    public TopFieldDocs search(final Query query, final int n, final Sort sort) throws IOException {
        return this.search(query, null, n, sort, false, false);
    }
    
    public TopDocs searchAfter(final ScoreDoc after, final Query query, final int n, final Sort sort) throws IOException {
        return this.searchAfter(after, query, null, n, sort, false, false);
    }
    
    @Deprecated
    public final TopFieldDocs searchAfter(final ScoreDoc after, final Query query, final Filter filter, final int numHits, final Sort sort, final boolean doDocScores, final boolean doMaxScore) throws IOException {
        if (after != null && !(after instanceof FieldDoc)) {
            throw new IllegalArgumentException("after must be a FieldDoc; got " + after);
        }
        return this.searchAfter((FieldDoc)after, this.wrapFilter(query, filter), numHits, sort, doDocScores, doMaxScore);
    }
    
    public final TopFieldDocs searchAfter(final ScoreDoc after, final Query query, final int numHits, final Sort sort, final boolean doDocScores, final boolean doMaxScore) throws IOException {
        if (after != null && !(after instanceof FieldDoc)) {
            throw new IllegalArgumentException("after must be a FieldDoc; got " + after);
        }
        return this.searchAfter((FieldDoc)after, query, numHits, sort, doDocScores, doMaxScore);
    }
    
    private TopFieldDocs searchAfter(final FieldDoc after, final Query query, final int numHits, final Sort sort, final boolean doDocScores, final boolean doMaxScore) throws IOException {
        final int limit = Math.max(1, this.reader.maxDoc());
        if (after != null && after.doc >= limit) {
            throw new IllegalArgumentException("after.doc exceeds the number of documents in the reader: after.doc=" + after.doc + " limit=" + limit);
        }
        final int cappedNumHits = Math.min(numHits, limit);
        final CollectorManager<TopFieldCollector, TopFieldDocs> manager = new CollectorManager<TopFieldCollector, TopFieldDocs>() {
            @Override
            public TopFieldCollector newCollector() throws IOException {
                final boolean fillFields = true;
                return TopFieldCollector.create(sort, cappedNumHits, after, true, doDocScores, doMaxScore);
            }
            
            @Override
            public TopFieldDocs reduce(final Collection<TopFieldCollector> collectors) throws IOException {
                final TopFieldDocs[] topDocs = new TopFieldDocs[collectors.size()];
                int i = 0;
                for (final TopFieldCollector collector : collectors) {
                    topDocs[i++] = collector.topDocs();
                }
                return TopDocs.merge(sort, cappedNumHits, topDocs);
            }
        };
        return this.search(query, manager);
    }
    
    public <C extends Collector, T> T search(final Query query, final CollectorManager<C, T> collectorManager) throws IOException {
        if (this.executor == null) {
            final C collector = collectorManager.newCollector();
            this.search(query, collector);
            return collectorManager.reduce(Collections.singletonList(collector));
        }
        final List<C> collectors = new ArrayList<C>(this.leafSlices.length);
        boolean needsScores = false;
        for (int i = 0; i < this.leafSlices.length; ++i) {
            final C collector2 = collectorManager.newCollector();
            collectors.add(collector2);
            needsScores |= collector2.needsScores();
        }
        final Weight weight = this.createNormalizedWeight(query, needsScores);
        final List<Future<C>> topDocsFutures = new ArrayList<Future<C>>(this.leafSlices.length);
        for (int j = 0; j < this.leafSlices.length; ++j) {
            final LeafReaderContext[] leaves = this.leafSlices[j].leaves;
            final C collector3 = collectors.get(j);
            topDocsFutures.add(this.executor.submit((Callable<C>)new Callable<C>() {
                @Override
                public C call() throws Exception {
                    IndexSearcher.this.search(Arrays.asList(leaves), weight, collector3);
                    return collector3;
                }
            }));
        }
        final List<C> collectedCollectors = new ArrayList<C>();
        for (final Future<C> future : topDocsFutures) {
            try {
                collectedCollectors.add(future.get());
            }
            catch (final InterruptedException e) {
                throw new ThreadInterruptedException(e);
            }
            catch (final ExecutionException e2) {
                throw new RuntimeException(e2);
            }
        }
        return collectorManager.reduce(collectors);
    }
    
    protected void search(final List<LeafReaderContext> leaves, final Weight weight, final Collector collector) throws IOException {
        for (final LeafReaderContext ctx : leaves) {
            LeafCollector leafCollector;
            try {
                leafCollector = collector.getLeafCollector(ctx);
            }
            catch (final CollectionTerminatedException e) {
                continue;
            }
            final BulkScorer scorer = weight.bulkScorer(ctx);
            if (scorer != null) {
                try {
                    scorer.score(leafCollector, ctx.reader().getLiveDocs());
                }
                catch (final CollectionTerminatedException ex) {}
            }
        }
    }
    
    public Query rewrite(final Query original) throws IOException {
        Query query = original;
        for (Query rewrittenQuery = query.rewrite(this.reader); rewrittenQuery != query; query = rewrittenQuery, rewrittenQuery = query.rewrite(this.reader)) {}
        return query;
    }
    
    public Explanation explain(final Query query, final int doc) throws IOException {
        return this.explain(this.createNormalizedWeight(query, true), doc);
    }
    
    protected Explanation explain(final Weight weight, final int doc) throws IOException {
        final int n = ReaderUtil.subIndex(doc, this.leafContexts);
        final LeafReaderContext ctx = this.leafContexts.get(n);
        final int deBasedDoc = doc - ctx.docBase;
        final Bits liveDocs = ctx.reader().getLiveDocs();
        if (liveDocs != null && !liveDocs.get(deBasedDoc)) {
            return Explanation.noMatch("Document " + doc + " is deleted", new Explanation[0]);
        }
        return weight.explain(ctx, deBasedDoc);
    }
    
    public Weight createNormalizedWeight(Query query, final boolean needsScores) throws IOException {
        query = this.rewrite(query);
        final Weight weight = this.createWeight(query, needsScores);
        final float v = weight.getValueForNormalization();
        float norm = this.getSimilarity(needsScores).queryNorm(v);
        if (Float.isInfinite(norm) || Float.isNaN(norm)) {
            norm = 1.0f;
        }
        weight.normalize(norm, 1.0f);
        return weight;
    }
    
    public Weight createWeight(final Query query, final boolean needsScores) throws IOException {
        final QueryCache queryCache = this.queryCache;
        Weight weight = query.createWeight(this, needsScores);
        if (!needsScores && queryCache != null) {
            weight = queryCache.doCache(weight, this.queryCachingPolicy);
        }
        return weight;
    }
    
    public IndexReaderContext getTopReaderContext() {
        return this.readerContext;
    }
    
    @Override
    public String toString() {
        return "IndexSearcher(" + this.reader + "; executor=" + this.executor + ")";
    }
    
    public TermStatistics termStatistics(final Term term, final TermContext context) throws IOException {
        return new TermStatistics(term.bytes(), context.docFreq(), context.totalTermFreq());
    }
    
    public CollectionStatistics collectionStatistics(final String field) throws IOException {
        assert field != null;
        final Terms terms = MultiFields.getTerms(this.reader, field);
        int docCount;
        long sumTotalTermFreq;
        long sumDocFreq;
        if (terms == null) {
            docCount = 0;
            sumTotalTermFreq = 0L;
            sumDocFreq = 0L;
        }
        else {
            docCount = terms.getDocCount();
            sumTotalTermFreq = terms.getSumTotalTermFreq();
            sumDocFreq = terms.getSumDocFreq();
        }
        return new CollectionStatistics(field, this.reader.maxDoc(), docCount, sumTotalTermFreq, sumDocFreq);
    }
    
    static {
        NON_SCORING_SIMILARITY = new Similarity() {
            @Override
            public long computeNorm(final FieldInvertState state) {
                throw new UnsupportedOperationException("This Similarity may only be used for searching, not indexing");
            }
            
            @Override
            public SimWeight computeWeight(final CollectionStatistics collectionStats, final TermStatistics... termStats) {
                return new SimWeight() {
                    @Override
                    public float getValueForNormalization() {
                        return 1.0f;
                    }
                    
                    @Override
                    public void normalize(final float queryNorm, final float boost) {
                    }
                };
            }
            
            @Override
            public SimScorer simScorer(final SimWeight weight, final LeafReaderContext context) throws IOException {
                return new SimScorer() {
                    @Override
                    public float score(final int doc, final float freq) {
                        return 0.0f;
                    }
                    
                    @Override
                    public float computeSlopFactor(final int distance) {
                        return 1.0f;
                    }
                    
                    @Override
                    public float computePayloadFactor(final int doc, final int start, final int end, final BytesRef payload) {
                        return 1.0f;
                    }
                };
            }
        };
        IndexSearcher.DEFAULT_CACHING_POLICY = new UsageTrackingQueryCachingPolicy();
        final int maxCachedQueries = 1000;
        final long maxRamBytesUsed = Math.min(33554432L, Runtime.getRuntime().maxMemory() / 20L);
        IndexSearcher.DEFAULT_QUERY_CACHE = new LRUQueryCache(1000, maxRamBytesUsed);
        defaultSimilarity = new DefaultSimilarity();
    }
    
    public static class LeafSlice
    {
        final LeafReaderContext[] leaves;
        
        public LeafSlice(final LeafReaderContext... leaves) {
            this.leaves = leaves;
        }
    }
}
