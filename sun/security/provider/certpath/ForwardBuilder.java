package sun.security.provider.certpath;

import sun.security.x509.X500Name;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.X509CertImpl;
import java.util.LinkedList;
import java.security.PublicKey;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.security.GeneralSecurityException;
import java.security.cert.CertPath;
import java.security.cert.PKIXReason;
import sun.security.x509.PKIXExtensions;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.CertPathValidatorException;
import java.util.Collections;
import java.security.cert.CertSelector;
import sun.security.x509.AccessDescription;
import sun.security.x509.AuthorityInfoAccessExtension;
import java.security.cert.Certificate;
import sun.security.x509.GeneralNameInterface;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertStoreException;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Collection;
import java.security.cert.CertStore;
import java.util.List;
import java.util.Iterator;
import java.util.HashSet;
import java.security.cert.X509CertSelector;
import java.security.cert.TrustAnchor;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import java.util.Set;
import sun.security.util.Debug;

class ForwardBuilder extends Builder
{
    private static final Debug debug;
    private final Set<X509Certificate> trustedCerts;
    private final Set<X500Principal> trustedSubjectDNs;
    private final Set<TrustAnchor> trustAnchors;
    private X509CertSelector eeSelector;
    private AdaptableX509CertSelector caSelector;
    private X509CertSelector caTargetSelector;
    TrustAnchor trustAnchor;
    private boolean searchAllCertStores;
    
    ForwardBuilder(final PKIX.BuilderParams builderParams, final boolean searchAllCertStores) {
        super(builderParams);
        this.searchAllCertStores = true;
        this.trustAnchors = builderParams.trustAnchors();
        this.trustedCerts = new HashSet<X509Certificate>(this.trustAnchors.size());
        this.trustedSubjectDNs = new HashSet<X500Principal>(this.trustAnchors.size());
        for (final TrustAnchor trustAnchor : this.trustAnchors) {
            final X509Certificate trustedCert = trustAnchor.getTrustedCert();
            if (trustedCert != null) {
                this.trustedCerts.add(trustedCert);
                this.trustedSubjectDNs.add(trustedCert.getSubjectX500Principal());
            }
            else {
                this.trustedSubjectDNs.add(trustAnchor.getCA());
            }
        }
        this.searchAllCertStores = searchAllCertStores;
    }
    
    @Override
    Collection<X509Certificate> getMatchingCerts(final State state, final List<CertStore> list) throws CertStoreException, CertificateException, IOException {
        if (ForwardBuilder.debug != null) {
            ForwardBuilder.debug.println("ForwardBuilder.getMatchingCerts()...");
        }
        final ForwardState forwardState = (ForwardState)state;
        final TreeSet set = new TreeSet<X509Certificate>(new PKIXCertComparator(this.trustedSubjectDNs, forwardState.cert));
        if (forwardState.isInitial()) {
            this.getMatchingEECerts(forwardState, list, (Collection<X509Certificate>)set);
        }
        this.getMatchingCACerts(forwardState, list, (Collection<X509Certificate>)set);
        return (Collection<X509Certificate>)set;
    }
    
    private void getMatchingEECerts(final ForwardState forwardState, final List<CertStore> list, final Collection<X509Certificate> collection) throws IOException {
        if (ForwardBuilder.debug != null) {
            ForwardBuilder.debug.println("ForwardBuilder.getMatchingEECerts()...");
        }
        if (this.eeSelector == null) {
            (this.eeSelector = (X509CertSelector)this.targetCertConstraints.clone()).setCertificateValid(this.buildParams.date());
            if (this.buildParams.explicitPolicyRequired()) {
                this.eeSelector.setPolicy(this.getMatchingPolicies());
            }
            this.eeSelector.setBasicConstraints(-2);
        }
        this.addMatchingCerts(this.eeSelector, list, collection, this.searchAllCertStores);
    }
    
