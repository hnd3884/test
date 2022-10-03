package org.apache.commons.text.similarity;

public class LongestCommonSubsequence implements SimilarityScore<Integer>
{
    @Override
    public Integer apply(final CharSequence left, final CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Inputs must not be null");
        }
        return this.longestCommonSubsequence(left, right).length();
    }
    
    @Deprecated
    public CharSequence logestCommonSubsequence(final CharSequence left, final CharSequence right) {
        return this.longestCommonSubsequence(left, right);
    }
    
    public CharSequence longestCommonSubsequence(final CharSequence left, final CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Inputs must not be null");
        }
        final StringBuilder longestCommonSubstringArray = new StringBuilder(Math.max(left.length(), right.length()));
        final int[][] lcsLengthArray = this.longestCommonSubstringLengthArray(left, right);
        int i = left.length() - 1;
        int j = right.length() - 1;
        int k = lcsLengthArray[left.length()][right.length()] - 1;
        while (k >= 0) {
            if (left.charAt(i) == right.charAt(j)) {
                longestCommonSubstringArray.append(left.charAt(i));
                --i;
                --j;
                --k;
            }
            else if (lcsLengthArray[i + 1][j] < lcsLengthArray[i][j + 1]) {
                --i;
            }
            else {
                --j;
            }
        }
        return longestCommonSubstringArray.reverse().toString();
    }
    
    public int[][] longestCommonSubstringLengthArray(final CharSequence left, final CharSequence right) {
        final int[][] lcsLengthArray = new int[left.length() + 1][right.length() + 1];
        for (int i = 0; i < left.length(); ++i) {
            for (int j = 0; j < right.length(); ++j) {
                if (i == 0) {
                    lcsLengthArray[i][j] = 0;
                }
                if (j == 0) {
                    lcsLengthArray[i][j] = 0;
                }
                if (left.charAt(i) == right.charAt(j)) {
                    lcsLengthArray[i + 1][j + 1] = lcsLengthArray[i][j] + 1;
                }
                else {
                    lcsLengthArray[i + 1][j + 1] = Math.max(lcsLengthArray[i + 1][j], lcsLengthArray[i][j + 1]);
                }
            }
        }
        return lcsLengthArray;
    }
}
