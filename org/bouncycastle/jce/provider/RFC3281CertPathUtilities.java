package org.bouncycastle.jce.provider;

import org.bouncycastle.asn1.x509.Extension;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import org.bouncycastle.asn1.x509.X509Extensions;
import java.security.Principal;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathBuilderException;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import java.security.cert.CertPathBuilder;
import org.bouncycastle.x509.X509CertStoreSelector;
import java.io.IOException;
import java.security.cert.CertSelector;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import java.security.cert.X509CertSelector;
import java.util.HashSet;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPathParameters;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.TrustAnchor;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.ReasonFlags;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.ASN1InputStream;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.jcajce.PKIXCRLStore;
import java.util.ArrayList;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.util.List;
import java.util.Date;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.security.cert.CertPathValidatorException;
import java.util.Collection;
import org.bouncycastle.x509.PKIXAttrCertChecker;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.asn1.x509.TargetInformation;
import java.security.cert.X509Extension;
import java.util.Set;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import java.security.cert.CertPath;
import org.bouncycastle.x509.X509AttributeCertificate;

class RFC3281CertPathUtilities
{
    private static final String TARGET_INFORMATION;
    private static final String NO_REV_AVAIL;
    private static final String CRL_DISTRIBUTION_POINTS;
    private static final String AUTHORITY_INFO_ACCESS;
    
