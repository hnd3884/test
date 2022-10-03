package com.maverick.crypto.asn1.x509;

import com.maverick.crypto.asn1.DERBoolean;
import com.maverick.crypto.asn1.ASN1OctetString;

public class X509Extension
{
    boolean c;
    ASN1OctetString b;
    
    public X509Extension(final DERBoolean derBoolean, final ASN1OctetString b) {
        this.c = derBoolean.isTrue();
        this.b = b;
    }
    
    public X509Extension(final boolean c, final ASN1OctetString b) {
        this.c = c;
        this.b = b;
    }
    
    public boolean isCritical() {
        return this.c;
    }
    
    public ASN1OctetString getValue() {
        return this.b;
    }
    
    public int hashCode() {
        if (this.isCritical()) {
            return this.getValue().hashCode();
        }
        return ~this.getValue().hashCode();
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof X509Extension)) {
            return false;
        }
        final X509Extension x509Extension = (X509Extension)o;
        return x509Extension.getValue().equals(this.getValue()) && x509Extension.isCritical() == this.isCritical();
    }
}
