package org.bouncycastle.jce.provider;

import java.security.cert.CertificateEncodingException;
import org.bouncycastle.asn1.x509.TBSCertificate;
import java.util.Iterator;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.PublicKey;
import org.bouncycastle.asn1.x500.X500Name;
import java.security.cert.TrustAnchor;
import java.security.cert.PKIXCertPathValidatorResult;
import org.bouncycastle.asn1.x509.Extension;
import java.util.Collection;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.Certificate;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import java.security.cert.PolicyNode;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.security.cert.X509Certificate;
import java.security.cert.CertPathValidatorException;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.x509.ExtendedPKIXParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPath;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.security.cert.CertPathValidatorSpi;

public class PKIXCertPathValidatorSpi extends CertPathValidatorSpi
{
    private final JcaJceHelper helper;
    
    public PKIXCertPathValidatorSpi() {
        this.helper = new BCJcaJceHelper();
    }
    
    @Override
    public CertPathValidatorResult engineValidate(final CertPath certPath, final CertPathParameters certPathParameters) throws CertPathValidatorException, InvalidAlgorithmParameterException {
        PKIXExtendedParameters pkixExtendedParameters;
        if (certPathParameters instanceof PKIXParameters) {
            final PKIXExtendedParameters.Builder builder = new PKIXExtendedParameters.Builder((PKIXParameters)certPathParameters);
            if (certPathParameters instanceof ExtendedPKIXParameters) {
                final ExtendedPKIXParameters extendedPKIXParameters = (ExtendedPKIXParameters)certPathParameters;
                builder.setUseDeltasEnabled(extendedPKIXParameters.isUseDeltasEnabled());
                builder.setValidityModel(extendedPKIXParameters.getValidityModel());
            }
            pkixExtendedParameters = builder.build();
        }
        else if (certPathParameters instanceof PKIXExtendedBuilderParameters) {
            pkixExtendedParameters = ((PKIXExtendedBuilderParameters)certPathParameters).getBaseParameters();
        }
        else {
            if (!(certPathParameters instanceof PKIXExtendedParameters)) {
                throw new InvalidAlgorithmParameterException("Parameters must be a " + PKIXParameters.class.getName() + " instance.");
            }
            pkixExtendedParameters = (PKIXExtendedParameters)certPathParameters;
        }
        if (pkixExtendedParameters.getTrustAnchors() == null) {
            throw new InvalidAlgorithmParameterException("trustAnchors is null, this is not allowed for certification path validation.");
        }
        final List<? extends Certificate> certificates = certPath.getCertificates();
        final int size = certificates.size();
        if (certificates.isEmpty()) {
            throw new CertPathValidatorException("Certification path is empty.", null, certPath, -1);
        }
        final Set initialPolicies = pkixExtendedParameters.getInitialPolicies();
        TrustAnchor trustAnchor;
        try {
            trustAnchor = CertPathValidatorUtilities.findTrustAnchor(certificates.get(certificates.size() - 1), pkixExtendedParameters.getTrustAnchors(), pkixExtendedParameters.getSigProvider());
            if (trustAnchor == null) {
                throw new CertPathValidatorException("Trust anchor for certification path not found.", null, certPath, -1);
            }
            checkCertificate(trustAnchor.getTrustedCert());
        }
        catch (final AnnotatedException ex) {
            throw new CertPathValidatorException(ex.getMessage(), ex.getUnderlyingException(), certPath, certificates.size() - 1);
        }
        final PKIXExtendedParameters build = new PKIXExtendedParameters.Builder(pkixExtendedParameters).setTrustAnchor(trustAnchor).build();
        final ArrayList[] array = new ArrayList[size + 1];
        for (int i = 0; i < array.length; ++i) {
            array[i] = new ArrayList();
        }
        final HashSet set = new HashSet();
        set.add("2.5.29.32.0");
        PKIXPolicyNode pkixPolicyNode = new PKIXPolicyNode(new ArrayList(), 0, set, null, new HashSet(), "2.5.29.32.0", false);
        array[0].add(pkixPolicyNode);
        final PKIXNameConstraintValidator pkixNameConstraintValidator = new PKIXNameConstraintValidator();
        final HashSet set2 = new HashSet();
        int prepareNextCertI1;
        if (build.isExplicitPolicyRequired()) {
            prepareNextCertI1 = 0;
        }
        else {
            prepareNextCertI1 = size + 1;
        }
        int prepareNextCertJ;
        if (build.isAnyPolicyInhibited()) {
            prepareNextCertJ = 0;
        }
        else {
            prepareNextCertJ = size + 1;
        }
        int prepareNextCertI2;
        if (build.isPolicyMappingInhibited()) {
            prepareNextCertI2 = 0;
        }
        else {
            prepareNextCertI2 = size + 1;
        }
        X509Certificate trustedCert = trustAnchor.getTrustedCert();
        X500Name x500Name;
        PublicKey publicKey;
        try {
            if (trustedCert != null) {
                x500Name = PrincipalUtils.getSubjectPrincipal(trustedCert);
                publicKey = trustedCert.getPublicKey();
            }
            else {
                x500Name = PrincipalUtils.getCA(trustAnchor);
                publicKey = trustAnchor.getCAPublicKey();
            }
        }
        catch (final IllegalArgumentException ex2) {
            throw new ExtCertPathValidatorException("Subject of trust anchor could not be (re)encoded.", ex2, certPath, -1);
        }
        AlgorithmIdentifier algorithmIdentifier;
        try {
            algorithmIdentifier = CertPathValidatorUtilities.getAlgorithmIdentifier(publicKey);
        }
        catch (final CertPathValidatorException ex3) {
            throw new ExtCertPathValidatorException("Algorithm identifier of public key of trust anchor could not be read.", ex3, certPath, -1);
        }
        algorithmIdentifier.getAlgorithm();
        algorithmIdentifier.getParameters();
        int prepareNextCertM = size;
        if (build.getTargetConstraints() != null && !build.getTargetConstraints().match(certificates.get(0))) {
            throw new ExtCertPathValidatorException("Target certificate in certification path does not match targetConstraints.", null, certPath, 0);
        }
        final List certPathCheckers = build.getCertPathCheckers();
        final Iterator iterator = certPathCheckers.iterator();
        while (iterator.hasNext()) {
            ((PKIXCertPathChecker)iterator.next()).init(false);
        }
        X509Certificate x509Certificate = null;
        int j;
        for (j = certificates.size() - 1; j >= 0; --j) {
            final int n = size - j;
            x509Certificate = (X509Certificate)certificates.get(j);
            final boolean b = j == certificates.size() - 1;
            try {
                checkCertificate(x509Certificate);
            }
            catch (final AnnotatedException ex4) {
                throw new CertPathValidatorException(ex4.getMessage(), ex4.getUnderlyingException(), certPath, j);
            }
            RFC3280CertPathUtilities.processCertA(certPath, build, j, publicKey, b, x500Name, trustedCert, this.helper);
            RFC3280CertPathUtilities.processCertBC(certPath, j, pkixNameConstraintValidator);
            pkixPolicyNode = RFC3280CertPathUtilities.processCertE(certPath, j, RFC3280CertPathUtilities.processCertD(certPath, j, set2, pkixPolicyNode, array, prepareNextCertJ));
            RFC3280CertPathUtilities.processCertF(certPath, j, pkixPolicyNode, prepareNextCertI1);
            if (n != size) {
                if (x509Certificate != null && x509Certificate.getVersion() == 1) {
                    if (n != 1 || !x509Certificate.equals(trustAnchor.getTrustedCert())) {
                        throw new CertPathValidatorException("Version 1 certificates can't be used as CA ones.", null, certPath, j);
                    }
                }
                else {
                    RFC3280CertPathUtilities.prepareNextCertA(certPath, j);
                    pkixPolicyNode = RFC3280CertPathUtilities.prepareCertB(certPath, j, array, pkixPolicyNode, prepareNextCertI2);
                    RFC3280CertPathUtilities.prepareNextCertG(certPath, j, pkixNameConstraintValidator);
                    final int prepareNextCertH1 = RFC3280CertPathUtilities.prepareNextCertH1(certPath, j, prepareNextCertI1);
                    final int prepareNextCertH2 = RFC3280CertPathUtilities.prepareNextCertH2(certPath, j, prepareNextCertI2);
                    final int prepareNextCertH3 = RFC3280CertPathUtilities.prepareNextCertH3(certPath, j, prepareNextCertJ);
                    prepareNextCertI1 = RFC3280CertPathUtilities.prepareNextCertI1(certPath, j, prepareNextCertH1);
                    prepareNextCertI2 = RFC3280CertPathUtilities.prepareNextCertI2(certPath, j, prepareNextCertH2);
                    prepareNextCertJ = RFC3280CertPathUtilities.prepareNextCertJ(certPath, j, prepareNextCertH3);
                    RFC3280CertPathUtilities.prepareNextCertK(certPath, j);
                    prepareNextCertM = RFC3280CertPathUtilities.prepareNextCertM(certPath, j, RFC3280CertPathUtilities.prepareNextCertL(certPath, j, prepareNextCertM));
                    RFC3280CertPathUtilities.prepareNextCertN(certPath, j);
                    final Set<String> criticalExtensionOIDs = x509Certificate.getCriticalExtensionOIDs();
                    HashSet set3;
                    if (criticalExtensionOIDs != null) {
                        set3 = new HashSet(criticalExtensionOIDs);
                        set3.remove(RFC3280CertPathUtilities.KEY_USAGE);
                        set3.remove(RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
                        set3.remove(RFC3280CertPathUtilities.POLICY_MAPPINGS);
                        set3.remove(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY);
                        set3.remove(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
                        set3.remove(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
                        set3.remove(RFC3280CertPathUtilities.POLICY_CONSTRAINTS);
                        set3.remove(RFC3280CertPathUtilities.BASIC_CONSTRAINTS);
                        set3.remove(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME);
                        set3.remove(RFC3280CertPathUtilities.NAME_CONSTRAINTS);
                    }
                    else {
                        set3 = new HashSet();
                    }
                    RFC3280CertPathUtilities.prepareNextCertO(certPath, j, set3, certPathCheckers);
                    trustedCert = x509Certificate;
                    x500Name = PrincipalUtils.getSubjectPrincipal(trustedCert);
                    try {
                        publicKey = CertPathValidatorUtilities.getNextWorkingKey(certPath.getCertificates(), j, this.helper);
                    }
                    catch (final CertPathValidatorException ex5) {
                        throw new CertPathValidatorException("Next working key could not be retrieved.", ex5, certPath, j);
                    }
                    final AlgorithmIdentifier algorithmIdentifier2 = CertPathValidatorUtilities.getAlgorithmIdentifier(publicKey);
                    algorithmIdentifier2.getAlgorithm();
                    algorithmIdentifier2.getParameters();
                }
            }
        }
        final int wrapupCertB = RFC3280CertPathUtilities.wrapupCertB(certPath, j + 1, RFC3280CertPathUtilities.wrapupCertA(prepareNextCertI1, x509Certificate));
        final Set<String> criticalExtensionOIDs2 = x509Certificate.getCriticalExtensionOIDs();
        HashSet set4;
        if (criticalExtensionOIDs2 != null) {
            set4 = new HashSet(criticalExtensionOIDs2);
            set4.remove(RFC3280CertPathUtilities.KEY_USAGE);
            set4.remove(RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
            set4.remove(RFC3280CertPathUtilities.POLICY_MAPPINGS);
            set4.remove(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY);
            set4.remove(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
            set4.remove(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
            set4.remove(RFC3280CertPathUtilities.POLICY_CONSTRAINTS);
            set4.remove(RFC3280CertPathUtilities.BASIC_CONSTRAINTS);
            set4.remove(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME);
            set4.remove(RFC3280CertPathUtilities.NAME_CONSTRAINTS);
            set4.remove(RFC3280CertPathUtilities.CRL_DISTRIBUTION_POINTS);
            set4.remove(Extension.extendedKeyUsage.getId());
        }
        else {
            set4 = new HashSet();
        }
        RFC3280CertPathUtilities.wrapupCertF(certPath, j + 1, certPathCheckers, set4);
        final PKIXPolicyNode wrapupCertG = RFC3280CertPathUtilities.wrapupCertG(certPath, build, initialPolicies, j + 1, array, pkixPolicyNode, set2);
        if (wrapupCertB > 0 || wrapupCertG != null) {
            return new PKIXCertPathValidatorResult(trustAnchor, wrapupCertG, x509Certificate.getPublicKey());
        }
        throw new CertPathValidatorException("Path processing failed on policy.", null, certPath, j);
    }
    
    static void checkCertificate(final X509Certificate x509Certificate) throws AnnotatedException {
        try {
            TBSCertificate.getInstance(x509Certificate.getTBSCertificate());
        }
        catch (final CertificateEncodingException ex) {
            throw new AnnotatedException("unable to process TBSCertificate");
        }
        catch (final IllegalArgumentException ex2) {
            throw new AnnotatedException(ex2.getMessage());
        }
    }
}
