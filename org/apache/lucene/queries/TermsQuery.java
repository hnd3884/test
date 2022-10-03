package org.apache.lucene.queries;

import java.util.Objects;
import org.apache.lucene.index.TermState;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.search.BulkScorer;
import org.apache.lucene.search.ConstantScoreScorer;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.DocIdSet;
import java.util.Iterator;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.Fields;
import java.util.List;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.util.DocIdSetBuilder;
import org.apache.lucene.search.DocIdSetIterator;
import java.util.ArrayList;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Set;
import org.apache.lucene.search.ConstantScoreWeight;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import java.util.Collections;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.index.IndexReader;
import java.util.Arrays;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.ArrayUtil;
import java.util.SortedSet;
import org.apache.lucene.index.Term;
import java.util.Collection;
import org.apache.lucene.index.PrefixCodedTerms;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.search.Query;

public class TermsQuery extends Query implements Accountable
{
    private static final long BASE_RAM_BYTES_USED;
    static final int BOOLEAN_REWRITE_TERM_COUNT_THRESHOLD = 16;
    private final PrefixCodedTerms termData;
    private final int termDataHashCode;
    
    public TermsQuery(final Collection<Term> terms) {
        final Term[] sortedTerms = terms.toArray(new Term[terms.size()]);
        final boolean sorted = terms instanceof SortedSet && ((SortedSet)terms).comparator() == null;
        if (!sorted) {
            ArrayUtil.timSort((Comparable[])sortedTerms);
        }
        final PrefixCodedTerms.Builder builder = new PrefixCodedTerms.Builder();
        Term previous = null;
        for (final Term term : sortedTerms) {
            if (!term.equals((Object)previous)) {
                builder.add(term);
            }
            previous = term;
        }
        this.termData = builder.finish();
        this.termDataHashCode = this.termData.hashCode();
    }
    
    public TermsQuery(final String field, final Collection<BytesRef> terms) {
        final BytesRef[] sortedTerms = terms.toArray(new BytesRef[terms.size()]);
        final boolean sorted = terms instanceof SortedSet && ((SortedSet)terms).comparator() == null;
        if (!sorted) {
            ArrayUtil.timSort((Comparable[])sortedTerms);
        }
        final PrefixCodedTerms.Builder builder = new PrefixCodedTerms.Builder();
        BytesRefBuilder previous = null;
        for (final BytesRef term : sortedTerms) {
            Label_0143: {
                if (previous == null) {
                    previous = new BytesRefBuilder();
                }
                else if (previous.get().equals((Object)term)) {
                    break Label_0143;
                }
                builder.add(field, term);
                previous.copyBytes(term);
            }
        }
        this.termData = builder.finish();
        this.termDataHashCode = this.termData.hashCode();
    }
    
    public TermsQuery(final String field, final BytesRef... terms) {
        this(field, Arrays.asList(terms));
    }
    
    public TermsQuery(final Term... terms) {
        this(Arrays.asList(terms));
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final int threshold = Math.min(16, BooleanQuery.getMaxClauseCount());
        if (this.termData.size() <= threshold) {
            final BooleanQuery.Builder bq = new BooleanQuery.Builder();
            final PrefixCodedTerms.TermIterator iterator = this.termData.iterator();
            for (BytesRef term = iterator.next(); term != null; term = iterator.next()) {
                bq.add((Query)new TermQuery(new Term(iterator.field(), BytesRef.deepCopyOf(term))), BooleanClause.Occur.SHOULD);
            }
            return (Query)new ConstantScoreQuery((Query)bq.build());
        }
        return super.rewrite(reader);
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final TermsQuery that = (TermsQuery)obj;
        return this.termDataHashCode == that.termDataHashCode && this.termData.equals((Object)that.termData);
    }
    
    public int hashCode() {
        return 31 * super.hashCode() + this.termDataHashCode;
    }
    
    public String toString(final String defaultField) {
        final StringBuilder builder = new StringBuilder();
        boolean first = true;
        final PrefixCodedTerms.TermIterator iterator = this.termData.iterator();
        for (BytesRef term = iterator.next(); term != null; term = iterator.next()) {
            if (!first) {
                builder.append(' ');
            }
            first = false;
            builder.append(new Term(iterator.field(), term).toString());
        }
        builder.append(ToStringUtils.boost(this.getBoost()));
        return builder.toString();
    }
    
