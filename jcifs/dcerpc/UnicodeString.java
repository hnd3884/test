package jcifs.dcerpc;

public class UnicodeString extends rpc.unicode_string
{
    boolean zterm;
    
    public UnicodeString(final boolean zterm) {
        this.zterm = zterm;
    }
    
    public UnicodeString(final rpc.unicode_string rus, final boolean zterm) {
        this.length = rus.length;
        this.maximum_length = rus.maximum_length;
        this.buffer = rus.buffer;
        this.zterm = zterm;
    }
    
    public UnicodeString(final String str, final boolean zterm) {
        this.zterm = zterm;
        final int len = str.length();
        final int zt = zterm ? 1 : 0;
        final short n = (short)((len + zt) * 2);
        this.maximum_length = n;
        this.length = n;
        this.buffer = new short[len + zt];
        int i;
        for (i = 0; i < len; ++i) {
            this.buffer[i] = (short)str.charAt(i);
        }
        if (zterm) {
            this.buffer[i] = 0;
        }
    }
    
    public String toString() {
        final int len = this.length / 2 - (this.zterm ? 1 : 0);
        final char[] ca = new char[len];
        for (int i = 0; i < len; ++i) {
            ca[i] = (char)this.buffer[i];
        }
        return new String(ca, 0, len);
    }
}
