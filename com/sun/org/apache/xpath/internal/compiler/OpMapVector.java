package com.sun.org.apache.xpath.internal.compiler;

public class OpMapVector
{
    protected int m_blocksize;
    protected int[] m_map;
    protected int m_lengthPos;
    protected int m_mapSize;
    
    public OpMapVector(final int blocksize, final int increaseSize, final int lengthPos) {
        this.m_lengthPos = 0;
        this.m_blocksize = increaseSize;
        this.m_mapSize = blocksize;
        this.m_lengthPos = lengthPos;
        this.m_map = new int[blocksize];
    }
    
    public final int elementAt(final int i) {
        return this.m_map[i];
    }
    
    public final void setElementAt(final int value, final int index) {
        if (index >= this.m_mapSize) {
            final int oldSize = this.m_mapSize;
            this.m_mapSize += this.m_blocksize;
            final int[] newMap = new int[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, oldSize);
            this.m_map = newMap;
        }
        this.m_map[index] = value;
    }
    
    public final void setToSize(final int size) {
        final int[] newMap = new int[size];
        System.arraycopy(this.m_map, 0, newMap, 0, this.m_map[this.m_lengthPos]);
        this.m_mapSize = size;
        this.m_map = newMap;
    }
}
