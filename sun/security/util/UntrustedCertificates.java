package sun.security.util;

import java.security.AccessController;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.security.PrivilegedAction;
import java.security.cert.CertificateException;
import sun.security.x509.X509CertImpl;
import java.security.cert.X509Certificate;
import java.util.Properties;

public final class UntrustedCertificates
{
    private static final Debug debug;
    private static final String ALGORITHM_KEY = "Algorithm";
    private static final Properties props;
    private static final String algorithm;
    
    public static boolean isUntrusted(final X509Certificate x509Certificate) {
        if (UntrustedCertificates.algorithm == null) {
            return false;
        }
        String s;
        if (x509Certificate instanceof X509CertImpl) {
            s = ((X509CertImpl)x509Certificate).getFingerprint(UntrustedCertificates.algorithm);
        }
        else {
            try {
                s = new X509CertImpl(x509Certificate.getEncoded()).getFingerprint(UntrustedCertificates.algorithm);
            }
            catch (final CertificateException ex) {
                return false;
            }
        }
        return UntrustedCertificates.props.containsKey(s);
    }
    
    private UntrustedCertificates() {
    }
    
    static {
        debug = Debug.getInstance("certpath");
        props = new Properties();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                final File file = new File(System.getProperty("java.home"), "lib/security/blacklisted.certs");
                try (final FileInputStream fileInputStream = new FileInputStream(file)) {
                    UntrustedCertificates.props.load(fileInputStream);
                }
                catch (final IOException ex) {
                    if (UntrustedCertificates.debug != null) {
                        UntrustedCertificates.debug.println("Error parsing blacklisted.certs");
                    }
                }
                return null;
            }
        });
        algorithm = UntrustedCertificates.props.getProperty("Algorithm");
    }
}
