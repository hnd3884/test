package javax.security.auth.message.config;

import javax.security.auth.message.AuthException;
import java.util.Map;
import javax.security.auth.Subject;

public interface ClientAuthConfig extends AuthConfig
{
    ClientAuthContext getAuthContext(final String p0, final Subject p1, final Map p2) throws AuthException;
}
