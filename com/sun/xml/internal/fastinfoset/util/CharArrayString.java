package com.sun.xml.internal.fastinfoset.util;

public class CharArrayString extends CharArray
{
    protected String _s;
    
    public CharArrayString(final String s) {
        this(s, true);
    }
    
    public CharArrayString(final String s, final boolean createArray) {
        this._s = s;
        if (createArray) {
            this.ch = this._s.toCharArray();
            this.start = 0;
            this.length = this.ch.length;
        }
    }
    
    @Override
    public String toString() {
        return this._s;
    }
    
    @Override
    public int hashCode() {
        return this._s.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CharArrayString) {
            final CharArrayString chas = (CharArrayString)obj;
            return this._s.equals(chas._s);
        }
        if (obj instanceof CharArray) {
            final CharArray cha = (CharArray)obj;
            if (this.length == cha.length) {
                int n = this.length;
                int i = this.start;
                int j = cha.start;
                while (n-- != 0) {
                    if (this.ch[i++] != cha.ch[j++]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
