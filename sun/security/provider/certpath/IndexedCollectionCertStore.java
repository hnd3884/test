package sun.security.provider.certpath;

import java.security.cert.X509CRLSelector;
import java.security.cert.CRLSelector;
import java.security.cert.CertStoreException;
import java.security.cert.X509CertSelector;
import java.security.cert.CertSelector;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashSet;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Collection;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.CertStoreParameters;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import java.util.Map;
import java.security.cert.CertStoreSpi;

public class IndexedCollectionCertStore extends CertStoreSpi
{
    private Map<X500Principal, Object> certSubjects;
    private Map<X500Principal, Object> crlIssuers;
    private Set<Certificate> otherCertificates;
    private Set<CRL> otherCRLs;
    
    public IndexedCollectionCertStore(final CertStoreParameters certStoreParameters) throws InvalidAlgorithmParameterException {
        super(certStoreParameters);
        if (!(certStoreParameters instanceof CollectionCertStoreParameters)) {
            throw new InvalidAlgorithmParameterException("parameters must be CollectionCertStoreParameters");
        }
        final Collection<?> collection = ((CollectionCertStoreParameters)certStoreParameters).getCollection();
        if (collection == null) {
            throw new InvalidAlgorithmParameterException("Collection must not be null");
        }
        this.buildIndex(collection);
    }
    
    private void buildIndex(final Collection<?> collection) {
        this.certSubjects = new HashMap<X500Principal, Object>();
        this.crlIssuers = new HashMap<X500Principal, Object>();
        this.otherCertificates = null;
        this.otherCRLs = null;
        for (final Object next : collection) {
            if (next instanceof X509Certificate) {
                this.indexCertificate((X509Certificate)next);
            }
            else if (next instanceof X509CRL) {
                this.indexCRL((X509CRL)next);
            }
            else if (next instanceof Certificate) {
                if (this.otherCertificates == null) {
                    this.otherCertificates = new HashSet<Certificate>();
                }
                this.otherCertificates.add((Certificate)next);
            }
            else {
                if (!(next instanceof CRL)) {
                    continue;
                }
                if (this.otherCRLs == null) {
                    this.otherCRLs = new HashSet<CRL>();
                }
                this.otherCRLs.add((CRL)next);
            }
        }
        if (this.otherCertificates == null) {
            this.otherCertificates = Collections.emptySet();
        }
        if (this.otherCRLs == null) {
            this.otherCRLs = Collections.emptySet();
        }
    }
    
    private void indexCertificate(final X509Certificate x509Certificate) {
        final X500Principal subjectX500Principal = x509Certificate.getSubjectX500Principal();
        final X509Certificate put = this.certSubjects.put(subjectX500Principal, x509Certificate);
        if (put != null) {
            if (put instanceof X509Certificate) {
                if (x509Certificate.equals(put)) {
                    return;
                }
                final ArrayList list = new ArrayList(2);
                list.add(x509Certificate);
                list.add(put);
                this.certSubjects.put(subjectX500Principal, list);
            }
            else {
                final List list2 = (List)put;
                if (!list2.contains(x509Certificate)) {
                    list2.add(x509Certificate);
                }
                this.certSubjects.put(subjectX500Principal, list2);
            }
        }
    }
    
    private void indexCRL(final X509CRL x509CRL) {
        final X500Principal issuerX500Principal = x509CRL.getIssuerX500Principal();
        final X509CRL put = this.crlIssuers.put(issuerX500Principal, x509CRL);
        if (put != null) {
            if (put instanceof X509CRL) {
                if (x509CRL.equals(put)) {
                    return;
                }
                final ArrayList list = new ArrayList(2);
                list.add(x509CRL);
                list.add(put);
                this.crlIssuers.put(issuerX500Principal, list);
            }
            else {
                final List list2 = (List)put;
                if (!list2.contains(x509CRL)) {
                    list2.add(x509CRL);
                }
                this.crlIssuers.put(issuerX500Principal, list2);
            }
        }
    }
    
