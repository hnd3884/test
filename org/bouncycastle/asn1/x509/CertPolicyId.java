package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class CertPolicyId extends ASN1Object
{
    private ASN1ObjectIdentifier id;
    
    private CertPolicyId(final ASN1ObjectIdentifier id) {
        this.id = id;
    }
    
    public static CertPolicyId getInstance(final Object o) {
        if (o instanceof CertPolicyId) {
            return (CertPolicyId)o;
        }
        if (o != null) {
            return new CertPolicyId(ASN1ObjectIdentifier.getInstance(o));
        }
        return null;
    }
    
    public String getId() {
        return this.id.getId();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.id;
    }
}
