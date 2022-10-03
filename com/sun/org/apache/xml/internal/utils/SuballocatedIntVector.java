package com.sun.org.apache.xml.internal.utils;

public class SuballocatedIntVector
{
    protected int m_blocksize;
    protected int m_SHIFT;
    protected int m_MASK;
    protected static final int NUMBLOCKS_DEFAULT = 32;
    protected int m_numblocks;
    protected int[][] m_map;
    protected int m_firstFree;
    protected int[] m_map0;
    protected int[] m_buildCache;
    protected int m_buildCacheStartIndex;
    
    public SuballocatedIntVector() {
        this(2048);
    }
    
    public SuballocatedIntVector(int blocksize, final int numblocks) {
        this.m_numblocks = 32;
        this.m_firstFree = 0;
        this.m_SHIFT = 0;
        while (0 != (blocksize >>>= 1)) {
            ++this.m_SHIFT;
        }
        this.m_blocksize = 1 << this.m_SHIFT;
        this.m_MASK = this.m_blocksize - 1;
        this.m_numblocks = numblocks;
        this.m_map0 = new int[this.m_blocksize];
        (this.m_map = new int[numblocks][])[0] = this.m_map0;
        this.m_buildCache = this.m_map0;
        this.m_buildCacheStartIndex = 0;
    }
    
    public SuballocatedIntVector(final int blocksize) {
        this(blocksize, 32);
    }
    
    public int size() {
        return this.m_firstFree;
    }
    
    public void setSize(final int sz) {
        if (this.m_firstFree > sz) {
            this.m_firstFree = sz;
        }
    }
    
    public void addElement(final int value) {
        final int indexRelativeToCache = this.m_firstFree - this.m_buildCacheStartIndex;
        if (indexRelativeToCache >= 0 && indexRelativeToCache < this.m_blocksize) {
            this.m_buildCache[indexRelativeToCache] = value;
            ++this.m_firstFree;
        }
        else {
            final int index = this.m_firstFree >>> this.m_SHIFT;
            final int offset = this.m_firstFree & this.m_MASK;
            if (index >= this.m_map.length) {
                final int newsize = index + this.m_numblocks;
                final int[][] newMap = new int[newsize][];
                System.arraycopy(this.m_map, 0, newMap, 0, this.m_map.length);
                this.m_map = newMap;
            }
            int[] block = this.m_map[index];
            if (null == block) {
                final int[][] map = this.m_map;
                final int n = index;
                final int[] array = new int[this.m_blocksize];
                map[n] = array;
                block = array;
            }
            block[offset] = value;
            this.m_buildCache = block;
            this.m_buildCacheStartIndex = this.m_firstFree - offset;
            ++this.m_firstFree;
        }
    }
    
    private void addElements(final int value, int numberOfElements) {
        if (this.m_firstFree + numberOfElements < this.m_blocksize) {
            for (int i = 0; i < numberOfElements; ++i) {
                this.m_map0[this.m_firstFree++] = value;
            }
        }
        else {
            int index = this.m_firstFree >>> this.m_SHIFT;
            int offset = this.m_firstFree & this.m_MASK;
            this.m_firstFree += numberOfElements;
            while (numberOfElements > 0) {
                if (index >= this.m_map.length) {
                    final int newsize = index + this.m_numblocks;
                    final int[][] newMap = new int[newsize][];
                    System.arraycopy(this.m_map, 0, newMap, 0, this.m_map.length);
                    this.m_map = newMap;
                }
                int[] block = this.m_map[index];
                if (null == block) {
                    final int[][] map = this.m_map;
                    final int n = index;
                    final int[] array = new int[this.m_blocksize];
                    map[n] = array;
                    block = array;
                }
                int copied = (this.m_blocksize - offset < numberOfElements) ? (this.m_blocksize - offset) : numberOfElements;
                numberOfElements -= copied;
                while (copied-- > 0) {
                    block[offset++] = value;
                }
                ++index;
                offset = 0;
            }
        }
    }
    
    private void addElements(final int numberOfElements) {
        final int newlen = this.m_firstFree + numberOfElements;
        if (newlen > this.m_blocksize) {
            final int index = this.m_firstFree >>> this.m_SHIFT;
            for (int newindex = this.m_firstFree + numberOfElements >>> this.m_SHIFT, i = index + 1; i <= newindex; ++i) {
                this.m_map[i] = new int[this.m_blocksize];
            }
        }
        this.m_firstFree = newlen;
    }
    
