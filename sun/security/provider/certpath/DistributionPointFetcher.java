package sun.security.provider.certpath;

import java.security.cert.Certificate;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.IssuingDistributionPointExtension;
import sun.security.x509.PKIXExtensions;
import java.security.cert.CertPathValidatorException;
import java.security.GeneralSecurityException;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathBuilder;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.CertSelector;
import java.security.cert.PKIXBuilderParameters;
import sun.security.x509.SerialNumber;
import java.security.cert.X509CertSelector;
import sun.security.x509.ReasonFlags;
import sun.security.x509.DistributionPointName;
import sun.security.x509.X509CRLImpl;
import javax.security.auth.x500.X500Principal;
import java.net.URI;
import java.security.cert.CRLSelector;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import sun.security.x509.RDN;
import sun.security.x509.GeneralNames;
import java.security.cert.CRLException;
import java.security.cert.CRL;
import sun.security.x509.URIName;
import sun.security.x509.GeneralName;
import java.util.ArrayList;
import sun.security.x509.X500Name;
import java.util.Iterator;
import sun.security.x509.CRLDistributionPointsExtension;
import java.io.IOException;
import java.security.cert.CertificateException;
import sun.security.x509.DistributionPoint;
import java.util.Arrays;
import java.util.HashSet;
import sun.security.x509.X509CertImpl;
import java.util.Collections;
import java.security.cert.CertStoreException;
import java.security.cert.X509Certificate;
import java.security.cert.X509CRL;
import java.util.Collection;
import java.util.Date;
import java.security.cert.TrustAnchor;
import java.util.Set;
import java.security.cert.CertStore;
import java.util.List;
import java.security.PublicKey;
import java.security.cert.X509CRLSelector;
import sun.security.util.Debug;

public class DistributionPointFetcher
{
    private static final Debug debug;
    private static final boolean[] ALL_REASONS;
    
    private DistributionPointFetcher() {
    }
    
    public static Collection<X509CRL> getCRLs(final X509CRLSelector x509CRLSelector, final boolean b, final PublicKey publicKey, final String s, final List<CertStore> list, final boolean[] array, final Set<TrustAnchor> set, final Date date, final String s2) throws CertStoreException {
        return getCRLs(x509CRLSelector, b, publicKey, null, s, list, array, set, date, s2);
    }
    
    public static Collection<X509CRL> getCRLs(final X509CRLSelector x509CRLSelector, final boolean b, final PublicKey publicKey, final String s, final List<CertStore> list, final boolean[] array, final Set<TrustAnchor> set, final Date date) throws CertStoreException {
        return getCRLs(x509CRLSelector, b, publicKey, null, s, list, array, set, date, "generic");
    }
    
    public static Collection<X509CRL> getCRLs(final X509CRLSelector x509CRLSelector, final boolean b, final PublicKey publicKey, final X509Certificate x509Certificate, final String s, final List<CertStore> list, final boolean[] array, final Set<TrustAnchor> set, final Date date, final String s2) throws CertStoreException {
        final X509Certificate certificateChecking = x509CRLSelector.getCertificateChecking();
        if (certificateChecking == null) {
            return (Collection<X509CRL>)Collections.emptySet();
        }
        try {
            final X509CertImpl impl = X509CertImpl.toImpl(certificateChecking);
            if (DistributionPointFetcher.debug != null) {
                DistributionPointFetcher.debug.println("DistributionPointFetcher.getCRLs: Checking CRLDPs for " + impl.getSubjectX500Principal());
            }
            final CRLDistributionPointsExtension crlDistributionPointsExtension = impl.getCRLDistributionPointsExtension();
            if (crlDistributionPointsExtension == null) {
                if (DistributionPointFetcher.debug != null) {
                    DistributionPointFetcher.debug.println("No CRLDP ext");
                }
                return (Collection<X509CRL>)Collections.emptySet();
            }
            final List<DistributionPoint> value = crlDistributionPointsExtension.get("points");
            final HashSet set2 = new HashSet();
            final Iterator<DistributionPoint> iterator = value.iterator();
            while (iterator.hasNext() && !Arrays.equals(array, DistributionPointFetcher.ALL_REASONS)) {
                set2.addAll(getCRLs(x509CRLSelector, impl, iterator.next(), array, b, publicKey, x509Certificate, s, list, set, date, s2));
            }
            if (DistributionPointFetcher.debug != null) {
                DistributionPointFetcher.debug.println("Returning " + set2.size() + " CRLs");
            }
            return set2;
        }
        catch (final CertificateException | IOException ex) {
            return (Collection<X509CRL>)Collections.emptySet();
        }
    }
    
