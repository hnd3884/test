package jcifs.smb;

class SecurityBlob
{
    private byte[] b;
    
    SecurityBlob() {
        this.b = new byte[0];
    }
    
    SecurityBlob(final byte[] b) {
        this.b = new byte[0];
        this.set(b);
    }
    
    void set(final byte[] b) {
        this.b = ((b == null) ? new byte[0] : b);
    }
    
    byte[] get() {
        return this.b;
    }
    
    int length() {
        if (this.b == null) {
            return 0;
        }
        return this.b.length;
    }
    
    protected Object clone() throws CloneNotSupportedException {
        return new SecurityBlob(this.b.clone());
    }
    
    public boolean equals(final Object arg0) {
        try {
            final SecurityBlob t = (SecurityBlob)arg0;
            for (int i = 0; i < this.b.length; ++i) {
                if (this.b[i] != t.b[i]) {
                    return false;
                }
            }
            return true;
        }
        catch (final Throwable e) {
            return false;
        }
    }
    
    public int hashCode() {
        return this.b.hashCode();
    }
    
    public String toString() {
        String ret = "";
        for (int i = 0; i < this.b.length; ++i) {
            final int n = this.b[i] & 0xFF;
            if (n <= 15) {
                ret += "0";
            }
            ret += Integer.toHexString(n);
        }
        return ret;
    }
}
