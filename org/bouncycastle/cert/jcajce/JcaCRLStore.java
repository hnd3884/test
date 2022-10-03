package org.bouncycastle.cert.jcajce;

import java.util.Iterator;
import java.io.IOException;
import org.bouncycastle.cert.X509CRLHolder;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.security.cert.CRLException;
import java.util.Collection;
import org.bouncycastle.util.CollectionStore;

public class JcaCRLStore extends CollectionStore
{
    public JcaCRLStore(final Collection collection) throws CRLException {
        super(convertCRLs(collection));
    }
    
    private static Collection convertCRLs(final Collection collection) throws CRLException {
        final ArrayList list = new ArrayList(collection.size());
        for (final Object next : collection) {
            if (next instanceof X509CRL) {
                try {
                    list.add(new X509CRLHolder(((X509CRL)next).getEncoded()));
                    continue;
                }
                catch (final IOException ex) {
                    throw new CRLException("cannot read encoding: " + ex.getMessage());
                }
            }
            list.add(next);
        }
        return list;
    }
}
