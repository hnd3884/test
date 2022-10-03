package org.bouncycastle.asn1.dvcs;

import java.util.Arrays;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.ASN1Object;

public class PathProcInput extends ASN1Object
{
    private PolicyInformation[] acceptablePolicySet;
    private boolean inhibitPolicyMapping;
    private boolean explicitPolicyReqd;
    private boolean inhibitAnyPolicy;
    
    public PathProcInput(final PolicyInformation[] acceptablePolicySet) {
        this.inhibitPolicyMapping = false;
        this.explicitPolicyReqd = false;
        this.inhibitAnyPolicy = false;
        this.acceptablePolicySet = acceptablePolicySet;
    }
    
    public PathProcInput(final PolicyInformation[] acceptablePolicySet, final boolean inhibitPolicyMapping, final boolean explicitPolicyReqd, final boolean inhibitAnyPolicy) {
        this.inhibitPolicyMapping = false;
        this.explicitPolicyReqd = false;
        this.inhibitAnyPolicy = false;
        this.acceptablePolicySet = acceptablePolicySet;
        this.inhibitPolicyMapping = inhibitPolicyMapping;
        this.explicitPolicyReqd = explicitPolicyReqd;
        this.inhibitAnyPolicy = inhibitAnyPolicy;
    }
    
    private static PolicyInformation[] fromSequence(final ASN1Sequence asn1Sequence) {
        final PolicyInformation[] array = new PolicyInformation[asn1Sequence.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = PolicyInformation.getInstance(asn1Sequence.getObjectAt(i));
        }
        return array;
    }
    
    public static PathProcInput getInstance(final Object o) {
        if (o instanceof PathProcInput) {
            return (PathProcInput)o;
        }
        if (o != null) {
            final ASN1Sequence instance = ASN1Sequence.getInstance(o);
            final PathProcInput pathProcInput = new PathProcInput(fromSequence(ASN1Sequence.getInstance(instance.getObjectAt(0))));
            for (int i = 1; i < instance.size(); ++i) {
                final ASN1Encodable object = instance.getObjectAt(i);
                if (object instanceof ASN1Boolean) {
                    pathProcInput.setInhibitPolicyMapping(ASN1Boolean.getInstance(object).isTrue());
                }
                else if (object instanceof ASN1TaggedObject) {
                    final ASN1TaggedObject instance2 = ASN1TaggedObject.getInstance(object);
                    switch (instance2.getTagNo()) {
                        case 0: {
                            pathProcInput.setExplicitPolicyReqd(ASN1Boolean.getInstance(instance2, false).isTrue());
                            break;
                        }
                        case 1: {
                            pathProcInput.setInhibitAnyPolicy(ASN1Boolean.getInstance(instance2, false).isTrue());
                            break;
                        }
                        default: {
                            throw new IllegalArgumentException("Unknown tag encountered: " + instance2.getTagNo());
                        }
                    }
                }
            }
            return pathProcInput;
        }
        return null;
    }
    
    public static PathProcInput getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
        for (int i = 0; i != this.acceptablePolicySet.length; ++i) {
            asn1EncodableVector2.add(this.acceptablePolicySet[i]);
        }
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector2));
        if (this.inhibitPolicyMapping) {
            asn1EncodableVector.add(ASN1Boolean.getInstance(this.inhibitPolicyMapping));
        }
        if (this.explicitPolicyReqd) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, ASN1Boolean.getInstance(this.explicitPolicyReqd)));
        }
        if (this.inhibitAnyPolicy) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, ASN1Boolean.getInstance(this.inhibitAnyPolicy)));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    @Override
    public String toString() {
        return "PathProcInput: {\nacceptablePolicySet: " + Arrays.asList(this.acceptablePolicySet) + "\ninhibitPolicyMapping: " + this.inhibitPolicyMapping + "\nexplicitPolicyReqd: " + this.explicitPolicyReqd + "\ninhibitAnyPolicy: " + this.inhibitAnyPolicy + "\n}\n";
    }
    
    public PolicyInformation[] getAcceptablePolicySet() {
        return this.acceptablePolicySet;
    }
    
    public boolean isInhibitPolicyMapping() {
        return this.inhibitPolicyMapping;
    }
    
    private void setInhibitPolicyMapping(final boolean inhibitPolicyMapping) {
        this.inhibitPolicyMapping = inhibitPolicyMapping;
    }
    
    public boolean isExplicitPolicyReqd() {
        return this.explicitPolicyReqd;
    }
    
    private void setExplicitPolicyReqd(final boolean explicitPolicyReqd) {
        this.explicitPolicyReqd = explicitPolicyReqd;
    }
    
    public boolean isInhibitAnyPolicy() {
        return this.inhibitAnyPolicy;
    }
    
    private void setInhibitAnyPolicy(final boolean inhibitAnyPolicy) {
        this.inhibitAnyPolicy = inhibitAnyPolicy;
    }
}
