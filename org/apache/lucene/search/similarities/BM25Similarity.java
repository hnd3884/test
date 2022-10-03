package org.apache.lucene.search.similarities;

import org.apache.lucene.index.NumericDocValues;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.util.SmallFloat;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.util.BytesRef;

public class BM25Similarity extends Similarity
{
    private final float k1;
    private final float b;
    protected boolean discountOverlaps;
    private static final float[] NORM_TABLE;
    
    public BM25Similarity(final float k1, final float b) {
        this.discountOverlaps = true;
        if (Float.isNaN(k1) || Float.isInfinite(k1) || k1 < 0.0f) {
            throw new IllegalArgumentException("illegal k1 value: " + k1 + ", must be a non-negative finite value");
        }
        if (Float.isNaN(b) || b < 0.0f || b > 1.0f) {
            throw new IllegalArgumentException("illegal b value: " + b + ", must be between 0 and 1");
        }
        this.k1 = k1;
        this.b = b;
    }
    
    public BM25Similarity() {
        this(1.2f, 0.75f);
    }
    
    protected float idf(final long docFreq, final long numDocs) {
        return (float)Math.log(1.0 + (numDocs - docFreq + 0.5) / (docFreq + 0.5));
    }
    
    protected float sloppyFreq(final int distance) {
        return 1.0f / (distance + 1);
    }
    
    protected float scorePayload(final int doc, final int start, final int end, final BytesRef payload) {
        return 1.0f;
    }
    
    protected float avgFieldLength(final CollectionStatistics collectionStats) {
        final long sumTotalTermFreq = collectionStats.sumTotalTermFreq();
        if (sumTotalTermFreq <= 0L) {
            return 1.0f;
        }
        return (float)(sumTotalTermFreq / (double)collectionStats.maxDoc());
    }
    
    protected byte encodeNormValue(final float boost, final int fieldLength) {
        return SmallFloat.floatToByte315(boost / (float)Math.sqrt(fieldLength));
    }
    
    protected float decodeNormValue(final byte b) {
        return BM25Similarity.NORM_TABLE[b & 0xFF];
    }
    
    public void setDiscountOverlaps(final boolean v) {
        this.discountOverlaps = v;
    }
    
    public boolean getDiscountOverlaps() {
        return this.discountOverlaps;
    }
    
    @Override
    public final long computeNorm(final FieldInvertState state) {
        final int numTerms = this.discountOverlaps ? (state.getLength() - state.getNumOverlap()) : state.getLength();
        return this.encodeNormValue(state.getBoost(), numTerms);
    }
    
    public Explanation idfExplain(final CollectionStatistics collectionStats, final TermStatistics termStats) {
        final long df = termStats.docFreq();
        final long max = collectionStats.maxDoc();
        final float idf = this.idf(df, max);
        return Explanation.match(idf, "idf(docFreq=" + df + ", maxDocs=" + max + ")", new Explanation[0]);
    }
    
    public Explanation idfExplain(final CollectionStatistics collectionStats, final TermStatistics[] termStats) {
        final long max = collectionStats.maxDoc();
        float idf = 0.0f;
        final List<Explanation> details = new ArrayList<Explanation>();
        for (final TermStatistics stat : termStats) {
            final long df = stat.docFreq();
            final float termIdf = this.idf(df, max);
            details.add(Explanation.match(termIdf, "idf(docFreq=" + df + ", maxDocs=" + max + ")", new Explanation[0]));
            idf += termIdf;
        }
        return Explanation.match(idf, "idf(), sum of:", details);
    }
    
    @Override
    public final SimWeight computeWeight(final CollectionStatistics collectionStats, final TermStatistics... termStats) {
        final Explanation idf = (termStats.length == 1) ? this.idfExplain(collectionStats, termStats[0]) : this.idfExplain(collectionStats, termStats);
        final float avgdl = this.avgFieldLength(collectionStats);
        final float[] cache = new float[256];
        for (int i = 0; i < cache.length; ++i) {
            cache[i] = this.k1 * (1.0f - this.b + this.b * this.decodeNormValue((byte)i) / avgdl);
        }
        return new BM25Stats(collectionStats.field(), idf, avgdl, cache);
    }
    
    @Override
    public final SimScorer simScorer(final SimWeight stats, final LeafReaderContext context) throws IOException {
        final BM25Stats bm25stats = (BM25Stats)stats;
        return new BM25DocScorer(bm25stats, context.reader().getNormValues(bm25stats.field));
    }
    
