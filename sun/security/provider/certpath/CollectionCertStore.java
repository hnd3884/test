package sun.security.provider.certpath;

import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.util.Iterator;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertSelector;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.CertStoreParameters;
import java.util.Collection;
import java.security.cert.CertStoreSpi;

public class CollectionCertStore extends CertStoreSpi
{
    private Collection<?> coll;
    
    public CollectionCertStore(final CertStoreParameters certStoreParameters) throws InvalidAlgorithmParameterException {
        super(certStoreParameters);
        if (!(certStoreParameters instanceof CollectionCertStoreParameters)) {
            throw new InvalidAlgorithmParameterException("parameters must be CollectionCertStoreParameters");
        }
        this.coll = ((CollectionCertStoreParameters)certStoreParameters).getCollection();
    }
    
    @Override
    public Collection<Certificate> engineGetCertificates(final CertSelector certSelector) throws CertStoreException {
        if (this.coll == null) {
            throw new CertStoreException("Collection is null");
        }
        int i = 0;
        while (i < 10) {
            try {
                final HashSet<Certificate> set = new HashSet<Certificate>();
                if (certSelector != null) {
                    for (final Object next : this.coll) {
                        if (next instanceof Certificate && certSelector.match((Certificate)next)) {
                            set.add((Certificate)next);
                        }
                    }
                }
                else {
                    for (final Object next2 : this.coll) {
                        if (next2 instanceof Certificate) {
                            set.add((Certificate)next2);
                        }
                    }
                }
                return set;
            }
            catch (final ConcurrentModificationException ex) {
                ++i;
                continue;
            }
            break;
        }
        throw new ConcurrentModificationException("Too many ConcurrentModificationExceptions");
    }
    
    @Override
    public Collection<CRL> engineGetCRLs(final CRLSelector crlSelector) throws CertStoreException {
        if (this.coll == null) {
            throw new CertStoreException("Collection is null");
        }
        int i = 0;
        while (i < 10) {
            try {
                final HashSet<CRL> set = new HashSet<CRL>();
                if (crlSelector != null) {
                    for (final Object next : this.coll) {
                        if (next instanceof CRL && crlSelector.match((CRL)next)) {
                            set.add((CRL)next);
                        }
                    }
                }
                else {
                    for (final Object next2 : this.coll) {
                        if (next2 instanceof CRL) {
                            set.add((CRL)next2);
                        }
                    }
                }
                return set;
            }
            catch (final ConcurrentModificationException ex) {
                ++i;
                continue;
            }
            break;
        }
        throw new ConcurrentModificationException("Too many ConcurrentModificationExceptions");
    }
}
