package sun.security.pkcs11.wrapper;

public class CK_RSA_PKCS_OAEP_PARAMS
{
    public long hashAlg;
    public long mgf;
    public long source;
    public byte[] pSourceData;
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append("hashAlg: ");
        sb.append(this.hashAlg);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("mgf: ");
        sb.append(this.mgf);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("source: ");
        sb.append(this.source);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pSourceData: ");
        sb.append(this.pSourceData.toString());
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pSourceDataLen: ");
        sb.append(Functions.toHexString(this.pSourceData));
        return sb.toString();
    }
}
