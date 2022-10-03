package org.apache.lucene.search.similarities;

import org.apache.lucene.index.NumericDocValues;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.FieldInvertState;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.CollectionStatistics;

public abstract class TFIDFSimilarity extends Similarity
{
    @Override
    public abstract float coord(final int p0, final int p1);
    
    @Override
    public abstract float queryNorm(final float p0);
    
    public abstract float tf(final float p0);
    
    public Explanation idfExplain(final CollectionStatistics collectionStats, final TermStatistics termStats) {
        final long df = termStats.docFreq();
        final long max = collectionStats.maxDoc();
        final float idf = this.idf(df, max);
        return Explanation.match(idf, "idf(docFreq=" + df + ", maxDocs=" + max + ")", new Explanation[0]);
    }
    
    public Explanation idfExplain(final CollectionStatistics collectionStats, final TermStatistics[] termStats) {
        final long max = collectionStats.maxDoc();
        float idf = 0.0f;
        final List<Explanation> subs = new ArrayList<Explanation>();
        for (final TermStatistics stat : termStats) {
            final long df = stat.docFreq();
            final float termIdf = this.idf(df, max);
            subs.add(Explanation.match(termIdf, "idf(docFreq=" + df + ", maxDocs=" + max + ")", new Explanation[0]));
            idf += termIdf;
        }
        return Explanation.match(idf, "idf(), sum of:", subs);
    }
    
    public abstract float idf(final long p0, final long p1);
    
    public abstract float lengthNorm(final FieldInvertState p0);
    
    @Override
    public final long computeNorm(final FieldInvertState state) {
        final float normValue = this.lengthNorm(state);
        return this.encodeNormValue(normValue);
    }
    
    public abstract float decodeNormValue(final long p0);
    
    public abstract long encodeNormValue(final float p0);
    
    public abstract float sloppyFreq(final int p0);
    
    public abstract float scorePayload(final int p0, final int p1, final int p2, final BytesRef p3);
    
    @Override
    public final SimWeight computeWeight(final CollectionStatistics collectionStats, final TermStatistics... termStats) {
        final Explanation idf = (termStats.length == 1) ? this.idfExplain(collectionStats, termStats[0]) : this.idfExplain(collectionStats, termStats);
        return new IDFStats(collectionStats.field(), idf);
    }
    
    @Override
    public final SimScorer simScorer(final SimWeight stats, final LeafReaderContext context) throws IOException {
        final IDFStats idfstats = (IDFStats)stats;
        return new TFIDFSimScorer(idfstats, context.reader().getNormValues(idfstats.field));
    }
    
    private Explanation explainQuery(final IDFStats stats) {
        final List<Explanation> subs = new ArrayList<Explanation>();
        final Explanation boostExpl = Explanation.match(stats.boost, "boost", new Explanation[0]);
        if (stats.boost != 1.0f) {
            subs.add(boostExpl);
        }
        subs.add(stats.idf);
        final Explanation queryNormExpl = Explanation.match(stats.queryNorm, "queryNorm", new Explanation[0]);
        subs.add(queryNormExpl);
        return Explanation.match(boostExpl.getValue() * stats.idf.getValue() * queryNormExpl.getValue(), "queryWeight, product of:", subs);
    }
    
    private Explanation explainField(final int doc, final Explanation freq, final IDFStats stats, final NumericDocValues norms) {
        final Explanation tfExplanation = Explanation.match(this.tf(freq.getValue()), "tf(freq=" + freq.getValue() + "), with freq of:", freq);
        final Explanation fieldNormExpl = Explanation.match((norms != null) ? this.decodeNormValue(norms.get(doc)) : 1.0f, "fieldNorm(doc=" + doc + ")", new Explanation[0]);
        return Explanation.match(tfExplanation.getValue() * stats.idf.getValue() * fieldNormExpl.getValue(), "fieldWeight in " + doc + ", product of:", tfExplanation, stats.idf, fieldNormExpl);
    }
    
    private Explanation explainScore(final int doc, final Explanation freq, final IDFStats stats, final NumericDocValues norms) {
        final Explanation queryExpl = this.explainQuery(stats);
        final Explanation fieldExpl = this.explainField(doc, freq, stats, norms);
        if (queryExpl.getValue() == 1.0f) {
            return fieldExpl;
        }
        return Explanation.match(queryExpl.getValue() * fieldExpl.getValue(), "score(doc=" + doc + ",freq=" + freq.getValue() + "), product of:", queryExpl, fieldExpl);
    }
    
    private final class TFIDFSimScorer extends SimScorer
    {
        private final IDFStats stats;
        private final float weightValue;
        private final NumericDocValues norms;
        
        TFIDFSimScorer(final IDFStats stats, final NumericDocValues norms) throws IOException {
            this.stats = stats;
            this.weightValue = stats.value;
            this.norms = norms;
        }
        
        @Override
        public float score(final int doc, final float freq) {
            final float raw = TFIDFSimilarity.this.tf(freq) * this.weightValue;
            return (this.norms == null) ? raw : (raw * TFIDFSimilarity.this.decodeNormValue(this.norms.get(doc)));
        }
        
        @Override
        public float computeSlopFactor(final int distance) {
            return TFIDFSimilarity.this.sloppyFreq(distance);
        }
        
        @Override
        public float computePayloadFactor(final int doc, final int start, final int end, final BytesRef payload) {
            return TFIDFSimilarity.this.scorePayload(doc, start, end, payload);
        }
        
        @Override
        public Explanation explain(final int doc, final Explanation freq) {
            return TFIDFSimilarity.this.explainScore(doc, freq, this.stats, this.norms);
        }
    }
    
    private static class IDFStats extends SimWeight
    {
        private final String field;
        private final Explanation idf;
        private float queryNorm;
        private float boost;
        private float queryWeight;
        private float value;
        
        public IDFStats(final String field, final Explanation idf) {
            this.field = field;
            this.idf = idf;
            this.normalize(1.0f, 1.0f);
        }
        
        @Override
        public float getValueForNormalization() {
            return this.queryWeight * this.queryWeight;
        }
        
        @Override
        public void normalize(final float queryNorm, final float boost) {
            this.boost = boost;
            this.queryNorm = queryNorm;
            this.queryWeight = queryNorm * boost * this.idf.getValue();
            this.value = this.queryWeight * this.idf.getValue();
        }
    }
}
