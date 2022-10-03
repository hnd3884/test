package com.sun.xml.internal.fastinfoset.util;

public class CharArray implements CharSequence
{
    public char[] ch;
    public int start;
    public int length;
    protected int _hash;
    
    protected CharArray() {
    }
    
    public CharArray(final char[] _ch, final int _start, final int _length, final boolean copy) {
        this.set(_ch, _start, _length, copy);
    }
    
    public final void set(final char[] _ch, final int _start, final int _length, final boolean copy) {
        if (copy) {
            this.ch = new char[_length];
            this.start = 0;
            this.length = _length;
            System.arraycopy(_ch, _start, this.ch, 0, _length);
        }
        else {
            this.ch = _ch;
            this.start = _start;
            this.length = _length;
        }
        this._hash = 0;
    }
    
    public final void cloneArray() {
        final char[] _ch = new char[this.length];
        System.arraycopy(this.ch, this.start, _ch, 0, this.length);
        this.ch = _ch;
        this.start = 0;
    }
    
    @Override
    public String toString() {
        return new String(this.ch, this.start, this.length);
    }
    
    @Override
    public int hashCode() {
        if (this._hash == 0) {
            for (int i = this.start; i < this.start + this.length; ++i) {
                this._hash = 31 * this._hash + this.ch[i];
            }
        }
        return this._hash;
    }
    
    public static final int hashCode(final char[] ch, final int start, final int length) {
        int hash = 0;
        for (int i = start; i < start + length; ++i) {
            hash = 31 * hash + ch[i];
        }
        return hash;
    }
    
    public final boolean equalsCharArray(final CharArray cha) {
        if (this == cha) {
            return true;
        }
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
        return false;
    }
    
    public final boolean equalsCharArray(final char[] ch, final int start, final int length) {
        if (this.length == length) {
            int n = this.length;
            int i = this.start;
            int j = start;
            while (n-- != 0) {
                if (this.ch[i++] != ch[j++]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
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
    
    @Override
    public final int length() {
        return this.length;
    }
    
    @Override
    public final char charAt(final int index) {
        return this.ch[this.start + index];
    }
    
    @Override
    public final CharSequence subSequence(final int start, final int end) {
        return new CharArray(this.ch, this.start + start, end - start, false);
    }
}
