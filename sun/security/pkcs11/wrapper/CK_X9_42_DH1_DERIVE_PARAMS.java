package sun.security.pkcs11.wrapper;

public class CK_X9_42_DH1_DERIVE_PARAMS
{
    public long kdf;
    public byte[] pOtherInfo;
    public byte[] pPublicData;
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append("kdf: 0x");
        sb.append(Functions.toFullHexString(this.kdf));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pOtherInfoLen: ");
        sb.append(this.pOtherInfo.length);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pOtherInfo: ");
        sb.append(Functions.toHexString(this.pOtherInfo));
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
