package com.sun.org.apache.xml.internal.utils;

public class IntVector implements Cloneable
{
    protected int m_blocksize;
    protected int[] m_map;
    protected int m_firstFree;
    protected int m_mapSize;
    
    public IntVector() {
        this.m_firstFree = 0;
        this.m_blocksize = 32;
        this.m_mapSize = this.m_blocksize;
        this.m_map = new int[this.m_blocksize];
    }
    
    public IntVector(final int blocksize) {
        this.m_firstFree = 0;
        this.m_blocksize = blocksize;
        this.m_mapSize = blocksize;
        this.m_map = new int[blocksize];
    }
    
    public IntVector(final int blocksize, final int increaseSize) {
        this.m_firstFree = 0;
        this.m_blocksize = increaseSize;
        this.m_mapSize = blocksize;
        this.m_map = new int[blocksize];
    }
    
    public IntVector(final IntVector v) {
        this.m_firstFree = 0;
        this.m_map = new int[v.m_mapSize];
        this.m_mapSize = v.m_mapSize;
        this.m_firstFree = v.m_firstFree;
        this.m_blocksize = v.m_blocksize;
        System.arraycopy(v.m_map, 0, this.m_map, 0, this.m_firstFree);
    }
    
    public final int size() {
        return this.m_firstFree;
    }
    
    public final void setSize(final int sz) {
        this.m_firstFree = sz;
    }
    
    public final void addElement(final int value) {
        if (this.m_firstFree + 1 >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize;
            final int[] newMap = new int[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
            this.m_map = newMap;
        }
        this.m_map[this.m_firstFree] = value;
        ++this.m_firstFree;
    }
    
    public final void addElements(final int value, final int numberOfElements) {
        if (this.m_firstFree + numberOfElements >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize + numberOfElements;
            final int[] newMap = new int[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
            this.m_map = newMap;
        }
        for (int i = 0; i < numberOfElements; ++i) {
            this.m_map[this.m_firstFree] = value;
            ++this.m_firstFree;
        }
    }
    
    public final void addElements(final int numberOfElements) {
        if (this.m_firstFree + numberOfElements >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize + numberOfElements;
            final int[] newMap = new int[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
            this.m_map = newMap;
        }
        this.m_firstFree += numberOfElements;
    }
    
    public final void insertElementAt(final int value, final int at) {
        if (this.m_firstFree + 1 >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize;
            final int[] newMap = new int[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
            this.m_map = newMap;
        }
        if (at <= this.m_firstFree - 1) {
            System.arraycopy(this.m_map, at, this.m_map, at + 1, this.m_firstFree - at);
        }
        this.m_map[at] = value;
        ++this.m_firstFree;
    }
    
    public final void removeAllElements() {
        for (int i = 0; i < this.m_firstFree; ++i) {
            this.m_map[i] = Integer.MIN_VALUE;
        }
        this.m_firstFree = 0;
    }
    
    public final boolean removeElement(final int s) {
        for (int i = 0; i < this.m_firstFree; ++i) {
            if (this.m_map[i] == s) {
                if (i + 1 < this.m_firstFree) {
                    System.arraycopy(this.m_map, i + 1, this.m_map, i - 1, this.m_firstFree - i);
                }
                else {
                    this.m_map[i] = Integer.MIN_VALUE;
                }
                --this.m_firstFree;
                return true;
            }
        }
        return false;
    }
    
    public final void removeElementAt(final int i) {
        if (i > this.m_firstFree) {
            System.arraycopy(this.m_map, i + 1, this.m_map, i, this.m_firstFree);
        }
        else {
            this.m_map[i] = Integer.MIN_VALUE;
        }
        --this.m_firstFree;
    }
    
    public final void setElementAt(final int value, final int index) {
        this.m_map[index] = value;
    }
    
    public final int elementAt(final int i) {
        return this.m_map[i];
    }
    
    public final boolean contains(final int s) {
        for (int i = 0; i < this.m_firstFree; ++i) {
            if (this.m_map[i] == s) {
                return true;
            }
        }
        return false;
    }
    
    public final int indexOf(final int elem, final int index) {
        for (int i = index; i < this.m_firstFree; ++i) {
            if (this.m_map[i] == elem) {
                return i;
            }
        }
        return Integer.MIN_VALUE;
    }
    
    public final int indexOf(final int elem) {
        for (int i = 0; i < this.m_firstFree; ++i) {
            if (this.m_map[i] == elem) {
                return i;
            }
        }
        return Integer.MIN_VALUE;
    }
    
    public final int lastIndexOf(final int elem) {
        for (int i = this.m_firstFree - 1; i >= 0; --i) {
            if (this.m_map[i] == elem) {
                return i;
            }
        }
        return Integer.MIN_VALUE;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return new IntVector(this);
    }
}
