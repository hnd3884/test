package sun.security.pkcs11.wrapper;

public class CK_GCM_PARAMS
{
    private final byte[] iv;
    private final byte[] aad;
    private final long tagBits;
    
    public CK_GCM_PARAMS(final int n, final byte[] iv, final byte[] aad) {
        this.iv = iv;
        this.aad = aad;
        this.tagBits = n;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("  ");
        sb.append("iv: ");
        sb.append(Functions.toHexString(this.iv));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("aad: ");
        sb.append(Functions.toHexString(this.aad));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("tagLen(in bits): ");
        sb.append(this.tagBits);
        return sb.toString();
    }
}
