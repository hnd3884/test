package com.maverick.crypto.asn1;

public class OIDTokenizer
{
    private String c;
    private int b;
    
    public OIDTokenizer(final String c) {
        this.c = c;
        this.b = 0;
    }
    
    public boolean hasMoreTokens() {
        return this.b != -1;
    }
    
    public String nextToken() {
        if (this.b == -1) {
            return null;
        }
        final int index = this.c.indexOf(46, this.b);
        if (index == -1) {
            final String substring = this.c.substring(this.b);
            this.b = -1;
            return substring;
        }
        final String substring2 = this.c.substring(this.b, index);
        this.b = index + 1;
        return substring2;
    }
}
