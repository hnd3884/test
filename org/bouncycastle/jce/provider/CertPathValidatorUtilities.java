package org.bouncycastle.jce.provider;

import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.x509.X509AttributeCertificate;
import java.security.GeneralSecurityException;
import java.security.cert.CertSelector;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import java.security.interfaces.DSAParams;
import java.security.spec.KeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.interfaces.DSAPublicKey;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.text.ParseException;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.isismtt.ISISMTTObjectIdentifiers;
import java.security.cert.CertPath;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.asn1.ASN1Integer;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509CRLEntry;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.x509.Extension;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.math.BigInteger;
import java.security.cert.X509CRLSelector;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import java.security.cert.CertStoreException;
import java.security.cert.CertStore;
import org.bouncycastle.util.StoreException;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import java.util.LinkedHashSet;
import java.util.Collection;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.asn1.x509.PolicyInformation;
import java.security.cert.PolicyNode;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Enumeration;
import java.security.cert.PolicyQualifierInfo;
import org.bouncycastle.asn1.ASN1Encodable;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import org.bouncycastle.asn1.ASN1Sequence;
import java.security.cert.CertPathValidatorException;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.cert.X509Extension;
import java.util.Date;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import java.security.cert.CertificateParsingException;
import java.util.Collections;
import java.util.ArrayList;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.ASN1OctetString;
import java.util.List;
import org.bouncycastle.jcajce.PKIXCertStore;
import org.bouncycastle.asn1.x509.GeneralName;
import java.util.Map;
import java.util.Iterator;
import org.bouncycastle.asn1.x500.X500Name;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.io.IOException;
import java.security.cert.X509CertSelector;
import java.security.cert.TrustAnchor;
import java.util.Set;
import java.security.cert.X509Certificate;

class CertPathValidatorUtilities
{
    protected static final PKIXCRLUtil CRL_UTIL;
    protected static final String CERTIFICATE_POLICIES;
    protected static final String BASIC_CONSTRAINTS;
    protected static final String POLICY_MAPPINGS;
    protected static final String SUBJECT_ALTERNATIVE_NAME;
    protected static final String NAME_CONSTRAINTS;
    protected static final String KEY_USAGE;
    protected static final String INHIBIT_ANY_POLICY;
    protected static final String ISSUING_DISTRIBUTION_POINT;
    protected static final String DELTA_CRL_INDICATOR;
    protected static final String POLICY_CONSTRAINTS;
    protected static final String FRESHEST_CRL;
    protected static final String CRL_DISTRIBUTION_POINTS;
    protected static final String AUTHORITY_KEY_IDENTIFIER;
    protected static final String ANY_POLICY = "2.5.29.32.0";
    protected static final String CRL_NUMBER;
    protected static final int KEY_CERT_SIGN = 5;
    protected static final int CRL_SIGN = 6;
    protected static final String[] crlReasons;
    
    protected static TrustAnchor findTrustAnchor(final X509Certificate x509Certificate, final Set set) throws AnnotatedException {
        return findTrustAnchor(x509Certificate, set, null);
    }
    
    protected static TrustAnchor findTrustAnchor(final X509Certificate x509Certificate, final Set set, final String s) throws AnnotatedException {
        TrustAnchor trustAnchor = null;
        PublicKey publicKey = null;
        Throwable t = null;
        final X509CertSelector x509CertSelector = new X509CertSelector();
        final X500Name encodedIssuerPrincipal = PrincipalUtils.getEncodedIssuerPrincipal(x509Certificate);
        try {
            x509CertSelector.setSubject(encodedIssuerPrincipal.getEncoded());
        }
        catch (final IOException ex) {
            throw new AnnotatedException("Cannot set subject search criteria for trust anchor.", ex);
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext() && trustAnchor == null) {
            trustAnchor = (TrustAnchor)iterator.next();
            if (trustAnchor.getTrustedCert() != null) {
                if (x509CertSelector.match(trustAnchor.getTrustedCert())) {
                    publicKey = trustAnchor.getTrustedCert().getPublicKey();
                }
                else {
                    trustAnchor = null;
                }
            }
            else if (trustAnchor.getCAName() != null && trustAnchor.getCAPublicKey() != null) {
                try {
                    if (encodedIssuerPrincipal.equals(PrincipalUtils.getCA(trustAnchor))) {
                        publicKey = trustAnchor.getCAPublicKey();
                    }
                    else {
                        trustAnchor = null;
                    }
                }
                catch (final IllegalArgumentException ex2) {
                    trustAnchor = null;
                }
            }
            else {
                trustAnchor = null;
            }
            if (publicKey != null) {
                try {
                    verifyX509Certificate(x509Certificate, publicKey, s);
                }
                catch (final Exception ex3) {
                    t = ex3;
                    trustAnchor = null;
                    publicKey = null;
                }
            }
        }
        if (trustAnchor == null && t != null) {
            throw new AnnotatedException("TrustAnchor found but certificate validation failed.", t);
        }
        return trustAnchor;
    }
    
