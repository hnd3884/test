package sun.security.provider.certpath;

import java.security.interfaces.DSAParams;
import java.security.spec.KeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.KeyFactory;
import java.security.interfaces.DSAPublicKey;
import java.security.cert.PKIXReason;
import sun.security.x509.X500Name;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import java.security.GeneralSecurityException;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.security.cert.Certificate;
import java.util.Set;
import java.security.cert.CertPathValidatorException;
import java.security.cert.TrustAnchor;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import java.security.PublicKey;
import sun.security.util.Debug;
import java.security.cert.PKIXCertPathChecker;

class BasicChecker extends PKIXCertPathChecker
{
    private static final Debug debug;
    private final PublicKey trustedPubKey;
    private final X500Principal caName;
    private final Date date;
    private final String sigProvider;
    private final boolean sigOnly;
    private X500Principal prevSubject;
    private PublicKey prevPubKey;
    
    BasicChecker(final TrustAnchor trustAnchor, final Date date, final String sigProvider, final boolean sigOnly) {
        if (trustAnchor.getTrustedCert() != null) {
            this.trustedPubKey = trustAnchor.getTrustedCert().getPublicKey();
            this.caName = trustAnchor.getTrustedCert().getSubjectX500Principal();
        }
        else {
            this.trustedPubKey = trustAnchor.getCAPublicKey();
            this.caName = trustAnchor.getCA();
        }
        this.date = date;
        this.sigProvider = sigProvider;
        this.sigOnly = sigOnly;
        this.prevPubKey = this.trustedPubKey;
    }
    
    @Override
    public void init(final boolean b) throws CertPathValidatorException {
        if (b) {
            throw new CertPathValidatorException("forward checking not supported");
        }
        this.prevPubKey = this.trustedPubKey;
        if (PKIX.isDSAPublicKeyWithoutParams(this.prevPubKey)) {
            throw new CertPathValidatorException("Key parameters missing");
        }
        this.prevSubject = this.caName;
    }
    
    @Override
    public boolean isForwardCheckingSupported() {
        return false;
    }
    
    @Override
    public Set<String> getSupportedExtensions() {
        return null;
    }
    
    @Override
    public void check(final Certificate certificate, final Collection<String> collection) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certificate;
        if (!this.sigOnly) {
            this.verifyValidity(x509Certificate);
            this.verifyNameChaining(x509Certificate);
        }
        this.verifySignature(x509Certificate);
        this.updateState(x509Certificate);
    }
    
    private void verifySignature(final X509Certificate x509Certificate) throws CertPathValidatorException {
        final String s = "signature";
        if (BasicChecker.debug != null) {
            BasicChecker.debug.println("---checking " + s + "...");
        }
        try {
            x509Certificate.verify(this.prevPubKey, this.sigProvider);
        }
        catch (final SignatureException ex) {
            throw new CertPathValidatorException(s + " check failed", ex, null, -1, CertPathValidatorException.BasicReason.INVALID_SIGNATURE);
        }
        catch (final GeneralSecurityException ex2) {
            throw new CertPathValidatorException(s + " check failed", ex2);
        }
        if (BasicChecker.debug != null) {
            BasicChecker.debug.println(s + " verified.");
        }
    }
    
    private void verifyValidity(final X509Certificate x509Certificate) throws CertPathValidatorException {
        final String s = "validity";
        if (BasicChecker.debug != null) {
            BasicChecker.debug.println("---checking " + s + ":" + this.date.toString() + "...");
        }
        try {
            x509Certificate.checkValidity(this.date);
        }
        catch (final CertificateExpiredException ex) {
            throw new CertPathValidatorException(s + " check failed", ex, null, -1, CertPathValidatorException.BasicReason.EXPIRED);
        }
        catch (final CertificateNotYetValidException ex2) {
            throw new CertPathValidatorException(s + " check failed", ex2, null, -1, CertPathValidatorException.BasicReason.NOT_YET_VALID);
        }
        if (BasicChecker.debug != null) {
            BasicChecker.debug.println(s + " verified.");
        }
    }
    
    private void verifyNameChaining(final X509Certificate x509Certificate) throws CertPathValidatorException {
        if (this.prevSubject != null) {
            final String s = "subject/issuer name chaining";
            if (BasicChecker.debug != null) {
                BasicChecker.debug.println("---checking " + s + "...");
            }
            final X500Principal issuerX500Principal = x509Certificate.getIssuerX500Principal();
            if (X500Name.asX500Name(issuerX500Principal).isEmpty()) {
                throw new CertPathValidatorException(s + " check failed: empty/null issuer DN in certificate is invalid", null, null, -1, PKIXReason.NAME_CHAINING);
            }
            if (!issuerX500Principal.equals(this.prevSubject)) {
                throw new CertPathValidatorException(s + " check failed", null, null, -1, PKIXReason.NAME_CHAINING);
            }
            if (BasicChecker.debug != null) {
                BasicChecker.debug.println(s + " verified.");
            }
        }
    }
    
    private void updateState(final X509Certificate x509Certificate) throws CertPathValidatorException {
        PublicKey prevPubKey = x509Certificate.getPublicKey();
        if (BasicChecker.debug != null) {
            BasicChecker.debug.println("BasicChecker.updateState issuer: " + x509Certificate.getIssuerX500Principal().toString() + "; subject: " + x509Certificate.getSubjectX500Principal() + "; serial#: " + x509Certificate.getSerialNumber().toString());
        }
        if (PKIX.isDSAPublicKeyWithoutParams(prevPubKey)) {
            prevPubKey = makeInheritedParamsKey(prevPubKey, this.prevPubKey);
            if (BasicChecker.debug != null) {
                BasicChecker.debug.println("BasicChecker.updateState Made key with inherited params");
            }
        }
        this.prevPubKey = prevPubKey;
        this.prevSubject = x509Certificate.getSubjectX500Principal();
    }
    
    static PublicKey makeInheritedParamsKey(final PublicKey publicKey, final PublicKey publicKey2) throws CertPathValidatorException {
        if (!(publicKey instanceof DSAPublicKey) || !(publicKey2 instanceof DSAPublicKey)) {
            throw new CertPathValidatorException("Input key is not appropriate type for inheriting parameters");
        }
        final DSAParams params = ((DSAPublicKey)publicKey2).getParams();
        if (params == null) {
            throw new CertPathValidatorException("Key parameters missing");
        }
        try {
            return KeyFactory.getInstance("DSA").generatePublic(new DSAPublicKeySpec(((DSAPublicKey)publicKey).getY(), params.getP(), params.getQ(), params.getG()));
        }
        catch (final GeneralSecurityException ex) {
            throw new CertPathValidatorException("Unable to generate key with inherited parameters: " + ex.getMessage(), ex);
        }
    }
    
    PublicKey getPublicKey() {
        return this.prevPubKey;
    }
    
    static {
        debug = Debug.getInstance("certpath");
    }
}
