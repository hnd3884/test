package org.apache.lucene.search.spell;

public class CombineSuggestion
{
    public final int[] originalTermIndexes;
    public final SuggestWord suggestion;
    
    public CombineSuggestion(final SuggestWord suggestion, final int[] originalTermIndexes) {
        this.suggestion = suggestion;
        this.originalTermIndexes = originalTermIndexes;
    }
}
