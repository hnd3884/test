package sun.security.jgss.wrapper;

import java.security.Permission;
import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.GSSException;

class Krb5Util
{
    static String getTGSName(final GSSNameElement gssNameElement) throws GSSException {
        final String krbName = gssNameElement.getKrbName();
        final String substring = krbName.substring(krbName.indexOf("@") + 1);
        final StringBuffer sb = new StringBuffer("krbtgt/");
        sb.append(substring).append('@').append(substring);
        return sb.toString();
    }
    
    static void checkServicePermission(final String s, final String s2) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            SunNativeProvider.debug("Checking ServicePermission(" + s + ", " + s2 + ")");
            securityManager.checkPermission(new ServicePermission(s, s2));
        }
    }
}
