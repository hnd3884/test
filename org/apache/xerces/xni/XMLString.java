package org.apache.xerces.xni;

public class XMLString
{
    public char[] ch;
    public int offset;
    public int length;
    
    public XMLString() {
    }
    
    public XMLString(final char[] array, final int n, final int n2) {
        this.setValues(array, n, n2);
    }
    
    public XMLString(final XMLString values) {
        this.setValues(values);
    }
    
    public void setValues(final char[] ch, final int offset, final int length) {
        this.ch = ch;
        this.offset = offset;
        this.length = length;
    }
    
    public void setValues(final XMLString xmlString) {
        this.setValues(xmlString.ch, xmlString.offset, xmlString.length);
    }
    
    public void clear() {
        this.ch = null;
        this.offset = 0;
        this.length = -1;
    }
    
    public boolean equals(final char[] array, final int n, final int n2) {
        if (array == null) {
            return false;
        }
        if (this.length != n2) {
            return false;
        }
        for (int i = 0; i < n2; ++i) {
            if (this.ch[this.offset + i] != array[n + i]) {
                return false;
            }
        }
        return true;
    }
    
    public boolean equals(final String s) {
        if (s == null) {
            return false;
        }
        if (this.length != s.length()) {
            return false;
        }
        for (int i = 0; i < this.length; ++i) {
            if (this.ch[this.offset + i] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    public String toString() {
        return (this.length > 0) ? new String(this.ch, this.offset, this.length) : "";
    }
}
