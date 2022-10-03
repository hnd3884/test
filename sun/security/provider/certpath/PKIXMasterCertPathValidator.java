package sun.security.provider.certpath;

import java.util.Set;
import java.util.Iterator;
import java.security.cert.PKIXReason;
import java.security.cert.CertPathValidatorException;
import java.util.Collection;
import java.security.cert.Certificate;
import java.util.StringJoiner;
import java.util.Collections;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.X509Certificate;
import java.util.List;
import java.security.cert.CertPath;
import sun.security.util.Debug;

class PKIXMasterCertPathValidator
{
    private static final Debug debug;
    
    static void validate(final CertPath certPath, final List<X509Certificate> list, final List<PKIXCertPathChecker> list2) throws CertPathValidatorException {
        final int size = list.size();
        if (PKIXMasterCertPathValidator.debug != null) {
            PKIXMasterCertPathValidator.debug.println("--------------------------------------------------------------");
            PKIXMasterCertPathValidator.debug.println("Executing PKIX certification path validation algorithm.");
        }
        for (int i = 0; i < size; ++i) {
            final X509Certificate x509Certificate = list.get(i);
            if (PKIXMasterCertPathValidator.debug != null) {
                PKIXMasterCertPathValidator.debug.println("Checking cert" + (i + 1) + " - Subject: " + x509Certificate.getSubjectX500Principal());
            }
            Object o = x509Certificate.getCriticalExtensionOIDs();
            if (o == null) {
                o = Collections.emptySet();
            }
            if (PKIXMasterCertPathValidator.debug != null && !((Set)o).isEmpty()) {
                final StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                final Iterator iterator = ((Set)o).iterator();
                while (iterator.hasNext()) {
                    stringJoiner.add((CharSequence)iterator.next());
                }
                PKIXMasterCertPathValidator.debug.println("Set of critical extensions: " + stringJoiner.toString());
            }
            for (int j = 0; j < list2.size(); ++j) {
                final PKIXCertPathChecker pkixCertPathChecker = list2.get(j);
                if (PKIXMasterCertPathValidator.debug != null) {
                    PKIXMasterCertPathValidator.debug.println("-Using checker" + (j + 1) + " ... [" + pkixCertPathChecker.getClass().getName() + "]");
                }
                if (i == 0) {
                    pkixCertPathChecker.init(false);
                }
                try {
                    pkixCertPathChecker.check(x509Certificate, (Collection<String>)o);
                    if (PKIXMasterCertPathValidator.debug != null) {
                        PKIXMasterCertPathValidator.debug.println("-checker" + (j + 1) + " validation succeeded");
                    }
                }
                catch (final CertPathValidatorException ex) {
                    throw new CertPathValidatorException(ex.getMessage(), (ex.getCause() != null) ? ex.getCause() : ex, certPath, size - (i + 1), ex.getReason());
                }
            }
            if (!((Set)o).isEmpty()) {
                throw new CertPathValidatorException("unrecognized critical extension(s)", null, certPath, size - (i + 1), PKIXReason.UNRECOGNIZED_CRIT_EXT);
            }
            if (PKIXMasterCertPathValidator.debug != null) {
                PKIXMasterCertPathValidator.debug.println("\ncert" + (i + 1) + " validation succeeded.\n");
            }
        }
        if (PKIXMasterCertPathValidator.debug != null) {
            PKIXMasterCertPathValidator.debug.println("Cert path validation succeeded. (PKIX validation algorithm)");
            PKIXMasterCertPathValidator.debug.println("--------------------------------------------------------------");
        }
    }
    
    static {
        debug = Debug.getInstance("certpath");
    }
}
