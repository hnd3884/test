package org.apache.lucene.search.suggest.document;

import org.apache.lucene.search.LeafCollector;
import java.io.IOException;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.util.Bits;
import org.apache.lucene.search.BulkScorer;

public class CompletionScorer extends BulkScorer
{
    private final NRTSuggester suggester;
    private final Bits filterDocs;
    protected final CompletionWeight weight;
    final LeafReader reader;
    final boolean filtered;
    final Automaton automaton;
    
    protected CompletionScorer(final CompletionWeight weight, final NRTSuggester suggester, final LeafReader reader, final Bits filterDocs, final boolean filtered, final Automaton automaton) throws IOException {
        this.weight = weight;
        this.suggester = suggester;
        this.reader = reader;
        this.automaton = automaton;
        this.filtered = filtered;
        this.filterDocs = filterDocs;
    }
    
    public int score(final LeafCollector collector, final Bits acceptDocs, final int min, final int max) throws IOException {
        if (!(collector instanceof TopSuggestDocsCollector)) {
            throw new IllegalArgumentException("collector is not of type TopSuggestDocsCollector");
        }
        this.suggester.lookup(this, acceptDocs, (TopSuggestDocsCollector)collector);
        return max;
    }
    
    public long cost() {
        return 0L;
    }
    
    public final boolean accept(final int docID, final Bits liveDocs) {
        return (this.filterDocs == null || this.filterDocs.get(docID)) && (liveDocs == null || liveDocs.get(docID));
    }
    
    public float score(final float weight, final float boost) {
        if (boost == 0.0f) {
            return weight;
        }
        if (weight == 0.0f) {
            return boost;
        }
        return weight * boost;
    }
}
