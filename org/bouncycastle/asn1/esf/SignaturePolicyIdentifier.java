package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;

public class SignaturePolicyIdentifier extends ASN1Object
{
    private SignaturePolicyId signaturePolicyId;
    private boolean isSignaturePolicyImplied;
    
    public static SignaturePolicyIdentifier getInstance(final Object o) {
        if (o instanceof SignaturePolicyIdentifier) {
            return (SignaturePolicyIdentifier)o;
        }
        if (o instanceof ASN1Null || ASN1Object.hasEncodedTagValue(o, 5)) {
            return new SignaturePolicyIdentifier();
        }
        if (o != null) {
            return new SignaturePolicyIdentifier(SignaturePolicyId.getInstance(o));
        }
        return null;
    }
    
    public SignaturePolicyIdentifier() {
        this.isSignaturePolicyImplied = true;
    }
    
    public SignaturePolicyIdentifier(final SignaturePolicyId signaturePolicyId) {
        this.signaturePolicyId = signaturePolicyId;
        this.isSignaturePolicyImplied = false;
    }
    
    public SignaturePolicyId getSignaturePolicyId() {
        return this.signaturePolicyId;
    }
    
    public boolean isSignaturePolicyImplied() {
        return this.isSignaturePolicyImplied;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.isSignaturePolicyImplied) {
            return DERNull.INSTANCE;
        }
        return this.signaturePolicyId.toASN1Primitive();
    }
}
