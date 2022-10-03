package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.security.Permission;
import java.security.Principal;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;
import java.security.AccessControlContext;

public interface Krb5Proxy
{
    Subject getClientSubject(final AccessControlContext p0) throws LoginException;
    
    Subject getServerSubject(final AccessControlContext p0) throws LoginException;
    
    Object getServiceCreds(final AccessControlContext p0) throws LoginException;
    
    String getServerPrincipalName(final Object p0);
    
    String getPrincipalHostName(final Principal p0);
    
    Permission getServicePermission(final String p0, final String p1);
    
    boolean isRelated(final Subject p0, final Principal p1);
}
