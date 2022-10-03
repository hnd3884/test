package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Object;

public class ServiceLocator extends ASN1Object
{
    private final X500Name issuer;
    private final AuthorityInformationAccess locator;
    
    private ServiceLocator(final ASN1Sequence asn1Sequence) {
        this.issuer = X500Name.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() == 2) {
            this.locator = AuthorityInformationAccess.getInstance(asn1Sequence.getObjectAt(1));
        }
        else {
            this.locator = null;
        }
    }
    
    public static ServiceLocator getInstance(final Object o) {
        if (o instanceof ServiceLocator) {
            return (ServiceLocator)o;
        }
        if (o != null) {
            return new ServiceLocator(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public X500Name getIssuer() {
        return this.issuer;
    }
    
    public AuthorityInformationAccess getLocator() {
        return this.locator;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.issuer);
        if (this.locator != null) {
            asn1EncodableVector.add(this.locator);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
