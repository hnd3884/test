package org.bouncycastle.cms;

import org.bouncycastle.util.Arrays;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.Selector;

class OriginatorId implements Selector
{
    private byte[] subjectKeyId;
    private X500Name issuer;
    private BigInteger serialNumber;
    
    public OriginatorId(final byte[] subjectKeyID) {
        this.setSubjectKeyID(subjectKeyID);
    }
    
    private void setSubjectKeyID(final byte[] subjectKeyId) {
        this.subjectKeyId = subjectKeyId;
    }
    
    public OriginatorId(final X500Name x500Name, final BigInteger bigInteger) {
        this.setIssuerAndSerial(x500Name, bigInteger);
    }
    
    private void setIssuerAndSerial(final X500Name issuer, final BigInteger serialNumber) {
        this.issuer = issuer;
        this.serialNumber = serialNumber;
    }
    
    public OriginatorId(final X500Name x500Name, final BigInteger bigInteger, final byte[] subjectKeyID) {
        this.setIssuerAndSerial(x500Name, bigInteger);
        this.setSubjectKeyID(subjectKeyID);
    }
    
    public X500Name getIssuer() {
        return this.issuer;
    }
    
    public Object clone() {
        return new OriginatorId(this.issuer, this.serialNumber, this.subjectKeyId);
    }
    
    @Override
    public int hashCode() {
        int hashCode = Arrays.hashCode(this.subjectKeyId);
        if (this.serialNumber != null) {
            hashCode ^= this.serialNumber.hashCode();
        }
        if (this.issuer != null) {
            hashCode ^= this.issuer.hashCode();
        }
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof OriginatorId)) {
            return false;
        }
        final OriginatorId originatorId = (OriginatorId)o;
        return Arrays.areEqual(this.subjectKeyId, originatorId.subjectKeyId) && this.equalsObj(this.serialNumber, originatorId.serialNumber) && this.equalsObj(this.issuer, originatorId.issuer);
    }
    
    private boolean equalsObj(final Object o, final Object o2) {
        return (o != null) ? o.equals(o2) : (o2 == null);
    }
    
    public boolean match(final Object o) {
        return false;
    }
}
