package org.apache.lucene.search.spans;

import java.util.ArrayList;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import java.util.Map;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.IndexReader;
import java.io.IOException;
import org.apache.lucene.search.IndexSearcher;

public final class SpanWithinQuery extends SpanContainQuery
{
    public SpanWithinQuery(final SpanQuery big, final SpanQuery little) {
        super(big, little);
    }
    
    @Override
    public String toString(final String field) {
        return this.toString(field, "SpanWithin");
    }
    
    @Override
    public SpanWeight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final SpanWeight bigWeight = this.big.createWeight(searcher, false);
        final SpanWeight littleWeight = this.little.createWeight(searcher, false);
        return new SpanWithinWeight(searcher, needsScores ? SpanQuery.getTermContexts(bigWeight, littleWeight) : null, bigWeight, littleWeight);
    }
    
    public class SpanWithinWeight extends SpanContainWeight
    {
        public SpanWithinWeight(final IndexSearcher searcher, final Map<Term, TermContext> terms, final SpanWeight bigWeight, final SpanWeight littleWeight) throws IOException {
            super(searcher, terms, bigWeight, littleWeight);
        }
        
        @Override
        public Spans getSpans(final LeafReaderContext context, final Postings requiredPostings) throws IOException {
            final ArrayList<Spans> containerContained = this.prepareConjunction(context, requiredPostings);
            if (containerContained == null) {
                return null;
            }
            final Spans big = containerContained.get(0);
            final Spans little = containerContained.get(1);
            return new ContainSpans(big, little, little) {
                @Override
                boolean twoPhaseCurrentDocMatches() throws IOException {
                    this.oneExhaustedInCurrentDoc = false;
                    assert this.littleSpans.startPosition() == -1;
                    while (this.littleSpans.nextStartPosition() != Integer.MAX_VALUE) {
                        while (this.bigSpans.endPosition() < this.littleSpans.endPosition()) {
                            if (this.bigSpans.nextStartPosition() == Integer.MAX_VALUE) {
                                this.oneExhaustedInCurrentDoc = true;
                                return false;
                            }
                        }
                        if (this.bigSpans.startPosition() <= this.littleSpans.startPosition()) {
                            return this.atFirstInCurrentDoc = true;
                        }
                    }
                    this.oneExhaustedInCurrentDoc = true;
                    return false;
                }
                
                @Override
                public int nextStartPosition() throws IOException {
                    if (this.atFirstInCurrentDoc) {
                        this.atFirstInCurrentDoc = false;
                        return this.littleSpans.startPosition();
                    }
                    while (this.littleSpans.nextStartPosition() != Integer.MAX_VALUE) {
                        while (this.bigSpans.endPosition() < this.littleSpans.endPosition()) {
                            if (this.bigSpans.nextStartPosition() == Integer.MAX_VALUE) {
                                this.oneExhaustedInCurrentDoc = true;
                                return Integer.MAX_VALUE;
                            }
                        }
                        if (this.bigSpans.startPosition() <= this.littleSpans.startPosition()) {
                            return this.littleSpans.startPosition();
                        }
                    }
                    this.oneExhaustedInCurrentDoc = true;
                    return Integer.MAX_VALUE;
                }
            };
        }
    }
}
