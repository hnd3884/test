package com.sun.org.apache.xml.internal.utils;

public class SuballocatedByteVector
{
    protected int m_blocksize;
    protected int m_numblocks;
    protected byte[][] m_map;
    protected int m_firstFree;
    protected byte[] m_map0;
    
    public SuballocatedByteVector() {
        this(2048);
    }
    
    public SuballocatedByteVector(final int blocksize) {
        this.m_numblocks = 32;
        this.m_firstFree = 0;
        this.m_blocksize = blocksize;
        this.m_map0 = new byte[blocksize];
        (this.m_map = new byte[this.m_numblocks][])[0] = this.m_map0;
    }
    
    public SuballocatedByteVector(final int blocksize, final int increaseSize) {
        this(blocksize);
    }
    
    public int size() {
        return this.m_firstFree;
    }
    
    private void setSize(final int sz) {
        if (this.m_firstFree < sz) {
            this.m_firstFree = sz;
        }
    }
    
    public void addElement(final byte value) {
        if (this.m_firstFree < this.m_blocksize) {
            this.m_map0[this.m_firstFree++] = value;
        }
        else {
            final int index = this.m_firstFree / this.m_blocksize;
            final int offset = this.m_firstFree % this.m_blocksize;
            ++this.m_firstFree;
            if (index >= this.m_map.length) {
                final int newsize = index + this.m_numblocks;
                final byte[][] newMap = new byte[newsize][];
                System.arraycopy(this.m_map, 0, newMap, 0, this.m_map.length);
                this.m_map = newMap;
            }
            byte[] block = this.m_map[index];
            if (null == block) {
                final byte[][] map = this.m_map;
                final int n = index;
                final byte[] array = new byte[this.m_blocksize];
                map[n] = array;
                block = array;
            }
            block[offset] = value;
        }
    }
    
