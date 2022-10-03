package org.apache.lucene.search.similarities;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.SmallFloat;
import org.apache.lucene.index.FieldInvertState;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.lucene.search.Explanation;
import java.util.List;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.CollectionStatistics;

public abstract class SimilarityBase extends Similarity
{
    private static final double LOG_2;
    protected boolean discountOverlaps;
    private static final float[] NORM_TABLE;
    
    public SimilarityBase() {
        this.discountOverlaps = true;
    }
    
    public void setDiscountOverlaps(final boolean v) {
        this.discountOverlaps = v;
    }
    
    public boolean getDiscountOverlaps() {
        return this.discountOverlaps;
    }
    
    @Override
    public final SimWeight computeWeight(final CollectionStatistics collectionStats, final TermStatistics... termStats) {
        final BasicStats[] stats = new BasicStats[termStats.length];
        for (int i = 0; i < termStats.length; ++i) {
            this.fillBasicStats(stats[i] = this.newStats(collectionStats.field()), collectionStats, termStats[i]);
        }
        return (stats.length == 1) ? stats[0] : new MultiSimilarity.MultiStats(stats);
    }
    
    protected BasicStats newStats(final String field) {
        return new BasicStats(field);
    }
    
    protected void fillBasicStats(final BasicStats stats, final CollectionStatistics collectionStats, final TermStatistics termStats) {
        assert collectionStats.sumTotalTermFreq() >= termStats.totalTermFreq();
        final long numberOfDocuments = collectionStats.maxDoc();
        final long docFreq = termStats.docFreq();
        long totalTermFreq = termStats.totalTermFreq();
        if (totalTermFreq == -1L) {
            totalTermFreq = docFreq;
        }
        final long sumTotalTermFreq = collectionStats.sumTotalTermFreq();
        long numberOfFieldTokens;
        float avgFieldLength;
        if (sumTotalTermFreq <= 0L) {
            numberOfFieldTokens = docFreq;
            avgFieldLength = 1.0f;
        }
        else {
            numberOfFieldTokens = sumTotalTermFreq;
            avgFieldLength = numberOfFieldTokens / (float)numberOfDocuments;
        }
        stats.setNumberOfDocuments(numberOfDocuments);
        stats.setNumberOfFieldTokens(numberOfFieldTokens);
        stats.setAvgFieldLength(avgFieldLength);
        stats.setDocFreq(docFreq);
        stats.setTotalTermFreq(totalTermFreq);
    }
    
    protected abstract float score(final BasicStats p0, final float p1, final float p2);
    
    protected void explain(final List<Explanation> subExpls, final BasicStats stats, final int doc, final float freq, final float docLen) {
    }
    
    protected Explanation explain(final BasicStats stats, final int doc, final Explanation freq, final float docLen) {
        final List<Explanation> subs = new ArrayList<Explanation>();
        this.explain(subs, stats, doc, freq.getValue(), docLen);
        return Explanation.match(this.score(stats, freq.getValue(), docLen), "score(" + this.getClass().getSimpleName() + ", doc=" + doc + ", freq=" + freq.getValue() + "), computed from:", subs);
    }
    
    @Override
    public SimScorer simScorer(final SimWeight stats, final LeafReaderContext context) throws IOException {
        if (stats instanceof MultiSimilarity.MultiStats) {
            final SimWeight[] subStats = ((MultiSimilarity.MultiStats)stats).subStats;
            final SimScorer[] subScorers = new SimScorer[subStats.length];
            for (int i = 0; i < subScorers.length; ++i) {
                final BasicStats basicstats = (BasicStats)subStats[i];
                subScorers[i] = new BasicSimScorer(basicstats, context.reader().getNormValues(basicstats.field));
            }
            return new MultiSimilarity.MultiSimScorer(subScorers);
        }
        final BasicStats basicstats2 = (BasicStats)stats;
        return new BasicSimScorer(basicstats2, context.reader().getNormValues(basicstats2.field));
    }
    
    @Override
    public abstract String toString();
    
    @Override
    public long computeNorm(final FieldInvertState state) {
        float numTerms;
        if (this.discountOverlaps) {
            numTerms = (float)(state.getLength() - state.getNumOverlap());
        }
        else {
            numTerms = (float)state.getLength();
        }
        return this.encodeNormValue(state.getBoost(), numTerms);
    }
    
    protected float decodeNormValue(final byte norm) {
        return SimilarityBase.NORM_TABLE[norm & 0xFF];
    }
    
    protected byte encodeNormValue(final float boost, final float length) {
        return SmallFloat.floatToByte315(boost / (float)Math.sqrt(length));
    }
    
    public static double log2(final double x) {
        return Math.log(x) / SimilarityBase.LOG_2;
    }
    
    static {
        LOG_2 = Math.log(2.0);
        NORM_TABLE = new float[256];
        for (int i = 1; i < 256; ++i) {
            final float floatNorm = SmallFloat.byte315ToFloat((byte)i);
            SimilarityBase.NORM_TABLE[i] = 1.0f / (floatNorm * floatNorm);
        }
        SimilarityBase.NORM_TABLE[0] = 1.0f / SimilarityBase.NORM_TABLE[255];
    }
    
    private class BasicSimScorer extends SimScorer
    {
        private final BasicStats stats;
        private final NumericDocValues norms;
        
        BasicSimScorer(final BasicStats stats, final NumericDocValues norms) throws IOException {
            this.stats = stats;
            this.norms = norms;
        }
        
        @Override
        public float score(final int doc, final float freq) {
            return SimilarityBase.this.score(this.stats, freq, (this.norms == null) ? 1.0f : SimilarityBase.this.decodeNormValue((byte)this.norms.get(doc)));
        }
        
        @Override
        public Explanation explain(final int doc, final Explanation freq) {
            return SimilarityBase.this.explain(this.stats, doc, freq, (this.norms == null) ? 1.0f : SimilarityBase.this.decodeNormValue((byte)this.norms.get(doc)));
        }
        
        @Override
        public float computeSlopFactor(final int distance) {
            return 1.0f / (distance + 1);
        }
        
        @Override
        public float computePayloadFactor(final int doc, final int start, final int end, final BytesRef payload) {
            return 1.0f;
        }
    }
}
