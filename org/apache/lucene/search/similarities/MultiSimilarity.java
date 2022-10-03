package org.apache.lucene.search.similarities;

import org.apache.lucene.util.BytesRef;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.lucene.search.Explanation;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.index.FieldInvertState;

public class MultiSimilarity extends Similarity
{
    protected final Similarity[] sims;
    
    public MultiSimilarity(final Similarity[] sims) {
        this.sims = sims;
    }
    
    @Override
    public long computeNorm(final FieldInvertState state) {
        return this.sims[0].computeNorm(state);
    }
    
    @Override
    public SimWeight computeWeight(final CollectionStatistics collectionStats, final TermStatistics... termStats) {
        final SimWeight[] subStats = new SimWeight[this.sims.length];
        for (int i = 0; i < subStats.length; ++i) {
            subStats[i] = this.sims[i].computeWeight(collectionStats, termStats);
        }
        return new MultiStats(subStats);
    }
    
    @Override
    public SimScorer simScorer(final SimWeight stats, final LeafReaderContext context) throws IOException {
        final SimScorer[] subScorers = new SimScorer[this.sims.length];
        for (int i = 0; i < subScorers.length; ++i) {
            subScorers[i] = this.sims[i].simScorer(((MultiStats)stats).subStats[i], context);
        }
        return new MultiSimScorer(subScorers);
    }
    
    static class MultiSimScorer extends SimScorer
    {
        private final SimScorer[] subScorers;
        
        MultiSimScorer(final SimScorer[] subScorers) {
            this.subScorers = subScorers;
        }
        
        @Override
        public float score(final int doc, final float freq) {
            float sum = 0.0f;
            for (final SimScorer subScorer : this.subScorers) {
                sum += subScorer.score(doc, freq);
            }
            return sum;
        }
        
        @Override
        public Explanation explain(final int doc, final Explanation freq) {
            final List<Explanation> subs = new ArrayList<Explanation>();
            for (final SimScorer subScorer : this.subScorers) {
                subs.add(subScorer.explain(doc, freq));
            }
            return Explanation.match(this.score(doc, freq.getValue()), "sum of:", subs);
        }
        
        @Override
        public float computeSlopFactor(final int distance) {
            return this.subScorers[0].computeSlopFactor(distance);
        }
        
        @Override
        public float computePayloadFactor(final int doc, final int start, final int end, final BytesRef payload) {
            return this.subScorers[0].computePayloadFactor(doc, start, end, payload);
        }
    }
    
    static class MultiStats extends SimWeight
    {
        final SimWeight[] subStats;
        
        MultiStats(final SimWeight[] subStats) {
            this.subStats = subStats;
        }
        
        @Override
        public float getValueForNormalization() {
            float sum = 0.0f;
            for (final SimWeight stat : this.subStats) {
                sum += stat.getValueForNormalization();
            }
            return sum / this.subStats.length;
        }
        
        @Override
        public void normalize(final float queryNorm, final float boost) {
            for (final SimWeight stat : this.subStats) {
                stat.normalize(queryNorm, boost);
            }
        }
    }
}
