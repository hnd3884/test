package org.apache.commons.text.similarity;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;

public class JaccardSimilarity implements SimilarityScore<Double>
{
    @Override
    public Double apply(final CharSequence left, final CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        return this.calculateJaccardSimilarity(left, right);
    }
    
    private Double calculateJaccardSimilarity(final CharSequence left, final CharSequence right) {
        final int leftLength = left.length();
        final int rightLength = right.length();
        if (leftLength == 0 || rightLength == 0) {
            return 0.0;
        }
        final Set<Character> leftSet = new HashSet<Character>();
        for (int i = 0; i < leftLength; ++i) {
            leftSet.add(left.charAt(i));
        }
        final Set<Character> rightSet = new HashSet<Character>();
        for (int j = 0; j < rightLength; ++j) {
            rightSet.add(right.charAt(j));
        }
        final Set<Character> unionSet = new HashSet<Character>(leftSet);
        unionSet.addAll(rightSet);
        final int intersectionSize = leftSet.size() + rightSet.size() - unionSet.size();
        return 1.0 * intersectionSize / unionSet.size();
    }
}
