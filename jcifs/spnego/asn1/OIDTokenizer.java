package jcifs.spnego.asn1;

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
        final int end = this.oid.indexOf(46, this.index);
        if (end == -1) {
            final String token = this.oid.substring(this.index);
            this.index = -1;
            return token;
        }
        final String token = this.oid.substring(this.index, end);
        this.index = end + 1;
        return token;
    }
}
