package org.bouncycastle.jce.provider;

import org.bouncycastle.util.StoreException;
import java.util.HashSet;
import java.util.Collections;
import org.bouncycastle.x509.X509CertPairStoreSelector;
import java.util.Collection;
import org.bouncycastle.util.Selector;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;
import org.bouncycastle.x509.X509StoreParameters;
import org.bouncycastle.x509.util.LDAPStoreHelper;
import org.bouncycastle.x509.X509StoreSpi;

public class X509StoreLDAPCertPairs extends X509StoreSpi
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
        if (!(selector instanceof X509CertPairStoreSelector)) {
            return Collections.EMPTY_SET;
        }
        final X509CertPairStoreSelector x509CertPairStoreSelector = (X509CertPairStoreSelector)selector;
        final HashSet set = new HashSet();
        set.addAll(this.helper.getCrossCertificatePairs(x509CertPairStoreSelector));
        return set;
    }
}
