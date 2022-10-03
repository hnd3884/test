package org.apache.lucene.search;

import org.apache.lucene.util.ArrayUtil;
import java.util.Arrays;
import java.util.ArrayList;
import org.apache.lucene.index.LeafReaderContext;
import java.util.List;
import java.io.IOException;

public abstract class CachingCollector extends FilterCollector
{
    private static final int INITIAL_ARRAY_SIZE = 128;
    private boolean cached;
    
    public static CachingCollector create(final boolean cacheScores, final double maxRAMMB) {
        final Collector other = new SimpleCollector() {
            @Override
            public void collect(final int doc) {
            }
            
            @Override
            public boolean needsScores() {
                return true;
            }
        };
        return create(other, cacheScores, maxRAMMB);
    }
    
    public static CachingCollector create(final Collector other, final boolean cacheScores, final double maxRAMMB) {
        int bytesPerDoc = 4;
        if (cacheScores) {
            bytesPerDoc += 4;
        }
        final int maxDocsToCache = (int)(maxRAMMB * 1024.0 * 1024.0 / bytesPerDoc);
        return create(other, cacheScores, maxDocsToCache);
    }
    
    public static CachingCollector create(final Collector other, final boolean cacheScores, final int maxDocsToCache) {
        return cacheScores ? new ScoreCachingCollector(other, maxDocsToCache) : new NoScoreCachingCollector(other, maxDocsToCache);
    }
    
    private CachingCollector(final Collector in) {
        super(in);
        this.cached = true;
    }
    
    public final boolean isCached() {
        return this.cached;
    }
    
    public abstract void replay(final Collector p0) throws IOException;
    
    private static final class CachedScorer extends Scorer
    {
        int doc;
        float score;
        
        private CachedScorer() {
            super(null);
        }
        
        @Override
        public DocIdSetIterator iterator() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public final float score() {
            return this.score;
        }
        
        @Override
        public int docID() {
            return this.doc;
        }
        
        @Override
        public final int freq() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class NoScoreCachingCollector extends CachingCollector
    {
        List<LeafReaderContext> contexts;
        List<int[]> docs;
        int maxDocsToCache;
        NoScoreCachingLeafCollector lastCollector;
        
        NoScoreCachingCollector(final Collector in, final int maxDocsToCache) {
            super(in, null);
            this.maxDocsToCache = maxDocsToCache;
            this.contexts = new ArrayList<LeafReaderContext>();
            this.docs = new ArrayList<int[]>();
        }
        
        protected NoScoreCachingLeafCollector wrap(final LeafCollector in, final int maxDocsToCache) {
            return new NoScoreCachingLeafCollector(in, maxDocsToCache);
        }
        
        @Override
        public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
            this.postCollection();
            final LeafCollector in = this.in.getLeafCollector(context);
            if (this.contexts != null) {
                this.contexts.add(context);
            }
            if (this.maxDocsToCache >= 0) {
                return this.lastCollector = this.wrap(in, this.maxDocsToCache);
            }
            return in;
        }
        
        protected void invalidate() {
            this.maxDocsToCache = -1;
            this.contexts = null;
            this.docs = null;
        }
        
        protected void postCollect(final NoScoreCachingLeafCollector collector) {
            final int[] docs = collector.cachedDocs();
            this.maxDocsToCache -= docs.length;
            this.docs.add(docs);
        }
        
        private void postCollection() {
            if (this.lastCollector != null) {
                if (!this.lastCollector.hasCache()) {
                    this.invalidate();
                }
                else {
                    this.postCollect(this.lastCollector);
                }
                this.lastCollector = null;
            }
        }
        
        protected void collect(final LeafCollector collector, final int i) throws IOException {
            final int[] arr$;
            final int[] docs = arr$ = this.docs.get(i);
            for (final int doc : arr$) {
                collector.collect(doc);
            }
        }
        
        @Override
        public void replay(final Collector other) throws IOException {
            this.postCollection();
            if (!this.isCached()) {
                throw new IllegalStateException("cannot replay: cache was cleared because too much RAM was required");
            }
            assert this.docs.size() == this.contexts.size();
            for (int i = 0; i < this.contexts.size(); ++i) {
                final LeafReaderContext context = this.contexts.get(i);
                final LeafCollector collector = other.getLeafCollector(context);
                this.collect(collector, i);
            }
        }
    }
    
