package org.bouncycastle.jce.provider;

import java.util.Iterator;
import org.bouncycastle.x509.X509CertificatePair;
import org.bouncycastle.x509.X509CertPairStoreSelector;
import org.bouncycastle.util.StoreException;
import java.util.HashSet;
import java.util.Collections;
import org.bouncycastle.x509.X509CertStoreSelector;
import java.util.Collection;
import org.bouncycastle.util.Selector;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;
import org.bouncycastle.x509.X509StoreParameters;
import org.bouncycastle.x509.util.LDAPStoreHelper;
import org.bouncycastle.x509.X509StoreSpi;

public class X509StoreLDAPCerts extends X509StoreSpi
{
    private LDAPStoreHelper helper;
    
    @Override
    public void engineInit(final X509StoreParameters x509StoreParameters) {
        if (!(x509StoreParameters instanceof X509LDAPCertStoreParameters)) {
            throw new IllegalArgumentException("Initialization parameters must be an instance of " + X509LDAPCertStoreParameters.class.getName() + ".");
        }
        this.helper = new LDAPStoreHelper((X509LDAPCertStoreParameters)x509StoreParameters);
    }
    
    @Override
    public Collection engineGetMatches(final Selector selector) throws StoreException {
        if (!(selector instanceof X509CertStoreSelector)) {
            return Collections.EMPTY_SET;
        }
        final X509CertStoreSelector x509CertStoreSelector = (X509CertStoreSelector)selector;
        final HashSet set = new HashSet();
        if (x509CertStoreSelector.getBasicConstraints() > 0) {
            set.addAll(this.helper.getCACertificates(x509CertStoreSelector));
            set.addAll(this.getCertificatesFromCrossCertificatePairs(x509CertStoreSelector));
        }
        else if (x509CertStoreSelector.getBasicConstraints() == -2) {
            set.addAll(this.helper.getUserCertificates(x509CertStoreSelector));
        }
        else {
            set.addAll(this.helper.getUserCertificates(x509CertStoreSelector));
            set.addAll(this.helper.getCACertificates(x509CertStoreSelector));
            set.addAll(this.getCertificatesFromCrossCertificatePairs(x509CertStoreSelector));
        }
        return set;
    }
    
    private Collection getCertificatesFromCrossCertificatePairs(final X509CertStoreSelector forwardSelector) throws StoreException {
        final HashSet set = new HashSet();
        final X509CertPairStoreSelector x509CertPairStoreSelector = new X509CertPairStoreSelector();
        x509CertPairStoreSelector.setForwardSelector(forwardSelector);
        x509CertPairStoreSelector.setReverseSelector(new X509CertStoreSelector());
        final HashSet set2 = new HashSet(this.helper.getCrossCertificatePairs(x509CertPairStoreSelector));
        final HashSet set3 = new HashSet();
        final HashSet set4 = new HashSet();
        for (final X509CertificatePair x509CertificatePair : set2) {
            if (x509CertificatePair.getForward() != null) {
                set3.add(x509CertificatePair.getForward());
            }
            if (x509CertificatePair.getReverse() != null) {
                set4.add(x509CertificatePair.getReverse());
            }
        }
        set.addAll(set3);
        set.addAll(set4);
        return set;
    }
}
