package jdk.internal.util.xml.impl;

import jdk.internal.org.xml.sax.Attributes;

public class Attrs implements Attributes
{
    String[] mItems;
    private char mLength;
    private char mAttrIdx;
    
    public Attrs() {
        this.mAttrIdx = '\0';
        this.mItems = new String[64];
    }
    
    public void setLength(final char mLength) {
        if (mLength > (char)(this.mItems.length >> 3)) {
            this.mItems = new String[mLength << 3];
        }
        this.mLength = mLength;
    }
    
    @Override
    public int getLength() {
        return this.mLength;
    }
    
    @Override
    public String getURI(final int n) {
        return (n >= 0 && n < this.mLength) ? this.mItems[n << 3] : null;
    }
    
    @Override
    public String getLocalName(final int n) {
        return (n >= 0 && n < this.mLength) ? this.mItems[(n << 3) + 2] : null;
    }
    
    @Override
    public String getQName(final int n) {
        if (n < 0 || n >= this.mLength) {
            return null;
        }
        return this.mItems[(n << 3) + 1];
    }
    
    @Override
    public String getType(final int n) {
        return (n >= 0 && n < this.mItems.length >> 3) ? this.mItems[(n << 3) + 4] : null;
    }
    
    @Override
    public String getValue(final int n) {
        return (n >= 0 && n < this.mLength) ? this.mItems[(n << 3) + 3] : null;
    }
    
    @Override
    public int getIndex(final String s, final String s2) {
        for (char mLength = this.mLength, c = '\0'; c < mLength; ++c) {
            if (this.mItems[c << 3].equals(s) && this.mItems[(c << 3) + 2].equals(s2)) {
                return c;
            }
        }
        return -1;
    }
    
    int getIndexNullNS(final String s, final String s2) {
        final char mLength = this.mLength;
        if (s != null) {
            for (char c = '\0'; c < mLength; ++c) {
                if (this.mItems[c << 3].equals(s) && this.mItems[(c << 3) + 2].equals(s2)) {
                    return c;
                }
            }
        }
        else {
            for (char c2 = '\0'; c2 < mLength; ++c2) {
                if (this.mItems[(c2 << 3) + 2].equals(s2)) {
                    return c2;
                }
            }
        }
        return -1;
    }
    
    @Override
    public int getIndex(final String s) {
        for (char mLength = this.mLength, c = '\0'; c < mLength; ++c) {
            if (this.mItems[(c << 3) + 1].equals(s)) {
                return c;
            }
        }
        return -1;
    }
    
    @Override
    public String getType(final String s, final String s2) {
        final int index = this.getIndex(s, s2);
        return (index >= 0) ? this.mItems[(index << 3) + 4] : null;
    }
    
    @Override
    public String getType(final String s) {
        final int index = this.getIndex(s);
        return (index >= 0) ? this.mItems[(index << 3) + 4] : null;
    }
    
    @Override
    public String getValue(final String s, final String s2) {
        final int index = this.getIndex(s, s2);
        return (index >= 0) ? this.mItems[(index << 3) + 3] : null;
    }
    
    @Override
    public String getValue(final String s) {
        final int index = this.getIndex(s);
        return (index >= 0) ? this.mItems[(index << 3) + 3] : null;
    }
    
    public boolean isDeclared(final int n) {
        if (n < 0 || n >= this.mLength) {
            throw new ArrayIndexOutOfBoundsException("");
        }
        return this.mItems[(n << 3) + 5] != null;
    }
    
    public boolean isDeclared(final String s) {
        final int index = this.getIndex(s);
        if (index < 0) {
            throw new IllegalArgumentException("");
        }
        return this.mItems[(index << 3) + 5] != null;
    }
    
    public boolean isDeclared(final String s, final String s2) {
        final int index = this.getIndex(s, s2);
        if (index < 0) {
            throw new IllegalArgumentException("");
        }
        return this.mItems[(index << 3) + 5] != null;
    }
    
    public boolean isSpecified(final int n) {
        if (n < 0 || n >= this.mLength) {
            throw new ArrayIndexOutOfBoundsException("");
        }
        final String s = this.mItems[(n << 3) + 5];
        return s == null || s.charAt(0) == 'd';
    }
    
    public boolean isSpecified(final String s, final String s2) {
        final int index = this.getIndex(s, s2);
        if (index < 0) {
            throw new IllegalArgumentException("");
        }
        final String s3 = this.mItems[(index << 3) + 5];
        return s3 == null || s3.charAt(0) == 'd';
    }
    
    public boolean isSpecified(final String s) {
        final int index = this.getIndex(s);
        if (index < 0) {
            throw new IllegalArgumentException("");
        }
        final String s2 = this.mItems[(index << 3) + 5];
        return s2 == null || s2.charAt(0) == 'd';
    }
}
