package org.bouncycastle.jce.provider;

import java.security.cert.CertStoreException;
import java.security.cert.CRL;
import java.security.cert.CertStore;
import org.bouncycastle.util.StoreException;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import java.util.Collection;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.security.cert.X509CRL;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Date;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;

class PKIXCRLUtil
{
    public Set findCRLs(final PKIXCRLStoreSelector pkixcrlStoreSelector, final Date date, final List list, final List list2) throws AnnotatedException {
        final HashSet set = new HashSet();
        try {
            set.addAll(this.findCRLs(pkixcrlStoreSelector, list2));
            set.addAll(this.findCRLs(pkixcrlStoreSelector, list));
        }
        catch (final AnnotatedException ex) {
            throw new AnnotatedException("Exception obtaining complete CRLs.", ex);
        }
        final HashSet set2 = new HashSet();
        for (final X509CRL x509CRL : set) {
            if (x509CRL.getNextUpdate().after(date)) {
                final X509Certificate certificateChecking = pkixcrlStoreSelector.getCertificateChecking();
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
    
    private final Collection findCRLs(final PKIXCRLStoreSelector pkixcrlStoreSelector, final List list) throws AnnotatedException {
        final HashSet set = new HashSet();
        final Iterator iterator = list.iterator();
        Object o = null;
        boolean b = false;
        while (iterator.hasNext()) {
            final Object next = iterator.next();
            if (next instanceof Store) {
                final Store store = (Store)next;
                try {
                    set.addAll(store.getMatches(pkixcrlStoreSelector));
                    b = true;
                }
                catch (final StoreException ex) {
                    o = new AnnotatedException("Exception searching in X.509 CRL store.", ex);
                }
            }
            else {
                final CertStore certStore = (CertStore)next;
                try {
                    set.addAll(PKIXCRLStoreSelector.getCRLs(pkixcrlStoreSelector, certStore));
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
