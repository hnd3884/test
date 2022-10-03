package HTTPClient;

final class CIString
{
    private String string;
    private int hash;
    private static final char[] lc;
    
    public CIString(final String string) {
        this.string = string;
        this.hash = calcHashCode(string);
    }
    
    public final String getString() {
        return this.string;
    }
    
    public int hashCode() {
        return this.hash;
    }
    
    private static final int calcHashCode(final String str) {
        int hash = 0;
        final char[] llc = CIString.lc;
        for (int len = str.length(), idx = 0; idx < len; ++idx) {
            hash = 31 * hash + llc[str.charAt(idx)];
        }
        return hash;
    }
    
    public boolean equals(final Object obj) {
        if (obj != null) {
            if (obj instanceof CIString) {
                return this.string.equalsIgnoreCase(((CIString)obj).string);
            }
            if (obj instanceof String) {
                return this.string.equalsIgnoreCase((String)obj);
            }
        }
        return false;
    }
    
    public String toString() {
        return this.string;
    }
    
    static {
        lc = new char[256];
        for (char idx = '\0'; idx < '\u0100'; ++idx) {
            CIString.lc[idx] = Character.toLowerCase(idx);
        }
    }
}
