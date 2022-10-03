package org.bouncycastle.cert.jcajce;

import java.util.Iterator;
import java.io.IOException;
import org.bouncycastle.cert.X509CertificateHolder;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.security.cert.CertificateEncodingException;
import java.util.Collection;
import org.bouncycastle.util.CollectionStore;

public class JcaCertStore extends CollectionStore
{
    public JcaCertStore(final Collection collection) throws CertificateEncodingException {
        super(convertCerts(collection));
    }
    
    private static Collection convertCerts(final Collection collection) throws CertificateEncodingException {
        final ArrayList list = new ArrayList(collection.size());
        for (final Object next : collection) {
            if (next instanceof X509Certificate) {
                final X509Certificate x509Certificate = (X509Certificate)next;
                try {
                    list.add(new X509CertificateHolder(x509Certificate.getEncoded()));
                }
                catch (final IOException ex) {
                    throw new CertificateEncodingException("unable to read encoding: " + ex.getMessage());
                }
            }
            else {
                list.add(next);
            }
        }
        return list;
    }
}
