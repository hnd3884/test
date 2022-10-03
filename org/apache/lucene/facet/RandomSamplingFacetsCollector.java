package org.apache.lucene.facet;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.DocIdSetIterator;
import java.io.IOException;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.FixedBitSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RandomSamplingFacetsCollector extends FacetsCollector
{
    private static final int NOT_CALCULATED = -1;
    private final int sampleSize;
    private final XORShift64Random random;
    private double samplingRate;
    private List<MatchingDocs> sampledDocs;
    private int totalHits;
    private int leftoverBin;
    private int leftoverIndex;
    
    public RandomSamplingFacetsCollector(final int sampleSize) {
        this(sampleSize, 0L);
    }
    
    public RandomSamplingFacetsCollector(final int sampleSize, final long seed) {
        super(false);
        this.totalHits = -1;
        this.leftoverBin = -1;
        this.leftoverIndex = -1;
        this.sampleSize = sampleSize;
        this.random = new XORShift64Random(seed);
        this.sampledDocs = null;
    }
    
    @Override
    public List<MatchingDocs> getMatchingDocs() {
        final List<MatchingDocs> matchingDocs = super.getMatchingDocs();
        if (this.totalHits == -1) {
            this.totalHits = 0;
            for (final MatchingDocs md : matchingDocs) {
                this.totalHits += md.totalHits;
            }
        }
        if (this.totalHits <= this.sampleSize) {
            return matchingDocs;
        }
        if (this.sampledDocs == null) {
            this.samplingRate = 1.0 * this.sampleSize / this.totalHits;
            this.sampledDocs = this.createSampledDocs(matchingDocs);
        }
        return this.sampledDocs;
    }
    
    public List<MatchingDocs> getOriginalMatchingDocs() {
        return super.getMatchingDocs();
    }
    
    private List<MatchingDocs> createSampledDocs(final List<MatchingDocs> matchingDocsList) {
        final List<MatchingDocs> sampledDocsList = new ArrayList<MatchingDocs>(matchingDocsList.size());
        for (final MatchingDocs docs : matchingDocsList) {
            sampledDocsList.add(this.createSample(docs));
        }
        return sampledDocsList;
    }
    
    private MatchingDocs createSample(final MatchingDocs docs) {
        final int maxdoc = docs.context.reader().maxDoc();
        final FixedBitSet sampleDocs = new FixedBitSet(maxdoc);
        final int binSize = (int)(1.0 / this.samplingRate);
        try {
            int counter = 0;
            int limit;
            int randomIndex;
            if (this.leftoverBin != -1) {
                limit = this.leftoverBin;
                randomIndex = this.leftoverIndex;
            }
            else {
                limit = binSize;
                randomIndex = this.random.nextInt(binSize);
            }
            final DocIdSetIterator it = docs.bits.iterator();
            for (int doc = it.nextDoc(); doc != Integer.MAX_VALUE; doc = it.nextDoc()) {
                if (counter == randomIndex) {
                    sampleDocs.set(doc);
                }
                if (++counter >= limit) {
                    counter = 0;
                    limit = binSize;
                    randomIndex = this.random.nextInt(binSize);
                }
            }
            if (counter == 0) {
                final int n = -1;
                this.leftoverIndex = n;
                this.leftoverBin = n;
            }
            else {
                this.leftoverBin = limit - counter;
                if (randomIndex > counter) {
                    this.leftoverIndex = randomIndex - counter;
                }
                else if (randomIndex < counter) {
                    this.leftoverIndex = -1;
                }
            }
            return new MatchingDocs(docs.context, (DocIdSet)new BitDocIdSet((BitSet)sampleDocs), docs.totalHits, null);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public FacetResult amortizeFacetCounts(final FacetResult res, final FacetsConfig config, final IndexSearcher searcher) throws IOException {
        if (res == null || this.totalHits <= this.sampleSize) {
            return res;
        }
        final LabelAndValue[] fixedLabelValues = new LabelAndValue[res.labelValues.length];
        final IndexReader reader = searcher.getIndexReader();
        final FacetsConfig.DimConfig dimConfig = config.getDimConfig(res.dim);
        final String[] childPath = new String[res.path.length + 2];
        childPath[0] = res.dim;
        System.arraycopy(res.path, 0, childPath, 1, res.path.length);
        for (int i = 0; i < res.labelValues.length; ++i) {
            childPath[res.path.length + 1] = res.labelValues[i].label;
            final String fullPath = FacetsConfig.pathToString(childPath, childPath.length);
            final int max = reader.docFreq(new Term(dimConfig.indexFieldName, fullPath));
            int correctedCount = (int)(res.labelValues[i].value.doubleValue() / this.samplingRate);
            correctedCount = Math.min(max, correctedCount);
            fixedLabelValues[i] = new LabelAndValue(res.labelValues[i].label, correctedCount);
        }
        int correctedTotalCount = res.value.intValue();
        if (correctedTotalCount > 0) {
            correctedTotalCount = Math.min(reader.numDocs(), (int)(res.value.doubleValue() / this.samplingRate));
        }
        return new FacetResult(res.dim, res.path, correctedTotalCount, fixedLabelValues, res.childCount);
    }
    
    public double getSamplingRate() {
        return this.samplingRate;
    }
    
    private static class XORShift64Random
    {
        private long x;
        
        public XORShift64Random(final long seed) {
            this.x = ((seed == 0L) ? -559038737L : seed);
        }
        
        public long randomLong() {
            this.x ^= this.x << 21;
            this.x ^= this.x >>> 35;
            return this.x ^= this.x << 4;
        }
        
        public int nextInt(final int n) {
            final int res = (int)(this.randomLong() % n);
            return (res < 0) ? (-res) : res;
        }
    }
}
