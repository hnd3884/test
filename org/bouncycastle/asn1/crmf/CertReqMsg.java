package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CertReqMsg extends ASN1Object
{
    private CertRequest certReq;
    private ProofOfPossession pop;
    private ASN1Sequence regInfo;
    
    private CertReqMsg(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.certReq = CertRequest.getInstance(objects.nextElement());
        while (objects.hasMoreElements()) {
            final Object nextElement = objects.nextElement();
            if (nextElement instanceof ASN1TaggedObject || nextElement instanceof ProofOfPossession) {
                this.pop = ProofOfPossession.getInstance(nextElement);
            }
            else {
                this.regInfo = ASN1Sequence.getInstance(nextElement);
            }
        }
    }
    
    public static CertReqMsg getInstance(final Object o) {
        if (o instanceof CertReqMsg) {
            return (CertReqMsg)o;
        }
        if (o != null) {
            return new CertReqMsg(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static CertReqMsg getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public CertReqMsg(final CertRequest certReq, final ProofOfPossession pop, final AttributeTypeAndValue[] array) {
        if (certReq == null) {
            throw new IllegalArgumentException("'certReq' cannot be null");
        }
        this.certReq = certReq;
        this.pop = pop;
        if (array != null) {
            this.regInfo = new DERSequence(array);
        }
    }
    
    public CertRequest getCertReq() {
        return this.certReq;
    }
    
    @Deprecated
    public ProofOfPossession getPop() {
        return this.pop;
    }
    
    public ProofOfPossession getPopo() {
        return this.pop;
    }
    
    public AttributeTypeAndValue[] getRegInfo() {
        if (this.regInfo == null) {
            return null;
        }
        final AttributeTypeAndValue[] array = new AttributeTypeAndValue[this.regInfo.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = AttributeTypeAndValue.getInstance(this.regInfo.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.certReq);
        this.addOptional(asn1EncodableVector, this.pop);
        this.addOptional(asn1EncodableVector, this.regInfo);
        return new DERSequence(asn1EncodableVector);
    }
    
    private void addOptional(final ASN1EncodableVector asn1EncodableVector, final ASN1Encodable asn1Encodable) {
        if (asn1Encodable != null) {
            asn1EncodableVector.add(asn1Encodable);
        }
    }
}
