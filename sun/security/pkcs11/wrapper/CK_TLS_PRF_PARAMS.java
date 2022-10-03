package sun.security.pkcs11.wrapper;

public class CK_TLS_PRF_PARAMS
{
    public byte[] pSeed;
    public byte[] pLabel;
    public byte[] pOutput;
    
    public CK_TLS_PRF_PARAMS(final byte[] pSeed, final byte[] pLabel, final byte[] pOutput) {
        this.pSeed = pSeed;
        this.pLabel = pLabel;
        this.pOutput = pOutput;
    }
}
