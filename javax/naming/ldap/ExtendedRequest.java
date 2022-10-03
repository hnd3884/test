package javax.naming.ldap;

import javax.naming.NamingException;
import java.io.Serializable;

public interface ExtendedRequest extends Serializable
{
    String getID();
    
    byte[] getEncodedValue();
    
    ExtendedResponse createExtendedResponse(final String p0, final byte[] p1, final int p2, final int p3) throws NamingException;
}
