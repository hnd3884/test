package org.bouncycastle.x509;

import java.security.cert.CertStoreException;
import java.security.cert.CRLSelector;
import java.security.cert.CertStore;
import org.bouncycastle.util.StoreException;
import org.bouncycastle.util.Selector;
import java.util.Collection;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.security.cert.X509CRL;
import org.bouncycastle.jce.provider.AnnotatedException;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;

class PKIXCRLUtil
{
    public Set findCRLs(final X509CRLStoreSelector x509CRLStoreSelector, final ExtendedPKIXParameters extendedPKIXParameters, final Date date) throws AnnotatedException {
        final HashSet set = new HashSet();
        try {
            set.addAll(this.findCRLs(x509CRLStoreSelector, extendedPKIXParameters.getAdditionalStores()));
            set.addAll(this.findCRLs(x509CRLStoreSelector, extendedPKIXParameters.getStores()));
            set.addAll(this.findCRLs(x509CRLStoreSelector, extendedPKIXParameters.getCertStores()));
        }
        catch (final AnnotatedException ex) {
            throw new AnnotatedException("Exception obtaining complete CRLs.", ex);
        }
        final HashSet set2 = new HashSet();
        Date date2 = date;
        if (extendedPKIXParameters.getDate() != null) {
            date2 = extendedPKIXParameters.getDate();
        }
        for (final X509CRL x509CRL : set) {
            if (x509CRL.getNextUpdate().after(date2)) {
                final X509Certificate certificateChecking = x509CRLStoreSelector.getCertificateChecking();
                if (certificateChecking != null) {
                    if (!x509CRL.getThisUpdate().before(certificateChecking.getNotAfter())) {
                        continue;
                    }
                    set2.add(x509CRL);
                }
                else {
                    set2.add(x509CRL);
                }
            }
        }
        return set2;
    }
    
    public Set findCRLs(final X509CRLStoreSelector x509CRLStoreSelector, final PKIXParameters pkixParameters) throws AnnotatedException {
        final HashSet set = new HashSet();
        try {
            set.addAll(this.findCRLs(x509CRLStoreSelector, pkixParameters.getCertStores()));
        }
        catch (final AnnotatedException ex) {
            throw new AnnotatedException("Exception obtaining complete CRLs.", ex);
        }
        return set;
    }
    
    private final Collection findCRLs(final X509CRLStoreSelector x509CRLStoreSelector, final List list) throws AnnotatedException {
        final HashSet set = new HashSet();
        final Iterator iterator = list.iterator();
        Object o = null;
        boolean b = false;
        while (iterator.hasNext()) {
            final Object next = iterator.next();
            if (next instanceof X509Store) {
                final X509Store x509Store = (X509Store)next;
                try {
                    set.addAll(x509Store.getMatches(x509CRLStoreSelector));
                    b = true;
                }
                catch (final StoreException ex) {
                    o = new AnnotatedException("Exception searching in X.509 CRL store.", ex);
                }
            }
            else {
                final CertStore certStore = (CertStore)next;
                try {
                    set.addAll(certStore.getCRLs(x509CRLStoreSelector));
                    b = true;
                }
                catch (final CertStoreException ex2) {
                    o = new AnnotatedException("Exception searching in X.509 CRL store.", ex2);
                }
            }
        }
        if (!b && o != null) {
            throw o;
        }
        return set;
    }
}
