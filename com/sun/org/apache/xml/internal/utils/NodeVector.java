package com.sun.org.apache.xml.internal.utils;

import java.io.Serializable;

public class NodeVector implements Serializable, Cloneable
{
    static final long serialVersionUID = -713473092200731870L;
    private int m_blocksize;
    private int[] m_map;
    protected int m_firstFree;
    private int m_mapSize;
    
    public NodeVector() {
        this.m_firstFree = 0;
        this.m_blocksize = 32;
        this.m_mapSize = 0;
    }
    
    public NodeVector(final int blocksize) {
        this.m_firstFree = 0;
        this.m_blocksize = blocksize;
        this.m_mapSize = 0;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final NodeVector clone = (NodeVector)super.clone();
        if (null != this.m_map && this.m_map == clone.m_map) {
            clone.m_map = new int[this.m_map.length];
            System.arraycopy(this.m_map, 0, clone.m_map, 0, this.m_map.length);
        }
        return clone;
    }
    
    public int size() {
        return this.m_firstFree;
    }
    
    public void addElement(final int value) {
        if (this.m_firstFree + 1 >= this.m_mapSize) {
            if (null == this.m_map) {
                this.m_map = new int[this.m_blocksize];
                this.m_mapSize = this.m_blocksize;
            }
            else {
                this.m_mapSize += this.m_blocksize;
                final int[] newMap = new int[this.m_mapSize];
                System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
                this.m_map = newMap;
            }
        }
        this.m_map[this.m_firstFree] = value;
        ++this.m_firstFree;
    }
    
    public final void push(final int value) {
        int ff = this.m_firstFree;
        if (ff + 1 >= this.m_mapSize) {
            if (null == this.m_map) {
                this.m_map = new int[this.m_blocksize];
                this.m_mapSize = this.m_blocksize;
            }
            else {
                this.m_mapSize += this.m_blocksize;
                final int[] newMap = new int[this.m_mapSize];
                System.arraycopy(this.m_map, 0, newMap, 0, ff + 1);
                this.m_map = newMap;
            }
        }
        this.m_map[ff] = value;
        ++ff;
        this.m_firstFree = ff;
    }
    
    public final int pop() {
        --this.m_firstFree;
        final int n = this.m_map[this.m_firstFree];
        this.m_map[this.m_firstFree] = -1;
        return n;
    }
    
    public final int popAndTop() {
        --this.m_firstFree;
        this.m_map[this.m_firstFree] = -1;
        return (this.m_firstFree == 0) ? -1 : this.m_map[this.m_firstFree - 1];
    }
    
    public final void popQuick() {
        --this.m_firstFree;
        this.m_map[this.m_firstFree] = -1;
    }
    
    public final int peepOrNull() {
        return (null != this.m_map && this.m_firstFree > 0) ? this.m_map[this.m_firstFree - 1] : -1;
    }
    
    public final void pushPair(final int v1, final int v2) {
        if (null == this.m_map) {
            this.m_map = new int[this.m_blocksize];
            this.m_mapSize = this.m_blocksize;
        }
        else if (this.m_firstFree + 2 >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize;
            final int[] newMap = new int[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree);
            this.m_map = newMap;
        }
        this.m_map[this.m_firstFree] = v1;
        this.m_map[this.m_firstFree + 1] = v2;
        this.m_firstFree += 2;
    }
    
    public final void popPair() {
        this.m_firstFree -= 2;
        this.m_map[this.m_firstFree] = -1;
        this.m_map[this.m_firstFree + 1] = -1;
    }
    
    public final void setTail(final int n) {
        this.m_map[this.m_firstFree - 1] = n;
    }
    
    public final void setTailSub1(final int n) {
        this.m_map[this.m_firstFree - 2] = n;
    }
    
    public final int peepTail() {
        return this.m_map[this.m_firstFree - 1];
    }
    
    public final int peepTailSub1() {
        return this.m_map[this.m_firstFree - 2];
    }
    
    public void insertInOrder(final int value) {
        for (int i = 0; i < this.m_firstFree; ++i) {
            if (value < this.m_map[i]) {
                this.insertElementAt(value, i);
                return;
            }
        }
        this.addElement(value);
    }
    
