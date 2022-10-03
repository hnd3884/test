package sun.security.provider.certpath;

import java.security.cert.CertificateException;
import java.io.IOException;
import java.security.cert.CertPath;
import java.security.cert.PKIXReason;
import sun.security.x509.X509CertImpl;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.security.cert.Certificate;
import java.util.Collections;
import sun.security.x509.PKIXExtensions;
import java.util.HashSet;
import java.security.cert.CertPathValidatorException;
import java.util.Set;
import sun.security.x509.NameConstraintsExtension;
import sun.security.util.Debug;
import java.security.cert.PKIXCertPathChecker;

class ConstraintsChecker extends PKIXCertPathChecker
{
    private static final Debug debug;
    private final int certPathLength;
    private int maxPathLength;
    private int i;
    private NameConstraintsExtension prevNC;
    private Set<String> supportedExts;
    
    ConstraintsChecker(final int certPathLength) {
        this.certPathLength = certPathLength;
    }
    
    @Override
    public void init(final boolean b) throws CertPathValidatorException {
        if (!b) {
            this.i = 0;
            this.maxPathLength = this.certPathLength;
            this.prevNC = null;
            return;
        }
        throw new CertPathValidatorException("forward checking not supported");
    }
    
    @Override
    public boolean isForwardCheckingSupported() {
        return false;
    }
    
    @Override
    public Set<String> getSupportedExtensions() {
        if (this.supportedExts == null) {
            (this.supportedExts = new HashSet<String>(2)).add(PKIXExtensions.BasicConstraints_Id.toString());
            this.supportedExts.add(PKIXExtensions.NameConstraints_Id.toString());
            this.supportedExts = Collections.unmodifiableSet((Set<? extends String>)this.supportedExts);
        }
        return this.supportedExts;
    }
    
    @Override
    public void check(final Certificate certificate, final Collection<String> collection) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certificate;
        ++this.i;
        this.checkBasicConstraints(x509Certificate);
        this.verifyNameConstraints(x509Certificate);
        if (collection != null && !collection.isEmpty()) {
            collection.remove(PKIXExtensions.BasicConstraints_Id.toString());
            collection.remove(PKIXExtensions.NameConstraints_Id.toString());
        }
    }
    
    private void verifyNameConstraints(final X509Certificate x509Certificate) throws CertPathValidatorException {
        final String s = "name constraints";
        if (ConstraintsChecker.debug != null) {
            ConstraintsChecker.debug.println("---checking " + s + "...");
        }
        if (this.prevNC != null && (this.i == this.certPathLength || !X509CertImpl.isSelfIssued(x509Certificate))) {
            if (ConstraintsChecker.debug != null) {
                ConstraintsChecker.debug.println("prevNC = " + this.prevNC + ", currDN = " + x509Certificate.getSubjectX500Principal());
            }
            try {
                if (!this.prevNC.verify(x509Certificate)) {
                    throw new CertPathValidatorException(s + " check failed", null, null, -1, PKIXReason.INVALID_NAME);
                }
            }
            catch (final IOException ex) {
                throw new CertPathValidatorException(ex);
            }
        }
        this.prevNC = mergeNameConstraints(x509Certificate, this.prevNC);
        if (ConstraintsChecker.debug != null) {
            ConstraintsChecker.debug.println(s + " verified.");
        }
    }
    
    static NameConstraintsExtension mergeNameConstraints(final X509Certificate x509Certificate, final NameConstraintsExtension nameConstraintsExtension) throws CertPathValidatorException {
        X509CertImpl impl;
        try {
            impl = X509CertImpl.toImpl(x509Certificate);
        }
        catch (final CertificateException ex) {
            throw new CertPathValidatorException(ex);
        }
        final NameConstraintsExtension nameConstraintsExtension2 = impl.getNameConstraintsExtension();
        if (ConstraintsChecker.debug != null) {
            ConstraintsChecker.debug.println("prevNC = " + nameConstraintsExtension + ", newNC = " + String.valueOf(nameConstraintsExtension2));
        }
        if (nameConstraintsExtension != null) {
            try {
                nameConstraintsExtension.merge(nameConstraintsExtension2);
            }
            catch (final IOException ex2) {
                throw new CertPathValidatorException(ex2);
            }
            if (ConstraintsChecker.debug != null) {
                ConstraintsChecker.debug.println("mergedNC = " + nameConstraintsExtension);
            }
            return nameConstraintsExtension;
        }
        if (ConstraintsChecker.debug != null) {
            ConstraintsChecker.debug.println("mergedNC = " + String.valueOf(nameConstraintsExtension2));
        }
        if (nameConstraintsExtension2 == null) {
            return nameConstraintsExtension2;
        }
        return (NameConstraintsExtension)nameConstraintsExtension2.clone();
    }
    
    private void checkBasicConstraints(final X509Certificate x509Certificate) throws CertPathValidatorException {
        final String s = "basic constraints";
        if (ConstraintsChecker.debug != null) {
            ConstraintsChecker.debug.println("---checking " + s + "...");
            ConstraintsChecker.debug.println("i = " + this.i + ", maxPathLength = " + this.maxPathLength);
        }
        if (this.i < this.certPathLength) {
            int basicConstraints = -1;
            if (x509Certificate.getVersion() < 3) {
                if (this.i == 1 && X509CertImpl.isSelfIssued(x509Certificate)) {
                    basicConstraints = Integer.MAX_VALUE;
                }
            }
            else {
                basicConstraints = x509Certificate.getBasicConstraints();
            }
            if (basicConstraints == -1) {
                throw new CertPathValidatorException(s + " check failed: this is not a CA certificate", null, null, -1, PKIXReason.NOT_CA_CERT);
            }
            if (!X509CertImpl.isSelfIssued(x509Certificate)) {
                if (this.maxPathLength <= 0) {
                    throw new CertPathValidatorException(s + " check failed: pathLenConstraint violated - this cert must be the last cert in the certification path", null, null, -1, PKIXReason.PATH_TOO_LONG);
                }
                --this.maxPathLength;
            }
            if (basicConstraints < this.maxPathLength) {
                this.maxPathLength = basicConstraints;
            }
        }
        if (ConstraintsChecker.debug != null) {
            ConstraintsChecker.debug.println("after processing, maxPathLength = " + this.maxPathLength);
            ConstraintsChecker.debug.println(s + " verified.");
        }
    }
    
    static int mergeBasicConstraints(final X509Certificate x509Certificate, int n) {
        final int basicConstraints = x509Certificate.getBasicConstraints();
        if (!X509CertImpl.isSelfIssued(x509Certificate)) {
            --n;
        }
        if (basicConstraints < n) {
            n = basicConstraints;
        }
        return n;
    }
    
    static {
        debug = Debug.getInstance("certpath");
    }
}