    private static Collection<X509CRL> getCRLs(final X509CRLSelector x509CRLSelector, final X509CertImpl x509CertImpl, final DistributionPoint distributionPoint, final boolean[] array, final boolean b, final PublicKey publicKey, final X509Certificate x509Certificate, final String s, final List<CertStore> list, final Set<TrustAnchor> set, final Date date, final String s2) throws CertStoreException {
        GeneralNames generalNames = distributionPoint.getFullName();
        if (generalNames == null) {
            final RDN relativeName = distributionPoint.getRelativeName();
            if (relativeName == null) {
                return (Collection<X509CRL>)Collections.emptySet();
            }
            try {
                final GeneralNames crlIssuer = distributionPoint.getCRLIssuer();
                if (crlIssuer == null) {
                    generalNames = getFullNames((X500Name)x509CertImpl.getIssuerDN(), relativeName);
                }
                else {
                    if (crlIssuer.size() != 1) {
                        return (Collection<X509CRL>)Collections.emptySet();
                    }
                    generalNames = getFullNames((X500Name)crlIssuer.get(0).getName(), relativeName);
                }
            }
            catch (final IOException ex) {
                return (Collection<X509CRL>)Collections.emptySet();
            }
        }
        final ArrayList list2 = new ArrayList();
        CertStoreException ex2 = null;
        final Iterator<GeneralName> iterator = generalNames.iterator();
        while (iterator.hasNext()) {
            try {
                final GeneralName generalName = iterator.next();
                if (generalName.getType() == 4) {
                    list2.addAll(getCRLs((X500Name)generalName.getName(), x509CertImpl.getIssuerX500Principal(), list));
                }
                else {
                    if (generalName.getType() != 6) {
                        continue;
                    }
                    final X509CRL crl = getCRL((URIName)generalName.getName());
                    if (crl == null) {
                        continue;
                    }
                    list2.add(crl);
                }
            }
            catch (final CertStoreException ex3) {
                ex2 = ex3;
            }
        }
        if (list2.isEmpty() && ex2 != null) {
            throw ex2;
        }
        final ArrayList list3 = new ArrayList(2);
        for (final X509CRL x509CRL : list2) {
            try {
                x509CRLSelector.setIssuerNames(null);
                if (!x509CRLSelector.match(x509CRL) || !verifyCRL(x509CertImpl, distributionPoint, x509CRL, array, b, publicKey, x509Certificate, s, set, list, date, s2)) {
                    continue;
                }
                list3.add(x509CRL);
            }
            catch (final IOException | CRLException ex4) {
                if (DistributionPointFetcher.debug == null) {
                    continue;
                }
                DistributionPointFetcher.debug.println("Exception verifying CRL: " + ((Throwable)ex4).getMessage());
                ((Exception)ex4).printStackTrace();
            }
        }
        return list3;
    }
    
