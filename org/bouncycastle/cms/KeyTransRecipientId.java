package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class KeyTransRecipientId extends RecipientId
{
    private X509CertificateHolderSelector baseSelector;
    
    private KeyTransRecipientId(final X509CertificateHolderSelector baseSelector) {
        super(0);
        this.baseSelector = baseSelector;
    }
    
    public KeyTransRecipientId(final byte[] array) {
        this(null, null, array);
    }
    
    public KeyTransRecipientId(final X500Name x500Name, final BigInteger bigInteger) {
        this(x500Name, bigInteger, null);
    }
    
    public KeyTransRecipientId(final X500Name x500Name, final BigInteger bigInteger, final byte[] array) {
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
        return o instanceof KeyTransRecipientId && this.baseSelector.equals(((KeyTransRecipientId)o).baseSelector);
    }
    
    @Override
    public Object clone() {
        return new KeyTransRecipientId(this.baseSelector);
    }
    
    public boolean match(final Object o) {
        if (o instanceof KeyTransRecipientInformation) {
            return ((KeyTransRecipientInformation)o).getRID().equals(this);
        }
        return this.baseSelector.match(o);
    }
}
