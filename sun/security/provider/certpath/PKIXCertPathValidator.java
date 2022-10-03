package sun.security.provider.certpath;

import java.util.Date;
import jdk.internal.event.EventHelper;
import jdk.jfr.events.X509ValidationEvent;
import java.util.Collection;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PolicyQualifierInfo;
import java.util.Set;
import java.util.Collections;
import java.security.AlgorithmConstraints;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.security.cert.PKIXReason;
import java.security.cert.Certificate;
import java.security.cert.TrustAnchor;
import java.io.IOException;
import java.security.cert.CertificateException;
import sun.security.x509.X509CertImpl;
import java.security.cert.X509Certificate;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPath;
import java.security.cert.CertPathChecker;
import java.util.concurrent.atomic.AtomicLong;
import sun.security.util.Debug;
import java.security.cert.CertPathValidatorSpi;

public final class PKIXCertPathValidator extends CertPathValidatorSpi
{
    private static final Debug debug;
    private static final AtomicLong validationCounter;
    
    @Override
    public CertPathChecker engineGetRevocationChecker() {
        return new RevocationChecker();
    }
    
    @Override
    public CertPathValidatorResult engineValidate(final CertPath certPath, final CertPathParameters certPathParameters) throws CertPathValidatorException, InvalidAlgorithmParameterException {
        return validate(PKIX.checkParams(certPath, certPathParameters));
    }
    
    private static PKIXCertPathValidatorResult validate(final PKIX.ValidatorParams validatorParams) throws CertPathValidatorException {
        if (PKIXCertPathValidator.debug != null) {
            PKIXCertPathValidator.debug.println("PKIXCertPathValidator.engineValidate()...");
        }
        AdaptableX509CertSelector adaptableX509CertSelector = null;
        final List<X509Certificate> certificates = validatorParams.certificates();
        if (!certificates.isEmpty()) {
            adaptableX509CertSelector = new AdaptableX509CertSelector();
            final X509Certificate x509Certificate = certificates.get(0);
            adaptableX509CertSelector.setSubject(x509Certificate.getIssuerX500Principal());
            try {
                adaptableX509CertSelector.setSkiAndSerialNumber(X509CertImpl.toImpl(x509Certificate).getAuthorityKeyIdentifierExtension());
            }
            catch (final CertificateException | IOException ex) {}
        }
        CertPathValidatorException ex2 = null;
        for (final TrustAnchor trustAnchor : validatorParams.trustAnchors()) {
            final X509Certificate trustedCert = trustAnchor.getTrustedCert();
            if (trustedCert != null) {
                if (adaptableX509CertSelector != null && !adaptableX509CertSelector.match(trustedCert)) {
                    if (PKIXCertPathValidator.debug != null) {
                        PKIXCertPathValidator.debug.println("NO - don't try this trustedCert");
                        continue;
                    }
                    continue;
                }
                else if (PKIXCertPathValidator.debug != null) {
                    PKIXCertPathValidator.debug.println("YES - try this trustedCert");
                    PKIXCertPathValidator.debug.println("anchor.getTrustedCert().getSubjectX500Principal() = " + trustedCert.getSubjectX500Principal());
                }
            }
            else if (PKIXCertPathValidator.debug != null) {
                PKIXCertPathValidator.debug.println("PKIXCertPathValidator.engineValidate(): anchor.getTrustedCert() == null");
            }
            try {
                return validate(trustAnchor, validatorParams);
            }
            catch (final CertPathValidatorException ex3) {
                ex2 = ex3;
                continue;
            }
            break;
        }
        if (ex2 != null) {
            throw ex2;
        }
        throw new CertPathValidatorException("Path does not chain with any of the trust anchors", null, null, -1, PKIXReason.NO_TRUST_ANCHOR);
    }
    
