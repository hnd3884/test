package sun.security.krb5;

import javax.security.auth.kerberos.KeyTab;
import sun.misc.Unsafe;

public class KerberosSecrets
{
    private static final Unsafe unsafe;
    private static JavaxSecurityAuthKerberosAccess javaxSecurityAuthKerberosAccess;
    
    public static void setJavaxSecurityAuthKerberosAccess(final JavaxSecurityAuthKerberosAccess javaxSecurityAuthKerberosAccess) {
        KerberosSecrets.javaxSecurityAuthKerberosAccess = javaxSecurityAuthKerberosAccess;
    }
    
    public static JavaxSecurityAuthKerberosAccess getJavaxSecurityAuthKerberosAccess() {
        if (KerberosSecrets.javaxSecurityAuthKerberosAccess == null) {
            KerberosSecrets.unsafe.ensureClassInitialized(KeyTab.class);
        }
        return KerberosSecrets.javaxSecurityAuthKerberosAccess;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
    }
}
