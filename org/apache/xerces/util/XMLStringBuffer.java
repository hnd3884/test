package org.apache.xerces.util;

import org.apache.xerces.xni.XMLString;

public class XMLStringBuffer extends XMLString
{
    public static final int DEFAULT_SIZE = 32;
    
    public XMLStringBuffer() {
        this(32);
    }
    
    public XMLStringBuffer(final int n) {
        this.ch = new char[n];
    }
    
    public XMLStringBuffer(final char c) {
        this(1);
        this.append(c);
    }
    
    public XMLStringBuffer(final String s) {
        this(s.length());
        this.append(s);
    }
    
    public XMLStringBuffer(final char[] array, final int n, final int n2) {
        this(n2);
        this.append(array, n, n2);
    }
    
    public XMLStringBuffer(final XMLString xmlString) {
        this(xmlString.length);
        this.append(xmlString);
    }
    
    public void clear() {
        this.offset = 0;
        this.length = 0;
    }
    
    public void append(final char c) {
        if (this.length + 1 > this.ch.length) {
            int n = this.ch.length * 2;
            if (n < this.ch.length + 32) {
                n = this.ch.length + 32;
            }
            final char[] ch = new char[n];
            System.arraycopy(this.ch, 0, ch, 0, this.length);
            this.ch = ch;
        }
        this.ch[this.length] = c;
        ++this.length;
    }
    
    public void append(final String s) {
        final int length = s.length();
        if (this.length + length > this.ch.length) {
            int n = this.ch.length * 2;
            if (n < this.length + length + 32) {
                n = this.ch.length + length + 32;
            }
            final char[] ch = new char[n];
            System.arraycopy(this.ch, 0, ch, 0, this.length);
            this.ch = ch;
        }
        s.getChars(0, length, this.ch, this.length);
        this.length += length;
    }
    
    public void append(final char[] array, final int n, final int n2) {
        if (this.length + n2 > this.ch.length) {
            int n3 = this.ch.length * 2;
            if (n3 < this.length + n2 + 32) {
                n3 = this.ch.length + n2 + 32;
            }
            final char[] ch = new char[n3];
            System.arraycopy(this.ch, 0, ch, 0, this.length);
            this.ch = ch;
        }
        System.arraycopy(array, n, this.ch, this.length, n2);
        this.length += n2;
    }
    
    public void append(final XMLString xmlString) {
        this.append(xmlString.ch, xmlString.offset, xmlString.length);
    }
}