    private void addElements(final byte value, int numberOfElements) {
        if (this.m_firstFree + numberOfElements < this.m_blocksize) {
            for (int i = 0; i < numberOfElements; ++i) {
                this.m_map0[this.m_firstFree++] = value;
            }
        }
        else {
            int index = this.m_firstFree / this.m_blocksize;
            int offset = this.m_firstFree % this.m_blocksize;
            this.m_firstFree += numberOfElements;
            while (numberOfElements > 0) {
                if (index >= this.m_map.length) {
                    final int newsize = index + this.m_numblocks;
                    final byte[][] newMap = new byte[newsize][];
                    System.arraycopy(this.m_map, 0, newMap, 0, this.m_map.length);
                    this.m_map = newMap;
                }
                byte[] block = this.m_map[index];
                if (null == block) {
                    final byte[][] map = this.m_map;
                    final int n = index;
                    final byte[] array = new byte[this.m_blocksize];
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
            final int index = this.m_firstFree % this.m_blocksize;
            for (int newindex = (this.m_firstFree + numberOfElements) % this.m_blocksize, i = index + 1; i <= newindex; ++i) {
                this.m_map[i] = new byte[this.m_blocksize];
            }
        }
        this.m_firstFree = newlen;
    }
    
    private void insertElementAt(byte value, final int at) {
        if (at == this.m_firstFree) {
            this.addElement(value);
        }
        else if (at > this.m_firstFree) {
            final int index = at / this.m_blocksize;
            if (index >= this.m_map.length) {
                final int newsize = index + this.m_numblocks;
                final byte[][] newMap = new byte[newsize][];
                System.arraycopy(this.m_map, 0, newMap, 0, this.m_map.length);
                this.m_map = newMap;
            }
            byte[] block = this.m_map[index];
            if (null == block) {
                final byte[][] map = this.m_map;
                final int n = index;
                final byte[] array = new byte[this.m_blocksize];
                map[n] = array;
                block = array;
            }
            final int offset = at % this.m_blocksize;
            block[offset] = value;
            this.m_firstFree = offset + 1;
        }
        else {
            int index = at / this.m_blocksize;
            final int maxindex = this.m_firstFree + 1 / this.m_blocksize;
            ++this.m_firstFree;
            int offset = at % this.m_blocksize;
            while (index <= maxindex) {
                final int copylen = this.m_blocksize - offset - 1;
                byte[] block2 = this.m_map[index];
                byte push;
                if (null == block2) {
                    push = 0;
                    final byte[][] map2 = this.m_map;
                    final int n2 = index;
                    final byte[] array2 = new byte[this.m_blocksize];
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
    }
    
    private boolean removeElement(final byte s) {
        final int at = this.indexOf(s, 0);
        if (at < 0) {
            return false;
        }
        this.removeElementAt(at);
        return true;
    }
    
    private void removeElementAt(final int at) {
        if (at < this.m_firstFree) {
            int index = at / this.m_blocksize;
            final int maxindex = this.m_firstFree / this.m_blocksize;
            int offset = at % this.m_blocksize;
            while (index <= maxindex) {
                final int copylen = this.m_blocksize - offset - 1;
                byte[] block = this.m_map[index];
                if (null == block) {
                    final byte[][] map = this.m_map;
                    final int n = index;
                    final byte[] array = new byte[this.m_blocksize];
                    map[n] = array;
                    block = array;
                }
                else {
                    System.arraycopy(block, offset + 1, block, offset, copylen);
                }
                if (index < maxindex) {
                    final byte[] next = this.m_map[index + 1];
                    if (next != null) {
                        block[this.m_blocksize - 1] = (byte)((next != null) ? next[0] : 0);
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
    
    public void setElementAt(final byte value, final int at) {
        if (at < this.m_blocksize) {
            this.m_map0[at] = value;
            return;
        }
        final int index = at / this.m_blocksize;
        final int offset = at % this.m_blocksize;
        if (index >= this.m_map.length) {
            final int newsize = index + this.m_numblocks;
            final byte[][] newMap = new byte[newsize][];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_map.length);
            this.m_map = newMap;
        }
        byte[] block = this.m_map[index];
        if (null == block) {
            final byte[][] map = this.m_map;
            final int n = index;
            final byte[] array = new byte[this.m_blocksize];
            map[n] = array;
            block = array;
        }
        block[offset] = value;
        if (at >= this.m_firstFree) {
            this.m_firstFree = at + 1;
        }
    }
    
    public byte elementAt(final int i) {
        if (i < this.m_blocksize) {
            return this.m_map0[i];
        }
        return this.m_map[i / this.m_blocksize][i % this.m_blocksize];
    }
    
    private boolean contains(final byte s) {
        return this.indexOf(s, 0) >= 0;
    }
    
    public int indexOf(final byte elem, final int index) {
        if (index >= this.m_firstFree) {
            return -1;
        }
        int bindex = index / this.m_blocksize;
        int boffset = index % this.m_blocksize;
        int maxindex;
        for (maxindex = this.m_firstFree / this.m_blocksize; bindex < maxindex; ++bindex) {
            final byte[] block = this.m_map[bindex];
            if (block != null) {
                for (int offset = boffset; offset < this.m_blocksize; ++offset) {
                    if (block[offset] == elem) {
                        return offset + bindex * this.m_blocksize;
                    }
                }
            }
            boffset = 0;
        }
        final int maxoffset = this.m_firstFree % this.m_blocksize;
        final byte[] block = this.m_map[maxindex];
        for (int offset2 = boffset; offset2 < maxoffset; ++offset2) {
            if (block[offset2] == elem) {
                return offset2 + maxindex * this.m_blocksize;
            }
        }
        return -1;
    }
    
    public int indexOf(final byte elem) {
        return this.indexOf(elem, 0);
    }
    
    private int lastIndexOf(final byte elem) {
        int boffset = this.m_firstFree % this.m_blocksize;
        for (int index = this.m_firstFree / this.m_blocksize; index >= 0; --index) {
            final byte[] block = this.m_map[index];
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
}
