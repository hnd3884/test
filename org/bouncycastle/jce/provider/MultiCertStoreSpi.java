package org.bouncycastle.jce.provider;

import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Iterator;
import java.security.cert.CertStore;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import java.security.cert.CertSelector;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertStoreParameters;
import org.bouncycastle.jce.MultiCertStoreParameters;
import java.security.cert.CertStoreSpi;

public class MultiCertStoreSpi extends CertStoreSpi
{
    private MultiCertStoreParameters params;
    
    public MultiCertStoreSpi(final CertStoreParameters certStoreParameters) throws InvalidAlgorithmParameterException {
        super(certStoreParameters);
        if (!(certStoreParameters instanceof MultiCertStoreParameters)) {
            throw new InvalidAlgorithmParameterException("org.bouncycastle.jce.provider.MultiCertStoreSpi: parameter must be a MultiCertStoreParameters object\n" + certStoreParameters.toString());
        }
        this.params = (MultiCertStoreParameters)certStoreParameters;
    }
    
    @Override
    public Collection engineGetCertificates(final CertSelector certSelector) throws CertStoreException {
        final boolean searchAllStores = this.params.getSearchAllStores();
        final Iterator iterator = this.params.getCertStores().iterator();
        final List list = searchAllStores ? new ArrayList() : Collections.EMPTY_LIST;
        while (iterator.hasNext()) {
            final Collection<? extends Certificate> certificates = ((CertStore)iterator.next()).getCertificates(certSelector);
            if (searchAllStores) {
                list.addAll(certificates);
            }
            else {
                if (!certificates.isEmpty()) {
                    return certificates;
                }
                continue;
            }
        }
        return list;
    }
    
    @Override
    public Collection engineGetCRLs(final CRLSelector crlSelector) throws CertStoreException {
        final boolean searchAllStores = this.params.getSearchAllStores();
        final Iterator iterator = this.params.getCertStores().iterator();
        final List list = searchAllStores ? new ArrayList() : Collections.EMPTY_LIST;
        while (iterator.hasNext()) {
            final Collection<? extends CRL> crLs = ((CertStore)iterator.next()).getCRLs(crlSelector);
            if (searchAllStores) {
                list.addAll(crLs);
            }
            else {
                if (!crLs.isEmpty()) {
                    return crLs;
                }
                continue;
            }
        }
        return list;
    }
}
