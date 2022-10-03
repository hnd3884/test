package org.bouncycastle.jce.provider;

import java.security.cert.PKIXCertPathChecker;
import java.math.BigInteger;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import org.bouncycastle.asn1.x509.ReasonFlags;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.NameConstraints;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1TaggedObject;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import java.security.GeneralSecurityException;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.x500.style.BCStyle;
import java.security.cert.PolicyNode;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.HashMap;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import java.security.cert.CertPath;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import java.security.cert.X509CRLSelector;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import java.util.Date;
import java.util.Iterator;
import java.util.Collection;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathParameters;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import java.security.cert.CertSelector;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import java.security.cert.X509CertSelector;
import java.util.Set;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.util.List;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import java.security.PublicKey;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.x509.DistributionPointName;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x509.GeneralNames;
import java.util.ArrayList;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import java.security.cert.X509Extension;
import java.security.cert.X509CRL;
import org.bouncycastle.asn1.x509.DistributionPoint;

class RFC3280CertPathUtilities
{
    private static final PKIXCRLUtil CRL_UTIL;
    public static final String CERTIFICATE_POLICIES;
    public static final String POLICY_MAPPINGS;
    public static final String INHIBIT_ANY_POLICY;
    public static final String ISSUING_DISTRIBUTION_POINT;
    public static final String FRESHEST_CRL;
    public static final String DELTA_CRL_INDICATOR;
    public static final String POLICY_CONSTRAINTS;
    public static final String BASIC_CONSTRAINTS;
    public static final String CRL_DISTRIBUTION_POINTS;
    public static final String SUBJECT_ALTERNATIVE_NAME;
    public static final String NAME_CONSTRAINTS;
    public static final String AUTHORITY_KEY_IDENTIFIER;
    public static final String KEY_USAGE;
    public static final String CRL_NUMBER;
    public static final String ANY_POLICY = "2.5.29.32.0";
    protected static final int KEY_CERT_SIGN = 5;
    protected static final int CRL_SIGN = 6;
    protected static final String[] crlReasons;
    
