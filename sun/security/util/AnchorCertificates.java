package sun.security.util;

import java.security.AccessController;
import java.util.Enumeration;
import java.util.HashSet;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.io.File;
import java.security.PrivilegedAction;
import java.util.Collections;
import sun.security.x509.X509CertImpl;
import java.security.cert.X509Certificate;
import java.util.Set;

public class AnchorCertificates
{
    private static final Debug debug;
    private static final String HASH = "SHA-256";
    private static Set<String> certs;
    
    public static boolean contains(final X509Certificate x509Certificate) {
        final boolean contains = AnchorCertificates.certs.contains(X509CertImpl.getFingerprint("SHA-256", x509Certificate));
        if (contains && AnchorCertificates.debug != null) {
            AnchorCertificates.debug.println("AnchorCertificate.contains: matched " + x509Certificate.getSubjectDN());
        }
        return contains;
    }
    
    private AnchorCertificates() {
    }
    
    static {
        debug = Debug.getInstance("certpath");
        AnchorCertificates.certs = Collections.emptySet();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                final File file = new File(System.getProperty("java.home"), "lib/security/cacerts");
                try {
                    final KeyStore instance = KeyStore.getInstance("JKS");
                    try (final FileInputStream fileInputStream = new FileInputStream(file)) {
                        instance.load(fileInputStream, null);
                        AnchorCertificates.certs = (Set<String>)new HashSet();
                        final Enumeration<String> aliases = instance.aliases();
                        while (aliases.hasMoreElements()) {
                            final String s = aliases.nextElement();
                            if (s.contains(" [jdk")) {
                                AnchorCertificates.certs.add(X509CertImpl.getFingerprint("SHA-256", (X509Certificate)instance.getCertificate(s)));
                            }
                        }
                    }
                }
                catch (final Exception ex) {
                    if (AnchorCertificates.debug != null) {
                        AnchorCertificates.debug.println("Error parsing cacerts");
                        ex.printStackTrace();
                    }
                }
                return null;
            }
        });
    }
}
