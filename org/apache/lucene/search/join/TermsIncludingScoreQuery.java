package org.apache.lucene.search.join;

import org.apache.lucene.util.BitSet;
import org.apache.lucene.util.BitSetIterator;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.NumericUtils;
import java.io.PrintStream;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.util.Set;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import java.util.Locale;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.search.Query;

class TermsIncludingScoreQuery extends Query
{
    final String field;
    final boolean multipleValuesPerDocument;
    final BytesRefHash terms;
    final float[] scores;
    final int[] ords;
    final Query originalQuery;
    final Query unwrittenOriginalQuery;
    
    TermsIncludingScoreQuery(final String field, final boolean multipleValuesPerDocument, final BytesRefHash terms, final float[] scores, final Query originalQuery) {
        this.field = field;
        this.multipleValuesPerDocument = multipleValuesPerDocument;
        this.terms = terms;
        this.scores = scores;
        this.originalQuery = originalQuery;
        this.ords = terms.sort(BytesRef.getUTF8SortedAsUnicodeComparator());
        this.unwrittenOriginalQuery = originalQuery;
    }
    
    private TermsIncludingScoreQuery(final String field, final boolean multipleValuesPerDocument, final BytesRefHash terms, final float[] scores, final int[] ords, final Query originalQuery, final Query unwrittenOriginalQuery) {
        this.field = field;
        this.multipleValuesPerDocument = multipleValuesPerDocument;
        this.terms = terms;
        this.scores = scores;
        this.originalQuery = originalQuery;
        this.ords = ords;
        this.unwrittenOriginalQuery = unwrittenOriginalQuery;
    }
    
    public String toString(final String string) {
        return String.format(Locale.ROOT, "TermsIncludingScoreQuery{field=%s;originalQuery=%s}", this.field, this.unwrittenOriginalQuery);
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final Query originalQueryRewrite = this.originalQuery.rewrite(reader);
        if (originalQueryRewrite != this.originalQuery) {
            return new TermsIncludingScoreQuery(this.field, this.multipleValuesPerDocument, this.terms, this.scores, this.ords, originalQueryRewrite, this.originalQuery);
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
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final TermsIncludingScoreQuery other = (TermsIncludingScoreQuery)obj;
        return this.field.equals(other.field) && this.unwrittenOriginalQuery.equals((Object)other.unwrittenOriginalQuery);
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result += 31 * this.field.hashCode();
        result += 31 * this.unwrittenOriginalQuery.hashCode();
        return result;
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final Weight originalWeight = this.originalQuery.createWeight(searcher, needsScores);
        return new Weight(this) {
            public void extractTerms(final Set<Term> terms) {
            }
            
            public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
                final Terms terms = context.reader().terms(TermsIncludingScoreQuery.this.field);
                if (terms != null) {
                    final TermsEnum segmentTermsEnum = terms.iterator();
                    final BytesRef spare = new BytesRef();
                    PostingsEnum postingsEnum = null;
                    for (int i = 0; i < TermsIncludingScoreQuery.this.terms.size(); ++i) {
                        if (segmentTermsEnum.seekExact(TermsIncludingScoreQuery.this.terms.get(TermsIncludingScoreQuery.this.ords[i], spare))) {
                            postingsEnum = segmentTermsEnum.postings(postingsEnum, 0);
                            if (postingsEnum.advance(doc) == doc) {
                                final float score = TermsIncludingScoreQuery.this.scores[TermsIncludingScoreQuery.this.ords[i]];
                                return Explanation.match(score, "Score based on join value " + segmentTermsEnum.term().utf8ToString(), new Explanation[0]);
                            }
                        }
                    }
                }
                return Explanation.noMatch("Not a match", new Explanation[0]);
            }
            
            public float getValueForNormalization() throws IOException {
                return originalWeight.getValueForNormalization();
            }
            
            public void normalize(final float norm, final float boost) {
                originalWeight.normalize(norm, boost);
            }
            
            public Scorer scorer(final LeafReaderContext context) throws IOException {
                final Terms terms = context.reader().terms(TermsIncludingScoreQuery.this.field);
                if (terms == null) {
                    return null;
                }
                final long cost = context.reader().maxDoc() * terms.size();
                final TermsEnum segmentTermsEnum = terms.iterator();
                if (TermsIncludingScoreQuery.this.multipleValuesPerDocument) {
                    return new MVInOrderScorer(this, segmentTermsEnum, context.reader().maxDoc(), cost);
                }
                return new SVInOrderScorer(this, segmentTermsEnum, context.reader().maxDoc(), cost);
            }
        };
    }
    
