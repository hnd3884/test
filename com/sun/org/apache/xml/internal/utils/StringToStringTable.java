package com.sun.org.apache.xml.internal.utils;

public class StringToStringTable
{
    private int m_blocksize;
    private String[] m_map;
    private int m_firstFree;
    private int m_mapSize;
    
    public StringToStringTable() {
        this.m_firstFree = 0;
        this.m_blocksize = 16;
        this.m_mapSize = this.m_blocksize;
        this.m_map = new String[this.m_blocksize];
    }
    
    public StringToStringTable(final int blocksize) {
        this.m_firstFree = 0;
        this.m_blocksize = blocksize;
        this.m_mapSize = blocksize;
        this.m_map = new String[blocksize];
    }
    
    public final int getLength() {
        return this.m_firstFree;
    }
    
    public final void put(final String key, final String value) {
        if (this.m_firstFree + 2 >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize;
            final String[] newMap = new String[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
            this.m_map = newMap;
        }
        this.m_map[this.m_firstFree] = key;
        ++this.m_firstFree;
        this.m_map[this.m_firstFree] = value;
        ++this.m_firstFree;
    }
    
    public final String get(final String key) {
        for (int i = 0; i < this.m_firstFree; i += 2) {
            if (this.m_map[i].equals(key)) {
                return this.m_map[i + 1];
            }
        }
        return null;
    }
    
    public final void remove(final String key) {
        for (int i = 0; i < this.m_firstFree; i += 2) {
            if (this.m_map[i].equals(key)) {
                if (i + 2 < this.m_firstFree) {
                    System.arraycopy(this.m_map, i + 2, this.m_map, i, this.m_firstFree - (i + 2));
                }
                this.m_firstFree -= 2;
                this.m_map[this.m_firstFree] = null;
                this.m_map[this.m_firstFree + 1] = null;
                break;
            }
        }
    }
    
    public final String getIgnoreCase(final String key) {
        if (null == key) {
            return null;
        }
        for (int i = 0; i < this.m_firstFree; i += 2) {
            if (this.m_map[i].equalsIgnoreCase(key)) {
                return this.m_map[i + 1];
            }
        }
        return null;
    }
    
    public final String getByValue(final String val) {
        for (int i = 1; i < this.m_firstFree; i += 2) {
            if (this.m_map[i].equals(val)) {
                return this.m_map[i - 1];
            }
        }
        return null;
    }
    
    public final String elementAt(final int i) {
        return this.m_map[i];
    }
    
    public final boolean contains(final String key) {
        for (int i = 0; i < this.m_firstFree; i += 2) {
            if (this.m_map[i].equals(key)) {
                return true;
            }
        }
        return false;
    }
    
    public final boolean containsValue(final String val) {
        for (int i = 1; i < this.m_firstFree; i += 2) {
            if (this.m_map[i].equals(val)) {
                return true;
            }
        }
        return false;
    }
}
