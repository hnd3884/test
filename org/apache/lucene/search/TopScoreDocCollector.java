package org.apache.lucene.search;

import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.util.PriorityQueue;

public abstract class TopScoreDocCollector extends TopDocsCollector<ScoreDoc>
{
    ScoreDoc pqTop;
    
    public static TopScoreDocCollector create(final int numHits) {
        return create(numHits, null);
    }
    
    public static TopScoreDocCollector create(final int numHits, final ScoreDoc after) {
        if (numHits <= 0) {
            throw new IllegalArgumentException("numHits must be > 0; please use TotalHitCountCollector if you just need the total hit count");
        }
        if (after == null) {
            return new SimpleTopScoreDocCollector(numHits);
        }
        return new PagingTopScoreDocCollector(numHits, after);
    }
    
    TopScoreDocCollector(final int numHits) {
        super(new HitQueue(numHits, true));
        this.pqTop = this.pq.top();
    }
    
    @Override
    protected TopDocs newTopDocs(final ScoreDoc[] results, final int start) {
        if (results == null) {
            return TopScoreDocCollector.EMPTY_TOPDOCS;
        }
        float maxScore = Float.NaN;
        if (start == 0) {
            maxScore = results[0].score;
        }
        else {
            for (int i = this.pq.size(); i > 1; --i) {
                this.pq.pop();
            }
            maxScore = this.pq.pop().score;
        }
        return new TopDocs(this.totalHits, results, maxScore);
    }
    
    @Override
    public boolean needsScores() {
        return true;
    }
    
    abstract static class ScorerLeafCollector implements LeafCollector
    {
        Scorer scorer;
        
        @Override
        public void setScorer(final Scorer scorer) throws IOException {
            this.scorer = scorer;
        }
    }
    
    private static class SimpleTopScoreDocCollector extends TopScoreDocCollector
    {
        SimpleTopScoreDocCollector(final int numHits) {
            super(numHits);
        }
        
        @Override
        public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
            final int docBase = context.docBase;
            return new ScorerLeafCollector() {
                @Override
                public void collect(final int doc) throws IOException {
                    final float score = this.scorer.score();
                    assert score != Float.NEGATIVE_INFINITY;
                    assert !Float.isNaN(score);
                    final SimpleTopScoreDocCollector this$0 = SimpleTopScoreDocCollector.this;
                    ++this$0.totalHits;
                    if (score <= SimpleTopScoreDocCollector.this.pqTop.score) {
                        return;
                    }
                    SimpleTopScoreDocCollector.this.pqTop.doc = doc + docBase;
                    SimpleTopScoreDocCollector.this.pqTop.score = score;
                    SimpleTopScoreDocCollector.this.pqTop = SimpleTopScoreDocCollector.this.pq.updateTop();
                }
            };
        }
    }
    
    private static class PagingTopScoreDocCollector extends TopScoreDocCollector
    {
        private final ScoreDoc after;
        private int collectedHits;
        
        PagingTopScoreDocCollector(final int numHits, final ScoreDoc after) {
            super(numHits);
            this.after = after;
            this.collectedHits = 0;
        }
        
        @Override
        protected int topDocsSize() {
            return (this.collectedHits < this.pq.size()) ? this.collectedHits : this.pq.size();
        }
        
        @Override
        protected TopDocs newTopDocs(final ScoreDoc[] results, final int start) {
            return (results == null) ? new TopDocs(this.totalHits, new ScoreDoc[0], Float.NaN) : new TopDocs(this.totalHits, results);
        }
        
        @Override
        public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
            final int docBase = context.docBase;
            final int afterDoc = this.after.doc - context.docBase;
            return new ScorerLeafCollector() {
                @Override
                public void collect(final int doc) throws IOException {
                    final float score = this.scorer.score();
                    assert score != Float.NEGATIVE_INFINITY;
                    assert !Float.isNaN(score);
                    final PagingTopScoreDocCollector this$0 = PagingTopScoreDocCollector.this;
                    ++this$0.totalHits;
                    if (score > PagingTopScoreDocCollector.this.after.score || (score == PagingTopScoreDocCollector.this.after.score && doc <= afterDoc)) {
                        return;
                    }
                    if (score <= PagingTopScoreDocCollector.this.pqTop.score) {
                        return;
                    }
                    PagingTopScoreDocCollector.this.collectedHits++;
                    PagingTopScoreDocCollector.this.pqTop.doc = doc + docBase;
                    PagingTopScoreDocCollector.this.pqTop.score = score;
                    PagingTopScoreDocCollector.this.pqTop = PagingTopScoreDocCollector.this.pq.updateTop();
                }
            };
        }
    }
}
