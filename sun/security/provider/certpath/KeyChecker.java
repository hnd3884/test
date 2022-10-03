package sun.security.provider.certpath;

import java.security.cert.CertPath;
import java.security.cert.PKIXReason;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.security.cert.Certificate;
import java.util.Collections;
import sun.security.x509.PKIXExtensions;
import java.util.HashSet;
import java.security.cert.CertPathValidatorException;
import java.util.Set;
import java.security.cert.CertSelector;
import sun.security.util.Debug;
import java.security.cert.PKIXCertPathChecker;

class KeyChecker extends PKIXCertPathChecker
{
    private static final Debug debug;
    private final int certPathLen;
    private final CertSelector targetConstraints;
    private int remainingCerts;
    private Set<String> supportedExts;
    private static final int KEY_CERT_SIGN = 5;
    
    KeyChecker(final int certPathLen, final CertSelector targetConstraints) {
        this.certPathLen = certPathLen;
        this.targetConstraints = targetConstraints;
    }
    
    @Override
    public void init(final boolean b) throws CertPathValidatorException {
        if (!b) {
            this.remainingCerts = this.certPathLen;
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
            (this.supportedExts = new HashSet<String>(3)).add(PKIXExtensions.KeyUsage_Id.toString());
            this.supportedExts.add(PKIXExtensions.ExtendedKeyUsage_Id.toString());
            this.supportedExts.add(PKIXExtensions.SubjectAlternativeName_Id.toString());
            this.supportedExts = Collections.unmodifiableSet((Set<? extends String>)this.supportedExts);
        }
        return this.supportedExts;
    }
    
    @Override
    public void check(final Certificate certificate, final Collection<String> collection) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certificate;
        --this.remainingCerts;
        if (this.remainingCerts == 0) {
            if (this.targetConstraints != null && !this.targetConstraints.match(x509Certificate)) {
                throw new CertPathValidatorException("target certificate constraints check failed");
            }
        }
        else {
            verifyCAKeyUsage(x509Certificate);
        }
        if (collection != null && !collection.isEmpty()) {
            collection.remove(PKIXExtensions.KeyUsage_Id.toString());
            collection.remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
            collection.remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
        }
    }
    
    static void verifyCAKeyUsage(final X509Certificate x509Certificate) throws CertPathValidatorException {
        final String s = "CA key usage";
        if (KeyChecker.debug != null) {
            KeyChecker.debug.println("KeyChecker.verifyCAKeyUsage() ---checking " + s + "...");
        }
        final boolean[] keyUsage = x509Certificate.getKeyUsage();
        if (keyUsage == null) {
            return;
        }
        if (!keyUsage[5]) {
            throw new CertPathValidatorException(s + " check failed: keyCertSign bit is not set", null, null, -1, PKIXReason.INVALID_KEY_USAGE);
        }
        if (KeyChecker.debug != null) {
            KeyChecker.debug.println("KeyChecker.verifyCAKeyUsage() " + s + " verified.");
        }
    }
    
    static {
        debug = Debug.getInstance("certpath");
    }
}
