package org.apache.lucene.search;

import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.util.Bits;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;

public final class MatchAllDocsQuery extends Query
{
    @Override
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) {
        return new ConstantScoreWeight(this) {
            @Override
            public String toString() {
                return "weight(" + MatchAllDocsQuery.this + ")";
            }
            
            @Override
            public Scorer scorer(final LeafReaderContext context) throws IOException {
                return new ConstantScoreScorer(this, this.score(), DocIdSetIterator.all(context.reader().maxDoc()));
            }
            
            @Override
            public BulkScorer bulkScorer(final LeafReaderContext context) throws IOException {
                final float score = this.score();
                final int maxDoc = context.reader().maxDoc();
                return new BulkScorer() {
                    @Override
                    public int score(final LeafCollector collector, final Bits acceptDocs, final int min, int max) throws IOException {
                        max = Math.min(max, maxDoc);
                        final FakeScorer scorer = new FakeScorer();
                        scorer.score = score;
                        collector.setScorer(scorer);
                        for (int doc = min; doc < max; ++doc) {
                            scorer.doc = doc;
                            if (acceptDocs == null || acceptDocs.get(doc)) {
                                collector.collect(doc);
                            }
                        }
                        return (max == maxDoc) ? Integer.MAX_VALUE : max;
                    }
                    
                    @Override
                    public long cost() {
                        return maxDoc;
                    }
                };
            }
        };
    }
    
    @Override
    public String toString(final String field) {
        return "*:*" + ToStringUtils.boost(this.getBoost());
    }
}
