package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;

public abstract class RandomAccessWeight extends ConstantScoreWeight
{
    protected RandomAccessWeight(final Query query) {
        super(query);
    }
    
    protected abstract Bits getMatchingDocs(final LeafReaderContext p0) throws IOException;
    
    @Override
    public final Scorer scorer(final LeafReaderContext context) throws IOException {
        final Bits matchingDocs = this.getMatchingDocs(context);
        if (matchingDocs == null || matchingDocs instanceof Bits.MatchNoBits) {
            return null;
        }
        final DocIdSetIterator approximation = DocIdSetIterator.all(context.reader().maxDoc());
        final TwoPhaseIterator twoPhase = new TwoPhaseIterator(approximation) {
            @Override
            public boolean matches() throws IOException {
                final int doc = this.approximation.docID();
                return matchingDocs.get(doc);
            }
            
            @Override
            public float matchCost() {
                return 10.0f;
            }
        };
        return new ConstantScoreScorer(this, this.score(), twoPhase);
    }
}
