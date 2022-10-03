package sun.security.ssl.krb5;

import java.util.Iterator;
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
import sun.security.ssl.Krb5Proxy;

public class Krb5ProxyImpl implements Krb5Proxy
{
    @Override
    public Subject getClientSubject(final AccessControlContext accessControlContext) throws LoginException {
        return Krb5Util.getSubject(GSSCaller.CALLER_SSL_CLIENT, accessControlContext);
    }
    
    @Override
    public Subject getServerSubject(final AccessControlContext accessControlContext) throws LoginException {
        return Krb5Util.getSubject(GSSCaller.CALLER_SSL_SERVER, accessControlContext);
    }
    
    @Override
    public Object getServiceCreds(final AccessControlContext accessControlContext) throws LoginException {
        return Krb5Util.getServiceCreds(GSSCaller.CALLER_SSL_SERVER, null, accessControlContext);
    }
    
    @Override
    public String getServerPrincipalName(final Object o) {
        return ((ServiceCreds)o).getName();
    }
    
    @Override
    public String getPrincipalHostName(final Principal principal) {
        if (principal == null) {
            return null;
        }
        String s = null;
        try {
            final String[] nameStrings = new PrincipalName(principal.getName(), 3).getNameStrings();
            if (nameStrings.length >= 2) {
                s = nameStrings[1];
            }
        }
        catch (final Exception ex) {}
        return s;
    }
    
    @Override
    public Permission getServicePermission(final String s, final String s2) {
        return new ServicePermission(s, s2);
    }
    
    @Override
    public boolean isRelated(final Subject subject, final Principal principal) {
        if (principal == null) {
            return false;
        }
        if (subject.getPrincipals(Principal.class).contains(principal)) {
            return true;
        }
        final Iterator<KeyTab> iterator = subject.getPrivateCredentials(KeyTab.class).iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().isBound()) {
                return true;
            }
        }
        return false;
    }
}