    public void insertElementAt(final int value, final int at) {
        if (null == this.m_map) {
            this.m_map = new int[this.m_blocksize];
            this.m_mapSize = this.m_blocksize;
        }
        else if (this.m_firstFree + 1 >= this.m_mapSize) {
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
    
    public void appendNodes(final NodeVector nodes) {
        final int nNodes = nodes.size();
        if (null == this.m_map) {
            this.m_mapSize = nNodes + this.m_blocksize;
            this.m_map = new int[this.m_mapSize];
        }
        else if (this.m_firstFree + nNodes >= this.m_mapSize) {
            this.m_mapSize += nNodes + this.m_blocksize;
            final int[] newMap = new int[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + nNodes);
            this.m_map = newMap;
        }
        System.arraycopy(nodes.m_map, 0, this.m_map, this.m_firstFree, nNodes);
        this.m_firstFree += nNodes;
    }
    
    public void removeAllElements() {
        if (null == this.m_map) {
            return;
        }
        for (int i = 0; i < this.m_firstFree; ++i) {
            this.m_map[i] = -1;
        }
        this.m_firstFree = 0;
    }
    
    public void RemoveAllNoClear() {
        if (null == this.m_map) {
            return;
        }
        this.m_firstFree = 0;
    }
    
    public boolean removeElement(final int s) {
        if (null == this.m_map) {
            return false;
        }
        for (int i = 0; i < this.m_firstFree; ++i) {
            final int node = this.m_map[i];
            if (node == s) {
                if (i > this.m_firstFree) {
                    System.arraycopy(this.m_map, i + 1, this.m_map, i - 1, this.m_firstFree - i);
                }
                else {
                    this.m_map[i] = -1;
                }
                --this.m_firstFree;
                return true;
            }
        }
        return false;
    }
    
    public void removeElementAt(final int i) {
        if (null == this.m_map) {
            return;
        }
        if (i > this.m_firstFree) {
            System.arraycopy(this.m_map, i + 1, this.m_map, i - 1, this.m_firstFree - i);
        }
        else {
            this.m_map[i] = -1;
        }
    }
    
    public void setElementAt(final int node, final int index) {
        if (null == this.m_map) {
            this.m_map = new int[this.m_blocksize];
            this.m_mapSize = this.m_blocksize;
        }
        if (index == -1) {
            this.addElement(node);
        }
        this.m_map[index] = node;
    }
    
    public int elementAt(final int i) {
        if (null == this.m_map) {
            return -1;
        }
        return this.m_map[i];
    }
    
    public boolean contains(final int s) {
        if (null == this.m_map) {
            return false;
        }
        for (int i = 0; i < this.m_firstFree; ++i) {
            final int node = this.m_map[i];
            if (node == s) {
                return true;
            }
        }
        return false;
    }
    
    public int indexOf(final int elem, final int index) {
        if (null == this.m_map) {
            return -1;
        }
        for (int i = index; i < this.m_firstFree; ++i) {
            final int node = this.m_map[i];
            if (node == elem) {
                return i;
            }
        }
        return -1;
    }
    
    public int indexOf(final int elem) {
        if (null == this.m_map) {
            return -1;
        }
        for (int i = 0; i < this.m_firstFree; ++i) {
            final int node = this.m_map[i];
            if (node == elem) {
                return i;
            }
        }
        return -1;
    }
    
    public void sort(final int[] a, final int lo0, final int hi0) throws Exception {
        int lo = lo0;
        int hi = hi0;
        if (lo >= hi) {
            return;
        }
        if (lo == hi - 1) {
            if (a[lo] > a[hi]) {
                final int T = a[lo];
                a[lo] = a[hi];
                a[hi] = T;
            }
            return;
        }
        final int mid = lo + hi >>> 1;
        final int pivot = a[mid];
        a[mid] = a[hi];
        a[hi] = pivot;
        while (lo < hi) {
            while (a[lo] <= pivot && lo < hi) {
                ++lo;
            }
            while (pivot <= a[hi] && lo < hi) {
                --hi;
            }
            if (lo < hi) {
                final int T2 = a[lo];
                a[lo] = a[hi];
                a[hi] = T2;
            }
        }
        a[hi0] = a[hi];
        a[hi] = pivot;
        this.sort(a, lo0, lo - 1);
        this.sort(a, hi + 1, hi0);
    }
    
    public void sort() throws Exception {
        this.sort(this.m_map, 0, this.m_firstFree - 1);
    }
}
