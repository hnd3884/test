package org.tukaani.xz;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.lang.ref.SoftReference;
import java.lang.ref.Reference;

public class BasicArrayCache extends ArrayCache
{
    private static final int CACHEABLE_SIZE_MIN = 32768;
    private static final int STACKS_MAX = 32;
    private static final int ELEMENTS_PER_STACK = 512;
    private final CacheMap<byte[]> byteArrayCache;
    private final CacheMap<int[]> intArrayCache;
    
    public BasicArrayCache() {
        this.byteArrayCache = new CacheMap<byte[]>();
        this.intArrayCache = new CacheMap<int[]>();
    }
    
    public static BasicArrayCache getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    private static <T> T getArray(final CacheMap<T> cacheMap, final int n) {
        if (n < 32768) {
            return null;
        }
        final CyclicStack cyclicStack;
        synchronized (cacheMap) {
            cyclicStack = cacheMap.get(n);
        }
        if (cyclicStack == null) {
            return null;
        }
        T value;
        do {
            final Reference reference = (Reference)cyclicStack.pop();
            if (reference == null) {
                return null;
            }
            value = (T)reference.get();
        } while (value == null);
        return value;
    }
    
    private static <T> void putArray(final CacheMap<T> cacheMap, final T t, final int n) {
        if (n < 32768) {
            return;
        }
        CyclicStack cyclicStack;
        synchronized (cacheMap) {
            cyclicStack = cacheMap.get(n);
            if (cyclicStack == null) {
                cyclicStack = new CyclicStack();
                cacheMap.put((K)n, cyclicStack);
            }
        }
        cyclicStack.push(new SoftReference(t));
    }
    
    @Override
    public byte[] getByteArray(final int n, final boolean b) {
        byte[] array = getArray(this.byteArrayCache, n);
        if (array == null) {
            array = new byte[n];
        }
        else if (b) {
            Arrays.fill(array, (byte)0);
        }
        return array;
    }
    
    @Override
    public void putArray(final byte[] array) {
        putArray(this.byteArrayCache, array, array.length);
    }
    
    @Override
    public int[] getIntArray(final int n, final boolean b) {
        int[] array = getArray(this.intArrayCache, n);
        if (array == null) {
            array = new int[n];
        }
        else if (b) {
            Arrays.fill(array, 0);
        }
        return array;
    }
    
    @Override
    public void putArray(final int[] array) {
        putArray(this.intArrayCache, array, array.length);
    }
    
    private static class CacheMap<T> extends LinkedHashMap<Integer, CyclicStack<Reference<T>>>
    {
        private static final long serialVersionUID = 1L;
        
        public CacheMap() {
            super(64, 0.75f, true);
        }
        
        @Override
        protected boolean removeEldestEntry(final Map.Entry<Integer, CyclicStack<Reference<T>>> entry) {
            return this.size() > 32;
        }
    }
    
    private static class CyclicStack<T>
    {
        private final T[] elements;
        private int pos;
        
        private CyclicStack() {
            this.elements = (T[])new Object[512];
            this.pos = 0;
        }
        
        public synchronized T pop() {
            final T t = this.elements[this.pos];
            this.elements[this.pos] = null;
            this.pos = (this.pos - 1 & 0x1FF);
            return t;
        }
        
        public synchronized void push(final T t) {
            this.pos = (this.pos + 1 & 0x1FF);
            this.elements[this.pos] = t;
        }
    }
    
    private static final class LazyHolder
    {
        static final BasicArrayCache INSTANCE;
        
        static {
            INSTANCE = new BasicArrayCache();
        }
    }
}
