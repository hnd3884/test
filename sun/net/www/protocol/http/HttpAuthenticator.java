package sun.net.www.protocol.http;

import java.net.URL;

@Deprecated
public interface HttpAuthenticator
{
    boolean schemeSupported(final String p0);
    
    String authString(final URL p0, final String p1, final String p2);
}
