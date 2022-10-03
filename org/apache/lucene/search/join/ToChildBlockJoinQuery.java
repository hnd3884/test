package org.apache.lucene.search.join;

import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.search.DocIdSetIterator;
import java.util.Locale;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.util.Set;
import org.apache.lucene.index.IndexReader;
import java.io.IOException;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

public class ToChildBlockJoinQuery extends Query
{
    static final String INVALID_QUERY_MESSAGE = "Parent query yields document which is not matched by parents filter, docID=";
    static final String ILLEGAL_ADVANCE_ON_PARENT = "Expect to be advanced on child docs only. got docID=";
    private final BitSetProducer parentsFilter;
    private final Query parentQuery;
    private final Query origParentQuery;
    
    public ToChildBlockJoinQuery(final Query parentQuery, final BitSetProducer parentsFilter) {
        this.origParentQuery = parentQuery;
        this.parentQuery = parentQuery;
        this.parentsFilter = parentsFilter;
    }
    
    private ToChildBlockJoinQuery(final Query origParentQuery, final Query parentQuery, final BitSetProducer parentsFilter) {
        this.origParentQuery = origParentQuery;
        this.parentQuery = parentQuery;
        this.parentsFilter = parentsFilter;
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return new ToChildBlockJoinWeight(this, this.parentQuery.createWeight(searcher, needsScores), this.parentsFilter, needsScores);
    }
    
    public Query getParentQuery() {
        return this.parentQuery;
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final Query parentRewrite = this.parentQuery.rewrite(reader);
        if (parentRewrite != this.parentQuery) {
            return new ToChildBlockJoinQuery(this.parentQuery, parentRewrite, this.parentsFilter);
        }
        return super.rewrite(reader);
    }
    
    public String toString(final String field) {
        return "ToChildBlockJoinQuery (" + this.parentQuery.toString() + ")";
    }
    
    public boolean equals(final Object _other) {
        if (_other instanceof ToChildBlockJoinQuery) {
            final ToChildBlockJoinQuery other = (ToChildBlockJoinQuery)_other;
            return this.origParentQuery.equals((Object)other.origParentQuery) && this.parentsFilter.equals(other.parentsFilter) && super.equals((Object)other);
        }
        return false;
    }
    
    public int hashCode() {
        final int prime = 31;
        int hash = super.hashCode();
        hash = 31 * hash + this.origParentQuery.hashCode();
        hash = 31 * hash + this.parentsFilter.hashCode();
        return hash;
    }
    
    private static class ToChildBlockJoinWeight extends Weight
    {
        private final Weight parentWeight;
        private final BitSetProducer parentsFilter;
        private final boolean doScores;
        
        public ToChildBlockJoinWeight(final Query joinQuery, final Weight parentWeight, final BitSetProducer parentsFilter, final boolean doScores) {
            super(joinQuery);
            this.parentWeight = parentWeight;
            this.parentsFilter = parentsFilter;
            this.doScores = doScores;
        }
        
        public void extractTerms(final Set<Term> terms) {
            this.parentWeight.extractTerms((Set)terms);
        }
        
        public float getValueForNormalization() throws IOException {
            return this.parentWeight.getValueForNormalization();
        }
        
        public void normalize(final float norm, final float boost) {
            this.parentWeight.normalize(norm, boost);
        }
        
        public Scorer scorer(final LeafReaderContext readerContext) throws IOException {
            final Scorer parentScorer = this.parentWeight.scorer(readerContext);
            if (parentScorer == null) {
                return null;
            }
            final BitSet parents = this.parentsFilter.getBitSet(readerContext);
            if (parents == null) {
                return null;
            }
            return new ToChildBlockJoinScorer(this, parentScorer, parents, this.doScores);
        }
        
