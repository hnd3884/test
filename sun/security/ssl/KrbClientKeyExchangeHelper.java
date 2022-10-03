package sun.security.ssl;

import java.security.Principal;
import java.io.IOException;
import java.security.AccessControlContext;

public interface KrbClientKeyExchangeHelper
{
    void init(final byte[] p0, final String p1, final AccessControlContext p2) throws IOException;
    
    void init(final byte[] p0, final byte[] p1, final Object p2, final AccessControlContext p3) throws IOException;
    
    byte[] getEncodedTicket();
    
    byte[] getEncryptedPreMasterSecret();
    
    byte[] getPlainPreMasterSecret();
    
    Principal getPeerPrincipal();
    
    Principal getLocalPrincipal();
}
