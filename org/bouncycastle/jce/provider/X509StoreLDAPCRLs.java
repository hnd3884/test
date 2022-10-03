package org.bouncycastle.jce.provider;

import org.bouncycastle.util.StoreException;
import java.util.HashSet;
import java.util.Collections;
import org.bouncycastle.x509.X509CRLStoreSelector;
import java.util.Collection;
import org.bouncycastle.util.Selector;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;
import org.bouncycastle.x509.X509StoreParameters;
import org.bouncycastle.x509.util.LDAPStoreHelper;
import org.bouncycastle.x509.X509StoreSpi;

public class X509StoreLDAPCRLs extends X509StoreSpi
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
        if (!(selector instanceof X509CRLStoreSelector)) {
            return Collections.EMPTY_SET;
        }
        final X509CRLStoreSelector x509CRLStoreSelector = (X509CRLStoreSelector)selector;
        final HashSet set = new HashSet();
        if (x509CRLStoreSelector.isDeltaCRLIndicatorEnabled()) {
            set.addAll(this.helper.getDeltaCertificateRevocationLists(x509CRLStoreSelector));
        }
        else {
            set.addAll(this.helper.getDeltaCertificateRevocationLists(x509CRLStoreSelector));
            set.addAll(this.helper.getAttributeAuthorityRevocationLists(x509CRLStoreSelector));
            set.addAll(this.helper.getAttributeCertificateRevocationLists(x509CRLStoreSelector));
            set.addAll(this.helper.getAuthorityRevocationLists(x509CRLStoreSelector));
            set.addAll(this.helper.getCertificateRevocationLists(x509CRLStoreSelector));
        }
        return set;
    }
}
