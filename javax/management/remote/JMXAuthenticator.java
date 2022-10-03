package javax.management.remote;

import javax.security.auth.Subject;

public interface JMXAuthenticator
{
    Subject authenticate(final Object p0);
}
