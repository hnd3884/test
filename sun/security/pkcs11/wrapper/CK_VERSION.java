package sun.security.pkcs11.wrapper;

public class CK_VERSION
{
    public byte major;
    public byte minor;
    
    public CK_VERSION(final int n, final int n2) {
        this.major = (byte)n;
        this.minor = (byte)n2;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.major & 0xFF);
        sb.append('.');
        final int n = this.minor & 0xFF;
        if (n < 10) {
            sb.append('0');
        }
        sb.append(n);
        return sb.toString();
    }
}