    static boolean isIssuerTrustAnchor(final X509Certificate x509Certificate, final Set set, final String s) throws AnnotatedException {
        try {
            return findTrustAnchor(x509Certificate, set, s) != null;
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    static List<PKIXCertStore> getAdditionalStoresFromAltNames(final byte[] array, final Map<GeneralName, PKIXCertStore> map) throws CertificateParsingException {
        if (array != null) {
            final GeneralName[] names = GeneralNames.getInstance(ASN1OctetString.getInstance(array).getOctets()).getNames();
            final ArrayList list = new ArrayList();
            for (int i = 0; i != names.length; ++i) {
                final PKIXCertStore pkixCertStore = map.get(names[i]);
                if (pkixCertStore != null) {
                    list.add(pkixCertStore);
                }
            }
            return list;
        }
        return Collections.EMPTY_LIST;
    }
    
    protected static Date getValidDate(final PKIXExtendedParameters pkixExtendedParameters) {
        Date date = pkixExtendedParameters.getDate();
        if (date == null) {
            date = new Date();
        }
        return date;
    }
    
    protected static boolean isSelfIssued(final X509Certificate x509Certificate) {
        return x509Certificate.getSubjectDN().equals(x509Certificate.getIssuerDN());
    }
    
    protected static ASN1Primitive getExtensionValue(final X509Extension x509Extension, final String s) throws AnnotatedException {
        final byte[] extensionValue = x509Extension.getExtensionValue(s);
        if (extensionValue == null) {
            return null;
        }
        return getObject(s, extensionValue);
    }
    
    private static ASN1Primitive getObject(final String s, final byte[] array) throws AnnotatedException {
        try {
            return new ASN1InputStream(((ASN1OctetString)new ASN1InputStream(array).readObject()).getOctets()).readObject();
        }
        catch (final Exception ex) {
            throw new AnnotatedException("exception processing extension " + s, ex);
        }
    }
    
    protected static AlgorithmIdentifier getAlgorithmIdentifier(final PublicKey publicKey) throws CertPathValidatorException {
        try {
            return SubjectPublicKeyInfo.getInstance(new ASN1InputStream(publicKey.getEncoded()).readObject()).getAlgorithm();
        }
        catch (final Exception ex) {
            throw new ExtCertPathValidatorException("Subject public key cannot be decoded.", ex);
        }
    }
    
    protected static final Set getQualifierSet(final ASN1Sequence asn1Sequence) throws CertPathValidatorException {
        final HashSet set = new HashSet();
        if (asn1Sequence == null) {
            return set;
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ASN1OutputStream asn1OutputStream = new ASN1OutputStream(byteArrayOutputStream);
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            try {
                asn1OutputStream.writeObject((ASN1Encodable)objects.nextElement());
                set.add(new PolicyQualifierInfo(byteArrayOutputStream.toByteArray()));
            }
            catch (final IOException ex) {
                throw new ExtCertPathValidatorException("Policy qualifier info cannot be decoded.", ex);
            }
            byteArrayOutputStream.reset();
        }
        return set;
    }
    
    protected static PKIXPolicyNode removePolicyNode(final PKIXPolicyNode pkixPolicyNode, final List[] array, final PKIXPolicyNode pkixPolicyNode2) {
        final PKIXPolicyNode pkixPolicyNode3 = (PKIXPolicyNode)pkixPolicyNode2.getParent();
        if (pkixPolicyNode == null) {
            return null;
        }
        if (pkixPolicyNode3 == null) {
            for (int i = 0; i < array.length; ++i) {
                array[i] = new ArrayList();
            }
            return null;
        }
        pkixPolicyNode3.removeChild(pkixPolicyNode2);
        removePolicyNodeRecurse(array, pkixPolicyNode2);
        return pkixPolicyNode;
    }
    
    private static void removePolicyNodeRecurse(final List[] array, final PKIXPolicyNode pkixPolicyNode) {
        array[pkixPolicyNode.getDepth()].remove(pkixPolicyNode);
        if (pkixPolicyNode.hasChildren()) {
            final Iterator children = pkixPolicyNode.getChildren();
            while (children.hasNext()) {
                removePolicyNodeRecurse(array, (PKIXPolicyNode)children.next());
            }
        }
    }
    
    protected static boolean processCertD1i(final int n, final List[] array, final ASN1ObjectIdentifier asn1ObjectIdentifier, final Set set) {
        final List list = array[n - 1];
        for (int i = 0; i < list.size(); ++i) {
            final PKIXPolicyNode pkixPolicyNode = list.get(i);
            if (pkixPolicyNode.getExpectedPolicies().contains(asn1ObjectIdentifier.getId())) {
                final HashSet set2 = new HashSet();
                set2.add(asn1ObjectIdentifier.getId());
                final PKIXPolicyNode pkixPolicyNode2 = new PKIXPolicyNode(new ArrayList(), n, set2, pkixPolicyNode, set, asn1ObjectIdentifier.getId(), false);
                pkixPolicyNode.addChild(pkixPolicyNode2);
                array[n].add(pkixPolicyNode2);
                return true;
            }
        }
        return false;
    }
    
    protected static void processCertD1ii(final int n, final List[] array, final ASN1ObjectIdentifier asn1ObjectIdentifier, final Set set) {
        final List list = array[n - 1];
        for (int i = 0; i < list.size(); ++i) {
            final PKIXPolicyNode pkixPolicyNode = list.get(i);
            if ("2.5.29.32.0".equals(pkixPolicyNode.getValidPolicy())) {
                final HashSet set2 = new HashSet();
                set2.add(asn1ObjectIdentifier.getId());
                final PKIXPolicyNode pkixPolicyNode2 = new PKIXPolicyNode(new ArrayList(), n, set2, pkixPolicyNode, set, asn1ObjectIdentifier.getId(), false);
                pkixPolicyNode.addChild(pkixPolicyNode2);
                array[n].add(pkixPolicyNode2);
                return;
            }
        }
    }
    
    protected static void prepareNextCertB1(final int n, final List[] array, final String s, final Map map, final X509Certificate x509Certificate) throws AnnotatedException, CertPathValidatorException {
        boolean b = false;
        for (final PKIXPolicyNode pkixPolicyNode : array[n]) {
            if (pkixPolicyNode.getValidPolicy().equals(s)) {
                b = true;
                pkixPolicyNode.expectedPolicies = map.get(s);
                break;
            }
        }
        if (!b) {
            for (final PKIXPolicyNode pkixPolicyNode2 : array[n]) {
                if ("2.5.29.32.0".equals(pkixPolicyNode2.getValidPolicy())) {
                    Set qualifierSet = null;
                    ASN1Sequence instance;
                    try {
                        instance = ASN1Sequence.getInstance(getExtensionValue(x509Certificate, CertPathValidatorUtilities.CERTIFICATE_POLICIES));
                    }
                    catch (final Exception ex) {
                        throw new AnnotatedException("Certificate policies cannot be decoded.", ex);
                    }
                    final Enumeration objects = instance.getObjects();
                    while (objects.hasMoreElements()) {
                        PolicyInformation instance2;
                        try {
                            instance2 = PolicyInformation.getInstance(objects.nextElement());
                        }
                        catch (final Exception ex2) {
                            throw new AnnotatedException("Policy information cannot be decoded.", ex2);
                        }
                        if ("2.5.29.32.0".equals(instance2.getPolicyIdentifier().getId())) {
                            try {
                                qualifierSet = getQualifierSet(instance2.getPolicyQualifiers());
                                break;
                            }
                            catch (final CertPathValidatorException ex3) {
                                throw new ExtCertPathValidatorException("Policy qualifier info set could not be built.", ex3);
                            }
                        }
                    }
                    boolean contains = false;
                    if (x509Certificate.getCriticalExtensionOIDs() != null) {
                        contains = x509Certificate.getCriticalExtensionOIDs().contains(CertPathValidatorUtilities.CERTIFICATE_POLICIES);
                    }
                    final PKIXPolicyNode pkixPolicyNode3 = (PKIXPolicyNode)pkixPolicyNode2.getParent();
                    if ("2.5.29.32.0".equals(pkixPolicyNode3.getValidPolicy())) {
                        final PKIXPolicyNode pkixPolicyNode4 = new PKIXPolicyNode(new ArrayList(), n, map.get(s), pkixPolicyNode3, qualifierSet, s, contains);
                        pkixPolicyNode3.addChild(pkixPolicyNode4);
                        array[n].add(pkixPolicyNode4);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    protected static PKIXPolicyNode prepareNextCertB2(final int n, final List[] array, final String s, PKIXPolicyNode removePolicyNode) {
        final Iterator iterator = array[n].iterator();
        while (iterator.hasNext()) {
            final PKIXPolicyNode pkixPolicyNode = (PKIXPolicyNode)iterator.next();
            if (pkixPolicyNode.getValidPolicy().equals(s)) {
                ((PKIXPolicyNode)pkixPolicyNode.getParent()).removeChild(pkixPolicyNode);
                iterator.remove();
                for (int i = n - 1; i >= 0; --i) {
                    final List list = array[i];
                    for (int j = 0; j < list.size(); ++j) {
                        final PKIXPolicyNode pkixPolicyNode2 = list.get(j);
                        if (!pkixPolicyNode2.hasChildren()) {
                            removePolicyNode = removePolicyNode(removePolicyNode, array, pkixPolicyNode2);
                            if (removePolicyNode == null) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return removePolicyNode;
    }
    
    protected static boolean isAnyPolicy(final Set set) {
        return set == null || set.contains("2.5.29.32.0") || set.isEmpty();
    }
    
    protected static Collection findCertificates(final PKIXCertStoreSelector pkixCertStoreSelector, final List list) throws AnnotatedException {
        final LinkedHashSet set = new LinkedHashSet();
        for (final Object next : list) {
            if (next instanceof Store) {
                final Store store = (Store)next;
                try {
                    set.addAll(store.getMatches(pkixCertStoreSelector));
                }
                catch (final StoreException ex) {
                    throw new AnnotatedException("Problem while picking certificates from X.509 store.", ex);
                }
            }
            else {
                final CertStore certStore = (CertStore)next;
                try {
                    set.addAll(PKIXCertStoreSelector.getCertificates(pkixCertStoreSelector, certStore));
                }
                catch (final CertStoreException ex2) {
                    throw new AnnotatedException("Problem while picking certificates from certificate store.", ex2);
                }
            }
        }
        return set;
    }
    
    static List<PKIXCRLStore> getAdditionalStoresFromCRLDistributionPoint(final CRLDistPoint crlDistPoint, final Map<GeneralName, PKIXCRLStore> map) throws AnnotatedException {
        if (crlDistPoint != null) {
            DistributionPoint[] distributionPoints;
            try {
                distributionPoints = crlDistPoint.getDistributionPoints();
            }
            catch (final Exception ex) {
                throw new AnnotatedException("Distribution points could not be read.", ex);
            }
            final ArrayList list = new ArrayList();
            for (int i = 0; i < distributionPoints.length; ++i) {
                final DistributionPointName distributionPoint = distributionPoints[i].getDistributionPoint();
                if (distributionPoint != null && distributionPoint.getType() == 0) {
                    final GeneralName[] names = GeneralNames.getInstance(distributionPoint.getName()).getNames();
                    for (int j = 0; j < names.length; ++j) {
                        final PKIXCRLStore pkixcrlStore = map.get(names[j]);
                        if (pkixcrlStore != null) {
                            list.add(pkixcrlStore);
                        }
                    }
                }
            }
            return list;
        }
        return Collections.EMPTY_LIST;
    }
    
    protected static void getCRLIssuersFromDistributionPoint(final DistributionPoint distributionPoint, final Collection collection, final X509CRLSelector x509CRLSelector) throws AnnotatedException {
        final ArrayList list = new ArrayList();
        if (distributionPoint.getCRLIssuer() != null) {
            final GeneralName[] names = distributionPoint.getCRLIssuer().getNames();
            for (int i = 0; i < names.length; ++i) {
                if (names[i].getTagNo() == 4) {
                    try {
                        list.add(X500Name.getInstance(names[i].getName().toASN1Primitive().getEncoded()));
                    }
                    catch (final IOException ex) {
                        throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", ex);
                    }
                }
            }
        }
        else {
            if (distributionPoint.getDistributionPoint() == null) {
                throw new AnnotatedException("CRL issuer is omitted from distribution point but no distributionPoint field present.");
            }
            final Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
        }
        final Iterator iterator2 = list.iterator();
        while (iterator2.hasNext()) {
            try {
                x509CRLSelector.addIssuerName(((X500Name)iterator2.next()).getEncoded());
                continue;
            }
            catch (final IOException ex2) {
                throw new AnnotatedException("Cannot decode CRL issuer information.", ex2);
            }
            break;
        }
    }
    
    private static BigInteger getSerialNumber(final Object o) {
        return ((X509Certificate)o).getSerialNumber();
    }
    
    protected static void getCertStatus(final Date date, final X509CRL x509CRL, final Object o, final CertStatus certStatus) throws AnnotatedException {
        boolean indirectCRL;
        try {
            indirectCRL = X509CRLObject.isIndirectCRL(x509CRL);
        }
        catch (final CRLException ex) {
            throw new AnnotatedException("Failed check for indirect CRL.", ex);
        }
        X509CRLEntry x509CRLEntry;
        if (indirectCRL) {
            x509CRLEntry = x509CRL.getRevokedCertificate(getSerialNumber(o));
            if (x509CRLEntry == null) {
                return;
            }
            final X500Principal certificateIssuer = x509CRLEntry.getCertificateIssuer();
            X500Name x500Name;
            if (certificateIssuer == null) {
                x500Name = PrincipalUtils.getIssuerPrincipal(x509CRL);
            }
            else {
                x500Name = X500Name.getInstance(certificateIssuer.getEncoded());
            }
            if (!PrincipalUtils.getEncodedIssuerPrincipal(o).equals(x500Name)) {
                return;
            }
        }
        else {
            if (!PrincipalUtils.getEncodedIssuerPrincipal(o).equals(PrincipalUtils.getIssuerPrincipal(x509CRL))) {
                return;
            }
            x509CRLEntry = x509CRL.getRevokedCertificate(getSerialNumber(o));
            if (x509CRLEntry == null) {
                return;
            }
        }
        ASN1Enumerated instance = null;
        if (x509CRLEntry.hasExtensions()) {
            try {
                instance = ASN1Enumerated.getInstance(getExtensionValue(x509CRLEntry, Extension.reasonCode.getId()));
            }
            catch (final Exception ex2) {
                throw new AnnotatedException("Reason code CRL entry extension could not be decoded.", ex2);
            }
        }
        if (date.getTime() >= x509CRLEntry.getRevocationDate().getTime() || instance == null || instance.getValue().intValue() == 0 || instance.getValue().intValue() == 1 || instance.getValue().intValue() == 2 || instance.getValue().intValue() == 8) {
            if (instance != null) {
                certStatus.setCertStatus(instance.getValue().intValue());
            }
            else {
                certStatus.setCertStatus(0);
            }
            certStatus.setRevocationDate(x509CRLEntry.getRevocationDate());
        }
    }
    
    protected static Set getDeltaCRLs(final Date date, final X509CRL x509CRL, final List<CertStore> list, final List<PKIXCRLStore> list2) throws AnnotatedException {
        final X509CRLSelector x509CRLSelector = new X509CRLSelector();
        try {
            x509CRLSelector.addIssuerName(PrincipalUtils.getIssuerPrincipal(x509CRL).getEncoded());
        }
        catch (final IOException ex) {
            throw new AnnotatedException("Cannot extract issuer from CRL.", ex);
        }
        BigInteger positiveValue = null;
        try {
            final ASN1Primitive extensionValue = getExtensionValue(x509CRL, CertPathValidatorUtilities.CRL_NUMBER);
            if (extensionValue != null) {
                positiveValue = ASN1Integer.getInstance(extensionValue).getPositiveValue();
            }
        }
        catch (final Exception ex2) {
            throw new AnnotatedException("CRL number extension could not be extracted from CRL.", ex2);
        }
        byte[] extensionValue2;
        try {
            extensionValue2 = x509CRL.getExtensionValue(CertPathValidatorUtilities.ISSUING_DISTRIBUTION_POINT);
        }
        catch (final Exception ex3) {
            throw new AnnotatedException("Issuing distribution point extension value could not be read.", ex3);
        }
        x509CRLSelector.setMinCRLNumber((positiveValue == null) ? null : positiveValue.add(BigInteger.valueOf(1L)));
        final PKIXCRLStoreSelector.Builder builder = new PKIXCRLStoreSelector.Builder(x509CRLSelector);
        builder.setIssuingDistributionPoint(extensionValue2);
        builder.setIssuingDistributionPointEnabled(true);
        builder.setMaxBaseCRLNumber(positiveValue);
        final Set crLs = CertPathValidatorUtilities.CRL_UTIL.findCRLs(builder.build(), date, list, list2);
        final HashSet set = new HashSet();
        for (final X509CRL x509CRL2 : crLs) {
            if (isDeltaCRL(x509CRL2)) {
                set.add(x509CRL2);
            }
        }
        return set;
    }
    
    private static boolean isDeltaCRL(final X509CRL x509CRL) {
        final Set<String> criticalExtensionOIDs = x509CRL.getCriticalExtensionOIDs();
        return criticalExtensionOIDs != null && criticalExtensionOIDs.contains(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
    }
    
    protected static Set getCompleteCRLs(final DistributionPoint distributionPoint, final Object o, final Date date, final PKIXExtendedParameters pkixExtendedParameters) throws AnnotatedException {
        final X509CRLSelector x509CRLSelector = new X509CRLSelector();
        try {
            final HashSet set = new HashSet();
            set.add(PrincipalUtils.getEncodedIssuerPrincipal(o));
            getCRLIssuersFromDistributionPoint(distributionPoint, set, x509CRLSelector);
        }
        catch (final AnnotatedException ex) {
            throw new AnnotatedException("Could not get issuer information from distribution point.", ex);
        }
        if (o instanceof X509Certificate) {
            x509CRLSelector.setCertificateChecking((X509Certificate)o);
        }
        final PKIXCRLStoreSelector<? extends CRL> build = new PKIXCRLStoreSelector.Builder(x509CRLSelector).setCompleteCRLEnabled(true).build();
        Date date2 = date;
        if (pkixExtendedParameters.getDate() != null) {
            date2 = pkixExtendedParameters.getDate();
        }
        final Set crLs = CertPathValidatorUtilities.CRL_UTIL.findCRLs(build, date2, pkixExtendedParameters.getCertStores(), pkixExtendedParameters.getCRLStores());
        checkCRLsNotEmpty(crLs, o);
        return crLs;
    }
    
    protected static Date getValidCertDateFromValidityModel(final PKIXExtendedParameters pkixExtendedParameters, final CertPath certPath, final int n) throws AnnotatedException {
        if (pkixExtendedParameters.getValidityModel() != 1) {
            return getValidDate(pkixExtendedParameters);
        }
        if (n <= 0) {
            return getValidDate(pkixExtendedParameters);
        }
        if (n - 1 == 0) {
            ASN1GeneralizedTime instance = null;
            try {
                final byte[] extensionValue = ((X509Certificate)certPath.getCertificates().get(n - 1)).getExtensionValue(ISISMTTObjectIdentifiers.id_isismtt_at_dateOfCertGen.getId());
                if (extensionValue != null) {
                    instance = ASN1GeneralizedTime.getInstance(ASN1Primitive.fromByteArray(extensionValue));
                }
            }
            catch (final IOException ex) {
                throw new AnnotatedException("Date of cert gen extension could not be read.");
            }
            catch (final IllegalArgumentException ex2) {
                throw new AnnotatedException("Date of cert gen extension could not be read.");
            }
            if (instance != null) {
                try {
                    return instance.getDate();
                }
                catch (final ParseException ex3) {
                    throw new AnnotatedException("Date from date of cert gen extension could not be parsed.", ex3);
                }
            }
            return ((X509Certificate)certPath.getCertificates().get(n - 1)).getNotBefore();
        }
        return ((X509Certificate)certPath.getCertificates().get(n - 1)).getNotBefore();
    }
    
    protected static PublicKey getNextWorkingKey(final List list, final int n, final JcaJceHelper jcaJceHelper) throws CertPathValidatorException {
        final PublicKey publicKey = list.get(n).getPublicKey();
        if (!(publicKey instanceof DSAPublicKey)) {
            return publicKey;
        }
        final DSAPublicKey dsaPublicKey = (DSAPublicKey)publicKey;
        if (dsaPublicKey.getParams() != null) {
            return dsaPublicKey;
        }
        for (int i = n + 1; i < list.size(); ++i) {
            final PublicKey publicKey2 = list.get(i).getPublicKey();
            if (!(publicKey2 instanceof DSAPublicKey)) {
                throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
            }
            final DSAPublicKey dsaPublicKey2 = (DSAPublicKey)publicKey2;
            if (dsaPublicKey2.getParams() != null) {
                final DSAParams params = dsaPublicKey2.getParams();
                final DSAPublicKeySpec dsaPublicKeySpec = new DSAPublicKeySpec(dsaPublicKey.getY(), params.getP(), params.getQ(), params.getG());
                try {
                    return jcaJceHelper.createKeyFactory("DSA").generatePublic(dsaPublicKeySpec);
                }
                catch (final Exception ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
        }
        throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
    }
    
    static Collection findIssuerCerts(final X509Certificate x509Certificate, final List<CertStore> list, final List<PKIXCertStore> list2) throws AnnotatedException {
        final X509CertSelector x509CertSelector = new X509CertSelector();
        try {
            x509CertSelector.setSubject(PrincipalUtils.getIssuerPrincipal(x509Certificate).getEncoded());
        }
        catch (final IOException ex) {
            throw new AnnotatedException("Subject criteria for certificate selector to find issuer certificate could not be set.", ex);
        }
        try {
            final byte[] extensionValue = x509Certificate.getExtensionValue(CertPathValidatorUtilities.AUTHORITY_KEY_IDENTIFIER);
            if (extensionValue != null) {
                final byte[] keyIdentifier = AuthorityKeyIdentifier.getInstance(ASN1OctetString.getInstance(extensionValue).getOctets()).getKeyIdentifier();
                if (keyIdentifier != null) {
                    x509CertSelector.setSubjectKeyIdentifier(new DEROctetString(keyIdentifier).getEncoded());
                }
            }
        }
        catch (final Exception ex2) {}
        final PKIXCertStoreSelector<? extends Certificate> build = new PKIXCertStoreSelector.Builder(x509CertSelector).build();
        final LinkedHashSet set = new LinkedHashSet();
        Iterator iterator;
        try {
            final ArrayList list3 = new ArrayList();
            list3.addAll(findCertificates(build, list));
            list3.addAll(findCertificates(build, list2));
            iterator = list3.iterator();
        }
        catch (final AnnotatedException ex3) {
            throw new AnnotatedException("Issuer certificate cannot be searched.", ex3);
        }
        while (iterator.hasNext()) {
            set.add(iterator.next());
        }
        return set;
    }
    
    protected static void verifyX509Certificate(final X509Certificate x509Certificate, final PublicKey publicKey, final String s) throws GeneralSecurityException {
        if (s == null) {
            x509Certificate.verify(publicKey);
        }
        else {
            x509Certificate.verify(publicKey, s);
        }
    }
    
    static void checkCRLsNotEmpty(final Set set, final Object o) throws AnnotatedException {
        if (!set.isEmpty()) {
            return;
        }
        if (o instanceof X509AttributeCertificate) {
            throw new AnnotatedException("No CRLs found for issuer \"" + ((X509AttributeCertificate)o).getIssuer().getPrincipals()[0] + "\"");
        }
        throw new AnnotatedException("No CRLs found for issuer \"" + RFC4519Style.INSTANCE.toString(PrincipalUtils.getIssuerPrincipal((X509Certificate)o)) + "\"");
    }
    
    static {
        CRL_UTIL = new PKIXCRLUtil();
        CERTIFICATE_POLICIES = Extension.certificatePolicies.getId();
        BASIC_CONSTRAINTS = Extension.basicConstraints.getId();
        POLICY_MAPPINGS = Extension.policyMappings.getId();
        SUBJECT_ALTERNATIVE_NAME = Extension.subjectAlternativeName.getId();
        NAME_CONSTRAINTS = Extension.nameConstraints.getId();
        KEY_USAGE = Extension.keyUsage.getId();
        INHIBIT_ANY_POLICY = Extension.inhibitAnyPolicy.getId();
        ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();
        DELTA_CRL_INDICATOR = Extension.deltaCRLIndicator.getId();
        POLICY_CONSTRAINTS = Extension.policyConstraints.getId();
        FRESHEST_CRL = Extension.freshestCRL.getId();
        CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
        AUTHORITY_KEY_IDENTIFIER = Extension.authorityKeyIdentifier.getId();
        CRL_NUMBER = Extension.cRLNumber.getId();
        crlReasons = new String[] { "unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", "aACompromise" };
    }
}
