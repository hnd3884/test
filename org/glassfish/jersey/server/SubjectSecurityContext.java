package org.glassfish.jersey.server;

import java.security.PrivilegedAction;
import javax.ws.rs.core.SecurityContext;

public interface SubjectSecurityContext extends SecurityContext
{
    Object doAsSubject(final PrivilegedAction p0);
}