        public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
            final ToChildBlockJoinScorer scorer = (ToChildBlockJoinScorer)this.scorer(context);
            if (scorer != null && scorer.iterator().advance(doc) == doc) {
                final int parentDoc = scorer.getParentDoc();
                return Explanation.match(scorer.score(), String.format(Locale.ROOT, "Score based on parent document %d", parentDoc + context.docBase), new Explanation[] { this.parentWeight.explain(context, parentDoc) });
            }
            return Explanation.noMatch("Not a match", new Explanation[0]);
        }
    }
    
    static class ToChildBlockJoinScorer extends Scorer
    {
        private final Scorer parentScorer;
        private final DocIdSetIterator parentIt;
        private final BitSet parentBits;
        private final boolean doScores;
        private float parentScore;
        private int parentFreq;
        private int childDoc;
        private int parentDoc;
        
        public ToChildBlockJoinScorer(final Weight weight, final Scorer parentScorer, final BitSet parentBits, final boolean doScores) {
            super(weight);
            this.parentFreq = 1;
            this.childDoc = -1;
            this.parentDoc = 0;
            this.doScores = doScores;
            this.parentBits = parentBits;
            this.parentScorer = parentScorer;
            this.parentIt = parentScorer.iterator();
        }
        
        public Collection<Scorer.ChildScorer> getChildren() {
            return Collections.singleton(new Scorer.ChildScorer(this.parentScorer, "BLOCK_JOIN"));
        }
        
        public DocIdSetIterator iterator() {
            return new DocIdSetIterator() {
                public int docID() {
                    return ToChildBlockJoinScorer.this.childDoc;
                }
                
                public int nextDoc() throws IOException {
                    if (ToChildBlockJoinScorer.this.childDoc + 1 == ToChildBlockJoinScorer.this.parentDoc) {
                        while (true) {
                            ToChildBlockJoinScorer.this.parentDoc = ToChildBlockJoinScorer.this.parentIt.nextDoc();
                            ToChildBlockJoinScorer.this.validateParentDoc();
                            if (ToChildBlockJoinScorer.this.parentDoc == 0) {
                                ToChildBlockJoinScorer.this.parentDoc = ToChildBlockJoinScorer.this.parentIt.nextDoc();
                                ToChildBlockJoinScorer.this.validateParentDoc();
                            }
                            if (ToChildBlockJoinScorer.this.parentDoc == Integer.MAX_VALUE) {
                                ToChildBlockJoinScorer.this.childDoc = Integer.MAX_VALUE;
                                return ToChildBlockJoinScorer.this.childDoc;
                            }
                            ToChildBlockJoinScorer.this.childDoc = 1 + ToChildBlockJoinScorer.this.parentBits.prevSetBit(ToChildBlockJoinScorer.this.parentDoc - 1);
                            if (ToChildBlockJoinScorer.this.childDoc == ToChildBlockJoinScorer.this.parentDoc) {
                                continue;
                            }
                            if (ToChildBlockJoinScorer.this.childDoc < ToChildBlockJoinScorer.this.parentDoc) {
                                if (ToChildBlockJoinScorer.this.doScores) {
                                    ToChildBlockJoinScorer.this.parentScore = ToChildBlockJoinScorer.this.parentScorer.score();
                                    ToChildBlockJoinScorer.this.parentFreq = ToChildBlockJoinScorer.this.parentScorer.freq();
                                }
                                return ToChildBlockJoinScorer.this.childDoc;
                            }
                        }
                    }
                    else {
                        assert ToChildBlockJoinScorer.this.childDoc < ToChildBlockJoinScorer.this.parentDoc : "childDoc=" + ToChildBlockJoinScorer.this.childDoc + " parentDoc=" + ToChildBlockJoinScorer.this.parentDoc;
                        ToChildBlockJoinScorer.this.childDoc++;
                        return ToChildBlockJoinScorer.this.childDoc;
                    }
                }
                
                public int advance(int childTarget) throws IOException {
                    if (childTarget >= ToChildBlockJoinScorer.this.parentDoc) {
                        if (childTarget == Integer.MAX_VALUE) {
                            return ToChildBlockJoinScorer.this.childDoc = (ToChildBlockJoinScorer.this.parentDoc = Integer.MAX_VALUE);
                        }
                        ToChildBlockJoinScorer.this.parentDoc = ToChildBlockJoinScorer.this.parentIt.advance(childTarget + 1);
                        ToChildBlockJoinScorer.this.validateParentDoc();
                        if (ToChildBlockJoinScorer.this.parentDoc == Integer.MAX_VALUE) {
                            return ToChildBlockJoinScorer.this.childDoc = Integer.MAX_VALUE;
                        }
                        while (true) {
                            final int firstChild = ToChildBlockJoinScorer.this.parentBits.prevSetBit(ToChildBlockJoinScorer.this.parentDoc - 1) + 1;
                            if (firstChild != ToChildBlockJoinScorer.this.parentDoc) {
                                childTarget = Math.max(childTarget, firstChild);
                                if (ToChildBlockJoinScorer.this.doScores) {
                                    ToChildBlockJoinScorer.this.parentScore = ToChildBlockJoinScorer.this.parentScorer.score();
                                    ToChildBlockJoinScorer.this.parentFreq = ToChildBlockJoinScorer.this.parentScorer.freq();
                                    break;
                                }
                                break;
                            }
                            else {
                                ToChildBlockJoinScorer.this.parentDoc = ToChildBlockJoinScorer.this.parentIt.nextDoc();
                                ToChildBlockJoinScorer.this.validateParentDoc();
                                if (ToChildBlockJoinScorer.this.parentDoc == Integer.MAX_VALUE) {
                                    return ToChildBlockJoinScorer.this.childDoc = Integer.MAX_VALUE;
                                }
                                continue;
                            }
                        }
                    }
                    assert childTarget < ToChildBlockJoinScorer.this.parentDoc;
                    assert !ToChildBlockJoinScorer.this.parentBits.get(childTarget);
                    ToChildBlockJoinScorer.this.childDoc = childTarget;
                    return ToChildBlockJoinScorer.this.childDoc;
                }
                
                public long cost() {
                    return ToChildBlockJoinScorer.this.parentIt.cost();
                }
            };
        }
        
        private void validateParentDoc() {
            if (this.parentDoc != Integer.MAX_VALUE && !this.parentBits.get(this.parentDoc)) {
                throw new IllegalStateException("Parent query yields document which is not matched by parents filter, docID=" + this.parentDoc);
            }
        }
        
        public int docID() {
            return this.childDoc;
        }
        
        public float score() throws IOException {
            return this.parentScore;
        }
        
        public int freq() throws IOException {
            return this.parentFreq;
        }
        
        int getParentDoc() {
            return this.parentDoc;
        }
    }
}
