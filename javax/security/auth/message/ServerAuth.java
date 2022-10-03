package javax.security.auth.message;

import javax.security.auth.Subject;

public interface ServerAuth
{
    AuthStatus validateRequest(final MessageInfo p0, final Subject p1, final Subject p2) throws AuthException;
    
    AuthStatus secureResponse(final MessageInfo p0, final Subject p1) throws AuthException;
    
    void cleanSubject(final MessageInfo p0, final Subject p1) throws AuthException;
}
