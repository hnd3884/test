package org.bouncycastle.jce.provider;

import org.bouncycastle.util.StoreException;
import java.util.HashSet;
import java.util.Collections;
import org.bouncycastle.x509.X509AttributeCertStoreSelector;
import java.util.Collection;
import org.bouncycastle.util.Selector;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;
import org.bouncycastle.x509.X509StoreParameters;
import org.bouncycastle.x509.util.LDAPStoreHelper;
import org.bouncycastle.x509.X509StoreSpi;

public class X509StoreLDAPAttrCerts extends X509StoreSpi
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
        if (!(selector instanceof X509AttributeCertStoreSelector)) {
            return Collections.EMPTY_SET;
        }
        final X509AttributeCertStoreSelector x509AttributeCertStoreSelector = (X509AttributeCertStoreSelector)selector;
        final HashSet set = new HashSet();
        set.addAll(this.helper.getAACertificates(x509AttributeCertStoreSelector));
        set.addAll(this.helper.getAttributeCertificateAttributes(x509AttributeCertStoreSelector));
        set.addAll(this.helper.getAttributeDescriptorCertificates(x509AttributeCertStoreSelector));
        return set;
    }
}
