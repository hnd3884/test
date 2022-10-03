package sun.security.provider;

import java.security.AccessController;
import java.util.Iterator;
import java.security.PrivilegedAction;
import sun.security.rsa.SunRsaSignEntries;
import java.security.Provider;

public final class VerificationProvider extends Provider
{
    private static final long serialVersionUID = 7482667077568930381L;
    private static final boolean ACTIVE;
    
    public VerificationProvider() {
        super("SunJarVerification", 1.8, "Jar Verification Provider");
        if (!VerificationProvider.ACTIVE) {
            return;
        }
        final Iterator<Service> iterator = new SunEntries(this).iterator();
        final Iterator<Service> iterator2 = new SunRsaSignEntries(this).iterator();
        if (System.getSecurityManager() == null) {
            this.putEntries(iterator);
            this.putEntries(iterator2);
        }
        else {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Void run() {
                    VerificationProvider.this.putEntries(iterator);
                    VerificationProvider.this.putEntries(iterator2);
                    return null;
                }
            });
        }
    }
    
    void putEntries(final Iterator<Service> iterator) {
        while (iterator.hasNext()) {
            this.putService(iterator.next());
        }
    }
    
    static {
        boolean active;
        try {
            Class.forName("sun.security.provider.Sun");
            Class.forName("sun.security.rsa.SunRsaSign");
            active = false;
        }
        catch (final ClassNotFoundException ex) {
            active = true;
        }
        ACTIVE = active;
    }
}
