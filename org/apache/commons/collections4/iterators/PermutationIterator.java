package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Iterator;

public class PermutationIterator<E> implements Iterator<List<E>>
{
    private int[] keys;
    private Map<Integer, E> objectMap;
    private boolean[] direction;
    private List<E> nextPermutation;
    
    public PermutationIterator(final Collection<? extends E> coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        this.keys = new int[coll.size()];
        Arrays.fill(this.direction = new boolean[coll.size()], false);
        int value = 1;
        this.objectMap = new HashMap<Integer, E>();
        for (final E e : coll) {
            this.objectMap.put(value, e);
            this.keys[value - 1] = value;
            ++value;
        }
        this.nextPermutation = new ArrayList<E>(coll);
    }
    
    @Override
    public boolean hasNext() {
        return this.nextPermutation != null;
    }
    
    @Override
    public List<E> next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        int indexOfLargestMobileInteger = -1;
        int largestKey = -1;
        for (int i = 0; i < this.keys.length; ++i) {
            if (((this.direction[i] && i < this.keys.length - 1 && this.keys[i] > this.keys[i + 1]) || (!this.direction[i] && i > 0 && this.keys[i] > this.keys[i - 1])) && this.keys[i] > largestKey) {
                largestKey = this.keys[i];
                indexOfLargestMobileInteger = i;
            }
        }
        if (largestKey == -1) {
            final List<E> toReturn = this.nextPermutation;
            this.nextPermutation = null;
            return toReturn;
        }
        final int offset = this.direction[indexOfLargestMobileInteger] ? 1 : -1;
        final int tmpKey = this.keys[indexOfLargestMobileInteger];
        this.keys[indexOfLargestMobileInteger] = this.keys[indexOfLargestMobileInteger + offset];
        this.keys[indexOfLargestMobileInteger + offset] = tmpKey;
        final boolean tmpDirection = this.direction[indexOfLargestMobileInteger];
        this.direction[indexOfLargestMobileInteger] = this.direction[indexOfLargestMobileInteger + offset];
        this.direction[indexOfLargestMobileInteger + offset] = tmpDirection;
        final List<E> nextP = new ArrayList<E>();
        for (int j = 0; j < this.keys.length; ++j) {
            if (this.keys[j] > largestKey) {
                this.direction[j] = !this.direction[j];
            }
            nextP.add(this.objectMap.get(this.keys[j]));
        }
        final List<E> result = this.nextPermutation;
        this.nextPermutation = nextP;
        return result;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }
}
