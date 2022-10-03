package org.apache.commons.text.similarity;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.function.Function;

public class IntersectionSimilarity<T> implements SimilarityScore<IntersectionResult>
{
    private final Function<CharSequence, Collection<T>> converter;
    
    public IntersectionSimilarity(final Function<CharSequence, Collection<T>> converter) {
        if (converter == null) {
            throw new IllegalArgumentException("Converter must not be null");
        }
        this.converter = converter;
    }
    
    @Override
    public IntersectionResult apply(final CharSequence left, final CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        final Collection<T> objectsA = this.converter.apply(left);
        final Collection<T> objectsB = this.converter.apply(right);
        final int sizeA = objectsA.size();
        final int sizeB = objectsB.size();
        if (Math.min(sizeA, sizeB) == 0) {
            return new IntersectionResult(sizeA, sizeB, 0);
        }
        int intersection;
        if (objectsA instanceof Set && objectsB instanceof Set) {
            intersection = ((sizeA < sizeB) ? getIntersection((Set)objectsA, (Set<Object>)(Set)objectsB) : getIntersection((Set)objectsB, (Set<Object>)(Set)objectsA));
        }
        else {
            final TinyBag bagA = this.toBag(objectsA);
            final TinyBag bagB = this.toBag(objectsB);
            intersection = ((bagA.uniqueElementSize() < bagB.uniqueElementSize()) ? this.getIntersection(bagA, bagB) : this.getIntersection(bagB, bagA));
        }
        return new IntersectionResult(sizeA, sizeB, intersection);
    }
    
    private TinyBag toBag(final Collection<T> objects) {
        final TinyBag bag = new TinyBag(objects.size());
        for (final T t : objects) {
            bag.add(t);
        }
        return bag;
    }
    
    private static <T> int getIntersection(final Set<T> setA, final Set<T> setB) {
        int intersection = 0;
        for (final T element : setA) {
            if (setB.contains(element)) {
                ++intersection;
            }
        }
        return intersection;
    }
    
    private int getIntersection(final TinyBag bagA, final TinyBag bagB) {
        int intersection = 0;
        for (final Map.Entry<T, BagCount> entry : bagA.entrySet()) {
            final T element = entry.getKey();
            final int count = entry.getValue().count;
            intersection += Math.min(count, bagB.getCount(element));
        }
        return intersection;
    }
    
    private static class BagCount
    {
        int count;
        
        private BagCount() {
            this.count = 1;
        }
    }
    
    private class TinyBag
    {
        private final Map<T, BagCount> map;
        
        TinyBag(final int initialCapacity) {
            this.map = new HashMap<T, BagCount>(initialCapacity);
        }
        
        void add(final T object) {
            final BagCount mut = this.map.get(object);
            if (mut == null) {
                this.map.put(object, new BagCount());
            }
            else {
                final BagCount bagCount = mut;
                ++bagCount.count;
            }
        }
        
        int getCount(final Object object) {
            final BagCount count = this.map.get(object);
            if (count != null) {
                return count.count;
            }
            return 0;
        }
        
        Set<Map.Entry<T, BagCount>> entrySet() {
            return this.map.entrySet();
        }
        
        int uniqueElementSize() {
            return this.map.size();
        }
    }
}
