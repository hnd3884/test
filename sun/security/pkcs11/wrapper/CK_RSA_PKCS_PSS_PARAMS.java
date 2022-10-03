package sun.security.pkcs11.wrapper;

import java.security.ProviderException;

public class CK_RSA_PKCS_PSS_PARAMS
{
    private final long hashAlg;
    private final long mgf;
    private final long sLen;
    
    public CK_RSA_PKCS_PSS_PARAMS(final String s, final String s2, final String s3, final int n) {
        this.hashAlg = Functions.getHashMechId(s);
        if (!s2.equals("MGF1")) {
            throw new ProviderException("Only MGF1 is supported");
        }
        this.mgf = Functions.getMGFId("CKG_MGF1_" + s3.replaceFirst("-", ""));
        this.sLen = n;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CK_RSA_PKCS_PSS_PARAMS)) {
            return false;
        }
        final CK_RSA_PKCS_PSS_PARAMS ck_RSA_PKCS_PSS_PARAMS = (CK_RSA_PKCS_PSS_PARAMS)o;
        return ck_RSA_PKCS_PSS_PARAMS.hashAlg == this.hashAlg && ck_RSA_PKCS_PSS_PARAMS.mgf == this.mgf && ck_RSA_PKCS_PSS_PARAMS.sLen == this.sLen;
    }
    
    @Override
    public int hashCode() {
        return (int)(this.hashAlg << (int)(2L + this.mgf) << (int)(1L + this.sLen));
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append("hashAlg: ");
        sb.append(Functions.toFullHexString(this.hashAlg));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("mgf: ");
        sb.append(Functions.toFullHexString(this.mgf));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("sLen(in bytes): ");
        sb.append(this.sLen);
        return sb.toString();
    }
}