    private void getMatchingCACerts(final ForwardState forwardState, final List<CertStore> list, final Collection<X509Certificate> collection) throws IOException {
        if (ForwardBuilder.debug != null) {
            ForwardBuilder.debug.println("ForwardBuilder.getMatchingCACerts()...");
        }
        final int size = collection.size();
        X509CertSelector x509CertSelector;
        if (forwardState.isInitial()) {
            if (this.targetCertConstraints.getBasicConstraints() == -2) {
                return;
            }
            if (ForwardBuilder.debug != null) {
                ForwardBuilder.debug.println("ForwardBuilder.getMatchingCACerts(): the target is a CA");
            }
            if (this.caTargetSelector == null) {
                this.caTargetSelector = (X509CertSelector)this.targetCertConstraints.clone();
                if (this.buildParams.explicitPolicyRequired()) {
                    this.caTargetSelector.setPolicy(this.getMatchingPolicies());
                }
            }
            x509CertSelector = this.caTargetSelector;
        }
        else {
            if (this.caSelector == null) {
                this.caSelector = new AdaptableX509CertSelector();
                if (this.buildParams.explicitPolicyRequired()) {
                    this.caSelector.setPolicy(this.getMatchingPolicies());
                }
            }
            this.caSelector.setSubject(forwardState.issuerDN);
            CertPathHelper.setPathToNames(this.caSelector, forwardState.subjectNamesTraversed);
            this.caSelector.setValidityPeriod(forwardState.cert.getNotBefore(), forwardState.cert.getNotAfter());
            x509CertSelector = this.caSelector;
        }
        x509CertSelector.setBasicConstraints(-1);
        for (final X509Certificate x509Certificate : this.trustedCerts) {
            if (x509CertSelector.match(x509Certificate)) {
                if (ForwardBuilder.debug != null) {
                    ForwardBuilder.debug.println("ForwardBuilder.getMatchingCACerts: found matching trust anchor.\n  SN: " + Debug.toHexString(x509Certificate.getSerialNumber()) + "\n  Subject: " + x509Certificate.getSubjectX500Principal() + "\n  Issuer: " + x509Certificate.getIssuerX500Principal());
                }
                if (collection.add(x509Certificate) && !this.searchAllCertStores) {
                    return;
                }
                continue;
            }
        }
        x509CertSelector.setCertificateValid(this.buildParams.date());
        x509CertSelector.setBasicConstraints(forwardState.traversedCACerts);
        if ((forwardState.isInitial() || this.buildParams.maxPathLength() == -1 || this.buildParams.maxPathLength() > forwardState.traversedCACerts) && this.addMatchingCerts(x509CertSelector, list, collection, this.searchAllCertStores) && !this.searchAllCertStores) {
            return;
        }
        if (!forwardState.isInitial() && Builder.USE_AIA) {
            final AuthorityInfoAccessExtension authorityInfoAccessExtension = forwardState.cert.getAuthorityInfoAccessExtension();
            if (authorityInfoAccessExtension != null) {
                this.getCerts(authorityInfoAccessExtension, collection);
            }
        }
        if (ForwardBuilder.debug != null) {
            ForwardBuilder.debug.println("ForwardBuilder.getMatchingCACerts: found " + (collection.size() - size) + " CA certs");
        }
    }
    
    private boolean getCerts(final AuthorityInfoAccessExtension authorityInfoAccessExtension, final Collection<X509Certificate> collection) {
        if (!Builder.USE_AIA) {
            return false;
        }
        final List<AccessDescription> accessDescriptions = authorityInfoAccessExtension.getAccessDescriptions();
        if (accessDescriptions == null || accessDescriptions.isEmpty()) {
            return false;
        }
        boolean b = false;
        final Iterator iterator = accessDescriptions.iterator();
        while (iterator.hasNext()) {
            final CertStore instance = URICertStore.getInstance((AccessDescription)iterator.next());
            if (instance != null) {
                try {
                    if (!collection.addAll((Collection<? extends X509Certificate>)instance.getCertificates(this.caSelector))) {
                        continue;
                    }
                    b = true;
                    if (!this.searchAllCertStores) {
                        return true;
                    }
                    continue;
                }
                catch (final CertStoreException ex) {
                    if (ForwardBuilder.debug == null) {
                        continue;
                    }
                    ForwardBuilder.debug.println("exception getting certs from CertStore:");
                    ex.printStackTrace();
                }
            }
        }
        return b;
    }
    
