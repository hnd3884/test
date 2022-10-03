package org.apache.commons.text.similarity;

public class HammingDistance implements EditDistance<Integer>
{
    @Override
    public Integer apply(final CharSequence left, final CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("CharSequences must not be null");
        }
        if (left.length() != right.length()) {
            throw new IllegalArgumentException("CharSequences must have the same length");
        }
        int distance = 0;
        for (int i = 0; i < left.length(); ++i) {
            if (left.charAt(i) != right.charAt(i)) {
                ++distance;
            }
        }
        return distance;
    }
}
