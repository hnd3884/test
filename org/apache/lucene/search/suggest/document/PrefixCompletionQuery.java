package org.apache.lucene.search.suggest.document;

import java.io.IOException;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.suggest.BitsProducer;
import org.apache.lucene.index.Term;
import org.apache.lucene.analysis.Analyzer;

public class PrefixCompletionQuery extends CompletionQuery
{
    protected final CompletionAnalyzer analyzer;
    
    public PrefixCompletionQuery(final Analyzer analyzer, final Term term) {
        this(analyzer, term, null);
    }
    
    public PrefixCompletionQuery(final Analyzer analyzer, final Term term, final BitsProducer filter) {
        super(term, filter);
        if (!(analyzer instanceof CompletionAnalyzer)) {
            this.analyzer = new CompletionAnalyzer(analyzer);
        }
        else {
            this.analyzer = (CompletionAnalyzer)analyzer;
        }
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final CompletionTokenStream stream = (CompletionTokenStream)this.analyzer.tokenStream(this.getField(), this.getTerm().text());
        return new CompletionWeight(this, stream.toAutomaton());
    }
}