    protected static void processAttrCert7(final X509AttributeCertificate x509AttributeCertificate, final CertPath certPath, final CertPath certPath2, final PKIXExtendedParameters pkixExtendedParameters, final Set set) throws CertPathValidatorException {
        final Set<String> criticalExtensionOIDs = x509AttributeCertificate.getCriticalExtensionOIDs();
        if (criticalExtensionOIDs.contains(RFC3281CertPathUtilities.TARGET_INFORMATION)) {
            try {
                TargetInformation.getInstance(CertPathValidatorUtilities.getExtensionValue(x509AttributeCertificate, RFC3281CertPathUtilities.TARGET_INFORMATION));
            }
            catch (final AnnotatedException ex) {
                throw new ExtCertPathValidatorException("Target information extension could not be read.", ex);
            }
            catch (final IllegalArgumentException ex2) {
                throw new ExtCertPathValidatorException("Target information extension could not be read.", ex2);
            }
        }
        criticalExtensionOIDs.remove(RFC3281CertPathUtilities.TARGET_INFORMATION);
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            ((PKIXAttrCertChecker)iterator.next()).check(x509AttributeCertificate, certPath, certPath2, criticalExtensionOIDs);
        }
        if (!criticalExtensionOIDs.isEmpty()) {
            throw new CertPathValidatorException("Attribute certificate contains unsupported critical extensions: " + criticalExtensionOIDs);
        }
    }
    
    protected static void checkCRLs(final X509AttributeCertificate x509AttributeCertificate, PKIXExtendedParameters build, final X509Certificate x509Certificate, final Date date, final List list, final JcaJceHelper jcaJceHelper) throws CertPathValidatorException {
        if (build.isRevocationEnabled()) {
            if (x509AttributeCertificate.getExtensionValue(RFC3281CertPathUtilities.NO_REV_AVAIL) == null) {
                CRLDistPoint instance;
                try {
                    instance = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509AttributeCertificate, RFC3281CertPathUtilities.CRL_DISTRIBUTION_POINTS));
                }
                catch (final AnnotatedException ex) {
                    throw new CertPathValidatorException("CRL distribution point extension could not be read.", ex);
                }
                final ArrayList list2 = new ArrayList();
                try {
                    list2.addAll(CertPathValidatorUtilities.getAdditionalStoresFromCRLDistributionPoint(instance, build.getNamedCRLStoreMap()));
                }
                catch (final AnnotatedException ex2) {
                    throw new CertPathValidatorException("No additional CRL locations could be decoded from CRL distribution point extension.", ex2);
                }
                final PKIXExtendedParameters.Builder builder = new PKIXExtendedParameters.Builder(build);
                while (list2.iterator().hasNext()) {
                    builder.addCRLStore((PKIXCRLStore)list2);
                }
                build = builder.build();
                final CertStatus certStatus = new CertStatus();
                final ReasonsMask reasonsMask = new ReasonsMask();
                Throwable t = null;
                boolean b = false;
                if (instance != null) {
                    DistributionPoint[] distributionPoints;
                    try {
                        distributionPoints = instance.getDistributionPoints();
                    }
                    catch (final Exception ex3) {
                        throw new ExtCertPathValidatorException("Distribution points could not be read.", ex3);
                    }
                    try {
                        for (int n = 0; n < distributionPoints.length && certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons(); ++n) {
                            checkCRL(distributionPoints[n], x509AttributeCertificate, (PKIXExtendedParameters)build.clone(), date, x509Certificate, certStatus, reasonsMask, list, jcaJceHelper);
                            b = true;
                        }
                    }
                    catch (final AnnotatedException ex4) {
                        t = new AnnotatedException("No valid CRL for distribution point found.", ex4);
                    }
                }
                if (certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons()) {
                    try {
                        ASN1Primitive object;
                        try {
                            object = new ASN1InputStream(((X500Principal)x509AttributeCertificate.getIssuer().getPrincipals()[0]).getEncoded()).readObject();
                        }
                        catch (final Exception ex5) {
                            throw new AnnotatedException("Issuer from certificate for CRL could not be reencoded.", ex5);
                        }
                        checkCRL(new DistributionPoint(new DistributionPointName(0, new GeneralNames(new GeneralName(4, object))), null, null), x509AttributeCertificate, (PKIXExtendedParameters)build.clone(), date, x509Certificate, certStatus, reasonsMask, list, jcaJceHelper);
                        b = true;
                    }
                    catch (final AnnotatedException ex6) {
                        t = new AnnotatedException("No valid CRL for distribution point found.", ex6);
                    }
                }
                if (!b) {
                    throw new ExtCertPathValidatorException("No valid CRL found.", t);
                }
                if (certStatus.getCertStatus() != 11) {
                    throw new CertPathValidatorException("Attribute certificate revocation after " + certStatus.getRevocationDate() + ", reason: " + RFC3280CertPathUtilities.crlReasons[certStatus.getCertStatus()]);
                }
                if (!reasonsMask.isAllReasons() && certStatus.getCertStatus() == 11) {
                    certStatus.setCertStatus(12);
                }
                if (certStatus.getCertStatus() == 12) {
                    throw new CertPathValidatorException("Attribute certificate status could not be determined.");
                }
            }
            else if (x509AttributeCertificate.getExtensionValue(RFC3281CertPathUtilities.CRL_DISTRIBUTION_POINTS) != null || x509AttributeCertificate.getExtensionValue(RFC3281CertPathUtilities.AUTHORITY_INFO_ACCESS) != null) {
                throw new CertPathValidatorException("No rev avail extension is set, but also an AC revocation pointer.");
            }
        }
    }
    
    protected static void additionalChecks(final X509AttributeCertificate x509AttributeCertificate, final Set set, final Set set2) throws CertPathValidatorException {
        for (final String s : set) {
            if (x509AttributeCertificate.getAttributes(s) != null) {
                throw new CertPathValidatorException("Attribute certificate contains prohibited attribute: " + s + ".");
            }
        }
        for (final String s2 : set2) {
            if (x509AttributeCertificate.getAttributes(s2) == null) {
                throw new CertPathValidatorException("Attribute certificate does not contain necessary attribute: " + s2 + ".");
            }
        }
    }
    
    protected static void processAttrCert5(final X509AttributeCertificate x509AttributeCertificate, final PKIXExtendedParameters pkixExtendedParameters) throws CertPathValidatorException {
        try {
            x509AttributeCertificate.checkValidity(CertPathValidatorUtilities.getValidDate(pkixExtendedParameters));
        }
        catch (final CertificateExpiredException ex) {
            throw new ExtCertPathValidatorException("Attribute certificate is not valid.", ex);
        }
        catch (final CertificateNotYetValidException ex2) {
            throw new ExtCertPathValidatorException("Attribute certificate is not valid.", ex2);
        }
    }
    
    protected static void processAttrCert4(final X509Certificate x509Certificate, final Set set) throws CertPathValidatorException {
        boolean b = false;
        for (final TrustAnchor trustAnchor : set) {
            if (x509Certificate.getSubjectX500Principal().getName("RFC2253").equals(trustAnchor.getCAName()) || x509Certificate.equals(trustAnchor.getTrustedCert())) {
                b = true;
            }
        }
        if (!b) {
            throw new CertPathValidatorException("Attribute certificate issuer is not directly trusted.");
        }
    }
    
    protected static void processAttrCert3(final X509Certificate x509Certificate, final PKIXExtendedParameters pkixExtendedParameters) throws CertPathValidatorException {
        if (x509Certificate.getKeyUsage() != null && !x509Certificate.getKeyUsage()[0] && !x509Certificate.getKeyUsage()[1]) {
            throw new CertPathValidatorException("Attribute certificate issuer public key cannot be used to validate digital signatures.");
        }
        if (x509Certificate.getBasicConstraints() != -1) {
            throw new CertPathValidatorException("Attribute certificate issuer is also a public key certificate issuer.");
        }
    }
    
    protected static CertPathValidatorResult processAttrCert2(final CertPath certPath, final PKIXExtendedParameters pkixExtendedParameters) throws CertPathValidatorException {
        CertPathValidator instance;
        try {
            instance = CertPathValidator.getInstance("PKIX", "BC");
        }
        catch (final NoSuchProviderException ex) {
            throw new ExtCertPathValidatorException("Support class could not be created.", ex);
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new ExtCertPathValidatorException("Support class could not be created.", ex2);
        }
        try {
            return instance.validate(certPath, pkixExtendedParameters);
        }
        catch (final CertPathValidatorException ex3) {
            throw new ExtCertPathValidatorException("Certification path for issuer certificate of attribute certificate could not be validated.", ex3);
        }
        catch (final InvalidAlgorithmParameterException ex4) {
            throw new RuntimeException(ex4.getMessage());
        }
    }
    
    protected static CertPath processAttrCert1(final X509AttributeCertificate x509AttributeCertificate, final PKIXExtendedParameters pkixExtendedParameters) throws CertPathValidatorException {
        CertPathBuilderResult build = null;
        final HashSet set = new HashSet();
        if (x509AttributeCertificate.getHolder().getIssuer() != null) {
            final X509CertSelector x509CertSelector = new X509CertSelector();
            x509CertSelector.setSerialNumber(x509AttributeCertificate.getHolder().getSerialNumber());
            final Principal[] issuer = x509AttributeCertificate.getHolder().getIssuer();
            for (int i = 0; i < issuer.length; ++i) {
                try {
                    if (issuer[i] instanceof X500Principal) {
                        x509CertSelector.setIssuer(((X500Principal)issuer[i]).getEncoded());
                    }
                    set.addAll(CertPathValidatorUtilities.findCertificates(new PKIXCertStoreSelector.Builder(x509CertSelector).build(), pkixExtendedParameters.getCertStores()));
                }
                catch (final AnnotatedException ex) {
                    throw new ExtCertPathValidatorException("Public key certificate for attribute certificate cannot be searched.", ex);
                }
                catch (final IOException ex2) {
                    throw new ExtCertPathValidatorException("Unable to encode X500 principal.", ex2);
                }
            }
            if (set.isEmpty()) {
                throw new CertPathValidatorException("Public key certificate specified in base certificate ID for attribute certificate cannot be found.");
            }
        }
        if (x509AttributeCertificate.getHolder().getEntityNames() != null) {
            final X509CertStoreSelector x509CertStoreSelector = new X509CertStoreSelector();
            final Principal[] entityNames = x509AttributeCertificate.getHolder().getEntityNames();
            for (int j = 0; j < entityNames.length; ++j) {
                try {
                    if (entityNames[j] instanceof X500Principal) {
                        x509CertStoreSelector.setIssuer(((X500Principal)entityNames[j]).getEncoded());
                    }
                    set.addAll(CertPathValidatorUtilities.findCertificates(new PKIXCertStoreSelector.Builder(x509CertStoreSelector).build(), pkixExtendedParameters.getCertStores()));
                }
                catch (final AnnotatedException ex3) {
                    throw new ExtCertPathValidatorException("Public key certificate for attribute certificate cannot be searched.", ex3);
                }
                catch (final IOException ex4) {
                    throw new ExtCertPathValidatorException("Unable to encode X500 principal.", ex4);
                }
            }
            if (set.isEmpty()) {
                throw new CertPathValidatorException("Public key certificate specified in entity name for attribute certificate cannot be found.");
            }
        }
        final PKIXExtendedParameters.Builder builder = new PKIXExtendedParameters.Builder(pkixExtendedParameters);
        Object o = null;
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            final X509CertStoreSelector x509CertStoreSelector2 = new X509CertStoreSelector();
            x509CertStoreSelector2.setCertificate((X509Certificate)iterator.next());
            builder.setTargetConstraints(new PKIXCertStoreSelector.Builder(x509CertStoreSelector2).build());
            CertPathBuilder instance;
            try {
                instance = CertPathBuilder.getInstance("PKIX", "BC");
            }
            catch (final NoSuchProviderException ex5) {
                throw new ExtCertPathValidatorException("Support class could not be created.", ex5);
            }
            catch (final NoSuchAlgorithmException ex6) {
                throw new ExtCertPathValidatorException("Support class could not be created.", ex6);
            }
            try {
                build = instance.build(new PKIXExtendedBuilderParameters.Builder(builder.build()).build());
            }
            catch (final CertPathBuilderException ex7) {
                o = new ExtCertPathValidatorException("Certification path for public key certificate of attribute certificate could not be build.", ex7);
            }
            catch (final InvalidAlgorithmParameterException ex8) {
                throw new RuntimeException(ex8.getMessage());
            }
        }
        if (o != null) {
            throw o;
        }
        return build.getCertPath();
    }
    
    private static void checkCRL(final DistributionPoint distributionPoint, final X509AttributeCertificate x509AttributeCertificate, final PKIXExtendedParameters pkixExtendedParameters, final Date date, final X509Certificate x509Certificate, final CertStatus certStatus, final ReasonsMask reasonsMask, final List list, final JcaJceHelper jcaJceHelper) throws AnnotatedException {
        if (x509AttributeCertificate.getExtensionValue(X509Extensions.NoRevAvail.getId()) != null) {
            return;
        }
        final Date date2 = new Date(System.currentTimeMillis());
        if (date.getTime() > date2.getTime()) {
            throw new AnnotatedException("Validation time is in future.");
        }
        final Set completeCRLs = CertPathValidatorUtilities.getCompleteCRLs(distributionPoint, x509AttributeCertificate, date2, pkixExtendedParameters);
        boolean b = false;
        AnnotatedException ex = null;
        final Iterator iterator = completeCRLs.iterator();
        while (iterator.hasNext() && certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons()) {
            try {
                final X509CRL x509CRL = (X509CRL)iterator.next();
                final ReasonsMask processCRLD = RFC3280CertPathUtilities.processCRLD(x509CRL, distributionPoint);
                if (!processCRLD.hasNewReasons(reasonsMask)) {
                    continue;
                }
                final PublicKey processCRLG = RFC3280CertPathUtilities.processCRLG(x509CRL, RFC3280CertPathUtilities.processCRLF(x509CRL, x509AttributeCertificate, null, null, pkixExtendedParameters, list, jcaJceHelper));
                X509CRL processCRLH = null;
                if (pkixExtendedParameters.isUseDeltasEnabled()) {
                    processCRLH = RFC3280CertPathUtilities.processCRLH(CertPathValidatorUtilities.getDeltaCRLs(date2, x509CRL, pkixExtendedParameters.getCertStores(), pkixExtendedParameters.getCRLStores()), processCRLG);
                }
                if (pkixExtendedParameters.getValidityModel() != 1 && x509AttributeCertificate.getNotAfter().getTime() < x509CRL.getThisUpdate().getTime()) {
                    throw new AnnotatedException("No valid CRL for current time found.");
                }
                RFC3280CertPathUtilities.processCRLB1(distributionPoint, x509AttributeCertificate, x509CRL);
                RFC3280CertPathUtilities.processCRLB2(distributionPoint, x509AttributeCertificate, x509CRL);
                RFC3280CertPathUtilities.processCRLC(processCRLH, x509CRL, pkixExtendedParameters);
                RFC3280CertPathUtilities.processCRLI(date, processCRLH, x509AttributeCertificate, certStatus, pkixExtendedParameters);
                RFC3280CertPathUtilities.processCRLJ(date, x509CRL, x509AttributeCertificate, certStatus);
                if (certStatus.getCertStatus() == 8) {
                    certStatus.setCertStatus(11);
                }
                reasonsMask.addReasons(processCRLD);
                b = true;
            }
            catch (final AnnotatedException ex2) {
                ex = ex2;
            }
        }
        if (!b) {
            throw ex;
        }
    }
    
    static {
        TARGET_INFORMATION = Extension.targetInformation.getId();
        NO_REV_AVAIL = Extension.noRevAvail.getId();
        CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
        AUTHORITY_INFO_ACCESS = Extension.authorityInfoAccess.getId();
    }
}
