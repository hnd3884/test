package sun.security.provider;

import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.Iterator;
import java.security.Provider;

public final class Sun extends Provider
{
    private static final long serialVersionUID = 6440182097568097204L;
    private static final String INFO = "SUN (DSA key/parameter generation; DSA signing; SHA-1, MD5 digests; SecureRandom; X.509 certificates; JKS & DKS keystores; PKIX CertPathValidator; PKIX CertPathBuilder; LDAP, Collection CertStores, JavaPolicy Policy; JavaLoginConfig Configuration)";
    
    public Sun() {
        super("SUN", 1.8, "SUN (DSA key/parameter generation; DSA signing; SHA-1, MD5 digests; SecureRandom; X.509 certificates; JKS & DKS keystores; PKIX CertPathValidator; PKIX CertPathBuilder; LDAP, Collection CertStores, JavaPolicy Policy; JavaLoginConfig Configuration)");
        final Iterator iterator = new SunEntries((Provider)this).iterator();
        if (System.getSecurityManager() == null) {
            this.putEntries(iterator);
        }
        else {
            AccessController.doPrivileged((PrivilegedAction<Object>)new Sun.Sun$1(this, iterator));
        }
    }
    
    void putEntries(final Iterator<Service> iterator) {
        while (iterator.hasNext()) {
            this.putService(iterator.next());
        }
    }
}
