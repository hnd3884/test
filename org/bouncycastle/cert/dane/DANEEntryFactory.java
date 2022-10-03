package org.bouncycastle.cert.dane;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestCalculator;

public class DANEEntryFactory
{
    private final DANEEntrySelectorFactory selectorFactory;
    
    public DANEEntryFactory(final DigestCalculator digestCalculator) {
        this.selectorFactory = new DANEEntrySelectorFactory(digestCalculator);
    }
    
    public DANEEntry createEntry(final String s, final X509CertificateHolder x509CertificateHolder) throws DANEException {
        return this.createEntry(s, 3, x509CertificateHolder);
    }
    
    public DANEEntry createEntry(final String s, final int n, final X509CertificateHolder x509CertificateHolder) throws DANEException {
        if (n < 0 || n > 3) {
            throw new DANEException("unknown certificate usage: " + n);
        }
        return new DANEEntry(this.selectorFactory.createSelector(s).getDomainName(), new byte[] { (byte)n, 0, 0 }, x509CertificateHolder);
    }
}
