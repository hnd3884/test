package sun.security.pkcs11.wrapper;

public class CK_CCM_PARAMS
{
    private final long dataLen;
    private final byte[] nonce;
    private final byte[] aad;
    private final long macLen;
    
    public CK_CCM_PARAMS(final int n, final byte[] nonce, final byte[] aad, final int n2) {
        this.dataLen = n2;
        this.nonce = nonce;
        this.aad = aad;
        this.macLen = n;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("  ");
        sb.append("ulDataLen: ");
        sb.append(this.dataLen);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("iv: ");
        sb.append(Functions.toHexString(this.nonce));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("aad: ");
        sb.append(Functions.toHexString(this.aad));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("tagLen: ");
        sb.append(this.macLen);
        return sb.toString();
    }
}
