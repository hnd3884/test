package sun.security.provider.certpath;

import java.util.Collections;
import java.security.cert.CRLException;
import sun.security.x509.X509CRLImpl;
import java.security.cert.X509CRL;
import java.security.interfaces.DSAParams;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.KeyFactory;
import java.security.interfaces.DSAPublicKey;
import sun.security.util.KeyUtil;
import java.security.Key;
import sun.security.util.ConstraintsParameters;
import java.util.EnumSet;
import java.security.cert.CertificateException;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X509CertImpl;
import java.security.cert.CertPath;
import java.security.cert.PKIXReason;
import java.util.Collection;
import java.security.cert.Certificate;
import java.security.cert.CertPathValidatorException;
import sun.security.util.AnchorCertificates;
import java.security.cert.X509Certificate;
import java.security.cert.TrustAnchor;
import sun.security.util.DisabledAlgorithmConstraints;
import java.security.CryptoPrimitive;
import java.util.Set;
import java.security.Timestamp;
import java.util.Date;
import java.security.PublicKey;
import java.security.AlgorithmConstraints;
import sun.security.util.Debug;
import java.security.cert.PKIXCertPathChecker;

public final class AlgorithmChecker extends PKIXCertPathChecker
{
    private static final Debug debug;
    private final AlgorithmConstraints constraints;
    private final PublicKey trustedPubKey;
    private final Date pkixdate;
    private PublicKey prevPubKey;
    private final Timestamp jarTimestamp;
    private final String variant;
    private static final Set<CryptoPrimitive> SIGNATURE_PRIMITIVE_SET;
    private static final Set<CryptoPrimitive> KU_PRIMITIVE_SET;
    private static final DisabledAlgorithmConstraints certPathDefaultConstraints;
    private static final boolean publicCALimits;
    private boolean trustedMatch;
    
    public AlgorithmChecker(final TrustAnchor trustAnchor, final String s) {
        this(trustAnchor, AlgorithmChecker.certPathDefaultConstraints, null, null, s);
    }
    
    public AlgorithmChecker(final AlgorithmConstraints algorithmConstraints, final Timestamp timestamp, final String s) {
        this(null, algorithmConstraints, null, timestamp, s);
    }
    
    public AlgorithmChecker(final TrustAnchor trustAnchor, final AlgorithmConstraints algorithmConstraints, final Date date, final Timestamp jarTimestamp, final String s) {
        this.trustedMatch = false;
        if (trustAnchor != null) {
            if (trustAnchor.getTrustedCert() != null) {
                this.trustedPubKey = trustAnchor.getTrustedCert().getPublicKey();
                this.trustedMatch = checkFingerprint(trustAnchor.getTrustedCert());
                if (this.trustedMatch && AlgorithmChecker.debug != null) {
                    AlgorithmChecker.debug.println("trustedMatch = true");
                }
            }
            else {
                this.trustedPubKey = trustAnchor.getCAPublicKey();
            }
        }
        else {
            this.trustedPubKey = null;
            if (AlgorithmChecker.debug != null) {
                AlgorithmChecker.debug.println("TrustAnchor is null, trustedMatch is false.");
            }
        }
        this.prevPubKey = this.trustedPubKey;
        this.constraints = ((algorithmConstraints == null) ? AlgorithmChecker.certPathDefaultConstraints : algorithmConstraints);
        this.pkixdate = ((jarTimestamp != null) ? jarTimestamp.getTimestamp() : date);
        this.jarTimestamp = jarTimestamp;
        this.variant = ((s == null) ? "generic" : s);
    }
    
    public AlgorithmChecker(final TrustAnchor trustAnchor, final Date date, final String s) {
        this(trustAnchor, AlgorithmChecker.certPathDefaultConstraints, date, null, s);
    }
    
    private static boolean checkFingerprint(final X509Certificate x509Certificate) {
        if (!AlgorithmChecker.publicCALimits) {
            return false;
        }
        if (AlgorithmChecker.debug != null) {
            AlgorithmChecker.debug.println("AlgorithmChecker.contains: " + x509Certificate.getSigAlgName());
        }
        return AnchorCertificates.contains(x509Certificate);
    }
    
