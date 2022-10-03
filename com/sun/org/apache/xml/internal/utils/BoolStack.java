package com.sun.org.apache.xml.internal.utils;

public final class BoolStack implements Cloneable
{
    private boolean[] m_values;
    private int m_allocatedSize;
    private int m_index;
    
    public BoolStack() {
        this(32);
    }
    
    public BoolStack(final int size) {
        this.m_allocatedSize = size;
        this.m_values = new boolean[size];
        this.m_index = -1;
    }
    
    public final int size() {
        return this.m_index + 1;
    }
    
    public final void clear() {
        this.m_index = -1;
    }
    
    public final boolean push(final boolean val) {
        if (this.m_index == this.m_allocatedSize - 1) {
            this.grow();
        }
        return this.m_values[++this.m_index] = val;
    }
    
    public final boolean pop() {
        return this.m_values[this.m_index--];
    }
    
    public final boolean popAndTop() {
        --this.m_index;
        return this.m_index >= 0 && this.m_values[this.m_index];
    }
    
    public final void setTop(final boolean b) {
        this.m_values[this.m_index] = b;
    }
    
    public final boolean peek() {
        return this.m_values[this.m_index];
    }
    
    public final boolean peekOrFalse() {
        return this.m_index > -1 && this.m_values[this.m_index];
    }
    
    public final boolean peekOrTrue() {
        return this.m_index <= -1 || this.m_values[this.m_index];
    }
    
    public boolean isEmpty() {
        return this.m_index == -1;
    }
    
    private void grow() {
        this.m_allocatedSize *= 2;
        final boolean[] newVector = new boolean[this.m_allocatedSize];
        System.arraycopy(this.m_values, 0, newVector, 0, this.m_index + 1);
        this.m_values = newVector;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
