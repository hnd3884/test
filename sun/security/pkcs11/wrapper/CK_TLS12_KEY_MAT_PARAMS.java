package sun.security.pkcs11.wrapper;

public class CK_TLS12_KEY_MAT_PARAMS
{
    public long ulMacSizeInBits;
    public long ulKeySizeInBits;
    public long ulIVSizeInBits;
    public boolean bIsExport;
    public CK_SSL3_RANDOM_DATA RandomInfo;
    public CK_SSL3_KEY_MAT_OUT pReturnedKeyMaterial;
    public long prfHashMechanism;
    
    public CK_TLS12_KEY_MAT_PARAMS(final int n, final int n2, final int n3, final boolean bIsExport, final CK_SSL3_RANDOM_DATA randomInfo, final long prfHashMechanism) {
        this.ulMacSizeInBits = n;
        this.ulKeySizeInBits = n2;
        this.ulIVSizeInBits = n3;
        this.bIsExport = bIsExport;
        this.RandomInfo = randomInfo;
        this.pReturnedKeyMaterial = new CK_SSL3_KEY_MAT_OUT();
        if (n3 != 0) {
            final int n4 = n3 >> 3;
            this.pReturnedKeyMaterial.pIVClient = new byte[n4];
            this.pReturnedKeyMaterial.pIVServer = new byte[n4];
        }
        this.prfHashMechanism = prfHashMechanism;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("  ");
        sb.append("ulMacSizeInBits: ");
        sb.append(this.ulMacSizeInBits);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulKeySizeInBits: ");
        sb.append(this.ulKeySizeInBits);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulIVSizeInBits: ");
        sb.append(this.ulIVSizeInBits);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("bIsExport: ");
        sb.append(this.bIsExport);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("RandomInfo: ");
        sb.append(this.RandomInfo);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pReturnedKeyMaterial: ");
        sb.append(this.pReturnedKeyMaterial);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("prfHashMechanism: ");
        sb.append(this.prfHashMechanism);
        return sb.toString();
    }
}
