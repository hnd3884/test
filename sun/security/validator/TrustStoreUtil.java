package sun.security.validator;

import java.util.Enumeration;
import java.util.Collections;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.security.cert.X509Certificate;
import java.util.Set;
import java.security.KeyStore;

public final class TrustStoreUtil
{
    private TrustStoreUtil() {
    }
    
    public static Set<X509Certificate> getTrustedCerts(final KeyStore keyStore) {
        final HashSet set = new HashSet();
        try {
            final Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                final String s = aliases.nextElement();
                if (keyStore.isCertificateEntry(s)) {
                    final Certificate certificate = keyStore.getCertificate(s);
                    if (!(certificate instanceof X509Certificate)) {
                        continue;
                    }
                    set.add(certificate);
                }
                else {
                    if (!keyStore.isKeyEntry(s)) {
                        continue;
                    }
                    final Certificate[] certificateChain = keyStore.getCertificateChain(s);
                    if (certificateChain == null || certificateChain.length <= 0 || !(certificateChain[0] instanceof X509Certificate)) {
                        continue;
                    }
                    set.add(certificateChain[0]);
                }
            }
        }
        catch (final KeyStoreException ex) {}
        return (Set<X509Certificate>)Collections.unmodifiableSet((Set<?>)set);
    }
}
