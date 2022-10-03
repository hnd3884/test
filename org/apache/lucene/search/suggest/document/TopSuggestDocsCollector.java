package org.apache.lucene.search.suggest.document;

import org.apache.lucene.search.CollectionTerminatedException;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;

public class TopSuggestDocsCollector extends SimpleCollector
{
    private final SuggestScoreDocPriorityQueue priorityQueue;
    private final int num;
    protected int docBase;
    
    public TopSuggestDocsCollector(final int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("'num' must be > 0");
        }
        this.num = num;
        this.priorityQueue = new SuggestScoreDocPriorityQueue(num);
    }
    
    public int getCountToCollect() {
        return this.num;
    }
    
    protected void doSetNextReader(final LeafReaderContext context) throws IOException {
        this.docBase = context.docBase;
    }
    
    public void collect(final int docID, final CharSequence key, final CharSequence context, final float score) throws IOException {
        final TopSuggestDocs.SuggestScoreDoc current = new TopSuggestDocs.SuggestScoreDoc(this.docBase + docID, key, context, score);
        if (current == this.priorityQueue.insertWithOverflow((Object)current)) {
            throw new CollectionTerminatedException();
        }
    }
    
    public TopSuggestDocs get() throws IOException {
        final TopSuggestDocs.SuggestScoreDoc[] suggestScoreDocs = this.priorityQueue.getResults();
        if (suggestScoreDocs.length > 0) {
            return new TopSuggestDocs(suggestScoreDocs.length, suggestScoreDocs, suggestScoreDocs[0].score);
        }
        return TopSuggestDocs.EMPTY;
    }
    
    public void collect(final int doc) throws IOException {
    }
    
    public boolean needsScores() {
        return true;
    }
}
