package org.apache.commons.text.similarity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

public class CosineSimilarity
{
    public Double cosineSimilarity(final Map<CharSequence, Integer> leftVector, final Map<CharSequence, Integer> rightVector) {
        if (leftVector == null || rightVector == null) {
            throw new IllegalArgumentException("Vectors must not be null");
        }
        final Set<CharSequence> intersection = this.getIntersection(leftVector, rightVector);
        final double dotProduct = this.dot(leftVector, rightVector, intersection);
        double d1 = 0.0;
        for (final Integer value : leftVector.values()) {
            d1 += Math.pow(value, 2.0);
        }
        double d2 = 0.0;
        for (final Integer value2 : rightVector.values()) {
            d2 += Math.pow(value2, 2.0);
        }
        double cosineSimilarity;
        if (d1 <= 0.0 || d2 <= 0.0) {
            cosineSimilarity = 0.0;
        }
        else {
            cosineSimilarity = dotProduct / (Math.sqrt(d1) * Math.sqrt(d2));
        }
        return cosineSimilarity;
    }
    
    private Set<CharSequence> getIntersection(final Map<CharSequence, Integer> leftVector, final Map<CharSequence, Integer> rightVector) {
        final Set<CharSequence> intersection = new HashSet<CharSequence>(leftVector.keySet());
        intersection.retainAll(rightVector.keySet());
        return intersection;
    }
    
    private double dot(final Map<CharSequence, Integer> leftVector, final Map<CharSequence, Integer> rightVector, final Set<CharSequence> intersection) {
        long dotProduct = 0L;
        for (final CharSequence key : intersection) {
            dotProduct += leftVector.get(key) * rightVector.get(key);
        }
        return (double)dotProduct;
    }
}
