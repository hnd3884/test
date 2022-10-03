package sun.security.pkcs11.wrapper;

public class CK_ECDH2_DERIVE_PARAMS
{
    public long kdf;
    public byte[] pSharedData;
    public byte[] pPublicData;
    public long ulPrivateDataLen;
    public long hPrivateData;
    public byte[] pPublicData2;
    
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
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulPrivateDataLen: ");
        sb.append(this.ulPrivateDataLen);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("hPrivateData: ");
        sb.append(this.hPrivateData);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pPublicDataLen2: ");
        sb.append(this.pPublicData2.length);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pPublicData2: ");
        sb.append(Functions.toHexString(this.pPublicData2));
        return sb.toString();
    }
}
