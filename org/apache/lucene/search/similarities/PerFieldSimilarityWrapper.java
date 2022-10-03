package org.apache.lucene.search.similarities;

import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.index.FieldInvertState;

public abstract class PerFieldSimilarityWrapper extends Similarity
{
    @Override
    public final long computeNorm(final FieldInvertState state) {
        return this.get(state.getName()).computeNorm(state);
    }
    
    @Override
    public final SimWeight computeWeight(final CollectionStatistics collectionStats, final TermStatistics... termStats) {
        final PerFieldSimWeight weight = new PerFieldSimWeight();
        weight.delegate = this.get(collectionStats.field());
        weight.delegateWeight = weight.delegate.computeWeight(collectionStats, termStats);
        return weight;
    }
    
    @Override
    public final SimScorer simScorer(final SimWeight weight, final LeafReaderContext context) throws IOException {
        final PerFieldSimWeight perFieldWeight = (PerFieldSimWeight)weight;
        return perFieldWeight.delegate.simScorer(perFieldWeight.delegateWeight, context);
    }
    
    public abstract Similarity get(final String p0);
    
    static class PerFieldSimWeight extends SimWeight
    {
        Similarity delegate;
        SimWeight delegateWeight;
        
        @Override
        public float getValueForNormalization() {
            return this.delegateWeight.getValueForNormalization();
        }
        
        @Override
        public void normalize(final float queryNorm, final float boost) {
            this.delegateWeight.normalize(queryNorm, boost);
        }
    }
}
