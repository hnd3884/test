package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class BasicOCSPResponse extends ASN1Object
{
    private ResponseData tbsResponseData;
    private AlgorithmIdentifier signatureAlgorithm;
    private DERBitString signature;
    private ASN1Sequence certs;
    
    public BasicOCSPResponse(final ResponseData tbsResponseData, final AlgorithmIdentifier signatureAlgorithm, final DERBitString signature, final ASN1Sequence certs) {
        this.tbsResponseData = tbsResponseData;
        this.signatureAlgorithm = signatureAlgorithm;
        this.signature = signature;
        this.certs = certs;
    }
    
    private BasicOCSPResponse(final ASN1Sequence asn1Sequence) {
        this.tbsResponseData = ResponseData.getInstance(asn1Sequence.getObjectAt(0));
        this.signatureAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        this.signature = (DERBitString)asn1Sequence.getObjectAt(2);
        if (asn1Sequence.size() > 3) {
            this.certs = ASN1Sequence.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(3), true);
        }
    }
    
    public static BasicOCSPResponse getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static BasicOCSPResponse getInstance(final Object o) {
        if (o instanceof BasicOCSPResponse) {
            return (BasicOCSPResponse)o;
        }
        if (o != null) {
            return new BasicOCSPResponse(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ResponseData getTbsResponseData() {
        return this.tbsResponseData;
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }
    
    public DERBitString getSignature() {
        return this.signature;
    }
    
    public ASN1Sequence getCerts() {
        return this.certs;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.tbsResponseData);
        asn1EncodableVector.add(this.signatureAlgorithm);
        asn1EncodableVector.add(this.signature);
        if (this.certs != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.certs));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
