package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class CertificationRequest extends ASN1Object
{
    protected CertificationRequestInfo reqInfo;
    protected AlgorithmIdentifier sigAlgId;
    protected DERBitString sigBits;
    
    public static CertificationRequest getInstance(final Object o) {
        if (o instanceof CertificationRequest) {
            return (CertificationRequest)o;
        }
        if (o != null) {
            return new CertificationRequest(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    protected CertificationRequest() {
        this.reqInfo = null;
        this.sigAlgId = null;
        this.sigBits = null;
    }
    
    public CertificationRequest(final CertificationRequestInfo reqInfo, final AlgorithmIdentifier sigAlgId, final DERBitString sigBits) {
        this.reqInfo = null;
        this.sigAlgId = null;
        this.sigBits = null;
        this.reqInfo = reqInfo;
        this.sigAlgId = sigAlgId;
        this.sigBits = sigBits;
    }
    
    @Deprecated
    public CertificationRequest(final ASN1Sequence asn1Sequence) {
        this.reqInfo = null;
        this.sigAlgId = null;
        this.sigBits = null;
        this.reqInfo = CertificationRequestInfo.getInstance(asn1Sequence.getObjectAt(0));
        this.sigAlgId = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        this.sigBits = (DERBitString)asn1Sequence.getObjectAt(2);
    }
    
    public CertificationRequestInfo getCertificationRequestInfo() {
        return this.reqInfo;
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.sigAlgId;
    }
    
    public DERBitString getSignature() {
        return this.sigBits;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.reqInfo);
        asn1EncodableVector.add(this.sigAlgId);
        asn1EncodableVector.add(this.sigBits);
        return new DERSequence(asn1EncodableVector);
    }
}