    @Override
    void verifyCert(final X509Certificate x509Certificate, final State state, final List<X509Certificate> list) throws GeneralSecurityException {
        if (ForwardBuilder.debug != null) {
            ForwardBuilder.debug.println("ForwardBuilder.verifyCert(SN: " + Debug.toHexString(x509Certificate.getSerialNumber()) + "\n  Issuer: " + x509Certificate.getIssuerX500Principal() + ")\n  Subject: " + x509Certificate.getSubjectX500Principal() + ")");
        }
        final ForwardState forwardState = (ForwardState)state;
        forwardState.untrustedChecker.check(x509Certificate, (Collection<String>)Collections.emptySet());
        if (list != null) {
            final Iterator<X509Certificate> iterator = list.iterator();
            while (iterator.hasNext()) {
                if (x509Certificate.equals(iterator.next())) {
                    if (ForwardBuilder.debug != null) {
                        ForwardBuilder.debug.println("loop detected!!");
                    }
                    throw new CertPathValidatorException("loop detected");
                }
            }
        }
        final boolean contains = this.trustedCerts.contains(x509Certificate);
        if (!contains) {
            Object o = x509Certificate.getCriticalExtensionOIDs();
            if (o == null) {
                o = Collections.emptySet();
            }
            final Iterator<PKIXCertPathChecker> iterator2 = forwardState.forwardCheckers.iterator();
            while (iterator2.hasNext()) {
                iterator2.next().check(x509Certificate, (Collection<String>)o);
            }
            for (final PKIXCertPathChecker pkixCertPathChecker : this.buildParams.certPathCheckers()) {
                if (!pkixCertPathChecker.isForwardCheckingSupported()) {
                    final Set<String> supportedExtensions = pkixCertPathChecker.getSupportedExtensions();
                    if (supportedExtensions == null) {
                        continue;
                    }
                    ((Set)o).removeAll(supportedExtensions);
                }
            }
            if (!((Set)o).isEmpty()) {
                ((Set)o).remove(PKIXExtensions.BasicConstraints_Id.toString());
                ((Set)o).remove(PKIXExtensions.NameConstraints_Id.toString());
                ((Set)o).remove(PKIXExtensions.CertificatePolicies_Id.toString());
                ((Set)o).remove(PKIXExtensions.PolicyMappings_Id.toString());
                ((Set)o).remove(PKIXExtensions.PolicyConstraints_Id.toString());
                ((Set)o).remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
                ((Set)o).remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
                ((Set)o).remove(PKIXExtensions.KeyUsage_Id.toString());
                ((Set)o).remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
                if (!((Set)o).isEmpty()) {
                    throw new CertPathValidatorException("Unrecognized critical extension(s)", null, null, -1, PKIXReason.UNRECOGNIZED_CRIT_EXT);
                }
            }
        }
        if (forwardState.isInitial()) {
            return;
        }
        if (!contains) {
            if (x509Certificate.getBasicConstraints() == -1) {
                throw new CertificateException("cert is NOT a CA cert");
            }
            KeyChecker.verifyCAKeyUsage(x509Certificate);
        }
        if (!forwardState.keyParamsNeeded()) {
            forwardState.cert.verify(x509Certificate.getPublicKey(), this.buildParams.sigProvider());
        }
    }
    
    @Override
    boolean isPathCompleted(final X509Certificate x509Certificate) {
        final ArrayList list = new ArrayList();
        for (final TrustAnchor trustAnchor : this.trustAnchors) {
            if (trustAnchor.getTrustedCert() != null) {
                if (x509Certificate.equals(trustAnchor.getTrustedCert())) {
                    this.trustAnchor = trustAnchor;
                    return true;
                }
                continue;
            }
            else {
                final X500Principal ca = trustAnchor.getCA();
                final PublicKey caPublicKey = trustAnchor.getCAPublicKey();
                if (ca != null && caPublicKey != null && ca.equals(x509Certificate.getSubjectX500Principal()) && caPublicKey.equals(x509Certificate.getPublicKey())) {
                    this.trustAnchor = trustAnchor;
                    return true;
                }
                list.add(trustAnchor);
            }
        }
        for (final TrustAnchor trustAnchor2 : list) {
            final X500Principal ca2 = trustAnchor2.getCA();
            final PublicKey caPublicKey2 = trustAnchor2.getCAPublicKey();
            if (ca2 != null) {
                if (!ca2.equals(x509Certificate.getIssuerX500Principal())) {
                    continue;
                }
                if (PKIX.isDSAPublicKeyWithoutParams(caPublicKey2)) {
                    continue;
                }
                try {
                    x509Certificate.verify(caPublicKey2, this.buildParams.sigProvider());
                }
                catch (final InvalidKeyException ex) {
                    if (ForwardBuilder.debug == null) {
                        continue;
                    }
                    ForwardBuilder.debug.println("ForwardBuilder.isPathCompleted() invalid DSA key found");
                    continue;
                }
                catch (final GeneralSecurityException ex2) {
                    if (ForwardBuilder.debug == null) {
                        continue;
                    }
                    ForwardBuilder.debug.println("ForwardBuilder.isPathCompleted() unexpected exception");
                    ex2.printStackTrace();
                    continue;
                }
                this.trustAnchor = trustAnchor2;
                return true;
            }
        }
        return false;
    }
    
