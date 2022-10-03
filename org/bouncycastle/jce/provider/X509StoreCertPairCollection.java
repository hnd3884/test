package org.bouncycastle.jce.provider;

import org.bouncycastle.util.Selector;
import java.util.Collection;
import org.bouncycastle.x509.X509CollectionStoreParameters;
import org.bouncycastle.x509.X509StoreParameters;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.x509.X509StoreSpi;

public class X509StoreCertPairCollection extends X509StoreSpi
{
    private CollectionStore _store;
    
    @Override
    public void engineInit(final X509StoreParameters x509StoreParameters) {
        if (!(x509StoreParameters instanceof X509CollectionStoreParameters)) {
            throw new IllegalArgumentException("Initialization parameters must be an instance of " + X509CollectionStoreParameters.class.getName() + ".");
        }
        this._store = new CollectionStore(((X509CollectionStoreParameters)x509StoreParameters).getCollection());
    }
    
    @Override
    public Collection engineGetMatches(final Selector selector) {
        return this._store.getMatches(selector);
    }
}
