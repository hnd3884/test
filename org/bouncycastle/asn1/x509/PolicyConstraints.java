package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;

public class PolicyConstraints extends ASN1Object
{
    private BigInteger requireExplicitPolicyMapping;
    private BigInteger inhibitPolicyMapping;
    
    public PolicyConstraints(final BigInteger requireExplicitPolicyMapping, final BigInteger inhibitPolicyMapping) {
        this.requireExplicitPolicyMapping = requireExplicitPolicyMapping;
        this.inhibitPolicyMapping = inhibitPolicyMapping;
    }
    
    private PolicyConstraints(final ASN1Sequence asn1Sequence) {
        for (int i = 0; i != asn1Sequence.size(); ++i) {
            final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(asn1Sequence.getObjectAt(i));
            if (instance.getTagNo() == 0) {
                this.requireExplicitPolicyMapping = ASN1Integer.getInstance(instance, false).getValue();
            }
            else {
                if (instance.getTagNo() != 1) {
                    throw new IllegalArgumentException("Unknown tag encountered.");
                }
                this.inhibitPolicyMapping = ASN1Integer.getInstance(instance, false).getValue();
            }
        }
    }
    
    public static PolicyConstraints getInstance(final Object o) {
        if (o instanceof PolicyConstraints) {
            return (PolicyConstraints)o;
        }
        if (o != null) {
            return new PolicyConstraints(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static PolicyConstraints fromExtensions(final Extensions extensions) {
        return getInstance(extensions.getExtensionParsedValue(Extension.policyConstraints));
    }
    
    public BigInteger getRequireExplicitPolicyMapping() {
        return this.requireExplicitPolicyMapping;
    }
    
    public BigInteger getInhibitPolicyMapping() {
        return this.inhibitPolicyMapping;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.requireExplicitPolicyMapping != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, new ASN1Integer(this.requireExplicitPolicyMapping)));
        }
        if (this.inhibitPolicyMapping != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, new ASN1Integer(this.inhibitPolicyMapping)));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
