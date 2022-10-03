package org.openjsse.sun.security.validator;

import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Collections;
import java.security.KeyStoreException;
import java.util.HashSet;
import java.security.cert.X509Certificate;
import java.util.Set;
import java.security.KeyStore;

public final class TrustStoreUtil
{
    private TrustStoreUtil() {
    }
    
    public static Set<X509Certificate> getTrustedCerts(final KeyStore ks) {
        final Set<X509Certificate> set = new HashSet<X509Certificate>();
        try {
            final Enumeration<String> e = ks.aliases();
            while (e.hasMoreElements()) {
                final String alias = e.nextElement();
                if (ks.isCertificateEntry(alias)) {
                    final Certificate cert = ks.getCertificate(alias);
                    if (!(cert instanceof X509Certificate)) {
                        continue;
                    }
                    set.add((X509Certificate)cert);
                }
                else {
                    if (!ks.isKeyEntry(alias)) {
                        continue;
                    }
                    final Certificate[] certs = ks.getCertificateChain(alias);
                    if (certs == null || certs.length <= 0 || !(certs[0] instanceof X509Certificate)) {
                        continue;
                    }
                    set.add((X509Certificate)certs[0]);
                }
            }
        }
        catch (final KeyStoreException ex) {}
        return Collections.unmodifiableSet((Set<? extends X509Certificate>)set);
    }
}
