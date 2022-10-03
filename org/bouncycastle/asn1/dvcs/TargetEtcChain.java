package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class TargetEtcChain extends ASN1Object
{
    private CertEtcToken target;
    private ASN1Sequence chain;
    private PathProcInput pathProcInput;
    
    public TargetEtcChain(final CertEtcToken certEtcToken) {
        this(certEtcToken, null, null);
    }
    
    public TargetEtcChain(final CertEtcToken certEtcToken, final CertEtcToken[] array) {
        this(certEtcToken, array, null);
    }
    
    public TargetEtcChain(final CertEtcToken certEtcToken, final PathProcInput pathProcInput) {
        this(certEtcToken, null, pathProcInput);
    }
    
    public TargetEtcChain(final CertEtcToken target, final CertEtcToken[] array, final PathProcInput pathProcInput) {
        this.target = target;
        if (array != null) {
            this.chain = new DERSequence(array);
        }
        this.pathProcInput = pathProcInput;
    }
    
    private TargetEtcChain(final ASN1Sequence asn1Sequence) {
        int n = 0;
        this.target = CertEtcToken.getInstance(asn1Sequence.getObjectAt(n++));
        if (asn1Sequence.size() > 1) {
            final ASN1Encodable object = asn1Sequence.getObjectAt(n++);
            if (object instanceof ASN1TaggedObject) {
                this.extractPathProcInput(object);
            }
            else {
                this.chain = ASN1Sequence.getInstance(object);
                if (asn1Sequence.size() > 2) {
                    this.extractPathProcInput(asn1Sequence.getObjectAt(n));
                }
            }
        }
    }
    
    private void extractPathProcInput(final ASN1Encodable asn1Encodable) {
        final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(asn1Encodable);
        switch (instance.getTagNo()) {
            case 0: {
                this.pathProcInput = PathProcInput.getInstance(instance, false);
                return;
            }
            default: {
                throw new IllegalArgumentException("Unknown tag encountered: " + instance.getTagNo());
            }
        }
    }
    
    public static TargetEtcChain getInstance(final Object o) {
        if (o instanceof TargetEtcChain) {
            return (TargetEtcChain)o;
        }
        if (o != null) {
            return new TargetEtcChain(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static TargetEtcChain getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.target);
        if (this.chain != null) {
            asn1EncodableVector.add(this.chain);
        }
        if (this.pathProcInput != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.pathProcInput));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("TargetEtcChain {\n");
        sb.append("target: " + this.target + "\n");
        if (this.chain != null) {
            sb.append("chain: " + this.chain + "\n");
        }
        if (this.pathProcInput != null) {
            sb.append("pathProcInput: " + this.pathProcInput + "\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
    
    public CertEtcToken getTarget() {
        return this.target;
    }
    
    public CertEtcToken[] getChain() {
        if (this.chain != null) {
            return CertEtcToken.arrayFromSequence(this.chain);
        }
        return null;
    }
    
    public PathProcInput getPathProcInput() {
        return this.pathProcInput;
    }
    
    public static TargetEtcChain[] arrayFromSequence(final ASN1Sequence asn1Sequence) {
        final TargetEtcChain[] array = new TargetEtcChain[asn1Sequence.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = getInstance(asn1Sequence.getObjectAt(i));
        }
        return array;
    }
}