    private static X509CRL getCRL(final URIName uriName) throws CertStoreException {
        final URI uri = uriName.getURI();
        if (DistributionPointFetcher.debug != null) {
            DistributionPointFetcher.debug.println("Trying to fetch CRL from DP " + uri);
        }
        CertStore instance;
        try {
            instance = URICertStore.getInstance(new URICertStore.URICertStoreParameters(uri));
        }
        catch (final InvalidAlgorithmParameterException | NoSuchAlgorithmException ex) {
            if (DistributionPointFetcher.debug != null) {
                DistributionPointFetcher.debug.println("Can't create URICertStore: " + ((Throwable)ex).getMessage());
            }
            return null;
        }
        final Collection<? extends CRL> crLs = instance.getCRLs(null);
        if (crLs.isEmpty()) {
            return null;
        }
        return (X509CRL)crLs.iterator().next();
    }
    
    private static Collection<X509CRL> getCRLs(final X500Name x500Name, final X500Principal x500Principal, final List<CertStore> list) throws CertStoreException {
        if (DistributionPointFetcher.debug != null) {
            DistributionPointFetcher.debug.println("Trying to fetch CRL from DP " + x500Name);
        }
        final X509CRLSelector x509CRLSelector = new X509CRLSelector();
        x509CRLSelector.addIssuer(x500Name.asX500Principal());
        x509CRLSelector.addIssuer(x500Principal);
        final ArrayList list2 = new ArrayList();
        Object o = null;
        for (final CertStore certStore : list) {
            try {
                final Iterator<? extends CRL> iterator2 = certStore.getCRLs(x509CRLSelector).iterator();
                while (iterator2.hasNext()) {
                    list2.add(iterator2.next());
                }
            }
            catch (final CertStoreException ex) {
                if (DistributionPointFetcher.debug != null) {
                    DistributionPointFetcher.debug.println("Exception while retrieving CRLs: " + ex);
                    ex.printStackTrace();
                }
                o = new PKIX.CertStoreTypeException(certStore.getType(), ex);
            }
        }
        if (list2.isEmpty() && o != null) {
            throw o;
        }
        return list2;
    }
    
