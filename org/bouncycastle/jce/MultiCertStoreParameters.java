package org.bouncycastle.jce;

import java.util.Collection;
import java.security.cert.CertStoreParameters;

public class MultiCertStoreParameters implements CertStoreParameters
{
    private Collection certStores;
    private boolean searchAllStores;
    
    public MultiCertStoreParameters(final Collection collection) {
        this(collection, true);
    }
    
    public MultiCertStoreParameters(final Collection certStores, final boolean searchAllStores) {
        this.certStores = certStores;
        this.searchAllStores = searchAllStores;
    }
    
    public Collection getCertStores() {
        return this.certStores;
    }
    
    public boolean getSearchAllStores() {
        return this.searchAllStores;
    }
    
    public Object clone() {
        return this;
    }
}
