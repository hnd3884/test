package org.apache.lucene.search.similarities;

import java.util.Locale;
import org.apache.lucene.search.Explanation;
import java.util.List;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.CollectionStatistics;

public abstract class LMSimilarity extends SimilarityBase
{
    protected final CollectionModel collectionModel;
    
    public LMSimilarity(final CollectionModel collectionModel) {
        this.collectionModel = collectionModel;
    }
    
    public LMSimilarity() {
        this(new DefaultCollectionModel());
    }
    
    @Override
    protected BasicStats newStats(final String field) {
        return new LMStats(field);
    }
    
    @Override
    protected void fillBasicStats(final BasicStats stats, final CollectionStatistics collectionStats, final TermStatistics termStats) {
        super.fillBasicStats(stats, collectionStats, termStats);
        final LMStats lmStats = (LMStats)stats;
        lmStats.setCollectionProbability(this.collectionModel.computeProbability(stats));
    }
    
    @Override
    protected void explain(final List<Explanation> subExpls, final BasicStats stats, final int doc, final float freq, final float docLen) {
        subExpls.add(Explanation.match(this.collectionModel.computeProbability(stats), "collection probability", new Explanation[0]));
    }
    
    public abstract String getName();
    
    @Override
    public String toString() {
        final String coll = this.collectionModel.getName();
        if (coll != null) {
            return String.format(Locale.ROOT, "LM %s - %s", this.getName(), coll);
        }
        return String.format(Locale.ROOT, "LM %s", this.getName());
    }
    
    public static class LMStats extends BasicStats
    {
        private float collectionProbability;
        
        public LMStats(final String field) {
            super(field);
        }
        
        public final float getCollectionProbability() {
            return this.collectionProbability;
        }
        
        public final void setCollectionProbability(final float collectionProbability) {
            this.collectionProbability = collectionProbability;
        }
    }
    
    public static class DefaultCollectionModel implements CollectionModel
    {
        @Override
        public float computeProbability(final BasicStats stats) {
            return (stats.getTotalTermFreq() + 1.0f) / (stats.getNumberOfFieldTokens() + 1.0f);
        }
        
        @Override
        public String getName() {
            return null;
        }
    }
    
    public interface CollectionModel
    {
        float computeProbability(final BasicStats p0);
        
        String getName();
    }
}
