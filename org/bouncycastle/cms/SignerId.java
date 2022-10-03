package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;
import org.bouncycastle.util.Selector;

public class SignerId implements Selector
{
    private X509CertificateHolderSelector baseSelector;
    
    private SignerId(final X509CertificateHolderSelector baseSelector) {
        this.baseSelector = baseSelector;
    }
    
    public SignerId(final byte[] array) {
        this(null, null, array);
    }
    
    public SignerId(final X500Name x500Name, final BigInteger bigInteger) {
        this(x500Name, bigInteger, null);
    }
    
    public SignerId(final X500Name x500Name, final BigInteger bigInteger, final byte[] array) {
        this(new X509CertificateHolderSelector(x500Name, bigInteger, array));
    }
    
    public X500Name getIssuer() {
        return this.baseSelector.getIssuer();
    }
    
    public BigInteger getSerialNumber() {
        return this.baseSelector.getSerialNumber();
    }
    
    public byte[] getSubjectKeyIdentifier() {
        return this.baseSelector.getSubjectKeyIdentifier();
    }
    
    @Override
    public int hashCode() {
        return this.baseSelector.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof SignerId && this.baseSelector.equals(((SignerId)o).baseSelector);
    }
    
    public boolean match(final Object o) {
        if (o instanceof SignerInformation) {
            return ((SignerInformation)o).getSID().equals(this);
        }
        return this.baseSelector.match(o);
    }
    
    public Object clone() {
        return new SignerId(this.baseSelector);
    }
}