    public long ramBytesUsed() {
        return TermsQuery.BASE_RAM_BYTES_USED + this.termData.ramBytesUsed();
    }
    
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return (Weight)new ConstantScoreWeight(this) {
            public void extractTerms(final Set<Term> terms) {
            }
            
            private WeightOrDocIdSet rewrite(final LeafReaderContext context) throws IOException {
                final LeafReader reader = context.reader();
                final int threshold = Math.min(16, BooleanQuery.getMaxClauseCount());
                assert TermsQuery.this.termData.size() > threshold : "Query should have been rewritten";
                List<TermAndState> matchingTerms = new ArrayList<TermAndState>(threshold);
                DocIdSetBuilder builder = null;
                final Fields fields = reader.fields();
                String lastField = null;
                Terms terms = null;
                TermsEnum termsEnum = null;
                PostingsEnum docs = null;
                final PrefixCodedTerms.TermIterator iterator = TermsQuery.this.termData.iterator();
                for (BytesRef term = iterator.next(); term != null; term = iterator.next()) {
                    final String field = iterator.field();
                    if (field != lastField) {
                        terms = fields.terms(field);
                        if (terms == null) {
                            termsEnum = null;
                        }
                        else {
                            termsEnum = terms.iterator();
                        }
                        lastField = field;
                    }
                    if (termsEnum != null && termsEnum.seekExact(term)) {
                        if (matchingTerms == null) {
                            docs = termsEnum.postings(docs, 0);
                            builder.add((DocIdSetIterator)docs);
                        }
                        else if (matchingTerms.size() < threshold) {
                            matchingTerms.add(new TermAndState(field, termsEnum));
                        }
                        else {
                            assert matchingTerms.size() == threshold;
                            builder = new DocIdSetBuilder(reader.maxDoc());
                            docs = termsEnum.postings(docs, 0);
                            builder.add((DocIdSetIterator)docs);
                            for (final TermAndState t : matchingTerms) {
                                t.termsEnum.seekExact(t.term, t.state);
                                docs = t.termsEnum.postings(docs, 0);
                                builder.add((DocIdSetIterator)docs);
                            }
                            matchingTerms = null;
                        }
                    }
                }
                if (matchingTerms != null) {
                    assert builder == null;
                    final BooleanQuery.Builder bq = new BooleanQuery.Builder();
                    for (final TermAndState t2 : matchingTerms) {
                        final TermContext termContext = new TermContext(searcher.getTopReaderContext());
                        termContext.register(t2.state, context.ord, t2.docFreq, t2.totalTermFreq);
                        bq.add((Query)new TermQuery(new Term(t2.field, t2.term), termContext), BooleanClause.Occur.SHOULD);
                    }
                    final Query q = (Query)new ConstantScoreQuery((Query)bq.build());
                    final Weight weight = searcher.rewrite(q).createWeight(searcher, needsScores);
                    weight.normalize(1.0f, this.score());
                    return new WeightOrDocIdSet(weight);
                }
                else {
                    assert builder != null;
                    return new WeightOrDocIdSet(builder.build());
                }
            }
            
            private Scorer scorer(final DocIdSet set) throws IOException {
                if (set == null) {
                    return null;
                }
                final DocIdSetIterator disi = set.iterator();
                if (disi == null) {
                    return null;
                }
                return (Scorer)new ConstantScoreScorer((Weight)this, this.score(), disi);
            }
            
            public BulkScorer bulkScorer(final LeafReaderContext context) throws IOException {
                final WeightOrDocIdSet weightOrBitSet = this.rewrite(context);
                if (weightOrBitSet.weight != null) {
                    return weightOrBitSet.weight.bulkScorer(context);
                }
                final Scorer scorer = this.scorer(weightOrBitSet.set);
                if (scorer == null) {
                    return null;
                }
                return (BulkScorer)new Weight.DefaultBulkScorer(scorer);
            }
            
            public Scorer scorer(final LeafReaderContext context) throws IOException {
                final WeightOrDocIdSet weightOrBitSet = this.rewrite(context);
                if (weightOrBitSet.weight != null) {
                    return weightOrBitSet.weight.scorer(context);
                }
                return this.scorer(weightOrBitSet.set);
            }
        };
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance((Class)TermsQuery.class);
    }
    
    private static class TermAndState
    {
        final String field;
        final TermsEnum termsEnum;
        final BytesRef term;
        final TermState state;
        final int docFreq;
        final long totalTermFreq;
        
        TermAndState(final String field, final TermsEnum termsEnum) throws IOException {
            this.field = field;
            this.termsEnum = termsEnum;
            this.term = BytesRef.deepCopyOf(termsEnum.term());
            this.state = termsEnum.termState();
            this.docFreq = termsEnum.docFreq();
            this.totalTermFreq = termsEnum.totalTermFreq();
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
