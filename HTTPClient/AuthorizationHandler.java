package HTTPClient;

import java.io.IOException;

public interface AuthorizationHandler
{
    AuthorizationInfo getAuthorization(final AuthorizationInfo p0, final RoRequest p1, final RoResponse p2) throws AuthSchemeNotImplException, IOException;
    
    AuthorizationInfo fixupAuthInfo(final AuthorizationInfo p0, final RoRequest p1, final AuthorizationInfo p2, final RoResponse p3) throws AuthSchemeNotImplException, IOException;
    
    void handleAuthHeaders(final Response p0, final RoRequest p1, final AuthorizationInfo p2, final AuthorizationInfo p3) throws IOException;
    
    void handleAuthTrailers(final Response p0, final RoRequest p1, final AuthorizationInfo p2, final AuthorizationInfo p3) throws IOException;
}