    @Override
    void addCertToPath(final X509Certificate x509Certificate, final LinkedList<X509Certificate> list) {
        list.addFirst(x509Certificate);
    }
    
    @Override
    void removeFinalCertFromPath(final LinkedList<X509Certificate> list) {
        list.removeFirst();
    }
    
    static {
        debug = Debug.getInstance("certpath");
    }
    
    static class PKIXCertComparator implements Comparator<X509Certificate>
    {
        static final String METHOD_NME = "PKIXCertComparator.compare()";
        private final Set<X500Principal> trustedSubjectDNs;
        private final X509CertSelector certSkidSelector;
        
        PKIXCertComparator(final Set<X500Principal> trustedSubjectDNs, final X509CertImpl x509CertImpl) throws IOException {
            this.trustedSubjectDNs = trustedSubjectDNs;
            this.certSkidSelector = this.getSelector(x509CertImpl);
        }
        
        private X509CertSelector getSelector(final X509CertImpl x509CertImpl) throws IOException {
            if (x509CertImpl != null) {
                final AuthorityKeyIdentifierExtension authorityKeyIdentifierExtension = x509CertImpl.getAuthorityKeyIdentifierExtension();
                if (authorityKeyIdentifierExtension != null) {
                    final byte[] encodedKeyIdentifier = authorityKeyIdentifierExtension.getEncodedKeyIdentifier();
                    if (encodedKeyIdentifier != null) {
                        final X509CertSelector x509CertSelector = new X509CertSelector();
                        x509CertSelector.setSubjectKeyIdentifier(encodedKeyIdentifier);
                        return x509CertSelector;
                    }
                }
            }
            return null;
        }
        
