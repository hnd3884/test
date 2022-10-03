package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class PolicyMappings extends ASN1Object
{
    ASN1Sequence seq;
    
    public static PolicyMappings getInstance(final Object o) {
        if (o instanceof PolicyMappings) {
            return (PolicyMappings)o;
        }
        if (o != null) {
            return new PolicyMappings(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private PolicyMappings(final ASN1Sequence seq) {
        this.seq = null;
        this.seq = seq;
    }
    
    @Deprecated
    public PolicyMappings(final Hashtable hashtable) {
        this.seq = null;
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final String s = (String)keys.nextElement();
            final String s2 = hashtable.get(s);
            final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
            asn1EncodableVector2.add(new ASN1ObjectIdentifier(s));
            asn1EncodableVector2.add(new ASN1ObjectIdentifier(s2));
            asn1EncodableVector.add(new DERSequence(asn1EncodableVector2));
        }
        this.seq = new DERSequence(asn1EncodableVector);
    }
    
    public PolicyMappings(final CertPolicyId certPolicyId, final CertPolicyId certPolicyId2) {
        this.seq = null;
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(certPolicyId);
        asn1EncodableVector.add(certPolicyId2);
        this.seq = new DERSequence(new DERSequence(asn1EncodableVector));
    }
    
    public PolicyMappings(final CertPolicyId[] array, final CertPolicyId[] array2) {
        this.seq = null;
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != array.length; ++i) {
            final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
            asn1EncodableVector2.add(array[i]);
            asn1EncodableVector2.add(array2[i]);
            asn1EncodableVector.add(new DERSequence(asn1EncodableVector2));
        }
        this.seq = new DERSequence(asn1EncodableVector);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}
