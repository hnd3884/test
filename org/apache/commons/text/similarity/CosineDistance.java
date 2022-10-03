package org.apache.commons.text.similarity;

import java.util.Map;

public class CosineDistance implements EditDistance<Double>
{
    private final Tokenizer<CharSequence> tokenizer;
    private final CosineSimilarity cosineSimilarity;
    
    public CosineDistance() {
        this.tokenizer = new RegexTokenizer();
        this.cosineSimilarity = new CosineSimilarity();
    }
    
    @Override
    public Double apply(final CharSequence left, final CharSequence right) {
        final CharSequence[] leftTokens = this.tokenizer.tokenize(left);
        final CharSequence[] rightTokens = this.tokenizer.tokenize(right);
        final Map<CharSequence, Integer> leftVector = Counter.of(leftTokens);
        final Map<CharSequence, Integer> rightVector = Counter.of(rightTokens);
        final double similarity = this.cosineSimilarity.cosineSimilarity(leftVector, rightVector);
        return 1.0 - similarity;
    }
}
