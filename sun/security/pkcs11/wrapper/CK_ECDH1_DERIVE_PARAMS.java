package sun.security.pkcs11.wrapper;

public class CK_ECDH1_DERIVE_PARAMS
{
    public long kdf;
    public byte[] pSharedData;
    public byte[] pPublicData;
    
    public CK_ECDH1_DERIVE_PARAMS(final long kdf, final byte[] pSharedData, final byte[] pPublicData) {
        this.kdf = kdf;
        this.pSharedData = pSharedData;
        this.pPublicData = pPublicData;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append("kdf: 0x");
        sb.append(Functions.toFullHexString(this.kdf));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pSharedDataLen: ");
        sb.append(this.pSharedData.length);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pSharedData: ");
        sb.append(Functions.toHexString(this.pSharedData));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pPublicDataLen: ");
        sb.append(this.pPublicData.length);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pPublicData: ");
        sb.append(Functions.toHexString(this.pPublicData));
        return sb.toString();
    }
}
