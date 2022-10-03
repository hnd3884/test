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
import org.apache.lucene.search.ConstantScoreWeight;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiDocValues;
import org.apache.lucene.util.LongBitSet;
import org.apache.lucene.search.Query;

final class GlobalOrdinalsQuery extends Query
{
    private final LongBitSet foundOrds;
    private final String joinField;
    private final MultiDocValues.OrdinalMap globalOrds;
    private final Query toQuery;
    private final Query fromQuery;
    private final IndexReader indexReader;
    
    GlobalOrdinalsQuery(final LongBitSet foundOrds, final String joinField, final MultiDocValues.OrdinalMap globalOrds, final Query toQuery, final Query fromQuery, final IndexReader indexReader) {
        this.foundOrds = foundOrds;
        this.joinField = joinField;
        this.globalOrds = globalOrds;
        this.toQuery = toQuery;
        this.fromQuery = fromQuery;
        this.indexReader = indexReader;
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return (Weight)new W(this, this.toQuery.createWeight(searcher, false));
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
        final GlobalOrdinalsQuery that = (GlobalOrdinalsQuery)o;
        return this.fromQuery.equals((Object)that.fromQuery) && this.joinField.equals(that.joinField) && this.toQuery.equals((Object)that.toQuery) && this.indexReader.equals((Object)that.indexReader);
    }
    
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.joinField.hashCode();
        result = 31 * result + this.toQuery.hashCode();
        result = 31 * result + this.fromQuery.hashCode();
        result = 31 * result + this.indexReader.hashCode();
        return result;
    }
    
    public String toString(final String field) {
        return "GlobalOrdinalsQuery{joinField=" + this.joinField + '}' + ToStringUtils.boost(this.getBoost());
    }
    
    final class W extends ConstantScoreWeight
    {
        private final Weight approximationWeight;
        
        W(final Query query, final Weight approximationWeight) {
            super(query);
            this.approximationWeight = approximationWeight;
        }
        
        public void extractTerms(final Set<Term> terms) {
        }
        
        public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
            final SortedDocValues values = DocValues.getSorted(context.reader(), GlobalOrdinalsQuery.this.joinField);
            if (values == null) {
                return Explanation.noMatch("Not a match", new Explanation[0]);
            }
            final int segmentOrd = values.getOrd(doc);
            if (segmentOrd == -1) {
                return Explanation.noMatch("Not a match", new Explanation[0]);
            }
            final BytesRef joinValue = values.lookupOrd(segmentOrd);
            int ord;
            if (GlobalOrdinalsQuery.this.globalOrds != null) {
                ord = (int)GlobalOrdinalsQuery.this.globalOrds.getGlobalOrds(context.ord).get(segmentOrd);
            }
            else {
                ord = segmentOrd;
            }
            if (!GlobalOrdinalsQuery.this.foundOrds.get((long)ord)) {
                return Explanation.noMatch("Not a match, join value " + Term.toString(joinValue), new Explanation[0]);
            }
            return Explanation.match(this.score(), "A match, join value " + Term.toString(joinValue), new Explanation[0]);
        }
        
        public Scorer scorer(final LeafReaderContext context) throws IOException {
            final SortedDocValues values = DocValues.getSorted(context.reader(), GlobalOrdinalsQuery.this.joinField);
            if (values == null) {
                return null;
            }
            final Scorer approximationScorer = this.approximationWeight.scorer(context);
            if (approximationScorer == null) {
                return null;
            }
            if (GlobalOrdinalsQuery.this.globalOrds != null) {
                return new OrdinalMapScorer((Weight)this, this.score(), GlobalOrdinalsQuery.this.foundOrds, values, approximationScorer.iterator(), GlobalOrdinalsQuery.this.globalOrds.getGlobalOrds(context.ord));
            }
            return new SegmentOrdinalScorer((Weight)this, this.score(), GlobalOrdinalsQuery.this.foundOrds, values, approximationScorer.iterator());
        }
    }
    
    static final class OrdinalMapScorer extends BaseGlobalOrdinalScorer
    {
        final LongBitSet foundOrds;
        final LongValues segmentOrdToGlobalOrdLookup;
        
        public OrdinalMapScorer(final Weight weight, final float score, final LongBitSet foundOrds, final SortedDocValues values, final DocIdSetIterator approximationScorer, final LongValues segmentOrdToGlobalOrdLookup) {
            super(weight, values, approximationScorer);
            this.score = score;
            this.foundOrds = foundOrds;
            this.segmentOrdToGlobalOrdLookup = segmentOrdToGlobalOrdLookup;
        }
        
        @Override
        protected TwoPhaseIterator createTwoPhaseIterator(final DocIdSetIterator approximation) {
            return new TwoPhaseIterator(approximation) {
                public boolean matches() throws IOException {
                    final long segmentOrd = OrdinalMapScorer.this.values.getOrd(this.approximation.docID());
                    if (segmentOrd != -1L) {
                        final long globalOrd = OrdinalMapScorer.this.segmentOrdToGlobalOrdLookup.get(segmentOrd);
                        if (OrdinalMapScorer.this.foundOrds.get(globalOrd)) {
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
        final LongBitSet foundOrds;
        
        public SegmentOrdinalScorer(final Weight weight, final float score, final LongBitSet foundOrds, final SortedDocValues values, final DocIdSetIterator approximationScorer) {
            super(weight, values, approximationScorer);
            this.score = score;
            this.foundOrds = foundOrds;
        }
        
        @Override
        protected TwoPhaseIterator createTwoPhaseIterator(final DocIdSetIterator approximation) {
            return new TwoPhaseIterator(approximation) {
                public boolean matches() throws IOException {
                    final long segmentOrd = SegmentOrdinalScorer.this.values.getOrd(this.approximation.docID());
                    return segmentOrd != -1L && SegmentOrdinalScorer.this.foundOrds.get(segmentOrd);
                }
                
                public float matchCost() {
                    return 100.0f;
                }
            };
        }
    }
}
