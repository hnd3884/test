package sun.security.pkcs11.wrapper;

public class CK_AES_CTR_PARAMS
{
    private final long ulCounterBits;
    private final byte[] cb;
    
    public CK_AES_CTR_PARAMS(final byte[] array) {
        this.ulCounterBits = 128L;
        this.cb = array.clone();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append("ulCounterBits: ");
        sb.append(this.ulCounterBits);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("cb: ");
        sb.append(Functions.toHexString(this.cb));
        return sb.toString();
    }
}
