package org.apache.lucene.search;

import org.apache.lucene.util.Bits;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.util.Set;

public abstract class Weight
{
    protected final Query parentQuery;
    
    protected Weight(final Query query) {
        this.parentQuery = query;
    }
    
    public abstract void extractTerms(final Set<Term> p0);
    
    public abstract Explanation explain(final LeafReaderContext p0, final int p1) throws IOException;
    
    public final Query getQuery() {
        return this.parentQuery;
    }
    
    public abstract float getValueForNormalization() throws IOException;
    
    public abstract void normalize(final float p0, final float p1);
    
    public abstract Scorer scorer(final LeafReaderContext p0) throws IOException;
    
    public BulkScorer bulkScorer(final LeafReaderContext context) throws IOException {
        final Scorer scorer = this.scorer(context);
        if (scorer == null) {
            return null;
        }
        return new DefaultBulkScorer(scorer);
    }
    
    protected static class DefaultBulkScorer extends BulkScorer
    {
        private final Scorer scorer;
        private final DocIdSetIterator iterator;
        private final TwoPhaseIterator twoPhase;
        
        public DefaultBulkScorer(final Scorer scorer) {
            if (scorer == null) {
                throw new NullPointerException();
            }
            this.scorer = scorer;
            this.iterator = scorer.iterator();
            this.twoPhase = scorer.twoPhaseIterator();
        }
        
        @Override
        public long cost() {
            return this.iterator.cost();
        }
        
        @Override
        public int score(final LeafCollector collector, final Bits acceptDocs, final int min, final int max) throws IOException {
            collector.setScorer(this.scorer);
            if (this.scorer.docID() == -1 && min == 0 && max == Integer.MAX_VALUE) {
                scoreAll(collector, this.iterator, this.twoPhase, acceptDocs);
                return Integer.MAX_VALUE;
            }
            int doc = this.scorer.docID();
            if (doc < min) {
                if (this.twoPhase == null) {
                    doc = this.iterator.advance(min);
                }
                else {
                    doc = this.twoPhase.approximation().advance(min);
                }
            }
            return scoreRange(collector, this.iterator, this.twoPhase, acceptDocs, doc, max);
        }
        
        static int scoreRange(final LeafCollector collector, final DocIdSetIterator iterator, final TwoPhaseIterator twoPhase, final Bits acceptDocs, int currentDoc, final int end) throws IOException {
            if (twoPhase == null) {
                while (currentDoc < end) {
                    if (acceptDocs == null || acceptDocs.get(currentDoc)) {
                        collector.collect(currentDoc);
                    }
                    currentDoc = iterator.nextDoc();
                }
                return currentDoc;
            }
            for (DocIdSetIterator approximation = twoPhase.approximation(); currentDoc < end; currentDoc = approximation.nextDoc()) {
                if ((acceptDocs == null || acceptDocs.get(currentDoc)) && twoPhase.matches()) {
                    collector.collect(currentDoc);
                }
            }
            return currentDoc;
        }
        
        static void scoreAll(final LeafCollector collector, final DocIdSetIterator iterator, final TwoPhaseIterator twoPhase, final Bits acceptDocs) throws IOException {
            if (twoPhase == null) {
                for (int doc = iterator.nextDoc(); doc != Integer.MAX_VALUE; doc = iterator.nextDoc()) {
                    if (acceptDocs == null || acceptDocs.get(doc)) {
                        collector.collect(doc);
                    }
                }
            }
            else {
                final DocIdSetIterator approximation = twoPhase.approximation();
                for (int doc2 = approximation.nextDoc(); doc2 != Integer.MAX_VALUE; doc2 = approximation.nextDoc()) {
                    if ((acceptDocs == null || acceptDocs.get(doc2)) && twoPhase.matches()) {
                        collector.collect(doc2);
                    }
                }
            }
        }
    }
}