    protected static void processCRLB2(final DistributionPoint distributionPoint, final Object o, final X509CRL x509CRL) throws AnnotatedException {
        IssuingDistributionPoint instance;
        try {
            instance = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509CRL, RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT));
        }
        catch (final Exception ex) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", ex);
        }
        if (instance != null) {
            if (instance.getDistributionPoint() != null) {
                final DistributionPointName distributionPoint2 = IssuingDistributionPoint.getInstance(instance).getDistributionPoint();
                final ArrayList list = new ArrayList();
                if (distributionPoint2.getType() == 0) {
                    final GeneralName[] names = GeneralNames.getInstance(distributionPoint2.getName()).getNames();
                    for (int i = 0; i < names.length; ++i) {
                        list.add(names[i]);
                    }
                }
                if (distributionPoint2.getType() == 1) {
                    final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
                    try {
                        final Enumeration objects = ASN1Sequence.getInstance(PrincipalUtils.getIssuerPrincipal(x509CRL)).getObjects();
                        while (objects.hasMoreElements()) {
                            asn1EncodableVector.add((ASN1Encodable)objects.nextElement());
                        }
                    }
                    catch (final Exception ex2) {
                        throw new AnnotatedException("Could not read CRL issuer.", ex2);
                    }
                    asn1EncodableVector.add(distributionPoint2.getName());
                    list.add(new GeneralName(X500Name.getInstance(new DERSequence(asn1EncodableVector))));
                }
                int n = 0;
                if (distributionPoint.getDistributionPoint() != null) {
                    final DistributionPointName distributionPoint3 = distributionPoint.getDistributionPoint();
                    GeneralName[] array = null;
                    if (distributionPoint3.getType() == 0) {
                        array = GeneralNames.getInstance(distributionPoint3.getName()).getNames();
                    }
                    if (distributionPoint3.getType() == 1) {
                        if (distributionPoint.getCRLIssuer() != null) {
                            array = distributionPoint.getCRLIssuer().getNames();
                        }
                        else {
                            array = new GeneralName[] { null };
                            try {
                                array[0] = new GeneralName(X500Name.getInstance(PrincipalUtils.getEncodedIssuerPrincipal(o).getEncoded()));
                            }
                            catch (final Exception ex3) {
                                throw new AnnotatedException("Could not read certificate issuer.", ex3);
                            }
                        }
                        for (int j = 0; j < array.length; ++j) {
                            final Enumeration objects2 = ASN1Sequence.getInstance(array[j].getName().toASN1Primitive()).getObjects();
                            final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
                            while (objects2.hasMoreElements()) {
                                asn1EncodableVector2.add(objects2.nextElement());
                            }
                            asn1EncodableVector2.add(distributionPoint3.getName());
                            array[j] = new GeneralName(X500Name.getInstance(new DERSequence(asn1EncodableVector2)));
                        }
                    }
                    if (array != null) {
                        for (int k = 0; k < array.length; ++k) {
                            if (list.contains(array[k])) {
                                n = 1;
                                break;
                            }
                        }
                    }
                    if (n == 0) {
                        throw new AnnotatedException("No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point.");
                    }
                }
                else {
                    if (distributionPoint.getCRLIssuer() == null) {
                        throw new AnnotatedException("Either the cRLIssuer or the distributionPoint field must be contained in DistributionPoint.");
                    }
                    final GeneralName[] names2 = distributionPoint.getCRLIssuer().getNames();
                    for (int l = 0; l < names2.length; ++l) {
                        if (list.contains(names2[l])) {
                            n = 1;
                            break;
                        }
                    }
                    if (n == 0) {
                        throw new AnnotatedException("No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point.");
                    }
                }
            }
            BasicConstraints instance2;
            try {
                instance2 = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Extension)o, RFC3280CertPathUtilities.BASIC_CONSTRAINTS));
            }
            catch (final Exception ex4) {
                throw new AnnotatedException("Basic constraints extension could not be decoded.", ex4);
            }
            if (o instanceof X509Certificate) {
                if (instance.onlyContainsUserCerts() && instance2 != null && instance2.isCA()) {
                    throw new AnnotatedException("CA Cert CRL only contains user certificates.");
                }
                if (instance.onlyContainsCACerts() && (instance2 == null || !instance2.isCA())) {
                    throw new AnnotatedException("End CRL only contains CA certificates.");
                }
            }
            if (instance.onlyContainsAttributeCerts()) {
                throw new AnnotatedException("onlyContainsAttributeCerts boolean is asserted.");
            }
        }
    }
    
    protected static void processCRLB1(final DistributionPoint distributionPoint, final Object o, final X509CRL x509CRL) throws AnnotatedException {
        final ASN1Primitive extensionValue = CertPathValidatorUtilities.getExtensionValue(x509CRL, RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
        boolean b = false;
        if (extensionValue != null && IssuingDistributionPoint.getInstance(extensionValue).isIndirectCRL()) {
            b = true;
        }
        byte[] encoded;
        try {
            encoded = PrincipalUtils.getIssuerPrincipal(x509CRL).getEncoded();
        }
        catch (final IOException ex) {
            throw new AnnotatedException("Exception encoding CRL issuer: " + ex.getMessage(), ex);
        }
        int n = 0;
        if (distributionPoint.getCRLIssuer() != null) {
            final GeneralName[] names = distributionPoint.getCRLIssuer().getNames();
            for (int i = 0; i < names.length; ++i) {
                if (names[i].getTagNo() == 4) {
                    try {
                        if (Arrays.areEqual(names[i].getName().toASN1Primitive().getEncoded(), encoded)) {
                            n = 1;
                        }
                    }
                    catch (final IOException ex2) {
                        throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", ex2);
                    }
                }
            }
            if (n != 0 && !b) {
                throw new AnnotatedException("Distribution point contains cRLIssuer field but CRL is not indirect.");
            }
            if (n == 0) {
                throw new AnnotatedException("CRL issuer of CRL does not match CRL issuer of distribution point.");
            }
        }
        else if (PrincipalUtils.getIssuerPrincipal(x509CRL).equals(PrincipalUtils.getEncodedIssuerPrincipal(o))) {
            n = 1;
        }
        if (n == 0) {
            throw new AnnotatedException("Cannot find matching CRL issuer for certificate.");
        }
    }
    
    protected static ReasonsMask processCRLD(final X509CRL x509CRL, final DistributionPoint distributionPoint) throws AnnotatedException {
        IssuingDistributionPoint instance;
        try {
            instance = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509CRL, RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT));
        }
        catch (final Exception ex) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", ex);
        }
        if (instance != null && instance.getOnlySomeReasons() != null && distributionPoint.getReasons() != null) {
            return new ReasonsMask(distributionPoint.getReasons()).intersect(new ReasonsMask(instance.getOnlySomeReasons()));
        }
        if ((instance == null || instance.getOnlySomeReasons() == null) && distributionPoint.getReasons() == null) {
            return ReasonsMask.allReasons;
        }
        return ((distributionPoint.getReasons() == null) ? ReasonsMask.allReasons : new ReasonsMask(distributionPoint.getReasons())).intersect((instance == null) ? ReasonsMask.allReasons : new ReasonsMask(instance.getOnlySomeReasons()));
    }
    
    protected static Set processCRLF(final X509CRL x509CRL, final Object o, final X509Certificate x509Certificate, final PublicKey publicKey, final PKIXExtendedParameters pkixExtendedParameters, final List list, final JcaJceHelper jcaJceHelper) throws AnnotatedException {
        final X509CertSelector x509CertSelector = new X509CertSelector();
        try {
            x509CertSelector.setSubject(PrincipalUtils.getIssuerPrincipal(x509CRL).getEncoded());
        }
        catch (final IOException ex) {
            throw new AnnotatedException("Subject criteria for certificate selector to find issuer certificate for CRL could not be set.", ex);
        }
        final PKIXCertStoreSelector<? extends Certificate> build = new PKIXCertStoreSelector.Builder(x509CertSelector).build();
        Collection certificates;
        try {
            certificates = CertPathValidatorUtilities.findCertificates(build, pkixExtendedParameters.getCertificateStores());
            certificates.addAll(CertPathValidatorUtilities.findCertificates(build, pkixExtendedParameters.getCertStores()));
        }
        catch (final AnnotatedException ex2) {
            throw new AnnotatedException("Issuer certificate for CRL cannot be searched.", ex2);
        }
        certificates.add(x509Certificate);
        final Iterator iterator = certificates.iterator();
        final ArrayList list2 = new ArrayList();
        final ArrayList list3 = new ArrayList();
        while (iterator.hasNext()) {
            final X509Certificate certificate = (X509Certificate)iterator.next();
            if (certificate.equals(x509Certificate)) {
                list2.add(certificate);
                list3.add(publicKey);
            }
            else {
                try {
                    final PKIXCertPathBuilderSpi pkixCertPathBuilderSpi = new PKIXCertPathBuilderSpi();
                    final X509CertSelector x509CertSelector2 = new X509CertSelector();
                    x509CertSelector2.setCertificate(certificate);
                    final PKIXExtendedParameters.Builder setTargetConstraints = new PKIXExtendedParameters.Builder(pkixExtendedParameters).setTargetConstraints(new PKIXCertStoreSelector.Builder(x509CertSelector2).build());
                    if (list.contains(certificate)) {
                        setTargetConstraints.setRevocationEnabled(false);
                    }
                    else {
                        setTargetConstraints.setRevocationEnabled(true);
                    }
                    final List<? extends Certificate> certificates2 = pkixCertPathBuilderSpi.engineBuild(new PKIXExtendedBuilderParameters.Builder(setTargetConstraints.build()).build()).getCertPath().getCertificates();
                    list2.add(certificate);
                    list3.add(CertPathValidatorUtilities.getNextWorkingKey(certificates2, 0, jcaJceHelper));
                }
                catch (final CertPathBuilderException ex3) {
                    throw new AnnotatedException("CertPath for CRL signer failed to validate.", ex3);
                }
                catch (final CertPathValidatorException ex4) {
                    throw new AnnotatedException("Public key of issuer certificate of CRL could not be retrieved.", ex4);
                }
                catch (final Exception ex5) {
                    throw new AnnotatedException(ex5.getMessage());
                }
            }
        }
        final HashSet set = new HashSet();
        Object o2 = null;
        for (int i = 0; i < list2.size(); ++i) {
            final boolean[] keyUsage = ((X509Certificate)list2.get(i)).getKeyUsage();
            if (keyUsage != null && (keyUsage.length < 7 || !keyUsage[6])) {
                o2 = new AnnotatedException("Issuer certificate key usage extension does not permit CRL signing.");
            }
            else {
                set.add(list3.get(i));
            }
        }
        if (set.isEmpty() && o2 == null) {
            throw new AnnotatedException("Cannot find a valid issuer certificate.");
        }
        if (set.isEmpty() && o2 != null) {
            throw o2;
        }
        return set;
    }
    
    protected static PublicKey processCRLG(final X509CRL x509CRL, final Set set) throws AnnotatedException {
        Throwable t = null;
        for (final PublicKey publicKey : set) {
            try {
                x509CRL.verify(publicKey);
                return publicKey;
            }
            catch (final Exception ex) {
                t = ex;
                continue;
            }
            break;
        }
        throw new AnnotatedException("Cannot verify CRL.", t);
    }
    
    protected static X509CRL processCRLH(final Set set, final PublicKey publicKey) throws AnnotatedException {
        Throwable t = null;
        for (final X509CRL x509CRL : set) {
            try {
                x509CRL.verify(publicKey);
                return x509CRL;
            }
            catch (final Exception ex) {
                t = ex;
                continue;
            }
            break;
        }
        if (t != null) {
            throw new AnnotatedException("Cannot verify delta CRL.", t);
        }
        return null;
    }
    
    protected static Set processCRLA1i(final Date date, final PKIXExtendedParameters pkixExtendedParameters, final X509Certificate x509Certificate, final X509CRL x509CRL) throws AnnotatedException {
        final HashSet set = new HashSet();
        if (pkixExtendedParameters.isUseDeltasEnabled()) {
            CRLDistPoint crlDistPoint;
            try {
                crlDistPoint = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.FRESHEST_CRL));
            }
            catch (final AnnotatedException ex) {
                throw new AnnotatedException("Freshest CRL extension could not be decoded from certificate.", ex);
            }
            if (crlDistPoint == null) {
                try {
                    crlDistPoint = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509CRL, RFC3280CertPathUtilities.FRESHEST_CRL));
                }
                catch (final AnnotatedException ex2) {
                    throw new AnnotatedException("Freshest CRL extension could not be decoded from CRL.", ex2);
                }
            }
            if (crlDistPoint != null) {
                final ArrayList list = new ArrayList();
                list.addAll(pkixExtendedParameters.getCRLStores());
                try {
                    list.addAll(CertPathValidatorUtilities.getAdditionalStoresFromCRLDistributionPoint(crlDistPoint, pkixExtendedParameters.getNamedCRLStoreMap()));
                }
                catch (final AnnotatedException ex3) {
                    throw new AnnotatedException("No new delta CRL locations could be added from Freshest CRL extension.", ex3);
                }
                try {
                    set.addAll(CertPathValidatorUtilities.getDeltaCRLs(date, x509CRL, pkixExtendedParameters.getCertStores(), list));
                }
                catch (final AnnotatedException ex4) {
                    throw new AnnotatedException("Exception obtaining delta CRLs.", ex4);
                }
            }
        }
        return set;
    }
    
    protected static Set[] processCRLA1ii(final Date date, final PKIXExtendedParameters pkixExtendedParameters, final X509Certificate certificateChecking, final X509CRL x509CRL) throws AnnotatedException {
        final HashSet set = new HashSet();
        final X509CRLSelector x509CRLSelector = new X509CRLSelector();
        x509CRLSelector.setCertificateChecking(certificateChecking);
        try {
            x509CRLSelector.addIssuerName(PrincipalUtils.getIssuerPrincipal(x509CRL).getEncoded());
        }
        catch (final IOException ex) {
            throw new AnnotatedException("Cannot extract issuer from CRL." + ex, ex);
        }
        final PKIXCRLStoreSelector<? extends CRL> build = new PKIXCRLStoreSelector.Builder(x509CRLSelector).setCompleteCRLEnabled(true).build();
        Date date2 = date;
        if (pkixExtendedParameters.getDate() != null) {
            date2 = pkixExtendedParameters.getDate();
        }
        final Set crLs = RFC3280CertPathUtilities.CRL_UTIL.findCRLs(build, date2, pkixExtendedParameters.getCertStores(), pkixExtendedParameters.getCRLStores());
        if (pkixExtendedParameters.isUseDeltasEnabled()) {
            try {
                set.addAll(CertPathValidatorUtilities.getDeltaCRLs(date2, x509CRL, pkixExtendedParameters.getCertStores(), pkixExtendedParameters.getCRLStores()));
            }
            catch (final AnnotatedException ex2) {
                throw new AnnotatedException("Exception obtaining delta CRLs.", ex2);
            }
        }
        return new Set[] { crLs, set };
    }
    
    protected static void processCRLC(final X509CRL x509CRL, final X509CRL x509CRL2, final PKIXExtendedParameters pkixExtendedParameters) throws AnnotatedException {
        if (x509CRL == null) {
            return;
        }
        IssuingDistributionPoint instance;
        try {
            instance = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509CRL2, RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT));
        }
        catch (final Exception ex) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", ex);
        }
        if (pkixExtendedParameters.isUseDeltasEnabled()) {
            if (!PrincipalUtils.getIssuerPrincipal(x509CRL).equals(PrincipalUtils.getIssuerPrincipal(x509CRL2))) {
                throw new AnnotatedException("Complete CRL issuer does not match delta CRL issuer.");
            }
            IssuingDistributionPoint instance2;
            try {
                instance2 = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509CRL, RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT));
            }
            catch (final Exception ex2) {
                throw new AnnotatedException("Issuing distribution point extension from delta CRL could not be decoded.", ex2);
            }
            boolean b = false;
            if (instance == null) {
                if (instance2 == null) {
                    b = true;
                }
            }
            else if (instance.equals(instance2)) {
                b = true;
            }
            if (!b) {
                throw new AnnotatedException("Issuing distribution point extension from delta CRL and complete CRL does not match.");
            }
            ASN1Primitive extensionValue;
            try {
                extensionValue = CertPathValidatorUtilities.getExtensionValue(x509CRL2, RFC3280CertPathUtilities.AUTHORITY_KEY_IDENTIFIER);
            }
            catch (final AnnotatedException ex3) {
                throw new AnnotatedException("Authority key identifier extension could not be extracted from complete CRL.", ex3);
            }
            ASN1Primitive extensionValue2;
            try {
                extensionValue2 = CertPathValidatorUtilities.getExtensionValue(x509CRL, RFC3280CertPathUtilities.AUTHORITY_KEY_IDENTIFIER);
            }
            catch (final AnnotatedException ex4) {
                throw new AnnotatedException("Authority key identifier extension could not be extracted from delta CRL.", ex4);
            }
            if (extensionValue == null) {
                throw new AnnotatedException("CRL authority key identifier is null.");
            }
            if (extensionValue2 == null) {
                throw new AnnotatedException("Delta CRL authority key identifier is null.");
            }
            if (!extensionValue.equals(extensionValue2)) {
                throw new AnnotatedException("Delta CRL authority key identifier does not match complete CRL authority key identifier.");
            }
        }
    }
    
    protected static void processCRLI(final Date date, final X509CRL x509CRL, final Object o, final CertStatus certStatus, final PKIXExtendedParameters pkixExtendedParameters) throws AnnotatedException {
        if (pkixExtendedParameters.isUseDeltasEnabled() && x509CRL != null) {
            CertPathValidatorUtilities.getCertStatus(date, x509CRL, o, certStatus);
        }
    }
    
    protected static void processCRLJ(final Date date, final X509CRL x509CRL, final Object o, final CertStatus certStatus) throws AnnotatedException {
        if (certStatus.getCertStatus() == 11) {
            CertPathValidatorUtilities.getCertStatus(date, x509CRL, o, certStatus);
        }
    }
    
    protected static PKIXPolicyNode prepareCertB(final CertPath certPath, final int n, final List[] array, final PKIXPolicyNode pkixPolicyNode, final int n2) throws CertPathValidatorException {
        final List<? extends Certificate> certificates = certPath.getCertificates();
        final X509Certificate x509Certificate = certificates.get(n);
        final int n3 = certificates.size() - n;
        ASN1Sequence instance;
        try {
            instance = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.POLICY_MAPPINGS));
        }
        catch (final AnnotatedException ex) {
            throw new ExtCertPathValidatorException("Policy mappings extension could not be decoded.", ex, certPath, n);
        }
        PKIXPolicyNode removePolicyNode = pkixPolicyNode;
        if (instance != null) {
            final ASN1Sequence asn1Sequence = instance;
            final HashMap hashMap = new HashMap();
            final HashSet set = new HashSet();
            for (int i = 0; i < asn1Sequence.size(); ++i) {
                final ASN1Sequence asn1Sequence2 = (ASN1Sequence)asn1Sequence.getObjectAt(i);
                final String id = ((ASN1ObjectIdentifier)asn1Sequence2.getObjectAt(0)).getId();
                final String id2 = ((ASN1ObjectIdentifier)asn1Sequence2.getObjectAt(1)).getId();
                if (!hashMap.containsKey(id)) {
                    final HashSet set2 = new HashSet();
                    set2.add(id2);
                    hashMap.put(id, set2);
                    set.add(id);
                }
                else {
                    ((Set)hashMap.get(id)).add(id2);
                }
            }
            for (final String s : set) {
                if (n2 > 0) {
                    boolean b = false;
                    for (final PKIXPolicyNode pkixPolicyNode2 : array[n3]) {
                        if (pkixPolicyNode2.getValidPolicy().equals(s)) {
                            b = true;
                            pkixPolicyNode2.expectedPolicies = (Set)hashMap.get(s);
                            break;
                        }
                    }
                    if (b) {
                        continue;
                    }
                    for (final PKIXPolicyNode pkixPolicyNode3 : array[n3]) {
                        if ("2.5.29.32.0".equals(pkixPolicyNode3.getValidPolicy())) {
                            Set qualifierSet = null;
                            ASN1Sequence asn1Sequence3;
                            try {
                                asn1Sequence3 = (ASN1Sequence)CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
                            }
                            catch (final AnnotatedException ex2) {
                                throw new ExtCertPathValidatorException("Certificate policies extension could not be decoded.", ex2, certPath, n);
                            }
                            final Enumeration objects = asn1Sequence3.getObjects();
                            while (objects.hasMoreElements()) {
                                PolicyInformation instance2;
                                try {
                                    instance2 = PolicyInformation.getInstance(objects.nextElement());
                                }
                                catch (final Exception ex3) {
                                    throw new CertPathValidatorException("Policy information could not be decoded.", ex3, certPath, n);
                                }
                                if ("2.5.29.32.0".equals(instance2.getPolicyIdentifier().getId())) {
                                    try {
                                        qualifierSet = CertPathValidatorUtilities.getQualifierSet(instance2.getPolicyQualifiers());
                                        break;
                                    }
                                    catch (final CertPathValidatorException ex4) {
                                        throw new ExtCertPathValidatorException("Policy qualifier info set could not be decoded.", ex4, certPath, n);
                                    }
                                }
                            }
                            boolean contains = false;
                            if (x509Certificate.getCriticalExtensionOIDs() != null) {
                                contains = x509Certificate.getCriticalExtensionOIDs().contains(RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
                            }
                            final PKIXPolicyNode pkixPolicyNode4 = (PKIXPolicyNode)pkixPolicyNode3.getParent();
                            if ("2.5.29.32.0".equals(pkixPolicyNode4.getValidPolicy())) {
                                final PKIXPolicyNode pkixPolicyNode5 = new PKIXPolicyNode(new ArrayList(), n3, (Set)hashMap.get(s), pkixPolicyNode4, qualifierSet, s, contains);
                                pkixPolicyNode4.addChild(pkixPolicyNode5);
                                array[n3].add(pkixPolicyNode5);
                                break;
                            }
                            break;
                        }
                    }
                }
                else {
                    if (n2 > 0) {
                        continue;
                    }
                    final Iterator iterator4 = array[n3].iterator();
                    while (iterator4.hasNext()) {
                        final PKIXPolicyNode pkixPolicyNode6 = (PKIXPolicyNode)iterator4.next();
                        if (pkixPolicyNode6.getValidPolicy().equals(s)) {
                            ((PKIXPolicyNode)pkixPolicyNode6.getParent()).removeChild(pkixPolicyNode6);
                            iterator4.remove();
                            for (int j = n3 - 1; j >= 0; --j) {
                                final List list = array[j];
                                for (int k = 0; k < list.size(); ++k) {
                                    final PKIXPolicyNode pkixPolicyNode7 = list.get(k);
                                    if (!pkixPolicyNode7.hasChildren()) {
                                        removePolicyNode = CertPathValidatorUtilities.removePolicyNode(removePolicyNode, array, pkixPolicyNode7);
                                        if (removePolicyNode == null) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return removePolicyNode;
    }
    
    protected static void prepareNextCertA(final CertPath certPath, final int n) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certPath.getCertificates().get(n);
        ASN1Sequence instance;
        try {
            instance = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.POLICY_MAPPINGS));
        }
        catch (final AnnotatedException ex) {
            throw new ExtCertPathValidatorException("Policy mappings extension could not be decoded.", ex, certPath, n);
        }
        if (instance != null) {
            final ASN1Sequence asn1Sequence = instance;
            for (int i = 0; i < asn1Sequence.size(); ++i) {
                ASN1ObjectIdentifier instance3;
                ASN1ObjectIdentifier instance4;
                try {
                    final ASN1Sequence instance2 = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(i));
                    instance3 = ASN1ObjectIdentifier.getInstance(instance2.getObjectAt(0));
                    instance4 = ASN1ObjectIdentifier.getInstance(instance2.getObjectAt(1));
                }
                catch (final Exception ex2) {
                    throw new ExtCertPathValidatorException("Policy mappings extension contents could not be decoded.", ex2, certPath, n);
                }
                if ("2.5.29.32.0".equals(instance3.getId())) {
                    throw new CertPathValidatorException("IssuerDomainPolicy is anyPolicy", null, certPath, n);
                }
                if ("2.5.29.32.0".equals(instance4.getId())) {
                    throw new CertPathValidatorException("SubjectDomainPolicy is anyPolicy,", null, certPath, n);
                }
            }
        }
    }
    
    protected static void processCertF(final CertPath certPath, final int n, final PKIXPolicyNode pkixPolicyNode, final int n2) throws CertPathValidatorException {
        if (n2 <= 0 && pkixPolicyNode == null) {
            throw new ExtCertPathValidatorException("No valid policy tree found when one expected.", null, certPath, n);
        }
    }
    
    protected static PKIXPolicyNode processCertE(final CertPath certPath, final int n, PKIXPolicyNode pkixPolicyNode) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certPath.getCertificates().get(n);
        ASN1Sequence instance;
        try {
            instance = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.CERTIFICATE_POLICIES));
        }
        catch (final AnnotatedException ex) {
            throw new ExtCertPathValidatorException("Could not read certificate policies extension from certificate.", ex, certPath, n);
        }
        if (instance == null) {
            pkixPolicyNode = null;
        }
        return pkixPolicyNode;
    }
    
    protected static void processCertBC(final CertPath certPath, final int n, final PKIXNameConstraintValidator pkixNameConstraintValidator) throws CertPathValidatorException {
        final List<? extends Certificate> certificates = certPath.getCertificates();
        final X509Certificate x509Certificate = certificates.get(n);
        final int size = certificates.size();
        final int n2 = size - n;
        if (!CertPathValidatorUtilities.isSelfIssued(x509Certificate) || n2 >= size) {
            final X500Name subjectPrincipal = PrincipalUtils.getSubjectPrincipal(x509Certificate);
            ASN1Sequence instance;
            try {
                instance = ASN1Sequence.getInstance(subjectPrincipal.getEncoded());
            }
            catch (final Exception ex) {
                throw new CertPathValidatorException("Exception extracting subject name when checking subtrees.", ex, certPath, n);
            }
            try {
                pkixNameConstraintValidator.checkPermittedDN(instance);
                pkixNameConstraintValidator.checkExcludedDN(instance);
            }
            catch (final PKIXNameConstraintValidatorException ex2) {
                throw new CertPathValidatorException("Subtree check for certificate subject failed.", ex2, certPath, n);
            }
            GeneralNames instance2;
            try {
                instance2 = GeneralNames.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME));
            }
            catch (final Exception ex3) {
                throw new CertPathValidatorException("Subject alternative name extension could not be decoded.", ex3, certPath, n);
            }
            final RDN[] rdNs = X500Name.getInstance(instance).getRDNs(BCStyle.EmailAddress);
            for (int i = 0; i != rdNs.length; ++i) {
                final GeneralName generalName = new GeneralName(1, ((ASN1String)rdNs[i].getFirst().getValue()).getString());
                try {
                    pkixNameConstraintValidator.checkPermitted(generalName);
                    pkixNameConstraintValidator.checkExcluded(generalName);
                }
                catch (final PKIXNameConstraintValidatorException ex4) {
                    throw new CertPathValidatorException("Subtree check for certificate subject alternative email failed.", ex4, certPath, n);
                }
            }
            if (instance2 != null) {
                GeneralName[] names;
                try {
                    names = instance2.getNames();
                }
                catch (final Exception ex5) {
                    throw new CertPathValidatorException("Subject alternative name contents could not be decoded.", ex5, certPath, n);
                }
                for (int j = 0; j < names.length; ++j) {
                    try {
                        pkixNameConstraintValidator.checkPermitted(names[j]);
                        pkixNameConstraintValidator.checkExcluded(names[j]);
                    }
                    catch (final PKIXNameConstraintValidatorException ex6) {
                        throw new CertPathValidatorException("Subtree check for certificate subject alternative name failed.", ex6, certPath, n);
                    }
                }
            }
        }
    }
    
    protected static PKIXPolicyNode processCertD(final CertPath certPath, final int n, final Set set, final PKIXPolicyNode pkixPolicyNode, final List[] array, final int n2) throws CertPathValidatorException {
        final List<? extends Certificate> certificates = certPath.getCertificates();
        final X509Certificate x509Certificate = certificates.get(n);
        final int size = certificates.size();
        final int n3 = size - n;
        ASN1Sequence instance;
        try {
            instance = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.CERTIFICATE_POLICIES));
        }
        catch (final AnnotatedException ex) {
            throw new ExtCertPathValidatorException("Could not read certificate policies extension from certificate.", ex, certPath, n);
        }
        if (instance != null && pkixPolicyNode != null) {
            final Enumeration objects = instance.getObjects();
            final HashSet set2 = new HashSet();
            while (objects.hasMoreElements()) {
                final PolicyInformation instance2 = PolicyInformation.getInstance(objects.nextElement());
                final ASN1ObjectIdentifier policyIdentifier = instance2.getPolicyIdentifier();
                set2.add(policyIdentifier.getId());
                if (!"2.5.29.32.0".equals(policyIdentifier.getId())) {
                    Set qualifierSet;
                    try {
                        qualifierSet = CertPathValidatorUtilities.getQualifierSet(instance2.getPolicyQualifiers());
                    }
                    catch (final CertPathValidatorException ex2) {
                        throw new ExtCertPathValidatorException("Policy qualifier info set could not be build.", ex2, certPath, n);
                    }
                    if (CertPathValidatorUtilities.processCertD1i(n3, array, policyIdentifier, qualifierSet)) {
                        continue;
                    }
                    CertPathValidatorUtilities.processCertD1ii(n3, array, policyIdentifier, qualifierSet);
                }
            }
            if (set.isEmpty() || set.contains("2.5.29.32.0")) {
                set.clear();
                set.addAll(set2);
            }
            else {
                final Iterator iterator = set.iterator();
                final HashSet set3 = new HashSet();
                while (iterator.hasNext()) {
                    final Object next = iterator.next();
                    if (set2.contains(next)) {
                        set3.add(next);
                    }
                }
                set.clear();
                set.addAll(set3);
            }
            if (n2 > 0 || (n3 < size && CertPathValidatorUtilities.isSelfIssued(x509Certificate))) {
                final Enumeration objects2 = instance.getObjects();
                while (objects2.hasMoreElements()) {
                    final PolicyInformation instance3 = PolicyInformation.getInstance(objects2.nextElement());
                    if ("2.5.29.32.0".equals(instance3.getPolicyIdentifier().getId())) {
                        final Set qualifierSet2 = CertPathValidatorUtilities.getQualifierSet(instance3.getPolicyQualifiers());
                        final List list = array[n3 - 1];
                        for (int i = 0; i < list.size(); ++i) {
                            final PKIXPolicyNode pkixPolicyNode2 = list.get(i);
                            for (final Object next2 : pkixPolicyNode2.getExpectedPolicies()) {
                                String id;
                                if (next2 instanceof String) {
                                    id = (String)next2;
                                }
                                else {
                                    if (!(next2 instanceof ASN1ObjectIdentifier)) {
                                        continue;
                                    }
                                    id = ((ASN1ObjectIdentifier)next2).getId();
                                }
                                boolean b = false;
                                final Iterator children = pkixPolicyNode2.getChildren();
                                while (children.hasNext()) {
                                    if (id.equals(((PKIXPolicyNode)children.next()).getValidPolicy())) {
                                        b = true;
                                    }
                                }
                                if (!b) {
                                    final HashSet set4 = new HashSet();
                                    set4.add(id);
                                    final PKIXPolicyNode pkixPolicyNode3 = new PKIXPolicyNode(new ArrayList(), n3, set4, pkixPolicyNode2, qualifierSet2, id, false);
                                    pkixPolicyNode2.addChild(pkixPolicyNode3);
                                    array[n3].add(pkixPolicyNode3);
                                }
                            }
                        }
                        break;
                    }
                }
            }
            PKIXPolicyNode removePolicyNode = pkixPolicyNode;
            for (int j = n3 - 1; j >= 0; --j) {
                final List list2 = array[j];
                for (int k = 0; k < list2.size(); ++k) {
                    final PKIXPolicyNode pkixPolicyNode4 = list2.get(k);
                    if (!pkixPolicyNode4.hasChildren()) {
                        removePolicyNode = CertPathValidatorUtilities.removePolicyNode(removePolicyNode, array, pkixPolicyNode4);
                        if (removePolicyNode == null) {
                            break;
                        }
                    }
                }
            }
            final Set<String> criticalExtensionOIDs = x509Certificate.getCriticalExtensionOIDs();
            if (criticalExtensionOIDs != null) {
                final boolean contains = criticalExtensionOIDs.contains(RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
                final List list3 = array[n3];
                for (int l = 0; l < list3.size(); ++l) {
                    ((PKIXPolicyNode)list3.get(l)).setCritical(contains);
                }
            }
            return removePolicyNode;
        }
        return null;
    }
    
    protected static void processCertA(final CertPath certPath, final PKIXExtendedParameters pkixExtendedParameters, final int n, final PublicKey publicKey, final boolean b, final X500Name x500Name, final X509Certificate x509Certificate, final JcaJceHelper jcaJceHelper) throws ExtCertPathValidatorException {
        final List<? extends Certificate> certificates = certPath.getCertificates();
        final X509Certificate x509Certificate2 = (X509Certificate)certificates.get(n);
        if (!b) {
            try {
                CertPathValidatorUtilities.verifyX509Certificate(x509Certificate2, publicKey, pkixExtendedParameters.getSigProvider());
            }
            catch (final GeneralSecurityException ex) {
                throw new ExtCertPathValidatorException("Could not validate certificate signature.", ex, certPath, n);
            }
        }
        try {
            x509Certificate2.checkValidity(CertPathValidatorUtilities.getValidCertDateFromValidityModel(pkixExtendedParameters, certPath, n));
        }
        catch (final CertificateExpiredException ex2) {
            throw new ExtCertPathValidatorException("Could not validate certificate: " + ex2.getMessage(), ex2, certPath, n);
        }
        catch (final CertificateNotYetValidException ex3) {
            throw new ExtCertPathValidatorException("Could not validate certificate: " + ex3.getMessage(), ex3, certPath, n);
        }
        catch (final AnnotatedException ex4) {
            throw new ExtCertPathValidatorException("Could not validate time of certificate.", ex4, certPath, n);
        }
        if (pkixExtendedParameters.isRevocationEnabled()) {
            try {
                checkCRLs(pkixExtendedParameters, x509Certificate2, CertPathValidatorUtilities.getValidCertDateFromValidityModel(pkixExtendedParameters, certPath, n), x509Certificate, publicKey, certificates, jcaJceHelper);
            }
            catch (final AnnotatedException ex5) {
                Throwable cause = ex5;
                if (null != ex5.getCause()) {
                    cause = ex5.getCause();
                }
                throw new ExtCertPathValidatorException(ex5.getMessage(), cause, certPath, n);
            }
        }
        if (!PrincipalUtils.getEncodedIssuerPrincipal(x509Certificate2).equals(x500Name)) {
            throw new ExtCertPathValidatorException("IssuerName(" + PrincipalUtils.getEncodedIssuerPrincipal(x509Certificate2) + ") does not match SubjectName(" + x500Name + ") of signing certificate.", null, certPath, n);
        }
    }
    
    protected static int prepareNextCertI1(final CertPath certPath, final int n, final int n2) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certPath.getCertificates().get(n);
        ASN1Sequence instance;
        try {
            instance = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.POLICY_CONSTRAINTS));
        }
        catch (final Exception ex) {
            throw new ExtCertPathValidatorException("Policy constraints extension cannot be decoded.", ex, certPath, n);
        }
        if (instance != null) {
            final Enumeration objects = instance.getObjects();
            while (objects.hasMoreElements()) {
                try {
                    final ASN1TaggedObject instance2 = ASN1TaggedObject.getInstance(objects.nextElement());
                    if (instance2.getTagNo() != 0) {
                        continue;
                    }
                    final int intValue = ASN1Integer.getInstance(instance2, false).getValue().intValue();
                    if (intValue < n2) {
                        return intValue;
                    }
                }
                catch (final IllegalArgumentException ex2) {
                    throw new ExtCertPathValidatorException("Policy constraints extension contents cannot be decoded.", ex2, certPath, n);
                }
                break;
            }
        }
        return n2;
    }
    
    protected static int prepareNextCertI2(final CertPath certPath, final int n, final int n2) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certPath.getCertificates().get(n);
        ASN1Sequence instance;
        try {
            instance = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.POLICY_CONSTRAINTS));
        }
        catch (final Exception ex) {
            throw new ExtCertPathValidatorException("Policy constraints extension cannot be decoded.", ex, certPath, n);
        }
        if (instance != null) {
            final Enumeration objects = instance.getObjects();
            while (objects.hasMoreElements()) {
                try {
                    final ASN1TaggedObject instance2 = ASN1TaggedObject.getInstance(objects.nextElement());
                    if (instance2.getTagNo() != 1) {
                        continue;
                    }
                    final int intValue = ASN1Integer.getInstance(instance2, false).getValue().intValue();
                    if (intValue < n2) {
                        return intValue;
                    }
                }
                catch (final IllegalArgumentException ex2) {
                    throw new ExtCertPathValidatorException("Policy constraints extension contents cannot be decoded.", ex2, certPath, n);
                }
                break;
            }
        }
        return n2;
    }
    
    protected static void prepareNextCertG(final CertPath certPath, final int n, final PKIXNameConstraintValidator pkixNameConstraintValidator) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certPath.getCertificates().get(n);
        NameConstraints instance = null;
        try {
            final ASN1Sequence instance2 = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.NAME_CONSTRAINTS));
            if (instance2 != null) {
                instance = NameConstraints.getInstance(instance2);
            }
        }
        catch (final Exception ex) {
            throw new ExtCertPathValidatorException("Name constraints extension could not be decoded.", ex, certPath, n);
        }
        if (instance != null) {
            final GeneralSubtree[] permittedSubtrees = instance.getPermittedSubtrees();
            if (permittedSubtrees != null) {
                try {
                    pkixNameConstraintValidator.intersectPermittedSubtree(permittedSubtrees);
                }
                catch (final Exception ex2) {
                    throw new ExtCertPathValidatorException("Permitted subtrees cannot be build from name constraints extension.", ex2, certPath, n);
                }
            }
            final GeneralSubtree[] excludedSubtrees = instance.getExcludedSubtrees();
            if (excludedSubtrees != null) {
                for (int i = 0; i != excludedSubtrees.length; ++i) {
                    try {
                        pkixNameConstraintValidator.addExcludedSubtree(excludedSubtrees[i]);
                    }
                    catch (final Exception ex3) {
                        throw new ExtCertPathValidatorException("Excluded subtrees cannot be build from name constraints extension.", ex3, certPath, n);
                    }
                }
            }
        }
    }
    
    private static void checkCRL(final DistributionPoint distributionPoint, final PKIXExtendedParameters pkixExtendedParameters, final X509Certificate x509Certificate, final Date date, final X509Certificate x509Certificate2, final PublicKey publicKey, final CertStatus certStatus, final ReasonsMask reasonsMask, final List list, final JcaJceHelper jcaJceHelper) throws AnnotatedException {
        final Date date2 = new Date(System.currentTimeMillis());
        if (date.getTime() > date2.getTime()) {
            throw new AnnotatedException("Validation time is in future.");
        }
        final Set completeCRLs = CertPathValidatorUtilities.getCompleteCRLs(distributionPoint, x509Certificate, date2, pkixExtendedParameters);
        boolean b = false;
        AnnotatedException ex = null;
        final Iterator iterator = completeCRLs.iterator();
        while (iterator.hasNext() && certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons()) {
            try {
                final X509CRL x509CRL = (X509CRL)iterator.next();
                final ReasonsMask processCRLD = processCRLD(x509CRL, distributionPoint);
                if (!processCRLD.hasNewReasons(reasonsMask)) {
                    continue;
                }
                final PublicKey processCRLG = processCRLG(x509CRL, processCRLF(x509CRL, x509Certificate, x509Certificate2, publicKey, pkixExtendedParameters, list, jcaJceHelper));
                X509CRL processCRLH = null;
                Date date3 = date2;
                if (pkixExtendedParameters.getDate() != null) {
                    date3 = pkixExtendedParameters.getDate();
                }
                if (pkixExtendedParameters.isUseDeltasEnabled()) {
                    processCRLH = processCRLH(CertPathValidatorUtilities.getDeltaCRLs(date3, x509CRL, pkixExtendedParameters.getCertStores(), pkixExtendedParameters.getCRLStores()), processCRLG);
                }
                if (pkixExtendedParameters.getValidityModel() != 1 && x509Certificate.getNotAfter().getTime() < x509CRL.getThisUpdate().getTime()) {
                    throw new AnnotatedException("No valid CRL for current time found.");
                }
                processCRLB1(distributionPoint, x509Certificate, x509CRL);
                processCRLB2(distributionPoint, x509Certificate, x509CRL);
                processCRLC(processCRLH, x509CRL, pkixExtendedParameters);
                processCRLI(date, processCRLH, x509Certificate, certStatus, pkixExtendedParameters);
                processCRLJ(date, x509CRL, x509Certificate, certStatus);
                if (certStatus.getCertStatus() == 8) {
                    certStatus.setCertStatus(11);
                }
                reasonsMask.addReasons(processCRLD);
                final Set<String> criticalExtensionOIDs = x509CRL.getCriticalExtensionOIDs();
                if (criticalExtensionOIDs != null) {
                    final HashSet set = new HashSet(criticalExtensionOIDs);
                    set.remove(Extension.issuingDistributionPoint.getId());
                    set.remove(Extension.deltaCRLIndicator.getId());
                    if (!set.isEmpty()) {
                        throw new AnnotatedException("CRL contains unsupported critical extensions.");
                    }
                }
                if (processCRLH != null) {
                    final Set<String> criticalExtensionOIDs2 = processCRLH.getCriticalExtensionOIDs();
                    if (criticalExtensionOIDs2 != null) {
                        final HashSet set2 = new HashSet(criticalExtensionOIDs2);
                        set2.remove(Extension.issuingDistributionPoint.getId());
                        set2.remove(Extension.deltaCRLIndicator.getId());
                        if (!set2.isEmpty()) {
                            throw new AnnotatedException("Delta CRL contains unsupported critical extension.");
                        }
                    }
                }
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
    
    protected static void checkCRLs(final PKIXExtendedParameters pkixExtendedParameters, final X509Certificate x509Certificate, final Date date, final X509Certificate x509Certificate2, final PublicKey publicKey, final List list, final JcaJceHelper jcaJceHelper) throws AnnotatedException {
        Throwable t = null;
        CRLDistPoint instance;
        try {
            instance = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.CRL_DISTRIBUTION_POINTS));
        }
        catch (final Exception ex) {
            throw new AnnotatedException("CRL distribution point extension could not be read.", ex);
        }
        final PKIXExtendedParameters.Builder builder = new PKIXExtendedParameters.Builder(pkixExtendedParameters);
        try {
            final Iterator<PKIXCRLStore> iterator = (Iterator<PKIXCRLStore>)CertPathValidatorUtilities.getAdditionalStoresFromCRLDistributionPoint(instance, pkixExtendedParameters.getNamedCRLStoreMap()).iterator();
            while (iterator.hasNext()) {
                builder.addCRLStore(iterator.next());
            }
        }
        catch (final AnnotatedException ex2) {
            throw new AnnotatedException("No additional CRL locations could be decoded from CRL distribution point extension.", ex2);
        }
        final CertStatus certStatus = new CertStatus();
        final ReasonsMask reasonsMask = new ReasonsMask();
        final PKIXExtendedParameters build = builder.build();
        boolean b = false;
        if (instance != null) {
            DistributionPoint[] distributionPoints;
            try {
                distributionPoints = instance.getDistributionPoints();
            }
            catch (final Exception ex3) {
                throw new AnnotatedException("Distribution points could not be read.", ex3);
            }
            if (distributionPoints != null) {
                for (int n = 0; n < distributionPoints.length && certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons(); ++n) {
                    try {
                        checkCRL(distributionPoints[n], build, x509Certificate, date, x509Certificate2, publicKey, certStatus, reasonsMask, list, jcaJceHelper);
                        b = true;
                    }
                    catch (final AnnotatedException ex4) {
                        t = ex4;
                    }
                }
            }
        }
        if (certStatus.getCertStatus() == 11 && !reasonsMask.isAllReasons()) {
            try {
                ASN1Primitive object;
                try {
                    object = new ASN1InputStream(PrincipalUtils.getEncodedIssuerPrincipal(x509Certificate).getEncoded()).readObject();
                }
                catch (final Exception ex5) {
                    throw new AnnotatedException("Issuer from certificate for CRL could not be reencoded.", ex5);
                }
                checkCRL(new DistributionPoint(new DistributionPointName(0, new GeneralNames(new GeneralName(4, object))), null, null), (PKIXExtendedParameters)pkixExtendedParameters.clone(), x509Certificate, date, x509Certificate2, publicKey, certStatus, reasonsMask, list, jcaJceHelper);
                b = true;
            }
            catch (final AnnotatedException ex6) {
                t = ex6;
            }
        }
        if (!b) {
            if (t instanceof AnnotatedException) {
                throw t;
            }
            throw new AnnotatedException("No valid CRL found.", t);
        }
        else {
            if (certStatus.getCertStatus() != 11) {
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                throw new AnnotatedException("Certificate revocation after " + simpleDateFormat.format(certStatus.getRevocationDate()) + ", reason: " + RFC3280CertPathUtilities.crlReasons[certStatus.getCertStatus()]);
            }
            if (!reasonsMask.isAllReasons() && certStatus.getCertStatus() == 11) {
                certStatus.setCertStatus(12);
            }
            if (certStatus.getCertStatus() == 12) {
                throw new AnnotatedException("Certificate status could not be determined.");
            }
        }
    }
    
    protected static int prepareNextCertJ(final CertPath certPath, final int n, final int n2) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certPath.getCertificates().get(n);
        ASN1Integer instance;
        try {
            instance = ASN1Integer.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.INHIBIT_ANY_POLICY));
        }
        catch (final Exception ex) {
            throw new ExtCertPathValidatorException("Inhibit any-policy extension cannot be decoded.", ex, certPath, n);
        }
        if (instance != null) {
            final int intValue = instance.getValue().intValue();
            if (intValue < n2) {
                return intValue;
            }
        }
        return n2;
    }
    
    protected static void prepareNextCertK(final CertPath certPath, final int n) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certPath.getCertificates().get(n);
        BasicConstraints instance;
        try {
            instance = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.BASIC_CONSTRAINTS));
        }
        catch (final Exception ex) {
            throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", ex, certPath, n);
        }
        if (instance == null) {
            throw new CertPathValidatorException("Intermediate certificate lacks BasicConstraints");
        }
        if (!instance.isCA()) {
            throw new CertPathValidatorException("Not a CA certificate");
        }
    }
    
    protected static int prepareNextCertL(final CertPath certPath, final int n, final int n2) throws CertPathValidatorException {
        if (CertPathValidatorUtilities.isSelfIssued((X509Certificate)certPath.getCertificates().get(n))) {
            return n2;
        }
        if (n2 <= 0) {
            throw new ExtCertPathValidatorException("Max path length not greater than zero", null, certPath, n);
        }
        return n2 - 1;
    }
    
    protected static int prepareNextCertM(final CertPath certPath, final int n, final int n2) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certPath.getCertificates().get(n);
        BasicConstraints instance;
        try {
            instance = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.BASIC_CONSTRAINTS));
        }
        catch (final Exception ex) {
            throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", ex, certPath, n);
        }
        if (instance != null) {
            final BigInteger pathLenConstraint = instance.getPathLenConstraint();
            if (pathLenConstraint != null) {
                final int intValue = pathLenConstraint.intValue();
                if (intValue < n2) {
                    return intValue;
                }
            }
        }
        return n2;
    }
    
    protected static void prepareNextCertN(final CertPath certPath, final int n) throws CertPathValidatorException {
        final boolean[] keyUsage = ((X509Certificate)certPath.getCertificates().get(n)).getKeyUsage();
        if (keyUsage != null && !keyUsage[5]) {
            throw new ExtCertPathValidatorException("Issuer certificate keyusage extension is critical and does not permit key signing.", null, certPath, n);
        }
    }
    
    protected static void prepareNextCertO(final CertPath certPath, final int n, final Set set, final List list) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certPath.getCertificates().get(n);
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            try {
                ((PKIXCertPathChecker)iterator.next()).check(x509Certificate, set);
                continue;
            }
            catch (final CertPathValidatorException ex) {
                throw new CertPathValidatorException(ex.getMessage(), ex.getCause(), certPath, n);
            }
            break;
        }
        if (!set.isEmpty()) {
            throw new ExtCertPathValidatorException("Certificate has unsupported critical extension: " + set, null, certPath, n);
        }
    }
    
    protected static int prepareNextCertH1(final CertPath certPath, final int n, final int n2) {
        if (!CertPathValidatorUtilities.isSelfIssued((X509Certificate)certPath.getCertificates().get(n)) && n2 != 0) {
            return n2 - 1;
        }
        return n2;
    }
    
    protected static int prepareNextCertH2(final CertPath certPath, final int n, final int n2) {
        if (!CertPathValidatorUtilities.isSelfIssued((X509Certificate)certPath.getCertificates().get(n)) && n2 != 0) {
            return n2 - 1;
        }
        return n2;
    }
    
    protected static int prepareNextCertH3(final CertPath certPath, final int n, final int n2) {
        if (!CertPathValidatorUtilities.isSelfIssued((X509Certificate)certPath.getCertificates().get(n)) && n2 != 0) {
            return n2 - 1;
        }
        return n2;
    }
    
    protected static int wrapupCertA(int n, final X509Certificate x509Certificate) {
        if (!CertPathValidatorUtilities.isSelfIssued(x509Certificate) && n != 0) {
            --n;
        }
        return n;
    }
    
    protected static int wrapupCertB(final CertPath certPath, final int n, final int n2) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certPath.getCertificates().get(n);
        ASN1Sequence instance;
        try {
            instance = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, RFC3280CertPathUtilities.POLICY_CONSTRAINTS));
        }
        catch (final AnnotatedException ex) {
            throw new ExtCertPathValidatorException("Policy constraints could not be decoded.", ex, certPath, n);
        }
        if (instance != null) {
            final Enumeration objects = instance.getObjects();
            while (objects.hasMoreElements()) {
                final ASN1TaggedObject asn1TaggedObject = objects.nextElement();
                switch (asn1TaggedObject.getTagNo()) {
                    case 0: {
                        int intValue;
                        try {
                            intValue = ASN1Integer.getInstance(asn1TaggedObject, false).getValue().intValue();
                        }
                        catch (final Exception ex2) {
                            throw new ExtCertPathValidatorException("Policy constraints requireExplicitPolicy field could not be decoded.", ex2, certPath, n);
                        }
                        if (intValue == 0) {
                            return 0;
                        }
                        continue;
                    }
                }
            }
        }
        return n2;
    }
    
    protected static void wrapupCertF(final CertPath certPath, final int n, final List list, final Set set) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certPath.getCertificates().get(n);
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            try {
                ((PKIXCertPathChecker)iterator.next()).check(x509Certificate, set);
                continue;
            }
            catch (final CertPathValidatorException ex) {
                throw new ExtCertPathValidatorException("Additional certificate path checker failed.", ex, certPath, n);
            }
            break;
        }
        if (!set.isEmpty()) {
            throw new ExtCertPathValidatorException("Certificate has unsupported critical extension: " + set, null, certPath, n);
        }
    }
    
    protected static PKIXPolicyNode wrapupCertG(final CertPath certPath, final PKIXExtendedParameters pkixExtendedParameters, final Set set, final int n, final List[] array, PKIXPolicyNode pkixPolicyNode, final Set set2) throws CertPathValidatorException {
        final int size = certPath.getCertificates().size();
        PKIXPolicyNode pkixPolicyNode2;
        if (pkixPolicyNode == null) {
            if (pkixExtendedParameters.isExplicitPolicyRequired()) {
                throw new ExtCertPathValidatorException("Explicit policy requested but none available.", null, certPath, n);
            }
            pkixPolicyNode2 = null;
        }
        else if (CertPathValidatorUtilities.isAnyPolicy(set)) {
            if (pkixExtendedParameters.isExplicitPolicyRequired()) {
                if (set2.isEmpty()) {
                    throw new ExtCertPathValidatorException("Explicit policy requested but none available.", null, certPath, n);
                }
                final HashSet set3 = new HashSet();
                for (int i = 0; i < array.length; ++i) {
                    final List list = array[i];
                    for (int j = 0; j < list.size(); ++j) {
                        final PKIXPolicyNode pkixPolicyNode3 = list.get(j);
                        if ("2.5.29.32.0".equals(pkixPolicyNode3.getValidPolicy())) {
                            final Iterator children = pkixPolicyNode3.getChildren();
                            while (children.hasNext()) {
                                set3.add(children.next());
                            }
                        }
                    }
                }
                final Iterator iterator = set3.iterator();
                while (iterator.hasNext()) {
                    if (!set2.contains(((PKIXPolicyNode)iterator.next()).getValidPolicy())) {}
                }
                if (pkixPolicyNode != null) {
                    for (int k = size - 1; k >= 0; --k) {
                        final List list2 = array[k];
                        for (int l = 0; l < list2.size(); ++l) {
                            final PKIXPolicyNode pkixPolicyNode4 = list2.get(l);
                            if (!pkixPolicyNode4.hasChildren()) {
                                pkixPolicyNode = CertPathValidatorUtilities.removePolicyNode(pkixPolicyNode, array, pkixPolicyNode4);
                            }
                        }
                    }
                }
            }
            pkixPolicyNode2 = pkixPolicyNode;
        }
        else {
            final HashSet set4 = new HashSet();
            for (int n2 = 0; n2 < array.length; ++n2) {
                final List list3 = array[n2];
                for (int n3 = 0; n3 < list3.size(); ++n3) {
                    final PKIXPolicyNode pkixPolicyNode5 = list3.get(n3);
                    if ("2.5.29.32.0".equals(pkixPolicyNode5.getValidPolicy())) {
                        final Iterator children2 = pkixPolicyNode5.getChildren();
                        while (children2.hasNext()) {
                            final PKIXPolicyNode pkixPolicyNode6 = children2.next();
                            if (!"2.5.29.32.0".equals(pkixPolicyNode6.getValidPolicy())) {
                                set4.add(pkixPolicyNode6);
                            }
                        }
                    }
                }
            }
            for (final PKIXPolicyNode pkixPolicyNode7 : set4) {
                if (!set.contains(pkixPolicyNode7.getValidPolicy())) {
                    pkixPolicyNode = CertPathValidatorUtilities.removePolicyNode(pkixPolicyNode, array, pkixPolicyNode7);
                }
            }
            if (pkixPolicyNode != null) {
                for (int n4 = size - 1; n4 >= 0; --n4) {
                    final List list4 = array[n4];
                    for (int n5 = 0; n5 < list4.size(); ++n5) {
                        final PKIXPolicyNode pkixPolicyNode8 = list4.get(n5);
                        if (!pkixPolicyNode8.hasChildren()) {
                            pkixPolicyNode = CertPathValidatorUtilities.removePolicyNode(pkixPolicyNode, array, pkixPolicyNode8);
                        }
                    }
                }
            }
            pkixPolicyNode2 = pkixPolicyNode;
        }
        return pkixPolicyNode2;
    }
    
    static {
        CRL_UTIL = new PKIXCRLUtil();
        CERTIFICATE_POLICIES = Extension.certificatePolicies.getId();
        POLICY_MAPPINGS = Extension.policyMappings.getId();
        INHIBIT_ANY_POLICY = Extension.inhibitAnyPolicy.getId();
        ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();
        FRESHEST_CRL = Extension.freshestCRL.getId();
        DELTA_CRL_INDICATOR = Extension.deltaCRLIndicator.getId();
        POLICY_CONSTRAINTS = Extension.policyConstraints.getId();
        BASIC_CONSTRAINTS = Extension.basicConstraints.getId();
        CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
        SUBJECT_ALTERNATIVE_NAME = Extension.subjectAlternativeName.getId();
        NAME_CONSTRAINTS = Extension.nameConstraints.getId();
        AUTHORITY_KEY_IDENTIFIER = Extension.authorityKeyIdentifier.getId();
        KEY_USAGE = Extension.keyUsage.getId();
        CRL_NUMBER = Extension.cRLNumber.getId();
        crlReasons = new String[] { "unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", "aACompromise" };
    }
}
