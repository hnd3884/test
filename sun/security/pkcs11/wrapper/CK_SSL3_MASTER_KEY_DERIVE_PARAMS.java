package sun.security.pkcs11.wrapper;

public class CK_SSL3_MASTER_KEY_DERIVE_PARAMS
{
    public CK_SSL3_RANDOM_DATA RandomInfo;
    public CK_VERSION pVersion;
    
    public CK_SSL3_MASTER_KEY_DERIVE_PARAMS(final CK_SSL3_RANDOM_DATA randomInfo, final CK_VERSION pVersion) {
        this.RandomInfo = randomInfo;
        this.pVersion = pVersion;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("  ");
        sb.append("RandomInfo: ");
        sb.append(this.RandomInfo);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pVersion: ");
        sb.append(this.pVersion);
        return sb.toString();
    }
}
