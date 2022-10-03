package sun.security.rsa;

import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.Iterator;
import java.security.Provider;

public final class SunRsaSign extends Provider
{
    private static final long serialVersionUID = 866040293550393045L;
    
    public SunRsaSign() {
        super("SunRsaSign", 1.8, "Sun RSA signature provider");
        final Iterator iterator = new SunRsaSignEntries((Provider)this).iterator();
        if (System.getSecurityManager() == null) {
            this.putEntries(iterator);
        }
        else {
            AccessController.doPrivileged((PrivilegedAction<Object>)new SunRsaSign.SunRsaSign$1(this, iterator));
        }
    }
    
    void putEntries(final Iterator<Service> iterator) {
        while (iterator.hasNext()) {
            this.putService(iterator.next());
        }
    }
}
