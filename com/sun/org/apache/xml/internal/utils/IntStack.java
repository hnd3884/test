package com.sun.org.apache.xml.internal.utils;

import java.util.EmptyStackException;

public class IntStack extends IntVector
{
    public IntStack() {
    }
    
    public IntStack(final int blocksize) {
        super(blocksize);
    }
    
    public IntStack(final IntStack v) {
        super(v);
    }
    
    public int push(final int i) {
        if (this.m_firstFree + 1 >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize;
            final int[] newMap = new int[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
            this.m_map = newMap;
        }
        this.m_map[this.m_firstFree] = i;
        ++this.m_firstFree;
        return i;
    }
    
    public final int pop() {
        final int[] map = this.m_map;
        final int firstFree = this.m_firstFree - 1;
        this.m_firstFree = firstFree;
        return map[firstFree];
    }
    
    public final void quickPop(final int n) {
        this.m_firstFree -= n;
    }
    
    public final int peek() {
        try {
            return this.m_map[this.m_firstFree - 1];
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }
    }
    
    public int peek(final int n) {
        try {
            return this.m_map[this.m_firstFree - (1 + n)];
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }
    }
    
    public void setTop(final int val) {
        try {
            this.m_map[this.m_firstFree - 1] = val;
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }
    }
    
    public boolean empty() {
        return this.m_firstFree == 0;
    }
    
    public int search(final int o) {
        final int i = this.lastIndexOf(o);
        if (i >= 0) {
            return this.size() - i;
        }
        return -1;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
