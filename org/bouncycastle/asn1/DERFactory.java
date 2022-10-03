package org.bouncycastle.asn1;

class DERFactory
{
    static final ASN1Sequence EMPTY_SEQUENCE;
    static final ASN1Set EMPTY_SET;
    
    static ASN1Sequence createSequence(final ASN1EncodableVector asn1EncodableVector) {
        return (asn1EncodableVector.size() < 1) ? DERFactory.EMPTY_SEQUENCE : new DLSequence(asn1EncodableVector);
    }
    
    static ASN1Set createSet(final ASN1EncodableVector asn1EncodableVector) {
        return (asn1EncodableVector.size() < 1) ? DERFactory.EMPTY_SET : new DLSet(asn1EncodableVector);
    }
    
    static {
        EMPTY_SEQUENCE = new DERSequence();
        EMPTY_SET = new DERSet();
    }
}
