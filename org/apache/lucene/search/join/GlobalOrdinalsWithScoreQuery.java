package org.apache.lucene.search.join;

import org.apache.lucene.search.TwoPhaseIterator;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.LongValues;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.util.Set;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiDocValues;
import org.apache.lucene.search.Query;

final class GlobalOrdinalsWithScoreQuery extends Query
{
    private final GlobalOrdinalsWithScoreCollector collector;
    private final String joinField;
    private final MultiDocValues.OrdinalMap globalOrds;
    private final Query toQuery;
    private final Query fromQuery;
    private final int min;
    private final int max;
    private final IndexReader indexReader;
    
    GlobalOrdinalsWithScoreQuery(final GlobalOrdinalsWithScoreCollector collector, final String joinField, final MultiDocValues.OrdinalMap globalOrds, final Query toQuery, final Query fromQuery, final int min, final int max, final IndexReader indexReader) {
        this.collector = collector;
        this.joinField = joinField;
        this.globalOrds = globalOrds;
        this.toQuery = toQuery;
        this.fromQuery = fromQuery;
        this.min = min;
        this.max = max;
        this.indexReader = indexReader;
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return new W(this, this.toQuery.createWeight(searcher, false));
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final GlobalOrdinalsWithScoreQuery that = (GlobalOrdinalsWithScoreQuery)o;
        return this.min == that.min && this.max == that.max && this.joinField.equals(that.joinField) && this.fromQuery.equals((Object)that.fromQuery) && this.toQuery.equals((Object)that.toQuery) && this.indexReader.equals((Object)that.indexReader);
    }
    
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.joinField.hashCode();
        result = 31 * result + this.toQuery.hashCode();
        result = 31 * result + this.fromQuery.hashCode();
        result = 31 * result + this.min;
        result = 31 * result + this.max;
        result = 31 * result + this.indexReader.hashCode();
        return result;
    }
    
    public String toString(final String field) {
        return "GlobalOrdinalsQuery{joinField=" + this.joinField + "min=" + this.min + "max=" + this.max + "fromQuery=" + this.fromQuery + '}' + ToStringUtils.boost(this.getBoost());
    }
    
    final class W extends Weight
    {
        private final Weight approximationWeight;
        
        W(final Query query, final Weight approximationWeight) {
            super(query);
            this.approximationWeight = approximationWeight;
        }
        
        public void extractTerms(final Set<Term> terms) {
        }
        
        public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
            final SortedDocValues values = DocValues.getSorted(context.reader(), GlobalOrdinalsWithScoreQuery.this.joinField);
            if (values == null) {
                return Explanation.noMatch("Not a match", new Explanation[0]);
            }
            final int segmentOrd = values.getOrd(doc);
            if (segmentOrd == -1) {
                return Explanation.noMatch("Not a match", new Explanation[0]);
            }
            final BytesRef joinValue = values.lookupOrd(segmentOrd);
            int ord;
            if (GlobalOrdinalsWithScoreQuery.this.globalOrds != null) {
                ord = (int)GlobalOrdinalsWithScoreQuery.this.globalOrds.getGlobalOrds(context.ord).get(segmentOrd);
            }
            else {
                ord = segmentOrd;
            }
            if (!GlobalOrdinalsWithScoreQuery.this.collector.match(ord)) {
                return Explanation.noMatch("Not a match, join value " + Term.toString(joinValue), new Explanation[0]);
            }
            final float score = GlobalOrdinalsWithScoreQuery.this.collector.score(ord);
            return Explanation.match(score, "A match, join value " + Term.toString(joinValue), new Explanation[0]);
        }
        
        public float getValueForNormalization() throws IOException {
            return 1.0f;
        }
        
        public void normalize(final float norm, final float boost) {
        }
        
        public Scorer scorer(final LeafReaderContext context) throws IOException {
            final SortedDocValues values = DocValues.getSorted(context.reader(), GlobalOrdinalsWithScoreQuery.this.joinField);
            if (values == null) {
                return null;
            }
            final Scorer approximationScorer = this.approximationWeight.scorer(context);
            if (approximationScorer == null) {
                return null;
            }
            if (GlobalOrdinalsWithScoreQuery.this.globalOrds != null) {
                return new OrdinalMapScorer(this, GlobalOrdinalsWithScoreQuery.this.collector, values, approximationScorer.iterator(), GlobalOrdinalsWithScoreQuery.this.globalOrds.getGlobalOrds(context.ord));
            }
            return new SegmentOrdinalScorer(this, GlobalOrdinalsWithScoreQuery.this.collector, values, approximationScorer.iterator());
        }
    }
    
    static final class OrdinalMapScorer extends BaseGlobalOrdinalScorer
    {
        final LongValues segmentOrdToGlobalOrdLookup;
        final GlobalOrdinalsWithScoreCollector collector;
        
        public OrdinalMapScorer(final Weight weight, final GlobalOrdinalsWithScoreCollector collector, final SortedDocValues values, final DocIdSetIterator approximation, final LongValues segmentOrdToGlobalOrdLookup) {
            super(weight, values, approximation);
            this.segmentOrdToGlobalOrdLookup = segmentOrdToGlobalOrdLookup;
            this.collector = collector;
        }
        
        @Override
        protected TwoPhaseIterator createTwoPhaseIterator(final DocIdSetIterator approximation) {
            return new TwoPhaseIterator(approximation) {
                public boolean matches() throws IOException {
                    final long segmentOrd = OrdinalMapScorer.this.values.getOrd(this.approximation.docID());
                    if (segmentOrd != -1L) {
                        final int globalOrd = (int)OrdinalMapScorer.this.segmentOrdToGlobalOrdLookup.get(segmentOrd);
                        if (OrdinalMapScorer.this.collector.match(globalOrd)) {
                            OrdinalMapScorer.this.score = OrdinalMapScorer.this.collector.score(globalOrd);
                            return true;
                        }
                    }
                    return false;
                }
                
                public float matchCost() {
                    return 100.0f;
                }
            };
        }
    }
    
    static final class SegmentOrdinalScorer extends BaseGlobalOrdinalScorer
    {
        final GlobalOrdinalsWithScoreCollector collector;
        
        public SegmentOrdinalScorer(final Weight weight, final GlobalOrdinalsWithScoreCollector collector, final SortedDocValues values, final DocIdSetIterator approximation) {
            super(weight, values, approximation);
            this.collector = collector;
        }
        
        @Override
        protected TwoPhaseIterator createTwoPhaseIterator(final DocIdSetIterator approximation) {
            return new TwoPhaseIterator(approximation) {
                public boolean matches() throws IOException {
                    final int segmentOrd = SegmentOrdinalScorer.this.values.getOrd(this.approximation.docID());
                    if (segmentOrd != -1 && SegmentOrdinalScorer.this.collector.match(segmentOrd)) {
                        SegmentOrdinalScorer.this.score = SegmentOrdinalScorer.this.collector.score(segmentOrd);
                        return true;
                    }
                    return false;
                }
                
                public float matchCost() {
                    return 100.0f;
                }
            };
        }
    }
}