    void dump(final PrintStream out) {
        out.println(this.field + ":");
        final BytesRef ref = new BytesRef();
        for (int i = 0; i < this.terms.size(); ++i) {
            this.terms.get(this.ords[i], ref);
            out.print(ref + " " + ref.utf8ToString() + " ");
            try {
                out.print(Long.toHexString(NumericUtils.prefixCodedToLong(ref)) + "L");
            }
            catch (final Exception e) {
                try {
                    out.print(Integer.toHexString(NumericUtils.prefixCodedToInt(ref)) + "i");
                }
                catch (final Exception ex) {}
            }
            out.println(" score=" + this.scores[this.ords[i]]);
            out.println("");
        }
    }
    
    class SVInOrderScorer extends Scorer
    {
        final DocIdSetIterator matchingDocsIterator;
        final float[] scores;
        final long cost;
        
        SVInOrderScorer(final Weight weight, final TermsEnum termsEnum, final int maxDoc, final long cost) throws IOException {
            super(weight);
            final FixedBitSet matchingDocs = new FixedBitSet(maxDoc);
            this.scores = new float[maxDoc];
            this.fillDocsAndScores(matchingDocs, termsEnum);
            this.matchingDocsIterator = (DocIdSetIterator)new BitSetIterator((BitSet)matchingDocs, cost);
            this.cost = cost;
        }
        
        protected void fillDocsAndScores(final FixedBitSet matchingDocs, final TermsEnum termsEnum) throws IOException {
            final BytesRef spare = new BytesRef();
            PostingsEnum postingsEnum = null;
            for (int i = 0; i < TermsIncludingScoreQuery.this.terms.size(); ++i) {
                if (termsEnum.seekExact(TermsIncludingScoreQuery.this.terms.get(TermsIncludingScoreQuery.this.ords[i], spare))) {
                    postingsEnum = termsEnum.postings(postingsEnum, 0);
                    final float score = TermsIncludingScoreQuery.this.scores[TermsIncludingScoreQuery.this.ords[i]];
                    for (int doc = postingsEnum.nextDoc(); doc != Integer.MAX_VALUE; doc = postingsEnum.nextDoc()) {
                        matchingDocs.set(doc);
                        this.scores[doc] = score;
                    }
                }
            }
        }
        
        public float score() throws IOException {
            return this.scores[this.docID()];
        }
        
        public int freq() throws IOException {
            return 1;
        }
        
        public int docID() {
            return this.matchingDocsIterator.docID();
        }
        
        public DocIdSetIterator iterator() {
            return this.matchingDocsIterator;
        }
    }
    
    class MVInOrderScorer extends SVInOrderScorer
    {
        MVInOrderScorer(final Weight weight, final TermsEnum termsEnum, final int maxDoc, final long cost) throws IOException {
            super(weight, termsEnum, maxDoc, cost);
        }
        
        @Override
        protected void fillDocsAndScores(final FixedBitSet matchingDocs, final TermsEnum termsEnum) throws IOException {
            final BytesRef spare = new BytesRef();
            PostingsEnum postingsEnum = null;
            for (int i = 0; i < TermsIncludingScoreQuery.this.terms.size(); ++i) {
                if (termsEnum.seekExact(TermsIncludingScoreQuery.this.terms.get(TermsIncludingScoreQuery.this.ords[i], spare))) {
                    postingsEnum = termsEnum.postings(postingsEnum, 0);
                    final float score = TermsIncludingScoreQuery.this.scores[TermsIncludingScoreQuery.this.ords[i]];
                    for (int doc = postingsEnum.nextDoc(); doc != Integer.MAX_VALUE; doc = postingsEnum.nextDoc()) {
                        if (!matchingDocs.get(doc)) {
                            this.scores[doc] = score;
                            matchingDocs.set(doc);
                        }
                    }
                }
            }
        }
    }
}
