package sun.security.pkcs11.wrapper;

public class CK_PKCS5_PBKD2_PARAMS
{
    public long saltSource;
    public byte[] pSaltSourceData;
    public long iterations;
    public long prf;
    public byte[] pPrfData;
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append("saltSource: ");
        sb.append(this.saltSource);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pSaltSourceData: ");
        sb.append(Functions.toHexString(this.pSaltSourceData));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulSaltSourceDataLen: ");
        sb.append(this.pSaltSourceData.length);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("iterations: ");
        sb.append(this.iterations);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("prf: ");
        sb.append(this.prf);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pPrfData: ");
        sb.append(Functions.toHexString(this.pPrfData));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulPrfDataLen: ");
        sb.append(this.pPrfData.length);
        return sb.toString();
    }
}
