package org.tukaani.xz;

import java.util.ArrayList;
import java.util.List;

public class ResettableArrayCache extends ArrayCache
{
    private final ArrayCache arrayCache;
    private final List<byte[]> byteArrays;
    private final List<int[]> intArrays;
    
    public ResettableArrayCache(final ArrayCache arrayCache) {
        this.arrayCache = arrayCache;
        if (arrayCache == ArrayCache.getDummyCache()) {
            this.byteArrays = null;
            this.intArrays = null;
        }
        else {
            this.byteArrays = new ArrayList<byte[]>();
            this.intArrays = new ArrayList<int[]>();
        }
    }
    
    @Override
    public byte[] getByteArray(final int n, final boolean b) {
        final byte[] byteArray = this.arrayCache.getByteArray(n, b);
        if (this.byteArrays != null) {
            synchronized (this.byteArrays) {
                this.byteArrays.add(byteArray);
            }
        }
        return byteArray;
    }
    
    @Override
    public void putArray(final byte[] array) {
        if (this.byteArrays != null) {
            synchronized (this.byteArrays) {
                final int lastIndex = this.byteArrays.lastIndexOf(array);
                if (lastIndex != -1) {
                    this.byteArrays.remove(lastIndex);
                }
            }
            this.arrayCache.putArray(array);
        }
    }
    
    @Override
    public int[] getIntArray(final int n, final boolean b) {
        final int[] intArray = this.arrayCache.getIntArray(n, b);
        if (this.intArrays != null) {
            synchronized (this.intArrays) {
                this.intArrays.add(intArray);
            }
        }
        return intArray;
    }
    
    @Override
    public void putArray(final int[] array) {
        if (this.intArrays != null) {
            synchronized (this.intArrays) {
                final int lastIndex = this.intArrays.lastIndexOf(array);
                if (lastIndex != -1) {
                    this.intArrays.remove(lastIndex);
                }
            }
            this.arrayCache.putArray(array);
        }
    }
    
    public void reset() {
        if (this.byteArrays != null) {
            synchronized (this.byteArrays) {
                for (int i = this.byteArrays.size() - 1; i >= 0; --i) {
                    this.arrayCache.putArray(this.byteArrays.get(i));
                }
                this.byteArrays.clear();
            }
            synchronized (this.intArrays) {
                for (int j = this.intArrays.size() - 1; j >= 0; --j) {
                    this.arrayCache.putArray(this.intArrays.get(j));
                }
                this.intArrays.clear();
            }
        }
    }
}
