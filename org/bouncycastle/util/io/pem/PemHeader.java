package org.bouncycastle.util.io.pem;

public class PemHeader
{
    private String name;
    private String value;
    
    public PemHeader(final String name, final String value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public int hashCode() {
        return this.getHashCode(this.name) + 31 * this.getHashCode(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PemHeader)) {
            return false;
        }
        final PemHeader pemHeader = (PemHeader)o;
        return pemHeader == this || (this.isEqual(this.name, pemHeader.name) && this.isEqual(this.value, pemHeader.value));
    }
    
    private int getHashCode(final String s) {
        if (s == null) {
            return 1;
        }
        return s.hashCode();
    }
    
    private boolean isEqual(final String s, final String s2) {
        return s == s2 || (s != null && s2 != null && s.equals(s2));
    }
}
