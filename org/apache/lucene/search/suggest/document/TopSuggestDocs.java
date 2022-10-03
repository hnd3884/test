package org.apache.lucene.search.suggest.document;

import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class TopSuggestDocs extends TopDocs
{
    public static final TopSuggestDocs EMPTY;
    
    public TopSuggestDocs(final int totalHits, final SuggestScoreDoc[] scoreDocs, final float maxScore) {
        super(totalHits, (ScoreDoc[])scoreDocs, maxScore);
    }
    
    public SuggestScoreDoc[] scoreLookupDocs() {
        return (SuggestScoreDoc[])this.scoreDocs;
    }
    
    public static TopSuggestDocs merge(final int topN, final TopSuggestDocs[] shardHits) {
        final SuggestScoreDocPriorityQueue priorityQueue = new SuggestScoreDocPriorityQueue(topN);
        for (final TopSuggestDocs shardHit : shardHits) {
            for (final SuggestScoreDoc scoreDoc : shardHit.scoreLookupDocs()) {
                if (scoreDoc == priorityQueue.insertWithOverflow((Object)scoreDoc)) {
                    break;
                }
            }
        }
        final SuggestScoreDoc[] topNResults = priorityQueue.getResults();
        if (topNResults.length > 0) {
            return new TopSuggestDocs(topNResults.length, topNResults, topNResults[0].score);
        }
        return TopSuggestDocs.EMPTY;
    }
    
    static {
        EMPTY = new TopSuggestDocs(0, new SuggestScoreDoc[0], 0.0f);
    }
    
    public static class SuggestScoreDoc extends ScoreDoc implements Comparable<SuggestScoreDoc>
    {
        public final CharSequence key;
        public final CharSequence context;
        
        public SuggestScoreDoc(final int doc, final CharSequence key, final CharSequence context, final float score) {
            super(doc, score);
            this.key = key;
            this.context = context;
        }
        
        public int compareTo(final SuggestScoreDoc o) {
            return Lookup.CHARSEQUENCE_COMPARATOR.compare(this.key, o.key);
        }
    }
}
