package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class CertRequest extends ASN1Object
{
    private ASN1Integer certReqId;
    private CertTemplate certTemplate;
    private Controls controls;
    
    private CertRequest(final ASN1Sequence asn1Sequence) {
        this.certReqId = new ASN1Integer(ASN1Integer.getInstance(asn1Sequence.getObjectAt(0)).getValue());
        this.certTemplate = CertTemplate.getInstance(asn1Sequence.getObjectAt(1));
        if (asn1Sequence.size() > 2) {
            this.controls = Controls.getInstance(asn1Sequence.getObjectAt(2));
        }
    }
    
    public static CertRequest getInstance(final Object o) {
        if (o instanceof CertRequest) {
            return (CertRequest)o;
        }
        if (o != null) {
            return new CertRequest(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public CertRequest(final int n, final CertTemplate certTemplate, final Controls controls) {
        this(new ASN1Integer(n), certTemplate, controls);
    }
    
    public CertRequest(final ASN1Integer certReqId, final CertTemplate certTemplate, final Controls controls) {
        this.certReqId = certReqId;
        this.certTemplate = certTemplate;
        this.controls = controls;
    }
    
    public ASN1Integer getCertReqId() {
        return this.certReqId;
    }
    
    public CertTemplate getCertTemplate() {
        return this.certTemplate;
    }
    
    public Controls getControls() {
        return this.controls;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.certReqId);
        asn1EncodableVector.add(this.certTemplate);
        if (this.controls != null) {
            asn1EncodableVector.add(this.controls);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