    @Override
    public void init(final boolean b) throws CertPathValidatorException {
        if (!b) {
            if (this.trustedPubKey != null) {
                this.prevPubKey = this.trustedPubKey;
            }
            else {
                this.prevPubKey = null;
            }
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
        return null;
    }
    
    @Override
    public void check(final Certificate certificate, final Collection<String> collection) throws CertPathValidatorException {
        if (!(certificate instanceof X509Certificate) || this.constraints == null) {
            return;
        }
        final boolean[] keyUsage = ((X509Certificate)certificate).getKeyUsage();
        if (keyUsage != null && keyUsage.length < 9) {
            throw new CertPathValidatorException("incorrect KeyUsage extension", null, null, -1, PKIXReason.INVALID_KEY_USAGE);
        }
        X509CertImpl impl;
        AlgorithmId algorithmId;
        try {
            impl = X509CertImpl.toImpl((X509Certificate)certificate);
            algorithmId = (AlgorithmId)impl.get("x509.algorithm");
        }
        catch (final CertificateException ex) {
            throw new CertPathValidatorException(ex);
        }
        final AlgorithmParameters parameters = algorithmId.getParameters();
        PublicKey publicKey = certificate.getPublicKey();
        final String sigAlgName = impl.getSigAlgName();
        if (!this.constraints.permits(AlgorithmChecker.SIGNATURE_PRIMITIVE_SET, sigAlgName, parameters)) {
            throw new CertPathValidatorException("Algorithm constraints check failed on signature algorithm: " + sigAlgName, null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
        }
        Set<CryptoPrimitive> set = AlgorithmChecker.KU_PRIMITIVE_SET;
        if (keyUsage != null) {
            set = EnumSet.noneOf(CryptoPrimitive.class);
            if (keyUsage[0] || keyUsage[1] || keyUsage[5] || keyUsage[6]) {
                set.add(CryptoPrimitive.SIGNATURE);
            }
            if (keyUsage[2]) {
                set.add(CryptoPrimitive.KEY_ENCAPSULATION);
            }
            if (keyUsage[3]) {
                set.add(CryptoPrimitive.PUBLIC_KEY_ENCRYPTION);
            }
            if (keyUsage[4]) {
                set.add(CryptoPrimitive.KEY_AGREEMENT);
            }
            if (set.isEmpty()) {
                throw new CertPathValidatorException("incorrect KeyUsage extension bits", null, null, -1, PKIXReason.INVALID_KEY_USAGE);
            }
        }
        final ConstraintsParameters constraintsParameters = new ConstraintsParameters((X509Certificate)certificate, this.trustedMatch, this.pkixdate, this.jarTimestamp, this.variant);
        if (this.constraints instanceof DisabledAlgorithmConstraints) {
            ((DisabledAlgorithmConstraints)this.constraints).permits(sigAlgName, constraintsParameters);
        }
        else {
            AlgorithmChecker.certPathDefaultConstraints.permits(sigAlgName, constraintsParameters);
            if (!this.constraints.permits(set, publicKey)) {
                throw new CertPathValidatorException("Algorithm constraints check failed on key " + publicKey.getAlgorithm() + " with size of " + KeyUtil.getKeySize(publicKey) + "bits", null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
            }
        }
        if (this.prevPubKey == null) {
            this.prevPubKey = publicKey;
            return;
        }
        if (!this.constraints.permits(AlgorithmChecker.SIGNATURE_PRIMITIVE_SET, sigAlgName, this.prevPubKey, parameters)) {
            throw new CertPathValidatorException("Algorithm constraints check failed on signature algorithm: " + sigAlgName, null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
        }
        if (PKIX.isDSAPublicKeyWithoutParams(publicKey)) {
            if (!(this.prevPubKey instanceof DSAPublicKey)) {
                throw new CertPathValidatorException("Input key is not of a appropriate type for inheriting parameters");
            }
            final DSAParams params = ((DSAPublicKey)this.prevPubKey).getParams();
            if (params == null) {
                throw new CertPathValidatorException("Key parameters missing from public key.");
            }
            try {
                publicKey = KeyFactory.getInstance("DSA").generatePublic(new DSAPublicKeySpec(((DSAPublicKey)publicKey).getY(), params.getP(), params.getQ(), params.getG()));
            }
            catch (final GeneralSecurityException ex2) {
                throw new CertPathValidatorException("Unable to generate key with inherited parameters: " + ex2.getMessage(), ex2);
            }
        }
        this.prevPubKey = publicKey;
    }
    
    void trySetTrustAnchor(final TrustAnchor trustAnchor) {
        if (this.prevPubKey == null) {
            if (trustAnchor == null) {
                throw new IllegalArgumentException("The trust anchor cannot be null");
            }
            if (trustAnchor.getTrustedCert() != null) {
                this.prevPubKey = trustAnchor.getTrustedCert().getPublicKey();
                this.trustedMatch = checkFingerprint(trustAnchor.getTrustedCert());
                if (this.trustedMatch && AlgorithmChecker.debug != null) {
                    AlgorithmChecker.debug.println("trustedMatch = true");
                }
            }
            else {
                this.prevPubKey = trustAnchor.getCAPublicKey();
            }
        }
    }
    
    static void check(final PublicKey publicKey, final X509CRL x509CRL, final String s) throws CertPathValidatorException {
        X509CRLImpl impl;
        try {
            impl = X509CRLImpl.toImpl(x509CRL);
        }
        catch (final CRLException ex) {
            throw new CertPathValidatorException(ex);
        }
        check(publicKey, impl.getSigAlgId(), s);
    }
    
    static void check(final PublicKey publicKey, final AlgorithmId algorithmId, final String s) throws CertPathValidatorException {
        AlgorithmChecker.certPathDefaultConstraints.permits(new ConstraintsParameters(algorithmId.getName(), algorithmId.getParameters(), publicKey, s));
    }
    
    static {
        debug = Debug.getInstance("certpath");
        SIGNATURE_PRIMITIVE_SET = Collections.unmodifiableSet((Set<? extends CryptoPrimitive>)EnumSet.of(CryptoPrimitive.SIGNATURE));
        KU_PRIMITIVE_SET = Collections.unmodifiableSet((Set<? extends CryptoPrimitive>)EnumSet.of(CryptoPrimitive.SIGNATURE, CryptoPrimitive.KEY_ENCAPSULATION, CryptoPrimitive.PUBLIC_KEY_ENCRYPTION, CryptoPrimitive.KEY_AGREEMENT));
        certPathDefaultConstraints = new DisabledAlgorithmConstraints("jdk.certpath.disabledAlgorithms");
        publicCALimits = AlgorithmChecker.certPathDefaultConstraints.checkProperty("jdkCA");
    }
}
