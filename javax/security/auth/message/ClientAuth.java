package javax.security.auth.message;

import javax.security.auth.Subject;

public interface ClientAuth
{
    AuthStatus secureRequest(final MessageInfo p0, final Subject p1) throws AuthException;
    
    AuthStatus validateResponse(final MessageInfo p0, final Subject p1, final Subject p2) throws AuthException;
    
    void cleanSubject(final MessageInfo p0, final Subject p1) throws AuthException;
}
