package org.apache.lucene.search;

import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Set;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.index.IndexReaderContext;
import java.util.Objects;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;

public class TermQuery extends Query
{
    private final Term term;
    private final TermContext perReaderTermState;
    
    public TermQuery(final Term t) {
        this.term = Objects.requireNonNull(t);
        this.perReaderTermState = null;
    }
    
    public TermQuery(final Term t, final TermContext states) {
        assert states != null;
        this.term = Objects.requireNonNull(t);
        if (!states.hasOnlyRealTerms()) {
            throw new IllegalArgumentException("Term queries must be created on real terms");
        }
        this.perReaderTermState = Objects.requireNonNull(states);
    }
    
    public Term getTerm() {
        return this.term;
    }
    
    @Override
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final IndexReaderContext context = searcher.getTopReaderContext();
        TermContext termState;
        if (this.perReaderTermState == null || this.perReaderTermState.topReaderContext != context) {
            termState = TermContext.build(context, this.term);
        }
        else {
            termState = this.perReaderTermState;
        }
        return new TermWeight(searcher, needsScores, termState);
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        if (!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }
        buffer.append(this.term.text());
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof TermQuery)) {
            return false;
        }
        final TermQuery other = (TermQuery)o;
        return super.equals(o) && this.term.equals(other.term);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ this.term.hashCode();
    }
    
    final class TermWeight extends Weight
    {
        private final Similarity similarity;
        private final Similarity.SimWeight stats;
        private final TermContext termStates;
        private final boolean needsScores;
        
        public TermWeight(final IndexSearcher searcher, final boolean needsScores, final TermContext termStates) throws IOException {
            super(TermQuery.this);
            this.needsScores = needsScores;
            assert termStates != null : "TermContext must not be null";
            assert termStates.hasOnlyRealTerms();
            this.termStates = termStates;
            this.similarity = searcher.getSimilarity(needsScores);
            CollectionStatistics collectionStats;
            TermStatistics termStats;
            if (needsScores) {
                collectionStats = searcher.collectionStatistics(TermQuery.this.term.field());
                termStats = searcher.termStatistics(TermQuery.this.term, termStates);
            }
            else {
                final int maxDoc = searcher.getIndexReader().maxDoc();
                final int docFreq = termStates.docFreq();
                final long totalTermFreq = termStates.totalTermFreq();
                collectionStats = new CollectionStatistics(TermQuery.this.term.field(), maxDoc, -1L, -1L, -1L);
                termStats = new TermStatistics(TermQuery.this.term.bytes(), docFreq, totalTermFreq);
            }
            this.stats = this.similarity.computeWeight(collectionStats, termStats);
        }
        
        @Override
        public void extractTerms(final Set<Term> terms) {
            terms.add(TermQuery.this.getTerm());
        }
        
        @Override
        public String toString() {
            return "weight(" + TermQuery.this + ")";
        }
        
        @Override
        public float getValueForNormalization() {
            return this.stats.getValueForNormalization();
        }
        
        @Override
        public void normalize(final float queryNorm, final float boost) {
            this.stats.normalize(queryNorm, boost);
        }
        
        @Override
        public Scorer scorer(final LeafReaderContext context) throws IOException {
            assert this.termStates.topReaderContext == ReaderUtil.getTopLevelContext(context) : "The top-reader used to create Weight (" + this.termStates.topReaderContext + ") is not the same as the current reader's top-reader (" + ReaderUtil.getTopLevelContext(context);
            final TermsEnum termsEnum = this.getTermsEnum(context);
            if (termsEnum == null) {
                return null;
            }
            final PostingsEnum docs = termsEnum.postings(null, this.needsScores ? 8 : 0);
            assert docs != null;
            return new TermScorer(this, docs, this.similarity.simScorer(this.stats, context));
        }
        
        private TermsEnum getTermsEnum(final LeafReaderContext context) throws IOException {
            final TermState state = this.termStates.get(context.ord);
            if (state != null) {
                final TermsEnum termsEnum = context.reader().terms(TermQuery.this.term.field()).iterator();
                termsEnum.seekExact(TermQuery.this.term.bytes(), state);
                return termsEnum;
            }
            assert this.termNotInReader(context.reader(), TermQuery.this.term) : "no termstate found but term exists in reader term=" + TermQuery.this.term;
            return null;
        }
        
        private boolean termNotInReader(final LeafReader reader, final Term term) throws IOException {
            return reader.docFreq(term) == 0;
        }
        
        @Override
        public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
            final Scorer scorer = this.scorer(context);
            if (scorer != null) {
                final int newDoc = scorer.iterator().advance(doc);
                if (newDoc == doc) {
                    final float freq = (float)scorer.freq();
                    final Similarity.SimScorer docScorer = this.similarity.simScorer(this.stats, context);
                    final Explanation freqExplanation = Explanation.match(freq, "termFreq=" + freq, new Explanation[0]);
                    final Explanation scoreExplanation = docScorer.explain(doc, freqExplanation);
                    return Explanation.match(scoreExplanation.getValue(), "weight(" + this.getQuery() + " in " + doc + ") [" + this.similarity.getClass().getSimpleName() + "], result of:", scoreExplanation);
                }
            }
            return Explanation.noMatch("no matching term", new Explanation[0]);
        }
    }
}
