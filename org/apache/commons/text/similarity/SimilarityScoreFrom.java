package org.apache.commons.text.similarity;

import org.apache.commons.lang3.Validate;

public class SimilarityScoreFrom<R>
{
    private final SimilarityScore<R> similarityScore;
    private final CharSequence left;
    
    public SimilarityScoreFrom(final SimilarityScore<R> similarityScore, final CharSequence left) {
        Validate.isTrue(similarityScore != null, "The edit distance may not be null.", new Object[0]);
        this.similarityScore = similarityScore;
        this.left = left;
    }
    
    public R apply(final CharSequence right) {
        return this.similarityScore.apply(this.left, right);
    }
    
    public CharSequence getLeft() {
        return this.left;
    }
    
    public SimilarityScore<R> getSimilarityScore() {
        return this.similarityScore;
    }
}