    private static PKIXCertPathValidatorResult validate(final TrustAnchor trustAnchor, final PKIX.ValidatorParams validatorParams) throws CertPathValidatorException {
        final UntrustedChecker untrustedChecker = new UntrustedChecker();
        final X509Certificate trustedCert = trustAnchor.getTrustedCert();
        if (trustedCert != null) {
            untrustedChecker.check(trustedCert);
        }
        final int size = validatorParams.certificates().size();
        final ArrayList list = new ArrayList();
        list.add(untrustedChecker);
        list.add(new AlgorithmChecker(trustAnchor, null, validatorParams.date(), validatorParams.timestamp(), validatorParams.variant()));
        list.add(new KeyChecker(size, validatorParams.targetCertConstraints()));
        list.add(new ConstraintsChecker(size));
        final PolicyChecker policyChecker = new PolicyChecker(validatorParams.initialPolicies(), size, validatorParams.explicitPolicyRequired(), validatorParams.policyMappingInhibited(), validatorParams.anyPolicyInhibited(), validatorParams.policyQualifiersRejected(), new PolicyNodeImpl(null, "2.5.29.32.0", null, false, Collections.singleton("2.5.29.32.0"), false));
        list.add(policyChecker);
        Date date;
        if ((validatorParams.variant() == "code signing" || validatorParams.variant() == "plugin code signing") && validatorParams.timestamp() != null) {
            date = validatorParams.timestamp().getTimestamp();
        }
        else {
            date = validatorParams.date();
        }
        final BasicChecker basicChecker = new BasicChecker(trustAnchor, date, validatorParams.sigProvider(), false);
        list.add(basicChecker);
        int n = 0;
        final List<PKIXCertPathChecker> certPathCheckers = validatorParams.certPathCheckers();
        for (final PKIXCertPathChecker pkixCertPathChecker : certPathCheckers) {
            if (pkixCertPathChecker instanceof PKIXRevocationChecker) {
                if (n != 0) {
                    throw new CertPathValidatorException("Only one PKIXRevocationChecker can be specified");
                }
                n = 1;
                if (!(pkixCertPathChecker instanceof RevocationChecker)) {
                    continue;
                }
                ((RevocationChecker)pkixCertPathChecker).init(trustAnchor, validatorParams);
            }
        }
        if (validatorParams.revocationEnabled() && n == 0) {
            list.add(new RevocationChecker(trustAnchor, validatorParams));
        }
        list.addAll(certPathCheckers);
        PKIXMasterCertPathValidator.validate(validatorParams.certPath(), validatorParams.certificates(), list);
        final X509ValidationEvent x509ValidationEvent = new X509ValidationEvent();
        if (x509ValidationEvent.shouldCommit() || EventHelper.isLoggingSecurity()) {
            final int[] array = validatorParams.certificates().stream().mapToInt(x509Certificate -> x509Certificate.hashCode()).toArray();
            final int hashCode = trustAnchor.getTrustedCert().hashCode();
            if (x509ValidationEvent.shouldCommit()) {
                x509ValidationEvent.certificateId = hashCode;
                int certificatePosition = 1;
                x509ValidationEvent.certificatePosition = certificatePosition;
                x509ValidationEvent.validationCounter = PKIXCertPathValidator.validationCounter.incrementAndGet();
                x509ValidationEvent.commit();
                final int[] array2 = array;
                for (int length = array2.length, i = 0; i < length; ++i) {
                    x509ValidationEvent.certificateId = array2[i];
                    x509ValidationEvent.certificatePosition = ++certificatePosition;
                    x509ValidationEvent.commit();
                }
            }
            if (EventHelper.isLoggingSecurity()) {
                EventHelper.logX509ValidationEvent(hashCode, array);
            }
        }
        return new PKIXCertPathValidatorResult(trustAnchor, policyChecker.getPolicyTree(), basicChecker.getPublicKey());
    }
    
    static {
        debug = Debug.getInstance("certpath");
        validationCounter = new AtomicLong();
    }
}
