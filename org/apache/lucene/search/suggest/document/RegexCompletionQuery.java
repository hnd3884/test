package org.apache.lucene.search.suggest.document;

import java.io.IOException;
import org.apache.lucene.util.automaton.RegExp;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.suggest.BitsProducer;
import org.apache.lucene.index.Term;

public class RegexCompletionQuery extends CompletionQuery
{
    private final int flags;
    private final int maxDeterminizedStates;
    
    public RegexCompletionQuery(final Term term) {
        this(term, null);
    }
    
    public RegexCompletionQuery(final Term term, final BitsProducer filter) {
        this(term, 65535, 10000, filter);
    }
    
    public RegexCompletionQuery(final Term term, final int flags, final int maxDeterminizedStates) {
        this(term, flags, maxDeterminizedStates, null);
    }
    
    public RegexCompletionQuery(final Term term, final int flags, final int maxDeterminizedStates, final BitsProducer filter) {
        super(term, filter);
        this.flags = flags;
        this.maxDeterminizedStates = maxDeterminizedStates;
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return new CompletionWeight(this, new RegExp(this.getTerm().text(), this.flags).toAutomaton(this.maxDeterminizedStates));
    }
}
