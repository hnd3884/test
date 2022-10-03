package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLString;

public class XMLStringBuffer extends XMLString
{
    public static final int DEFAULT_SIZE = 32;
    
    public XMLStringBuffer() {
        this(32);
    }
    
    public XMLStringBuffer(final int size) {
        this.ch = new char[size];
    }
    
    public XMLStringBuffer(final char c) {
        this(1);
        this.append(c);
    }
    
    public XMLStringBuffer(final String s) {
        this(s.length());
        this.append(s);
    }
    
    public XMLStringBuffer(final char[] ch, final int offset, final int length) {
        this(length);
        this.append(ch, offset, length);
    }
    
    public XMLStringBuffer(final XMLString s) {
        this(s.length);
        this.append(s);
    }
    
    @Override
    public void clear() {
        this.offset = 0;
        this.length = 0;
    }
    
    public void append(final char c) {
        if (this.length + 1 > this.ch.length) {
            int newLength = this.ch.length * 2;
            if (newLength < this.ch.length + 32) {
                newLength = this.ch.length + 32;
            }
            final char[] tmp = new char[newLength];
            System.arraycopy(this.ch, 0, tmp, 0, this.length);
            this.ch = tmp;
        }
        this.ch[this.length] = c;
        ++this.length;
    }
    
    public void append(final String s) {
        final int length = s.length();
        if (this.length + length > this.ch.length) {
            int newLength = this.ch.length * 2;
            if (newLength < this.ch.length + length + 32) {
                newLength = this.ch.length + length + 32;
            }
            final char[] newch = new char[newLength];
            System.arraycopy(this.ch, 0, newch, 0, this.length);
            this.ch = newch;
        }
        s.getChars(0, length, this.ch, this.length);
        this.length += length;
    }
    
    public void append(final char[] ch, final int offset, final int length) {
        if (this.length + length > this.ch.length) {
            int newLength = this.ch.length * 2;
            if (newLength < this.ch.length + length + 32) {
                newLength = this.ch.length + length + 32;
            }
            final char[] newch = new char[newLength];
            System.arraycopy(this.ch, 0, newch, 0, this.length);
            this.ch = newch;
        }
        if (ch != null && length > 0) {
            System.arraycopy(ch, offset, this.ch, this.length, length);
            this.length += length;
        }
    }
    
    public void append(final XMLString s) {
        this.append(s.ch, s.offset, s.length);
    }
}
