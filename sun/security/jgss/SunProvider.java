package sun.security.jgss;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;

public final class SunProvider extends Provider
{
    private static final long serialVersionUID = -238911724858694198L;
    private static final String INFO = "Sun (Kerberos v5, SPNEGO)";
    public static final SunProvider INSTANCE;
    
    public SunProvider() {
        super("SunJGSS", 1.8, "Sun (Kerberos v5, SPNEGO)");
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                SunProvider.this.put("GssApiMechanism.1.2.840.113554.1.2.2", "sun.security.jgss.krb5.Krb5MechFactory");
                SunProvider.this.put("GssApiMechanism.1.3.6.1.5.5.2", "sun.security.jgss.spnego.SpNegoMechFactory");
                return null;
            }
        });
    }
    
    static {
        INSTANCE = new SunProvider();
    }
}
