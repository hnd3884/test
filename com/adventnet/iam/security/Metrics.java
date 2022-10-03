package com.adventnet.iam.security;

import java.util.TreeMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Metrics<E extends Enum>
{
    private E[] values;
    private AtomicIntegerArray array;
    
    public Metrics(final Class<E> enumClass) {
        this.values = enumClass.getEnumConstants();
        this.reset();
    }
    
    public void inc(final E e) {
        final int index = e.ordinal();
        this.array.incrementAndGet(index);
    }
    
    public void inc(final E e, final int delta) {
        final int index = e.ordinal();
        this.array.getAndAdd(index, delta);
    }
    
    public void set(final E e, final int newValue) {
        final int index = e.ordinal();
        this.array.set(index, newValue);
    }
    
    public int getAndSet(final E e, final int newValue) {
        final int index = e.ordinal();
        return this.array.getAndSet(index, newValue);
    }
    
    public int get(final E e) {
        final int index = e.ordinal();
        return this.array.get(index);
    }
    
    public void reset() {
        this.array = new AtomicIntegerArray(this.values.length);
    }
    
    public Map<E, Integer> getNumbers() {
        final Map<E, Integer> map = new TreeMap<E, Integer>();
        for (final E e : this.values) {
            map.put(e, this.get(e));
        }
        return map;
    }
}