    static boolean verifyCRL(final X509CertImpl x509CertImpl, final DistributionPoint distributionPoint, final X509CRL x509CRL, final boolean[] array, final boolean b, PublicKey publicKey, final X509Certificate x509Certificate, final String sigProvider, final Set<TrustAnchor> set, final List<CertStore> certStores, final Date date, final String s) throws CRLException, IOException {
        if (DistributionPointFetcher.debug != null) {
            DistributionPointFetcher.debug.println("DistributionPointFetcher.verifyCRL: checking revocation status for\n  SN: " + Debug.toHexString(x509CertImpl.getSerialNumber()) + "\n  Subject: " + x509CertImpl.getSubjectX500Principal() + "\n  Issuer: " + x509CertImpl.getIssuerX500Principal());
        }
        boolean b2 = false;
        final X509CRLImpl impl = X509CRLImpl.toImpl(x509CRL);
        final IssuingDistributionPointExtension issuingDistributionPointExtension = impl.getIssuingDistributionPointExtension();
        final X500Name x500Name = (X500Name)x509CertImpl.getIssuerDN();
        final X500Name x500Name2 = (X500Name)impl.getIssuerDN();
        final GeneralNames crlIssuer = distributionPoint.getCRLIssuer();
        X500Name x500Name3 = null;
        if (crlIssuer != null) {
            if (issuingDistributionPointExtension == null || ((Boolean)issuingDistributionPointExtension.get("indirect_crl")).equals(Boolean.FALSE)) {
                return false;
            }
            int n = 0;
            for (Iterator<GeneralName> iterator = crlIssuer.iterator(); n == 0 && iterator.hasNext(); n = 1) {
                final GeneralNameInterface name = iterator.next().getName();
                if (x500Name2.equals(name)) {
                    x500Name3 = (X500Name)name;
                }
            }
            if (n == 0) {
                return false;
            }
            if (issues(x509CertImpl, impl, sigProvider)) {
                publicKey = x509CertImpl.getPublicKey();
            }
            else {
                b2 = true;
            }
        }
        else {
            if (!x500Name2.equals(x500Name)) {
                if (DistributionPointFetcher.debug != null) {
                    DistributionPointFetcher.debug.println("crl issuer does not equal cert issuer.\ncrl issuer: " + x500Name2 + "\ncert issuer: " + x500Name);
                }
                return false;
            }
            final KeyIdentifier authKeyId = x509CertImpl.getAuthKeyId();
            final KeyIdentifier authKeyId2 = impl.getAuthKeyId();
            if (authKeyId == null || authKeyId2 == null) {
                if (issues(x509CertImpl, impl, sigProvider)) {
                    publicKey = x509CertImpl.getPublicKey();
                }
            }
            else if (!authKeyId.equals(authKeyId2)) {
                if (issues(x509CertImpl, impl, sigProvider)) {
                    publicKey = x509CertImpl.getPublicKey();
                }
                else {
                    b2 = true;
                }
            }
        }
        if (!b2 && !b) {
            return false;
        }
        if (issuingDistributionPointExtension != null) {
            final DistributionPointName distributionPointName = (DistributionPointName)issuingDistributionPointExtension.get("point");
            if (distributionPointName != null) {
                GeneralNames generalNames = distributionPointName.getFullName();
                if (generalNames == null) {
                    final RDN relativeName = distributionPointName.getRelativeName();
                    if (relativeName == null) {
                        if (DistributionPointFetcher.debug != null) {
                            DistributionPointFetcher.debug.println("IDP must be relative or full DN");
                        }
                        return false;
                    }
                    if (DistributionPointFetcher.debug != null) {
                        DistributionPointFetcher.debug.println("IDP relativeName:" + relativeName);
                    }
                    generalNames = getFullNames(x500Name2, relativeName);
                }
                if (distributionPoint.getFullName() != null || distributionPoint.getRelativeName() != null) {
                    GeneralNames generalNames2 = distributionPoint.getFullName();
                    if (generalNames2 == null) {
                        final RDN relativeName2 = distributionPoint.getRelativeName();
                        if (relativeName2 == null) {
                            if (DistributionPointFetcher.debug != null) {
                                DistributionPointFetcher.debug.println("DP must be relative or full DN");
                            }
                            return false;
                        }
                        if (DistributionPointFetcher.debug != null) {
                            DistributionPointFetcher.debug.println("DP relativeName:" + relativeName2);
                        }
                        if (b2) {
                            if (crlIssuer.size() != 1) {
                                if (DistributionPointFetcher.debug != null) {
                                    DistributionPointFetcher.debug.println("must only be one CRL issuer when relative name present");
                                }
                                return false;
                            }
                            generalNames2 = getFullNames(x500Name3, relativeName2);
                        }
                        else {
                            generalNames2 = getFullNames(x500Name, relativeName2);
                        }
                    }
                    boolean equals = false;
                    final Iterator<GeneralName> iterator2 = generalNames.iterator();
                    while (!equals && iterator2.hasNext()) {
                        final GeneralNameInterface name2 = iterator2.next().getName();
                        if (DistributionPointFetcher.debug != null) {
                            DistributionPointFetcher.debug.println("idpName: " + name2);
                        }
                        GeneralNameInterface name3;
                        for (Iterator<GeneralName> iterator3 = generalNames2.iterator(); !equals && iterator3.hasNext(); equals = name2.equals(name3)) {
                            name3 = iterator3.next().getName();
                            if (DistributionPointFetcher.debug != null) {
                                DistributionPointFetcher.debug.println("pointName: " + name3);
                            }
                        }
                    }
                    if (!equals) {
                        if (DistributionPointFetcher.debug != null) {
                            DistributionPointFetcher.debug.println("IDP name does not match DP name");
                        }
                        return false;
                    }
                }
                else {
                    boolean equals2 = false;
                    final Iterator<GeneralName> iterator4 = crlIssuer.iterator();
                    while (!equals2 && iterator4.hasNext()) {
                        final GeneralNameInterface name4 = iterator4.next().getName();
                        for (Iterator<GeneralName> iterator5 = generalNames.iterator(); !equals2 && iterator5.hasNext(); equals2 = name4.equals(iterator5.next().getName())) {}
                    }
                    if (!equals2) {
                        return false;
                    }
                }
            }
            if (((Boolean)issuingDistributionPointExtension.get("only_user_certs")).equals(Boolean.TRUE) && x509CertImpl.getBasicConstraints() != -1) {
                if (DistributionPointFetcher.debug != null) {
                    DistributionPointFetcher.debug.println("cert must be a EE cert");
                }
                return false;
            }
            if (((Boolean)issuingDistributionPointExtension.get("only_ca_certs")).equals(Boolean.TRUE) && x509CertImpl.getBasicConstraints() == -1) {
                if (DistributionPointFetcher.debug != null) {
                    DistributionPointFetcher.debug.println("cert must be a CA cert");
                }
                return false;
            }
            if (((Boolean)issuingDistributionPointExtension.get("only_attribute_certs")).equals(Boolean.TRUE)) {
                if (DistributionPointFetcher.debug != null) {
                    DistributionPointFetcher.debug.println("cert must not be an AA cert");
                }
                return false;
            }
        }
        boolean[] array2 = new boolean[9];
        ReasonFlags reasonFlags = null;
        if (issuingDistributionPointExtension != null) {
            reasonFlags = (ReasonFlags)issuingDistributionPointExtension.get("reasons");
        }
        final boolean[] reasonFlags2 = distributionPoint.getReasonFlags();
        if (reasonFlags != null) {
            if (reasonFlags2 != null) {
                final boolean[] flags = reasonFlags.getFlags();
                for (int i = 0; i < array2.length; ++i) {
                    array2[i] = (i < flags.length && flags[i] && i < reasonFlags2.length && reasonFlags2[i]);
                }
            }
            else {
                array2 = reasonFlags.getFlags().clone();
            }
        }
        else if (issuingDistributionPointExtension == null || reasonFlags == null) {
            if (reasonFlags2 != null) {
                array2 = reasonFlags2.clone();
            }
            else {
                Arrays.fill(array2, true);
            }
        }
        int n2 = 0;
        for (int n3 = 0; n3 < array2.length && n2 == 0; ++n3) {
            if (array2[n3] && (n3 >= array.length || !array[n3])) {
                n2 = 1;
            }
        }
        if (n2 == 0) {
            return false;
        }
        if (b2) {
            final X509CertSelector x509CertSelector = new X509CertSelector();
            x509CertSelector.setSubject(x500Name2.asX500Principal());
            x509CertSelector.setKeyUsage(new boolean[] { false, false, false, false, false, false, true });
            final AuthorityKeyIdentifierExtension authKeyIdExtension = impl.getAuthKeyIdExtension();
            if (authKeyIdExtension != null) {
                final byte[] encodedKeyIdentifier = authKeyIdExtension.getEncodedKeyIdentifier();
                if (encodedKeyIdentifier != null) {
                    x509CertSelector.setSubjectKeyIdentifier(encodedKeyIdentifier);
                }
                final SerialNumber serialNumber = (SerialNumber)authKeyIdExtension.get("serial_number");
                if (serialNumber != null) {
                    x509CertSelector.setSerialNumber(serialNumber.getNumber());
                }
            }
            final HashSet<TrustAnchor> set2 = new HashSet<TrustAnchor>(set);
            if (publicKey != null) {
                TrustAnchor trustAnchor;
                if (x509Certificate != null) {
                    trustAnchor = new TrustAnchor(x509Certificate, null);
                }
                else {
                    trustAnchor = new TrustAnchor(x509CertImpl.getIssuerX500Principal(), publicKey, null);
                }
                set2.add(trustAnchor);
            }
            PKIXBuilderParameters pkixBuilderParameters;
            try {
                pkixBuilderParameters = new PKIXBuilderParameters(set2, x509CertSelector);
            }
            catch (final InvalidAlgorithmParameterException ex) {
                throw new CRLException(ex);
            }
            pkixBuilderParameters.setCertStores(certStores);
            pkixBuilderParameters.setSigProvider(sigProvider);
            pkixBuilderParameters.setDate(date);
            try {
                publicKey = ((PKIXCertPathBuilderResult)CertPathBuilder.getInstance("PKIX").build(pkixBuilderParameters)).getPublicKey();
            }
            catch (final GeneralSecurityException ex2) {
                throw new CRLException(ex2);
            }
        }
        try {
            AlgorithmChecker.check(publicKey, x509CRL, s);
        }
        catch (final CertPathValidatorException ex3) {
            if (DistributionPointFetcher.debug != null) {
                DistributionPointFetcher.debug.println("CRL signature algorithm check failed: " + ex3);
            }
            return false;
        }
        try {
            x509CRL.verify(publicKey, sigProvider);
        }
        catch (final GeneralSecurityException ex4) {
            if (DistributionPointFetcher.debug != null) {
                DistributionPointFetcher.debug.println("CRL signature failed to verify");
            }
            return false;
        }
        final Set<String> criticalExtensionOIDs = x509CRL.getCriticalExtensionOIDs();
        if (criticalExtensionOIDs != null) {
            criticalExtensionOIDs.remove(PKIXExtensions.IssuingDistributionPoint_Id.toString());
            if (!criticalExtensionOIDs.isEmpty()) {
                if (DistributionPointFetcher.debug != null) {
                    DistributionPointFetcher.debug.println("Unrecognized critical extension(s) in CRL: " + criticalExtensionOIDs);
                    final Iterator iterator6 = criticalExtensionOIDs.iterator();
                    while (iterator6.hasNext()) {
                        DistributionPointFetcher.debug.println((String)iterator6.next());
                    }
                }
                return false;
            }
        }
        for (int j = 0; j < array.length; ++j) {
            array[j] = (array[j] || (j < array2.length && array2[j]));
        }
        return true;
    }
    
