package com.sun.org.apache.xml.internal.utils;

import java.io.Serializable;

public class StringVector implements Serializable
{
    static final long serialVersionUID = 4995234972032919748L;
    protected int m_blocksize;
    protected String[] m_map;
    protected int m_firstFree;
    protected int m_mapSize;
    
    public StringVector() {
        this.m_firstFree = 0;
        this.m_blocksize = 8;
        this.m_mapSize = this.m_blocksize;
        this.m_map = new String[this.m_blocksize];
    }
    
    public StringVector(final int blocksize) {
        this.m_firstFree = 0;
        this.m_blocksize = blocksize;
        this.m_mapSize = blocksize;
        this.m_map = new String[blocksize];
    }
    
    public int getLength() {
        return this.m_firstFree;
    }
    
    public final int size() {
        return this.m_firstFree;
    }
    
    public final void addElement(final String value) {
        if (this.m_firstFree + 1 >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize;
            final String[] newMap = new String[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
            this.m_map = newMap;
        }
        this.m_map[this.m_firstFree] = value;
        ++this.m_firstFree;
    }
    
    public final String elementAt(final int i) {
        return this.m_map[i];
    }
    
    public final boolean contains(final String s) {
        if (null == s) {
            return false;
        }
        for (int i = 0; i < this.m_firstFree; ++i) {
            if (this.m_map[i].equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    public final boolean containsIgnoreCase(final String s) {
        if (null == s) {
            return false;
        }
        for (int i = 0; i < this.m_firstFree; ++i) {
            if (this.m_map[i].equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
    
    public final void push(final String s) {
        if (this.m_firstFree + 1 >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize;
            final String[] newMap = new String[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
            this.m_map = newMap;
        }
        this.m_map[this.m_firstFree] = s;
        ++this.m_firstFree;
    }
    
    public final String pop() {
        if (this.m_firstFree <= 0) {
            return null;
        }
        --this.m_firstFree;
        final String s = this.m_map[this.m_firstFree];
        this.m_map[this.m_firstFree] = null;
        return s;
    }
    
    public final String peek() {
        return (this.m_firstFree <= 0) ? null : this.m_map[this.m_firstFree - 1];
    }
}