    private void insertElementAt(int value, final int at) {
        if (at == this.m_firstFree) {
            this.addElement(value);
        }
        else if (at > this.m_firstFree) {
            final int index = at >>> this.m_SHIFT;
            if (index >= this.m_map.length) {
                final int newsize = index + this.m_numblocks;
                final int[][] newMap = new int[newsize][];
                System.arraycopy(this.m_map, 0, newMap, 0, this.m_map.length);
                this.m_map = newMap;
            }
            int[] block = this.m_map[index];
            if (null == block) {
                final int[][] map = this.m_map;
                final int n = index;
                final int[] array = new int[this.m_blocksize];
                map[n] = array;
                block = array;
            }
            final int offset = at & this.m_MASK;
            block[offset] = value;
            this.m_firstFree = offset + 1;
        }
        else {
            int index = at >>> this.m_SHIFT;
            final int maxindex = this.m_firstFree >>> this.m_SHIFT;
            ++this.m_firstFree;
            int offset = at & this.m_MASK;
            while (index <= maxindex) {
                final int copylen = this.m_blocksize - offset - 1;
                int[] block2 = this.m_map[index];
                int push;
                if (null == block2) {
                    push = 0;
                    final int[][] map2 = this.m_map;
                    final int n2 = index;
                    final int[] array2 = new int[this.m_blocksize];
                    map2[n2] = array2;
                    block2 = array2;
                }
                else {
                    push = block2[this.m_blocksize - 1];
                    System.arraycopy(block2, offset, block2, offset + 1, copylen);
                }
                block2[offset] = value;
                value = push;
                offset = 0;
                ++index;
            }
        }
    }
    
    public void removeAllElements() {
        this.m_firstFree = 0;
        this.m_buildCache = this.m_map0;
        this.m_buildCacheStartIndex = 0;
    }
    
    private boolean removeElement(final int s) {
        final int at = this.indexOf(s, 0);
        if (at < 0) {
            return false;
        }
        this.removeElementAt(at);
        return true;
    }
    
    private void removeElementAt(final int at) {
        if (at < this.m_firstFree) {
            int index = at >>> this.m_SHIFT;
            final int maxindex = this.m_firstFree >>> this.m_SHIFT;
            int offset = at & this.m_MASK;
            while (index <= maxindex) {
                final int copylen = this.m_blocksize - offset - 1;
                int[] block = this.m_map[index];
                if (null == block) {
                    final int[][] map = this.m_map;
                    final int n = index;
                    final int[] array = new int[this.m_blocksize];
                    map[n] = array;
                    block = array;
                }
                else {
                    System.arraycopy(block, offset + 1, block, offset, copylen);
                }
                if (index < maxindex) {
                    final int[] next = this.m_map[index + 1];
                    if (next != null) {
                        block[this.m_blocksize - 1] = ((next != null) ? next[0] : 0);
                    }
                }
                else {
                    block[this.m_blocksize - 1] = 0;
                }
                offset = 0;
                ++index;
            }
        }
        --this.m_firstFree;
    }
    
    public void setElementAt(final int value, final int at) {
        if (at < this.m_blocksize) {
            this.m_map0[at] = value;
        }
        else {
            final int index = at >>> this.m_SHIFT;
            final int offset = at & this.m_MASK;
            if (index >= this.m_map.length) {
                final int newsize = index + this.m_numblocks;
                final int[][] newMap = new int[newsize][];
                System.arraycopy(this.m_map, 0, newMap, 0, this.m_map.length);
                this.m_map = newMap;
            }
            int[] block = this.m_map[index];
            if (null == block) {
                final int[][] map = this.m_map;
                final int n = index;
                final int[] array = new int[this.m_blocksize];
                map[n] = array;
                block = array;
            }
            block[offset] = value;
        }
        if (at >= this.m_firstFree) {
            this.m_firstFree = at + 1;
        }
    }
    
    public int elementAt(final int i) {
        if (i < this.m_blocksize) {
            return this.m_map0[i];
        }
        return this.m_map[i >>> this.m_SHIFT][i & this.m_MASK];
    }
    
    private boolean contains(final int s) {
        return this.indexOf(s, 0) >= 0;
    }
    
    public int indexOf(final int elem, final int index) {
        if (index >= this.m_firstFree) {
            return -1;
        }
        int bindex = index >>> this.m_SHIFT;
        int boffset = index & this.m_MASK;
        int maxindex;
        for (maxindex = this.m_firstFree >>> this.m_SHIFT; bindex < maxindex; ++bindex) {
            final int[] block = this.m_map[bindex];
            if (block != null) {
                for (int offset = boffset; offset < this.m_blocksize; ++offset) {
                    if (block[offset] == elem) {
                        return offset + bindex * this.m_blocksize;
                    }
                }
            }
            boffset = 0;
        }
        final int maxoffset = this.m_firstFree & this.m_MASK;
        final int[] block = this.m_map[maxindex];
        for (int offset2 = boffset; offset2 < maxoffset; ++offset2) {
            if (block[offset2] == elem) {
                return offset2 + maxindex * this.m_blocksize;
            }
        }
        return -1;
    }
    
    public int indexOf(final int elem) {
        return this.indexOf(elem, 0);
    }
    
    private int lastIndexOf(final int elem) {
        int boffset = this.m_firstFree & this.m_MASK;
        for (int index = this.m_firstFree >>> this.m_SHIFT; index >= 0; --index) {
            final int[] block = this.m_map[index];
            if (block != null) {
                for (int offset = boffset; offset >= 0; --offset) {
                    if (block[offset] == elem) {
                        return offset + index * this.m_blocksize;
                    }
                }
            }
            boffset = 0;
        }
        return -1;
    }
    
    public final int[] getMap0() {
        return this.m_map0;
    }
    
    public final int[][] getMap() {
        return this.m_map;
    }
}
