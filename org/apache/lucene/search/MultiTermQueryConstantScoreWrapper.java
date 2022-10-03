package org.apache.lucene.search;

import java.util.Objects;
import java.util.Iterator;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.util.DocIdSetBuilder;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.lucene.index.TermState;
import org.apache.lucene.util.BytesRef;
import java.util.List;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.ToStringUtils;

final class MultiTermQueryConstantScoreWrapper<Q extends MultiTermQuery> extends Query
{
    private static final int BOOLEAN_REWRITE_TERM_COUNT_THRESHOLD = 16;
    protected final Q query;
    
    protected MultiTermQueryConstantScoreWrapper(final Q query) {
        this.query = query;
    }
    
    @Override
    public String toString(final String field) {
        return this.query.toString(field) + ToStringUtils.boost(this.getBoost());
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final MultiTermQueryConstantScoreWrapper<?> that = (MultiTermQueryConstantScoreWrapper<?>)o;
        return this.query.equals(that.query);
    }
    
    @Override
    public final int hashCode() {
        return 31 * super.hashCode() + this.query.hashCode();
    }
    
    public final String getField() {
        return this.query.getField();
    }
    
    @Override
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return new ConstantScoreWeight(this) {
            private boolean collectTerms(final LeafReaderContext context, final TermsEnum termsEnum, final List<TermAndState> terms) throws IOException {
                for (int threshold = Math.min(16, BooleanQuery.getMaxClauseCount()), i = 0; i < threshold; ++i) {
                    final BytesRef term = termsEnum.next();
                    if (term == null) {
                        return true;
                    }
                    final TermState state = termsEnum.termState();
                    if (!state.isRealTerm()) {
                        return false;
                    }
                    terms.add(new TermAndState(BytesRef.deepCopyOf(term), state, termsEnum.docFreq(), termsEnum.totalTermFreq()));
                }
                return termsEnum.next() == null;
            }
            
            private WeightOrDocIdSet rewrite(final LeafReaderContext context) throws IOException {
                final Terms terms = context.reader().terms(MultiTermQueryConstantScoreWrapper.this.query.field);
                if (terms == null) {
                    return new WeightOrDocIdSet((DocIdSet)null);
                }
                final TermsEnum termsEnum = MultiTermQueryConstantScoreWrapper.this.query.getTermsEnum(terms);
                assert termsEnum != null;
                PostingsEnum docs = null;
                final List<TermAndState> collectedTerms = new ArrayList<TermAndState>();
                if (this.collectTerms(context, termsEnum, collectedTerms)) {
                    final BooleanQuery.Builder bq = new BooleanQuery.Builder();
                    for (final TermAndState t : collectedTerms) {
                        final TermContext termContext = new TermContext(searcher.getTopReaderContext());
                        termContext.register(t.state, context.ord, t.docFreq, t.totalTermFreq);
                        bq.add(new TermQuery(new Term(MultiTermQueryConstantScoreWrapper.this.query.field, t.term), termContext), BooleanClause.Occur.SHOULD);
                    }
                    final Query q = new ConstantScoreQuery(bq.build());
                    final Weight weight = searcher.rewrite(q).createWeight(searcher, needsScores);
                    weight.normalize(1.0f, this.score());
                    return new WeightOrDocIdSet(weight);
                }
                final DocIdSetBuilder builder = new DocIdSetBuilder(context.reader().maxDoc());
                if (!collectedTerms.isEmpty()) {
                    final TermsEnum termsEnum2 = terms.iterator();
                    for (final TermAndState t2 : collectedTerms) {
                        termsEnum2.seekExact(t2.term, t2.state);
                        docs = termsEnum2.postings(docs, 0);
                        builder.add(docs);
                    }
                }
                do {
                    docs = termsEnum.postings(docs, 0);
                    builder.add(docs);
                } while (termsEnum.next() != null);
                return new WeightOrDocIdSet(builder.build());
            }
            
            private Scorer scorer(final DocIdSet set) throws IOException {
                if (set == null) {
                    return null;
                }
                final DocIdSetIterator disi = set.iterator();
                if (disi == null) {
                    return null;
                }
                return new ConstantScoreScorer(this, this.score(), disi);
            }
            
            @Override
            public BulkScorer bulkScorer(final LeafReaderContext context) throws IOException {
                final WeightOrDocIdSet weightOrBitSet = this.rewrite(context);
                if (weightOrBitSet.weight != null) {
                    return weightOrBitSet.weight.bulkScorer(context);
                }
                final Scorer scorer = this.scorer(weightOrBitSet.set);
                if (scorer == null) {
                    return null;
                }
                return new DefaultBulkScorer(scorer);
            }
            
            @Override
            public Scorer scorer(final LeafReaderContext context) throws IOException {
                final WeightOrDocIdSet weightOrBitSet = this.rewrite(context);
                if (weightOrBitSet.weight != null) {
                    return weightOrBitSet.weight.scorer(context);
                }
                return this.scorer(weightOrBitSet.set);
            }
        };
    }
    
    private static class TermAndState
    {
        final BytesRef term;
        final TermState state;
        final int docFreq;
        final long totalTermFreq;
        
        TermAndState(final BytesRef term, final TermState state, final int docFreq, final long totalTermFreq) {
            this.term = term;
            this.state = state;
            this.docFreq = docFreq;
            this.totalTermFreq = totalTermFreq;
        }
    }
    
    private static class WeightOrDocIdSet
    {
        final Weight weight;
        final DocIdSet set;
        
        WeightOrDocIdSet(final Weight weight) {
            this.weight = Objects.requireNonNull(weight);
            this.set = null;
        }
        
        WeightOrDocIdSet(final DocIdSet bitset) {
            this.set = bitset;
            this.weight = null;
        }
    }
}
