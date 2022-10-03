package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class KeyAgreeRecipientId extends RecipientId
{
    private X509CertificateHolderSelector baseSelector;
    
    private KeyAgreeRecipientId(final X509CertificateHolderSelector baseSelector) {
        super(2);
        this.baseSelector = baseSelector;
    }
    
    public KeyAgreeRecipientId(final byte[] array) {
        this(null, null, array);
    }
    
    public KeyAgreeRecipientId(final X500Name x500Name, final BigInteger bigInteger) {
        this(x500Name, bigInteger, null);
    }
    
    public KeyAgreeRecipientId(final X500Name x500Name, final BigInteger bigInteger, final byte[] array) {
        this(new X509CertificateHolderSelector(x500Name, bigInteger, array));
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
        return o instanceof KeyAgreeRecipientId && this.baseSelector.equals(((KeyAgreeRecipientId)o).baseSelector);
    }
    
    @Override
    public Object clone() {
        return new KeyAgreeRecipientId(this.baseSelector);
    }
    
    public boolean match(final Object o) {
        if (o instanceof KeyAgreeRecipientInformation) {
            return ((KeyAgreeRecipientInformation)o).getRID().equals(this);
        }
        return this.baseSelector.match(o);
    }
}
