package org.bouncycastle.asn1;

public class OIDTokenizer
{
    private String oid;
    private int index;
    
    public OIDTokenizer(final String oid) {
        this.oid = oid;
        this.index = 0;
    }
    
    public boolean hasMoreTokens() {
        return this.index != -1;
    }
    
    public String nextToken() {
        if (this.index == -1) {
            return null;
        }
        final int index = this.oid.indexOf(46, this.index);
        if (index == -1) {
            final String substring = this.oid.substring(this.index);
            this.index = -1;
            return substring;
        }
        final String substring2 = this.oid.substring(this.index, index);
        this.index = index + 1;
        return substring2;
    }
}