    private static class ScoreCachingCollector extends NoScoreCachingCollector
    {
        List<float[]> scores;
        
        ScoreCachingCollector(final Collector in, final int maxDocsToCache) {
            super(in, maxDocsToCache);
            this.scores = new ArrayList<float[]>();
        }
        
        @Override
        protected NoScoreCachingLeafCollector wrap(final LeafCollector in, final int maxDocsToCache) {
            return new ScoreCachingLeafCollector(in, maxDocsToCache);
        }
        
        @Override
        protected void postCollect(final NoScoreCachingLeafCollector collector) {
            final ScoreCachingLeafCollector coll = (ScoreCachingLeafCollector)collector;
            super.postCollect(coll);
            this.scores.add(coll.cachedScores());
        }
        
        @Override
        public boolean needsScores() {
            return true;
        }
        
        @Override
        protected void collect(final LeafCollector collector, final int i) throws IOException {
            final int[] docs = this.docs.get(i);
            final float[] scores = this.scores.get(i);
            assert docs.length == scores.length;
            final CachedScorer scorer = new CachedScorer();
            collector.setScorer(scorer);
            for (int j = 0; j < docs.length; ++j) {
                scorer.doc = docs[j];
                scorer.score = scores[j];
                collector.collect(scorer.doc);
            }
        }
    }
    
    private class NoScoreCachingLeafCollector extends FilterLeafCollector
    {
        final int maxDocsToCache;
        int[] docs;
        int docCount;
        
        NoScoreCachingLeafCollector(final LeafCollector in, final int maxDocsToCache) {
            super(in);
            this.maxDocsToCache = maxDocsToCache;
            this.docs = new int[Math.min(maxDocsToCache, 128)];
            this.docCount = 0;
        }
        
        protected void grow(final int newLen) {
            this.docs = Arrays.copyOf(this.docs, newLen);
        }
        
        protected void invalidate() {
            this.docs = null;
            this.docCount = -1;
            CachingCollector.this.cached = false;
        }
        
        protected void buffer(final int doc) throws IOException {
            this.docs[this.docCount] = doc;
        }
        
        @Override
        public void collect(final int doc) throws IOException {
            if (this.docs != null) {
                if (this.docCount >= this.docs.length) {
                    if (this.docCount >= this.maxDocsToCache) {
                        this.invalidate();
                    }
                    else {
                        final int newLen = Math.min(ArrayUtil.oversize(this.docCount + 1, 4), this.maxDocsToCache);
                        this.grow(newLen);
                    }
                }
                if (this.docs != null) {
                    this.buffer(doc);
                    ++this.docCount;
                }
            }
            super.collect(doc);
        }
        
        boolean hasCache() {
            return this.docs != null;
        }
        
        int[] cachedDocs() {
            return (int[])((this.docs == null) ? null : Arrays.copyOf(this.docs, this.docCount));
        }
    }
    
    private class ScoreCachingLeafCollector extends NoScoreCachingLeafCollector
    {
        Scorer scorer;
        float[] scores;
        
        ScoreCachingLeafCollector(final LeafCollector in, final int maxDocsToCache) {
            super(in, maxDocsToCache);
            this.scores = new float[this.docs.length];
        }
        
        @Override
        public void setScorer(final Scorer scorer) throws IOException {
            super.setScorer(this.scorer = scorer);
        }
        
        @Override
        protected void grow(final int newLen) {
            super.grow(newLen);
            this.scores = Arrays.copyOf(this.scores, newLen);
        }
        
        @Override
        protected void invalidate() {
            super.invalidate();
            this.scores = null;
        }
        
        @Override
        protected void buffer(final int doc) throws IOException {
            super.buffer(doc);
            this.scores[this.docCount] = this.scorer.score();
        }
        
        float[] cachedScores() {
            return (float[])((this.docs == null) ? null : Arrays.copyOf(this.scores, this.docCount));
        }
    }
}
