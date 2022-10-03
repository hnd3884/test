package com.sun.org.apache.xml.internal.utils;

public class CharKey
{
    private char m_char;
    
    public CharKey(final char key) {
        this.m_char = key;
    }
    
    public CharKey() {
    }
    
    public final void setChar(final char c) {
        this.m_char = c;
    }
    
    @Override
    public final int hashCode() {
        return this.m_char;
    }
    
    @Override
    public final boolean equals(final Object obj) {
        return ((CharKey)obj).m_char == this.m_char;
    }
}