        @Override
        public int compare(final X509Certificate x509Certificate, final X509Certificate x509Certificate2) {
            if (x509Certificate.equals(x509Certificate2)) {
                return 0;
            }
            if (this.certSkidSelector != null) {
                if (this.certSkidSelector.match(x509Certificate)) {
                    return -1;
                }
                if (this.certSkidSelector.match(x509Certificate2)) {
                    return 1;
                }
            }
            final X500Principal issuerX500Principal = x509Certificate.getIssuerX500Principal();
            final X500Principal issuerX500Principal2 = x509Certificate2.getIssuerX500Principal();
            final X500Name x500Name = X500Name.asX500Name(issuerX500Principal);
            final X500Name x500Name2 = X500Name.asX500Name(issuerX500Principal2);
            if (ForwardBuilder.debug != null) {
                ForwardBuilder.debug.println("PKIXCertComparator.compare() o1 Issuer:  " + issuerX500Principal);
                ForwardBuilder.debug.println("PKIXCertComparator.compare() o2 Issuer:  " + issuerX500Principal2);
            }
            if (ForwardBuilder.debug != null) {
                ForwardBuilder.debug.println("PKIXCertComparator.compare() MATCH TRUSTED SUBJECT TEST...");
            }
            final boolean contains = this.trustedSubjectDNs.contains(issuerX500Principal);
            final boolean contains2 = this.trustedSubjectDNs.contains(issuerX500Principal2);
            if (ForwardBuilder.debug != null) {
                ForwardBuilder.debug.println("PKIXCertComparator.compare() m1: " + contains);
                ForwardBuilder.debug.println("PKIXCertComparator.compare() m2: " + contains2);
            }
            if (contains && contains2) {
                return -1;
            }
            if (contains) {
                return -1;
            }
            if (contains2) {
                return 1;
            }
            if (ForwardBuilder.debug != null) {
                ForwardBuilder.debug.println("PKIXCertComparator.compare() NAMING DESCENDANT TEST...");
            }
            final Iterator<X500Principal> iterator = this.trustedSubjectDNs.iterator();
            while (iterator.hasNext()) {
                final X500Name x500Name3 = X500Name.asX500Name(iterator.next());
                final int distance = Builder.distance(x500Name3, x500Name, -1);
                final int distance2 = Builder.distance(x500Name3, x500Name2, -1);
                if (ForwardBuilder.debug != null) {
                    ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto1: " + distance);
                    ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto2: " + distance2);
                }
                if (distance > 0 || distance2 > 0) {
                    if (distance == distance2) {
                        return -1;
                    }
                    if (distance > 0 && distance2 <= 0) {
                        return -1;
                    }
                    if (distance <= 0 && distance2 > 0) {
                        return 1;
                    }
                    if (distance < distance2) {
                        return -1;
                    }
                    return 1;
                }
            }
            if (ForwardBuilder.debug != null) {
                ForwardBuilder.debug.println("PKIXCertComparator.compare() NAMING ANCESTOR TEST...");
            }
            final Iterator<X500Principal> iterator2 = this.trustedSubjectDNs.iterator();
            while (iterator2.hasNext()) {
                final X500Name x500Name4 = X500Name.asX500Name(iterator2.next());
                final int distance3 = Builder.distance(x500Name4, x500Name, Integer.MAX_VALUE);
                final int distance4 = Builder.distance(x500Name4, x500Name2, Integer.MAX_VALUE);
                if (ForwardBuilder.debug != null) {
                    ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto1: " + distance3);
                    ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceTto2: " + distance4);
                }
                if (distance3 < 0 || distance4 < 0) {
                    if (distance3 == distance4) {
                        return -1;
                    }
                    if (distance3 < 0 && distance4 >= 0) {
                        return -1;
                    }
                    if (distance3 >= 0 && distance4 < 0) {
                        return 1;
                    }
                    if (distance3 > distance4) {
                        return -1;
                    }
                    return 1;
                }
            }
            if (ForwardBuilder.debug != null) {
                ForwardBuilder.debug.println("PKIXCertComparator.compare() SAME NAMESPACE AS TRUSTED TEST...");
            }
            final Iterator<X500Principal> iterator3 = this.trustedSubjectDNs.iterator();
            while (iterator3.hasNext()) {
                final X500Name x500Name5 = X500Name.asX500Name(iterator3.next());
                final X500Name commonAncestor = x500Name5.commonAncestor(x500Name);
                final X500Name commonAncestor2 = x500Name5.commonAncestor(x500Name2);
                if (ForwardBuilder.debug != null) {
                    ForwardBuilder.debug.println("PKIXCertComparator.compare() tAo1: " + String.valueOf(commonAncestor));
                    ForwardBuilder.debug.println("PKIXCertComparator.compare() tAo2: " + String.valueOf(commonAncestor2));
                }
                if (commonAncestor != null || commonAncestor2 != null) {
                    if (commonAncestor != null && commonAncestor2 != null) {
                        final int hops = Builder.hops(x500Name5, x500Name, Integer.MAX_VALUE);
                        final int hops2 = Builder.hops(x500Name5, x500Name2, Integer.MAX_VALUE);
                        if (ForwardBuilder.debug != null) {
                            ForwardBuilder.debug.println("PKIXCertComparator.compare() hopsTto1: " + hops);
                            ForwardBuilder.debug.println("PKIXCertComparator.compare() hopsTto2: " + hops2);
                        }
                        if (hops == hops2) {
                            continue;
                        }
                        if (hops > hops2) {
                            return 1;
                        }
                        return -1;
                    }
                    else {
                        if (commonAncestor == null) {
                            return 1;
                        }
                        return -1;
                    }
                }
            }
            if (ForwardBuilder.debug != null) {
                ForwardBuilder.debug.println("PKIXCertComparator.compare() CERT ISSUER/SUBJECT COMPARISON TEST...");
            }
            final X500Principal subjectX500Principal = x509Certificate.getSubjectX500Principal();
            final X500Principal subjectX500Principal2 = x509Certificate2.getSubjectX500Principal();
            final X500Name x500Name6 = X500Name.asX500Name(subjectX500Principal);
            final X500Name x500Name7 = X500Name.asX500Name(subjectX500Principal2);
            if (ForwardBuilder.debug != null) {
                ForwardBuilder.debug.println("PKIXCertComparator.compare() o1 Subject: " + subjectX500Principal);
                ForwardBuilder.debug.println("PKIXCertComparator.compare() o2 Subject: " + subjectX500Principal2);
            }
            final int distance5 = Builder.distance(x500Name6, x500Name, Integer.MAX_VALUE);
            final int distance6 = Builder.distance(x500Name7, x500Name2, Integer.MAX_VALUE);
            if (ForwardBuilder.debug != null) {
                ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceStoI1: " + distance5);
                ForwardBuilder.debug.println("PKIXCertComparator.compare() distanceStoI2: " + distance6);
            }
            if (distance6 > distance5) {
                return -1;
            }
            if (distance6 < distance5) {
                return 1;
            }
            if (ForwardBuilder.debug != null) {
                ForwardBuilder.debug.println("PKIXCertComparator.compare() no tests matched; RETURN 0");
            }
            return -1;
        }
    }
}