    @Override
    public Collection<? extends Certificate> engineGetCertificates(final CertSelector certSelector) throws CertStoreException {
        if (certSelector == null) {
            final HashSet set = new HashSet();
            this.matchX509Certs(new X509CertSelector(), set);
            set.addAll(this.otherCertificates);
            return set;
        }
        if (!(certSelector instanceof X509CertSelector)) {
            final HashSet set2 = new HashSet();
            this.matchX509Certs(certSelector, set2);
            for (final Certificate certificate : this.otherCertificates) {
                if (certSelector.match(certificate)) {
                    set2.add(certificate);
                }
            }
            return set2;
        }
        if (this.certSubjects.isEmpty()) {
            return (Collection<? extends Certificate>)Collections.emptySet();
        }
        final X509CertSelector x509CertSelector = (X509CertSelector)certSelector;
        final X509Certificate certificate2 = x509CertSelector.getCertificate();
        X500Principal x500Principal;
        if (certificate2 != null) {
            x500Principal = certificate2.getSubjectX500Principal();
        }
        else {
            x500Principal = x509CertSelector.getSubject();
        }
        if (x500Principal == null) {
            final HashSet<Certificate> set3 = new HashSet<Certificate>(16);
            this.matchX509Certs(x509CertSelector, set3);
            return set3;
        }
        final Object value = this.certSubjects.get(x500Principal);
        if (value == null) {
            return (Collection<? extends Certificate>)Collections.emptySet();
        }
        if (!(value instanceof X509Certificate)) {
            final List list = (List)value;
            final HashSet set4 = new HashSet(16);
            for (final X509Certificate x509Certificate : list) {
                if (x509CertSelector.match(x509Certificate)) {
                    set4.add(x509Certificate);
                }
            }
            return set4;
        }
        final X509Certificate x509Certificate2 = (X509Certificate)value;
        if (x509CertSelector.match(x509Certificate2)) {
            return Collections.singleton(x509Certificate2);
        }
        return (Collection<? extends Certificate>)Collections.emptySet();
    }
    
    private void matchX509Certs(final CertSelector certSelector, final Collection<Certificate> collection) {
        for (final List next : this.certSubjects.values()) {
            if (next instanceof X509Certificate) {
                final X509Certificate x509Certificate = (X509Certificate)next;
                if (!certSelector.match(x509Certificate)) {
                    continue;
                }
                collection.add(x509Certificate);
            }
            else {
                for (final X509Certificate x509Certificate2 : next) {
                    if (certSelector.match(x509Certificate2)) {
                        collection.add(x509Certificate2);
                    }
                }
            }
        }
    }
    
    @Override
    public Collection<CRL> engineGetCRLs(final CRLSelector crlSelector) throws CertStoreException {
        if (crlSelector == null) {
            final HashSet set = new HashSet();
            this.matchX509CRLs(new X509CRLSelector(), set);
            set.addAll(this.otherCRLs);
            return set;
        }
        if (!(crlSelector instanceof X509CRLSelector)) {
            final HashSet set2 = new HashSet();
            this.matchX509CRLs(crlSelector, set2);
            for (final CRL crl : this.otherCRLs) {
                if (crlSelector.match(crl)) {
                    set2.add(crl);
                }
            }
            return set2;
        }
        if (this.crlIssuers.isEmpty()) {
            return (Collection<CRL>)Collections.emptySet();
        }
        final X509CRLSelector x509CRLSelector = (X509CRLSelector)crlSelector;
        final Collection<X500Principal> issuers = x509CRLSelector.getIssuers();
        if (issuers != null) {
            final HashSet set3 = new HashSet(16);
            final Iterator<X500Principal> iterator2 = issuers.iterator();
            while (iterator2.hasNext()) {
                final Object value = this.crlIssuers.get(iterator2.next());
                if (value == null) {
                    continue;
                }
                if (value instanceof X509CRL) {
                    final X509CRL x509CRL = (X509CRL)value;
                    if (!x509CRLSelector.match(x509CRL)) {
                        continue;
                    }
                    set3.add(x509CRL);
                }
                else {
                    for (final X509CRL x509CRL2 : (List)value) {
                        if (x509CRLSelector.match(x509CRL2)) {
                            set3.add(x509CRL2);
                        }
                    }
                }
            }
            return set3;
        }
        final HashSet set4 = new HashSet(16);
        this.matchX509CRLs(x509CRLSelector, set4);
        return set4;
    }
    
    private void matchX509CRLs(final CRLSelector crlSelector, final Collection<CRL> collection) {
        for (final List next : this.crlIssuers.values()) {
            if (next instanceof X509CRL) {
                final X509CRL x509CRL = (X509CRL)next;
                if (!crlSelector.match(x509CRL)) {
                    continue;
                }
                collection.add(x509CRL);
            }
            else {
                for (final X509CRL x509CRL2 : next) {
                    if (crlSelector.match(x509CRL2)) {
                        collection.add(x509CRL2);
                    }
                }
            }
        }
    }
}
