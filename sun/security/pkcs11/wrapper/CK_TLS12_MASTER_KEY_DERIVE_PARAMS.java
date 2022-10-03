package sun.security.pkcs11.wrapper;

public class CK_TLS12_MASTER_KEY_DERIVE_PARAMS
{
    public CK_SSL3_RANDOM_DATA RandomInfo;
    public CK_VERSION pVersion;
    public long prfHashMechanism;
    
    public CK_TLS12_MASTER_KEY_DERIVE_PARAMS(final CK_SSL3_RANDOM_DATA randomInfo, final CK_VERSION pVersion, final long prfHashMechanism) {
        this.RandomInfo = randomInfo;
        this.pVersion = pVersion;
        this.prfHashMechanism = prfHashMechanism;
    }
}