    private Explanation explainTFNorm(final int doc, final Explanation freq, final BM25Stats stats, final NumericDocValues norms) {
        final List<Explanation> subs = new ArrayList<Explanation>();
        subs.add(freq);
        subs.add(Explanation.match(this.k1, "parameter k1", new Explanation[0]));
        if (norms == null) {
            subs.add(Explanation.match(0.0f, "parameter b (norms omitted for field)", new Explanation[0]));
            return Explanation.match(freq.getValue() * (this.k1 + 1.0f) / (freq.getValue() + this.k1), "tfNorm, computed from:", subs);
        }
        final float doclen = this.decodeNormValue((byte)norms.get(doc));
        subs.add(Explanation.match(this.b, "parameter b", new Explanation[0]));
        subs.add(Explanation.match(stats.avgdl, "avgFieldLength", new Explanation[0]));
        subs.add(Explanation.match(doclen, "fieldLength", new Explanation[0]));
        return Explanation.match(freq.getValue() * (this.k1 + 1.0f) / (freq.getValue() + this.k1 * (1.0f - this.b + this.b * doclen / stats.avgdl)), "tfNorm, computed from:", subs);
    }
    
    private Explanation explainScore(final int doc, final Explanation freq, final BM25Stats stats, final NumericDocValues norms) {
        final Explanation boostExpl = Explanation.match(stats.boost, "boost", new Explanation[0]);
        final List<Explanation> subs = new ArrayList<Explanation>();
        if (boostExpl.getValue() != 1.0f) {
            subs.add(boostExpl);
        }
        subs.add(stats.idf);
        final Explanation tfNormExpl = this.explainTFNorm(doc, freq, stats, norms);
        subs.add(tfNormExpl);
        return Explanation.match(boostExpl.getValue() * stats.idf.getValue() * tfNormExpl.getValue(), "score(doc=" + doc + ",freq=" + freq + "), product of:", subs);
    }
    
    @Override
    public String toString() {
        return "BM25(k1=" + this.k1 + ",b=" + this.b + ")";
    }
    
    public float getK1() {
        return this.k1;
    }
    
    public float getB() {
        return this.b;
    }
    
    static {
        NORM_TABLE = new float[256];
        for (int i = 1; i < 256; ++i) {
            final float f = SmallFloat.byte315ToFloat((byte)i);
            BM25Similarity.NORM_TABLE[i] = 1.0f / (f * f);
        }
        BM25Similarity.NORM_TABLE[0] = 1.0f / BM25Similarity.NORM_TABLE[255];
    }
    
    private class BM25DocScorer extends SimScorer
    {
        private final BM25Stats stats;
        private final float weightValue;
        private final NumericDocValues norms;
        private final float[] cache;
        
        BM25DocScorer(final BM25Stats stats, final NumericDocValues norms) throws IOException {
            this.stats = stats;
            this.weightValue = stats.weight * (BM25Similarity.this.k1 + 1.0f);
            this.cache = stats.cache;
            this.norms = norms;
        }
        
        @Override
        public float score(final int doc, final float freq) {
            final float norm = (this.norms == null) ? BM25Similarity.this.k1 : this.cache[(byte)this.norms.get(doc) & 0xFF];
            return this.weightValue * freq / (freq + norm);
        }
        
        @Override
        public Explanation explain(final int doc, final Explanation freq) {
            return BM25Similarity.this.explainScore(doc, freq, this.stats, this.norms);
        }
        
        @Override
        public float computeSlopFactor(final int distance) {
            return BM25Similarity.this.sloppyFreq(distance);
        }
        
        @Override
        public float computePayloadFactor(final int doc, final int start, final int end, final BytesRef payload) {
            return BM25Similarity.this.scorePayload(doc, start, end, payload);
        }
    }
    
    private static class BM25Stats extends SimWeight
    {
        private final Explanation idf;
        private final float avgdl;
        private float boost;
        private float weight;
        private final String field;
        private final float[] cache;
        
        BM25Stats(final String field, final Explanation idf, final float avgdl, final float[] cache) {
            this.field = field;
            this.idf = idf;
            this.avgdl = avgdl;
            this.cache = cache;
            this.normalize(1.0f, 1.0f);
        }
        
        @Override
        public float getValueForNormalization() {
            return this.weight * this.weight;
        }
        
        @Override
        public void normalize(final float queryNorm, final float boost) {
            this.boost = boost;
            this.weight = this.idf.getValue() * boost;
        }
    }
}
