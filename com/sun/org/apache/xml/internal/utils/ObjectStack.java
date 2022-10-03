package com.sun.org.apache.xml.internal.utils;

import java.util.EmptyStackException;

public class ObjectStack extends ObjectVector
{
    public ObjectStack() {
    }
    
    public ObjectStack(final int blocksize) {
        super(blocksize);
    }
    
    public ObjectStack(final ObjectStack v) {
        super(v);
    }
    
    public Object push(final Object i) {
        if (this.m_firstFree + 1 >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize;
            final Object[] newMap = new Object[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
            this.m_map = newMap;
        }
        this.m_map[this.m_firstFree] = i;
        ++this.m_firstFree;
        return i;
    }
    
    public Object pop() {
        final Object[] map = this.m_map;
        final int firstFree = this.m_firstFree - 1;
        this.m_firstFree = firstFree;
        final Object val = map[firstFree];
        this.m_map[this.m_firstFree] = null;
        return val;
    }
    
    public void quickPop(final int n) {
        this.m_firstFree -= n;
    }
    
    public Object peek() {
        try {
            return this.m_map[this.m_firstFree - 1];
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }
    }
    
    public Object peek(final int n) {
        try {
            return this.m_map[this.m_firstFree - (1 + n)];
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }
    }
    
    public void setTop(final Object val) {
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
    
    public int search(final Object o) {
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
