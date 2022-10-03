package org.apache.lucene.search.spans;

import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.CollectionStatistics;
import java.util.Iterator;
import org.apache.lucene.search.TermStatistics;
import java.io.IOException;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import java.util.Map;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.Weight;

public abstract class SpanWeight extends Weight
{
    protected final Similarity similarity;
    protected final Similarity.SimWeight simWeight;
    protected final String field;
    
    public SpanWeight(final SpanQuery query, final IndexSearcher searcher, final Map<Term, TermContext> termContexts) throws IOException {
        super(query);
        this.field = query.getField();
        this.similarity = searcher.getSimilarity(termContexts != null);
        this.simWeight = this.buildSimWeight(query, searcher, termContexts);
    }
    
    private Similarity.SimWeight buildSimWeight(final SpanQuery query, final IndexSearcher searcher, final Map<Term, TermContext> termContexts) throws IOException {
        if (termContexts == null || termContexts.size() == 0 || query.getField() == null) {
            return null;
        }
        final TermStatistics[] termStats = new TermStatistics[termContexts.size()];
        int i = 0;
        for (final Term term : termContexts.keySet()) {
            termStats[i] = searcher.termStatistics(term, termContexts.get(term));
            ++i;
        }
        final CollectionStatistics collectionStats = searcher.collectionStatistics(query.getField());
        return searcher.getSimilarity(true).computeWeight(collectionStats, termStats);
    }
    
    public abstract void extractTermContexts(final Map<Term, TermContext> p0);
    
    public abstract Spans getSpans(final LeafReaderContext p0, final Postings p1) throws IOException;
    
    @Override
    public float getValueForNormalization() throws IOException {
        return (this.simWeight == null) ? 1.0f : this.simWeight.getValueForNormalization();
    }
    
    @Override
    public void normalize(final float queryNorm, final float boost) {
        if (this.simWeight != null) {
            this.simWeight.normalize(queryNorm, boost);
        }
    }
    
    @Override
    public SpanScorer scorer(final LeafReaderContext context) throws IOException {
        final Spans spans = this.getSpans(context, Postings.POSITIONS);
        if (spans == null) {
            return null;
        }
        final Similarity.SimScorer docScorer = this.getSimScorer(context);
        return new SpanScorer(this, spans, docScorer);
    }
    
    public Similarity.SimScorer getSimScorer(final LeafReaderContext context) throws IOException {
        return (this.simWeight == null) ? null : this.similarity.simScorer(this.simWeight, context);
    }
    
    @Override
    public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
        final SpanScorer scorer = this.scorer(context);
        if (scorer != null) {
            final int newDoc = scorer.iterator().advance(doc);
            if (newDoc == doc) {
                final float freq = scorer.sloppyFreq();
                final Similarity.SimScorer docScorer = this.similarity.simScorer(this.simWeight, context);
                final Explanation freqExplanation = Explanation.match(freq, "phraseFreq=" + freq, new Explanation[0]);
                final Explanation scoreExplanation = docScorer.explain(doc, freqExplanation);
                return Explanation.match(scoreExplanation.getValue(), "weight(" + this.getQuery() + " in " + doc + ") [" + this.similarity.getClass().getSimpleName() + "], result of:", scoreExplanation);
            }
        }
        return Explanation.noMatch("no matching term", new Explanation[0]);
    }
    
    public enum Postings
    {
        POSITIONS {
            @Override
            public int getRequiredPostings() {
                return 24;
            }
        }, 
        PAYLOADS {
            @Override
            public int getRequiredPostings() {
                return 88;
            }
        }, 
        OFFSETS {
            @Override
            public int getRequiredPostings() {
                return 120;
            }
        };
        
        public abstract int getRequiredPostings();
        
        public Postings atLeast(final Postings postings) {
            if (postings.compareTo(this) > 0) {
                return postings;
            }
            return this;
        }
    }
}
