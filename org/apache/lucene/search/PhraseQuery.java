package org.apache.lucene.search;

import org.apache.lucene.index.TermState;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.index.PostingsEnum;
import java.util.Objects;
import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.index.TermsEnum;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.BytesRef;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.lucene.index.Term;
import java.util.List;

public class PhraseQuery extends Query
{
    private final boolean mutable;
    private int slop;
    private String field;
    private final List<Term> terms;
    private final List<Integer> positions;
    private static final int TERM_POSNS_SEEK_OPS_PER_DOC = 128;
    private static final int TERM_OPS_PER_POS = 7;
    
    private PhraseQuery(final int slop, final Term[] terms, final int[] positions) {
        if (terms.length != positions.length) {
            throw new IllegalArgumentException("Must have as many terms as positions");
        }
        if (slop < 0) {
            throw new IllegalArgumentException("Slop must be >= 0, got " + slop);
        }
        for (int i = 1; i < terms.length; ++i) {
            if (!terms[i - 1].field().equals(terms[i].field())) {
                throw new IllegalArgumentException("All terms should have the same field");
            }
        }
        for (final int position : positions) {
            if (position < 0) {
                throw new IllegalArgumentException("Positions must be >= 0, got " + position);
            }
        }
        for (int i = 1; i < positions.length; ++i) {
            if (positions[i] < positions[i - 1]) {
                throw new IllegalArgumentException("Positions should not go backwards, got " + positions[i - 1] + " before " + positions[i]);
            }
        }
        this.slop = slop;
        this.terms = Arrays.asList(terms);
        this.positions = new ArrayList<Integer>(positions.length);
        for (final int pos : positions) {
            this.positions.add(pos);
        }
        this.field = ((terms.length == 0) ? null : terms[0].field());
        this.mutable = false;
    }
    
    private static int[] incrementalPositions(final int length) {
        final int[] positions = new int[length];
        for (int i = 0; i < length; ++i) {
            positions[i] = i;
        }
        return positions;
    }
    
    private static Term[] toTerms(final String field, final String... termStrings) {
        final Term[] terms = new Term[termStrings.length];
        for (int i = 0; i < terms.length; ++i) {
            terms[i] = new Term(field, termStrings[i]);
        }
        return terms;
    }
    
    private static Term[] toTerms(final String field, final BytesRef... termBytes) {
        final Term[] terms = new Term[termBytes.length];
        for (int i = 0; i < terms.length; ++i) {
            terms[i] = new Term(field, termBytes[i]);
        }
        return terms;
    }
    
    public PhraseQuery(final int slop, final String field, final String... terms) {
        this(slop, toTerms(field, terms), incrementalPositions(terms.length));
    }
    
    public PhraseQuery(final String field, final String... terms) {
        this(0, field, terms);
    }
    
    public PhraseQuery(final int slop, final String field, final BytesRef... terms) {
        this(slop, toTerms(field, terms), incrementalPositions(terms.length));
    }
    
    public PhraseQuery(final String field, final BytesRef... terms) {
        this(0, field, terms);
    }
    
    public int getSlop() {
        return this.slop;
    }
    
    public Term[] getTerms() {
        return this.terms.toArray(new Term[0]);
    }
    
