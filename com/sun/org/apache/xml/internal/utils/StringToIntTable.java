package com.sun.org.apache.xml.internal.utils;

public class StringToIntTable
{
    public static final int INVALID_KEY = -10000;
    private int m_blocksize;
    private String[] m_map;
    private int[] m_values;
    private int m_firstFree;
    private int m_mapSize;
    
    public StringToIntTable() {
        this.m_firstFree = 0;
        this.m_blocksize = 8;
        this.m_mapSize = this.m_blocksize;
        this.m_map = new String[this.m_blocksize];
        this.m_values = new int[this.m_blocksize];
    }
    
    public StringToIntTable(final int blocksize) {
        this.m_firstFree = 0;
        this.m_blocksize = blocksize;
        this.m_mapSize = blocksize;
        this.m_map = new String[blocksize];
        this.m_values = new int[this.m_blocksize];
    }
    
    public final int getLength() {
        return this.m_firstFree;
    }
    
    public final void put(final String key, final int value) {
        if (this.m_firstFree + 1 >= this.m_mapSize) {
            this.m_mapSize += this.m_blocksize;
            final String[] newMap = new String[this.m_mapSize];
            System.arraycopy(this.m_map, 0, newMap, 0, this.m_firstFree + 1);
            this.m_map = newMap;
            final int[] newValues = new int[this.m_mapSize];
            System.arraycopy(this.m_values, 0, newValues, 0, this.m_firstFree + 1);
            this.m_values = newValues;
        }
        this.m_map[this.m_firstFree] = key;
        this.m_values[this.m_firstFree] = value;
        ++this.m_firstFree;
    }
    
    public final int get(final String key) {
        for (int i = 0; i < this.m_firstFree; ++i) {
            if (this.m_map[i].equals(key)) {
                return this.m_values[i];
            }
        }
        return -10000;
    }
    
    public final int getIgnoreCase(final String key) {
        if (null == key) {
            return -10000;
        }
        for (int i = 0; i < this.m_firstFree; ++i) {
            if (this.m_map[i].equalsIgnoreCase(key)) {
                return this.m_values[i];
            }
        }
        return -10000;
    }
    
    public final boolean contains(final String key) {
        for (int i = 0; i < this.m_firstFree; ++i) {
            if (this.m_map[i].equals(key)) {
                return true;
            }
        }
        return false;
    }
    
    public final String[] keys() {
        final String[] keysArr = new String[this.m_firstFree];
        for (int i = 0; i < this.m_firstFree; ++i) {
            keysArr[i] = this.m_map[i];
        }
        return keysArr;
    }
}
