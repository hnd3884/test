package org.apache.lucene.search.suggest.document;

import org.apache.lucene.util.PriorityQueue;

final class SuggestScoreDocPriorityQueue extends PriorityQueue<TopSuggestDocs.SuggestScoreDoc>
{
    public SuggestScoreDocPriorityQueue(final int size) {
        super(size);
    }
    
    protected boolean lessThan(final TopSuggestDocs.SuggestScoreDoc a, final TopSuggestDocs.SuggestScoreDoc b) {
        if (a.score == b.score) {
            return a.doc > b.doc;
        }
        return a.score < b.score;
    }
    
    public TopSuggestDocs.SuggestScoreDoc[] getResults() {
        final int size = this.size();
        final TopSuggestDocs.SuggestScoreDoc[] res = new TopSuggestDocs.SuggestScoreDoc[size];
        for (int i = size - 1; i >= 0; --i) {
            res[i] = (TopSuggestDocs.SuggestScoreDoc)this.pop();
        }
        return res;
    }
}
