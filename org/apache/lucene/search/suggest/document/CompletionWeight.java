package org.apache.lucene.search.suggest.document;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.index.Term;
import java.util.Set;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.search.suggest.BitsProducer;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.util.Bits;
import org.apache.lucene.search.BulkScorer;
import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.search.Weight;

public class CompletionWeight extends Weight
{
    private final CompletionQuery completionQuery;
    private final Automaton automaton;
    
    public CompletionWeight(final CompletionQuery query, final Automaton automaton) throws IOException {
        super((Query)query);
        this.completionQuery = query;
        this.automaton = automaton;
    }
    
    public Automaton getAutomaton() {
        return this.automaton;
    }
    
    public BulkScorer bulkScorer(final LeafReaderContext context) throws IOException {
        final LeafReader reader = context.reader();
        final Terms terms;
        if ((terms = reader.terms(this.completionQuery.getField())) == null) {
            return null;
        }
        if (!(terms instanceof CompletionTerms)) {
            throw new IllegalArgumentException(this.completionQuery.getField() + " is not a SuggestField");
        }
        final CompletionTerms completionTerms = (CompletionTerms)terms;
        final NRTSuggester suggester;
        if ((suggester = completionTerms.suggester()) == null) {
            return null;
        }
        final BitsProducer filter = this.completionQuery.getFilter();
        Bits filteredDocs = null;
        if (filter != null) {
            filteredDocs = filter.getBits(context);
            if (filteredDocs.getClass() == Bits.MatchNoBits.class) {
                return null;
            }
        }
        return new CompletionScorer(this, suggester, reader, filteredDocs, filter != null, this.automaton);
    }
    
    protected void setNextMatch(final IntsRef pathPrefix) {
    }
    
    protected float boost() {
        return 0.0f;
    }
    
    protected CharSequence context() {
        return null;
    }
    
    public Scorer scorer(final LeafReaderContext context) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    public void extractTerms(final Set<Term> terms) {
    }
    
    public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
        return null;
    }
    
    public float getValueForNormalization() throws IOException {
        return 0.0f;
    }
    
    public void normalize(final float norm, final float boost) {
    }
}
