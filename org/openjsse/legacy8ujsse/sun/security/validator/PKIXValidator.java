package org.openjsse.legacy8ujsse.sun.security.validator;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509CertSelector;
import java.security.GeneralSecurityException;
import java.security.cert.CertPathParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.util.Arrays;
import java.security.cert.CertPathValidator;
import java.util.Date;
import java.security.cert.Certificate;
import java.security.cert.CertPath;
import java.util.Collections;
import java.security.cert.PKIXCertPathChecker;
import sun.security.provider.certpath.AlgorithmChecker;
import sun.security.provider.certpath.PKIXExtendedParameters;
import java.security.Timestamp;
import java.security.AlgorithmConstraints;
import java.util.Iterator;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertSelector;
import java.security.cert.TrustAnchor;
import java.util.HashSet;
import java.util.Collection;
import sun.security.action.GetPropertyAction;
import java.security.cert.CertificateFactory;
import java.security.PublicKey;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import java.util.Map;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509Certificate;
import java.util.Set;

public final class PKIXValidator extends Validator
{
    private static final boolean checkTLSRevocation;
    private static final boolean TRY_VALIDATOR = true;
    private static final boolean ALLOW_NON_CA_ANCHOR;
    private final Set<X509Certificate> trustedCerts;
    private final PKIXBuilderParameters parameterTemplate;
    private int certPathLength;
    private final Map<X500Principal, List<PublicKey>> trustedSubjects;
    private final CertificateFactory factory;
    private final boolean plugin;
    
    private static boolean allowNonCaAnchor() {
        final String prop = GetPropertyAction.privilegedGetProperty("jdk.security.allowNonCaAnchor");
        return prop != null && (prop.isEmpty() || prop.equalsIgnoreCase("true"));
    }
    
    PKIXValidator(final String variant, final Collection<X509Certificate> trustedCerts) {
        super("PKIX", variant);
        this.certPathLength = -1;
        if (trustedCerts instanceof Set) {
            this.trustedCerts = (Set)trustedCerts;
        }
        else {
            this.trustedCerts = new HashSet<X509Certificate>(trustedCerts);
        }
        final Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
        for (final X509Certificate cert : trustedCerts) {
            trustAnchors.add(new TrustAnchor(cert, null));
        }
        try {
            this.parameterTemplate = new PKIXBuilderParameters(trustAnchors, null);
        }
        catch (final InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Unexpected error: " + e.toString(), e);
        }
        this.setDefaultParameters(variant);
        this.trustedSubjects = new HashMap<X500Principal, List<PublicKey>>();
        for (final X509Certificate cert : trustedCerts) {
            final X500Principal dn = cert.getSubjectX500Principal();
            List<PublicKey> keys;
            if (this.trustedSubjects.containsKey(dn)) {
                keys = this.trustedSubjects.get(dn);
            }
            else {
                keys = new ArrayList<PublicKey>();
                this.trustedSubjects.put(dn, keys);
            }
            keys.add(cert.getPublicKey());
        }
        try {
            this.factory = CertificateFactory.getInstance("X.509");
        }
        catch (final CertificateException e2) {
            throw new RuntimeException("Internal error", e2);
        }
        this.plugin = variant.equals("plugin code signing");
    }
    
    PKIXValidator(final String variant, final PKIXBuilderParameters params) {
        super("PKIX", variant);
        this.certPathLength = -1;
        this.trustedCerts = new HashSet<X509Certificate>();
        for (final TrustAnchor anchor : params.getTrustAnchors()) {
            final X509Certificate cert = anchor.getTrustedCert();
            if (cert != null) {
                this.trustedCerts.add(cert);
            }
        }
        this.parameterTemplate = params;
        this.trustedSubjects = new HashMap<X500Principal, List<PublicKey>>();
        for (final X509Certificate cert2 : this.trustedCerts) {
            final X500Principal dn = cert2.getSubjectX500Principal();
            List<PublicKey> keys;
            if (this.trustedSubjects.containsKey(dn)) {
                keys = this.trustedSubjects.get(dn);
            }
            else {
                keys = new ArrayList<PublicKey>();
                this.trustedSubjects.put(dn, keys);
            }
            keys.add(cert2.getPublicKey());
        }
        try {
            this.factory = CertificateFactory.getInstance("X.509");
        }
        catch (final CertificateException e) {
            throw new RuntimeException("Internal error", e);
        }
        this.plugin = variant.equals("plugin code signing");
    }
    
    @Override
    public Collection<X509Certificate> getTrustedCertificates() {
        return this.trustedCerts;
    }
    
    public int getCertPathLength() {
        return this.certPathLength;
    }
    
    private void setDefaultParameters(final String variant) {
        if (variant == "tls server" || variant == "tls client") {
            this.parameterTemplate.setRevocationEnabled(PKIXValidator.checkTLSRevocation);
        }
        else {
            this.parameterTemplate.setRevocationEnabled(false);
        }
    }
    
    public PKIXBuilderParameters getParameters() {
        return this.parameterTemplate;
    }
    
