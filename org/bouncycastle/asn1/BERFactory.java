package org.bouncycastle.asn1;

class BERFactory
{
    static final BERSequence EMPTY_SEQUENCE;
    static final BERSet EMPTY_SET;
    
    static BERSequence createSequence(final ASN1EncodableVector asn1EncodableVector) {
        return (asn1EncodableVector.size() < 1) ? BERFactory.EMPTY_SEQUENCE : new BERSequence(asn1EncodableVector);
    }
    
    static BERSet createSet(final ASN1EncodableVector asn1EncodableVector) {
        return (asn1EncodableVector.size() < 1) ? BERFactory.EMPTY_SET : new BERSet(asn1EncodableVector);
    }
    
    static {
        EMPTY_SEQUENCE = new BERSequence();
        EMPTY_SET = new BERSet();
    }
}
