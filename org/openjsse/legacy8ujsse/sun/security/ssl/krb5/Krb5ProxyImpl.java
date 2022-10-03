package org.openjsse.legacy8ujsse.sun.security.ssl.krb5;

import java.util.Iterator;
import java.util.Set;
import javax.security.auth.kerberos.KeyTab;
import javax.security.auth.kerberos.ServicePermission;
import java.security.Permission;
import sun.security.krb5.PrincipalName;
import java.security.Principal;
import sun.security.jgss.krb5.ServiceCreds;
import javax.security.auth.login.LoginException;
import sun.security.jgss.krb5.Krb5Util;
import sun.security.jgss.GSSCaller;
import javax.security.auth.Subject;
import java.security.AccessControlContext;
import org.openjsse.legacy8ujsse.sun.security.ssl.Krb5Proxy;

public class Krb5ProxyImpl implements Krb5Proxy
{
    @Override
    public Subject getClientSubject(final AccessControlContext acc) throws LoginException {
        return Krb5Util.getSubject(GSSCaller.CALLER_SSL_CLIENT, acc);
    }
    
    @Override
    public Subject getServerSubject(final AccessControlContext acc) throws LoginException {
        return Krb5Util.getSubject(GSSCaller.CALLER_SSL_SERVER, acc);
    }
    
    @Override
    public Object getServiceCreds(final AccessControlContext acc) throws LoginException {
        final ServiceCreds serviceCreds = Krb5Util.getServiceCreds(GSSCaller.CALLER_SSL_SERVER, null, acc);
        return serviceCreds;
    }
    
    @Override
    public String getServerPrincipalName(final Object serviceCreds) {
        return ((ServiceCreds)serviceCreds).getName();
    }
    
    @Override
    public String getPrincipalHostName(final Principal principal) {
        if (principal == null) {
            return null;
        }
        String hostName = null;
        try {
            final PrincipalName princName = new PrincipalName(principal.getName(), 3);
            final String[] nameParts = princName.getNameStrings();
            if (nameParts.length >= 2) {
                hostName = nameParts[1];
            }
        }
        catch (final Exception ex) {}
        return hostName;
    }
    
    @Override
    public Permission getServicePermission(final String principalName, final String action) {
        return new ServicePermission(principalName, action);
    }
    
    @Override
    public boolean isRelated(final Subject subject, final Principal princ) {
        if (princ == null) {
            return false;
        }
        final Set<Principal> principals = subject.getPrincipals(Principal.class);
        if (principals.contains(princ)) {
            return true;
        }
        for (final KeyTab pc : subject.getPrivateCredentials(KeyTab.class)) {
            if (!pc.isBound()) {
                return true;
            }
        }
        return false;
    }
}