    @Override
    X509Certificate[] engineValidate(final X509Certificate[] chain, final Collection<X509Certificate> otherCerts, final AlgorithmConstraints constraints, final Object parameter) throws CertificateException {
        if (chain == null || chain.length == 0) {
            throw new CertificateException("null or zero-length certificate chain");
        }
        PKIXBuilderParameters pkixParameters = null;
        try {
            pkixParameters = (PKIXBuilderParameters)new PKIXExtendedParameters((PKIXBuilderParameters)this.parameterTemplate.clone(), (parameter instanceof Timestamp) ? ((Timestamp)parameter) : null, this.variant);
        }
        catch (final InvalidAlgorithmParameterException ex) {}
        if (constraints != null) {
            pkixParameters.addCertPathChecker(new AlgorithmChecker(constraints, (Timestamp)null, this.variant));
        }
        X500Principal prevIssuer = null;
        for (int i = 0; i < chain.length; ++i) {
            final X509Certificate cert = chain[i];
            final X500Principal dn = cert.getSubjectX500Principal();
            if (i == 0) {
                if (this.trustedCerts.contains(cert)) {
                    return new X509Certificate[] { chain[0] };
                }
            }
            else {
                if (!dn.equals(prevIssuer)) {
                    return this.doBuild(chain, otherCerts, pkixParameters);
                }
                if (this.trustedCerts.contains(cert) || (this.trustedSubjects.containsKey(dn) && this.trustedSubjects.get(dn).contains(cert.getPublicKey()))) {
                    final X509Certificate[] newChain = new X509Certificate[i];
                    System.arraycopy(chain, 0, newChain, 0, i);
                    return this.doValidate(newChain, pkixParameters);
                }
            }
            prevIssuer = cert.getIssuerX500Principal();
        }
        final X509Certificate last = chain[chain.length - 1];
        final X500Principal issuer = last.getIssuerX500Principal();
        final X500Principal subject = last.getSubjectX500Principal();
        if (this.trustedSubjects.containsKey(issuer) && this.isSignatureValid(this.trustedSubjects.get(issuer), last)) {
            return this.doValidate(chain, pkixParameters);
        }
        if (this.plugin) {
            if (chain.length > 1) {
                final X509Certificate[] newChain = new X509Certificate[chain.length - 1];
                System.arraycopy(chain, 0, newChain, 0, newChain.length);
                try {
                    pkixParameters.setTrustAnchors(Collections.singleton(new TrustAnchor(chain[chain.length - 1], null)));
                }
                catch (final InvalidAlgorithmParameterException iape) {
                    throw new CertificateException(iape);
                }
                this.doValidate(newChain, pkixParameters);
            }
            throw new ValidatorException(ValidatorException.T_NO_TRUST_ANCHOR);
        }
        return this.doBuild(chain, otherCerts, pkixParameters);
    }
    
    private boolean isSignatureValid(final List<PublicKey> keys, final X509Certificate sub) {
        if (this.plugin) {
            for (final PublicKey key : keys) {
                try {
                    sub.verify(key);
                    return true;
                }
                catch (final Exception ex) {
                    continue;
                }
                break;
            }
            return false;
        }
        return true;
    }
    
    private static X509Certificate[] toArray(final CertPath path, final TrustAnchor anchor) throws CertificateException {
        final X509Certificate trustedCert = anchor.getTrustedCert();
        if (trustedCert == null) {
            throw new ValidatorException("TrustAnchor must be specified as certificate");
        }
        verifyTrustAnchor(trustedCert);
        final List<? extends Certificate> list = path.getCertificates();
        final X509Certificate[] chain = new X509Certificate[list.size() + 1];
        list.toArray(chain);
        chain[chain.length - 1] = trustedCert;
        return chain;
    }
    
    private void setDate(final PKIXBuilderParameters params) {
        final Date date = this.validationDate;
        if (date != null) {
            params.setDate(date);
        }
    }
    
    private X509Certificate[] doValidate(final X509Certificate[] chain, final PKIXBuilderParameters params) throws CertificateException {
        try {
            this.setDate(params);
            final CertPathValidator validator = CertPathValidator.getInstance("PKIX");
            final CertPath path = this.factory.generateCertPath(Arrays.asList(chain));
            this.certPathLength = chain.length;
            final PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult)validator.validate(path, params);
            return toArray(path, result.getTrustAnchor());
        }
        catch (final GeneralSecurityException e) {
            throw new ValidatorException("PKIX path validation failed: " + e.toString(), e);
        }
    }
    
    private static void verifyTrustAnchor(final X509Certificate trustedCert) throws ValidatorException {
        if (PKIXValidator.ALLOW_NON_CA_ANCHOR) {
            return;
        }
        if (trustedCert.getVersion() < 3) {
            return;
        }
        if (trustedCert.getBasicConstraints() == -1) {
            throw new ValidatorException("TrustAnchor with subject \"" + trustedCert.getSubjectX500Principal() + "\" is not a CA certificate");
        }
        final boolean[] keyUsageBits = trustedCert.getKeyUsage();
        if (keyUsageBits != null && !keyUsageBits[5]) {
            throw new ValidatorException("TrustAnchor with subject \"" + trustedCert.getSubjectX500Principal() + "\" does not have keyCertSign bit set in KeyUsage extension");
        }
    }
    
    private X509Certificate[] doBuild(final X509Certificate[] chain, final Collection<X509Certificate> otherCerts, final PKIXBuilderParameters params) throws CertificateException {
        try {
            this.setDate(params);
            final X509CertSelector selector = new X509CertSelector();
            selector.setCertificate(chain[0]);
            params.setTargetCertConstraints(selector);
            final Collection<X509Certificate> certs = new ArrayList<X509Certificate>();
            certs.addAll(Arrays.asList(chain));
            if (otherCerts != null) {
                certs.addAll(otherCerts);
            }
            final CertStore store = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certs));
            params.addCertStore(store);
            final CertPathBuilder builder = CertPathBuilder.getInstance("PKIX");
            final PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult)builder.build(params);
            return toArray(result.getCertPath(), result.getTrustAnchor());
        }
        catch (final GeneralSecurityException e) {
            throw new ValidatorException("PKIX path building failed: " + e.toString(), e);
        }
    }
    
    static {
        checkTLSRevocation = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("com.sun.net.ssl.checkRevocation"));
        ALLOW_NON_CA_ANCHOR = allowNonCaAnchor();
    }
}