    public int[] getPositions() {
        final int[] result = new int[this.positions.size()];
        for (int i = 0; i < this.positions.size(); ++i) {
            result[i] = this.positions.get(i);
        }
        return result;
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.terms.isEmpty()) {
            final Query rewritten = new MatchNoDocsQuery();
            rewritten.setBoost(this.getBoost());
            return rewritten;
        }
        if (this.terms.size() == 1) {
            final TermQuery tq = new TermQuery(this.terms.get(0));
            tq.setBoost(this.getBoost());
            return tq;
        }
        if (this.positions.get(0) != 0) {
            final int[] oldPositions = this.getPositions();
            final int[] newPositions = new int[oldPositions.length];
            for (int i = 0; i < oldPositions.length; ++i) {
                newPositions[i] = oldPositions[i] - oldPositions[0];
            }
            final PhraseQuery rewritten2 = new PhraseQuery(this.slop, this.getTerms(), newPositions);
            rewritten2.setBoost(this.getBoost());
            return rewritten2;
        }
        return super.rewrite(reader);
    }
    
    static float termPositionsCost(final TermsEnum termsEnum) throws IOException {
        final int docFreq = termsEnum.docFreq();
        assert docFreq > 0;
        final long totalTermFreq = termsEnum.totalTermFreq();
        final float expOccurrencesInMatchingDoc = (totalTermFreq < docFreq) ? 1.0f : (totalTermFreq / (float)docFreq);
        return 128.0f + expOccurrencesInMatchingDoc * 7.0f;
    }
    
    @Override
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return new PhraseWeight(searcher, needsScores);
    }
    
    @Override
    public String toString(final String f) {
        final Term[] terms = this.getTerms();
        final int[] positions = this.getPositions();
        final StringBuilder buffer = new StringBuilder();
        if (this.field != null && !this.field.equals(f)) {
            buffer.append(this.field);
            buffer.append(":");
        }
        buffer.append("\"");
        int maxPosition;
        if (positions.length == 0) {
            maxPosition = -1;
        }
        else {
            maxPosition = positions[positions.length - 1];
        }
        final String[] pieces = new String[maxPosition + 1];
        for (int i = 0; i < terms.length; ++i) {
            final int pos = positions[i];
            String s = pieces[pos];
            if (s == null) {
                s = terms[i].text();
            }
            else {
                s = s + "|" + terms[i].text();
            }
            pieces[pos] = s;
        }
        for (int i = 0; i < pieces.length; ++i) {
            if (i > 0) {
                buffer.append(' ');
            }
            final String s2 = pieces[i];
            if (s2 == null) {
                buffer.append('?');
            }
            else {
                buffer.append(s2);
            }
        }
        buffer.append("\"");
        if (this.slop != 0) {
            buffer.append("~");
            buffer.append(this.slop);
        }
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final PhraseQuery that = (PhraseQuery)o;
        return this.slop == that.slop && this.terms.equals(that.terms) && this.positions.equals(that.positions);
    }
    
    @Override
    public int hashCode() {
        int h = super.hashCode();
        h = 31 * h + this.slop;
        h = 31 * h + this.terms.hashCode();
        h = 31 * h + this.positions.hashCode();
        return h;
    }
    
    @Deprecated
    public PhraseQuery() {
        this.terms = new ArrayList<Term>();
        this.positions = new ArrayList<Integer>();
        this.mutable = true;
    }
    
    private void ensureMutable(final String method) {
        if (!this.mutable) {
            throw new IllegalStateException("This PhraseQuery has been created with the new PhraseQuery.Builder API. It must not be modified afterwards. The " + method + " method only exists for backward compatibility");
        }
    }
    
    @Deprecated
    public void setSlop(final int s) {
        this.ensureMutable("setSlop");
        if (s < 0) {
            throw new IllegalArgumentException("slop value cannot be negative");
        }
        this.slop = s;
    }
    
    @Deprecated
    public void add(final Term term) {
        int position = 0;
        if (this.positions.size() > 0) {
            position = this.positions.get(this.positions.size() - 1) + 1;
        }
        this.add(term, position);
    }
    
    @Deprecated
    public void add(final Term term, final int position) {
        this.ensureMutable("add");
        Objects.requireNonNull(term, "Term must not be null");
        if (this.positions.size() > 0) {
            final int previousPosition = this.positions.get(this.positions.size() - 1);
            if (position < previousPosition) {
                throw new IllegalArgumentException("Positions must be added in order. Got position=" + position + " while previous position was " + previousPosition);
            }
        }
        else if (position < 0) {
            throw new IllegalArgumentException("Positions must be positive, got " + position);
        }
        if (this.terms.size() == 0) {
            this.field = term.field();
        }
        else if (!term.field().equals(this.field)) {
            throw new IllegalArgumentException("All phrase terms must be in the same field: " + term);
        }
        this.terms.add(term);
        this.positions.add(position);
    }
    
    public static class Builder
    {
        private int slop;
        private final List<Term> terms;
        private final List<Integer> positions;
        
        public Builder() {
            this.slop = 0;
            this.terms = new ArrayList<Term>();
            this.positions = new ArrayList<Integer>();
        }
        
        public Builder setSlop(final int slop) {
            this.slop = slop;
            return this;
        }
        
        public Builder add(final Term term) {
            return this.add(term, this.positions.isEmpty() ? 0 : (1 + this.positions.get(this.positions.size() - 1)));
        }
        
        public Builder add(final Term term, final int position) {
            if (position < 0) {
                throw new IllegalArgumentException("Positions must be >= 0, got " + position);
            }
            if (!this.positions.isEmpty()) {
                final int lastPosition = this.positions.get(this.positions.size() - 1);
                if (position < lastPosition) {
                    throw new IllegalArgumentException("Positions must be added in order, got " + position + " after " + lastPosition);
                }
            }
            if (!this.terms.isEmpty() && !term.field().equals(this.terms.get(0).field())) {
                throw new IllegalArgumentException("All terms must be on the same field, got " + term.field() + " and " + this.terms.get(0).field());
            }
            this.terms.add(term);
            this.positions.add(position);
            return this;
        }
        
        public PhraseQuery build() {
            final Term[] terms = this.terms.toArray(new Term[this.terms.size()]);
            final int[] positions = new int[this.positions.size()];
            for (int i = 0; i < positions.length; ++i) {
                positions[i] = this.positions.get(i);
            }
            return new PhraseQuery(this.slop, terms, positions, null);
        }
    }
    
    static class PostingsAndFreq implements Comparable<PostingsAndFreq>
    {
        final PostingsEnum postings;
        final int position;
        final Term[] terms;
        final int nTerms;
        
        public PostingsAndFreq(final PostingsEnum postings, final int position, final Term... terms) {
            this.postings = postings;
            this.position = position;
            this.nTerms = ((terms == null) ? 0 : terms.length);
            if (this.nTerms > 0) {
                if (terms.length == 1) {
                    this.terms = terms;
                }
                else {
                    final Term[] terms2 = new Term[terms.length];
                    System.arraycopy(terms, 0, terms2, 0, terms.length);
                    Arrays.sort(terms2);
                    this.terms = terms2;
                }
            }
            else {
                this.terms = null;
            }
        }
        
        @Override
        public int compareTo(final PostingsAndFreq other) {
            if (this.position != other.position) {
                return this.position - other.position;
            }
            if (this.nTerms != other.nTerms) {
                return this.nTerms - other.nTerms;
            }
            if (this.nTerms == 0) {
                return 0;
            }
            for (int i = 0; i < this.terms.length; ++i) {
                final int res = this.terms[i].compareTo(other.terms[i]);
                if (res != 0) {
                    return res;
                }
            }
            return 0;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + this.position;
            for (int i = 0; i < this.nTerms; ++i) {
                result = 31 * result + this.terms[i].hashCode();
            }
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final PostingsAndFreq other = (PostingsAndFreq)obj;
            if (this.position != other.position) {
                return false;
            }
            if (this.terms == null) {
                return other.terms == null;
            }
            return Arrays.equals(this.terms, other.terms);
        }
    }
    
    private class PhraseWeight extends Weight
    {
        private final Similarity similarity;
        private final Similarity.SimWeight stats;
        private final boolean needsScores;
        private transient TermContext[] states;
        private final Term[] terms;
        private final int[] positions;
        
        public PhraseWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
            super(PhraseQuery.this);
            this.terms = PhraseQuery.this.getTerms();
            this.positions = PhraseQuery.this.getPositions();
            final int[] positions = PhraseQuery.this.getPositions();
            if (positions.length < 2) {
                throw new IllegalStateException("PhraseWeight does not support less than 2 terms, call rewrite first");
            }
            if (positions[0] != 0) {
                throw new IllegalStateException("PhraseWeight requires that the first position is 0, call rewrite first");
            }
            this.needsScores = needsScores;
            this.similarity = searcher.getSimilarity(needsScores);
            final IndexReaderContext context = searcher.getTopReaderContext();
            this.states = new TermContext[this.terms.length];
            final TermStatistics[] termStats = new TermStatistics[this.terms.length];
            for (int i = 0; i < this.terms.length; ++i) {
                final Term term = this.terms[i];
                this.states[i] = TermContext.build(context, term);
                termStats[i] = searcher.termStatistics(term, this.states[i]);
            }
            this.stats = this.similarity.computeWeight(searcher.collectionStatistics(PhraseQuery.this.field), termStats);
        }
        
        @Override
        public void extractTerms(final Set<Term> queryTerms) {
            Collections.addAll(queryTerms, this.terms);
        }
        
        @Override
        public String toString() {
            return "weight(" + PhraseQuery.this + ")";
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
            assert this.terms.length > 0;
            final LeafReader reader = context.reader();
            final PostingsAndFreq[] postingsFreqs = new PostingsAndFreq[this.terms.length];
            final Terms fieldTerms = reader.terms(PhraseQuery.this.field);
            if (fieldTerms == null) {
                return null;
            }
            if (!fieldTerms.hasPositions()) {
                throw new IllegalStateException("field \"" + PhraseQuery.this.field + "\" was indexed without position data; cannot run PhraseQuery (phrase=" + this.getQuery() + ")");
            }
            final TermsEnum te = fieldTerms.iterator();
            float totalMatchCost = 0.0f;
            int i = 0;
            while (i < this.terms.length) {
                final Term t = this.terms[i];
                final TermState state = this.states[i].get(context.ord);
                if (state == null) {
                    assert this.termNotInReader(reader, t) : "no termstate found but term exists in reader";
                    return null;
                }
                else {
                    te.seekExact(t.bytes(), state);
                    final PostingsEnum postingsEnum = te.postings(null, 24);
                    postingsFreqs[i] = new PostingsAndFreq(postingsEnum, this.positions[i], new Term[] { t });
                    totalMatchCost += PhraseQuery.termPositionsCost(te);
                    ++i;
                }
            }
            if (PhraseQuery.this.slop == 0) {
                ArrayUtil.timSort(postingsFreqs);
            }
            if (PhraseQuery.this.slop == 0) {
                return new ExactPhraseScorer(this, postingsFreqs, this.similarity.simScorer(this.stats, context), this.needsScores, totalMatchCost);
            }
            return new SloppyPhraseScorer(this, postingsFreqs, PhraseQuery.this.slop, this.similarity.simScorer(this.stats, context), this.needsScores, totalMatchCost);
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
                    final float freq = (PhraseQuery.this.slop == 0) ? ((float)scorer.freq()) : ((SloppyPhraseScorer)scorer).sloppyFreq();
                    final Similarity.SimScorer docScorer = this.similarity.simScorer(this.stats, context);
                    final Explanation freqExplanation = Explanation.match(freq, "phraseFreq=" + freq, new Explanation[0]);
                    final Explanation scoreExplanation = docScorer.explain(doc, freqExplanation);
                    return Explanation.match(scoreExplanation.getValue(), "weight(" + this.getQuery() + " in " + doc + ") [" + this.similarity.getClass().getSimpleName() + "], result of:", scoreExplanation);
                }
            }
            return Explanation.noMatch("no matching term", new Explanation[0]);
        }
    }
}