    private static GeneralNames getFullNames(final X500Name x500Name, final RDN rdn) throws IOException {
        final ArrayList list = new ArrayList((Collection<? extends E>)x500Name.rdns());
        list.add(rdn);
        final X500Name x500Name2 = new X500Name((RDN[])list.toArray(new RDN[0]));
        final GeneralNames generalNames = new GeneralNames();
        generalNames.add(new GeneralName(x500Name2));
        return generalNames;
    }
    
    private static boolean issues(final X509CertImpl x509CertImpl, final X509CRLImpl x509CRLImpl, final String s) throws IOException {
        final AdaptableX509CertSelector adaptableX509CertSelector = new AdaptableX509CertSelector();
        final boolean[] keyUsage = x509CertImpl.getKeyUsage();
        if (keyUsage != null) {
            keyUsage[6] = true;
            adaptableX509CertSelector.setKeyUsage(keyUsage);
        }
        adaptableX509CertSelector.setSubject(x509CRLImpl.getIssuerX500Principal());
        final AuthorityKeyIdentifierExtension authKeyIdExtension = x509CRLImpl.getAuthKeyIdExtension();
        adaptableX509CertSelector.setSkiAndSerialNumber(authKeyIdExtension);
        boolean match = adaptableX509CertSelector.match(x509CertImpl);
        if (match) {
            if (authKeyIdExtension != null) {
                if (x509CertImpl.getAuthorityKeyIdentifierExtension() != null) {
                    return match;
                }
            }
            try {
                x509CRLImpl.verify(x509CertImpl.getPublicKey(), s);
                match = true;
            }
            catch (final GeneralSecurityException ex) {
                match = false;
            }
        }
        return match;
    }
    
    static {
        debug = Debug.getInstance("certpath");
        ALL_REASONS = new boolean[] { true, true, true, true, true, true, true, true, true };
    }
}
