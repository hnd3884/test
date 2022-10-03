package org.apache.commons.text.similarity;

public class LongestCommonSubsequenceDistance implements EditDistance<Integer>
{
    private final LongestCommonSubsequence longestCommonSubsequence;
    
    public LongestCommonSubsequenceDistance() {
        this.longestCommonSubsequence = new LongestCommonSubsequence();
    }
    
    @Override
    public Integer apply(final CharSequence left, final CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Inputs must not be null");
        }
        return left.length() + right.length() - 2 * this.longestCommonSubsequence.apply(left, right);
    }
}
