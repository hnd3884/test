package sun.security.pkcs11.wrapper;

public class CK_TLS_MAC_PARAMS
{
    public long prfMechanism;
    public long ulMacLength;
    public long ulServerOrClient;
    
    public CK_TLS_MAC_PARAMS(final long prfMechanism, final long ulMacLength, final long ulServerOrClient) {
        this.prfMechanism = prfMechanism;
        this.ulMacLength = ulMacLength;
        this.ulServerOrClient = ulServerOrClient;
    }
}
