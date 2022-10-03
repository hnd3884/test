package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.ASN1Integer;

public class PKIHeaderBuilder
{
    private ASN1Integer pvno;
    private GeneralName sender;
    private GeneralName recipient;
    private ASN1GeneralizedTime messageTime;
    private AlgorithmIdentifier protectionAlg;
    private ASN1OctetString senderKID;
    private ASN1OctetString recipKID;
    private ASN1OctetString transactionID;
    private ASN1OctetString senderNonce;
    private ASN1OctetString recipNonce;
    private PKIFreeText freeText;
    private ASN1Sequence generalInfo;
    
    public PKIHeaderBuilder(final int n, final GeneralName generalName, final GeneralName generalName2) {
        this(new ASN1Integer(n), generalName, generalName2);
    }
    
    private PKIHeaderBuilder(final ASN1Integer pvno, final GeneralName sender, final GeneralName recipient) {
        this.pvno = pvno;
        this.sender = sender;
        this.recipient = recipient;
    }
    
    public PKIHeaderBuilder setMessageTime(final ASN1GeneralizedTime messageTime) {
        this.messageTime = messageTime;
        return this;
    }
    
    public PKIHeaderBuilder setProtectionAlg(final AlgorithmIdentifier protectionAlg) {
        this.protectionAlg = protectionAlg;
        return this;
    }
    
    public PKIHeaderBuilder setSenderKID(final byte[] array) {
        return this.setSenderKID((array == null) ? null : new DEROctetString(array));
    }
    
    public PKIHeaderBuilder setSenderKID(final ASN1OctetString senderKID) {
        this.senderKID = senderKID;
        return this;
    }
    
    public PKIHeaderBuilder setRecipKID(final byte[] array) {
        return this.setRecipKID((array == null) ? null : new DEROctetString(array));
    }
    
    public PKIHeaderBuilder setRecipKID(final DEROctetString recipKID) {
        this.recipKID = recipKID;
        return this;
    }
    
    public PKIHeaderBuilder setTransactionID(final byte[] array) {
        return this.setTransactionID((array == null) ? null : new DEROctetString(array));
    }
    
    public PKIHeaderBuilder setTransactionID(final ASN1OctetString transactionID) {
        this.transactionID = transactionID;
        return this;
    }
    
    public PKIHeaderBuilder setSenderNonce(final byte[] array) {
        return this.setSenderNonce((array == null) ? null : new DEROctetString(array));
    }
    
    public PKIHeaderBuilder setSenderNonce(final ASN1OctetString senderNonce) {
        this.senderNonce = senderNonce;
        return this;
    }
    
    public PKIHeaderBuilder setRecipNonce(final byte[] array) {
        return this.setRecipNonce((array == null) ? null : new DEROctetString(array));
    }
    
    public PKIHeaderBuilder setRecipNonce(final ASN1OctetString recipNonce) {
        this.recipNonce = recipNonce;
        return this;
    }
    
    public PKIHeaderBuilder setFreeText(final PKIFreeText freeText) {
        this.freeText = freeText;
        return this;
    }
    
    public PKIHeaderBuilder setGeneralInfo(final InfoTypeAndValue infoTypeAndValue) {
        return this.setGeneralInfo(makeGeneralInfoSeq(infoTypeAndValue));
    }
    
    public PKIHeaderBuilder setGeneralInfo(final InfoTypeAndValue[] array) {
        return this.setGeneralInfo(makeGeneralInfoSeq(array));
    }
    
    public PKIHeaderBuilder setGeneralInfo(final ASN1Sequence generalInfo) {
        this.generalInfo = generalInfo;
        return this;
    }
    
    private static ASN1Sequence makeGeneralInfoSeq(final InfoTypeAndValue infoTypeAndValue) {
        return new DERSequence(infoTypeAndValue);
    }
    
    private static ASN1Sequence makeGeneralInfoSeq(final InfoTypeAndValue[] array) {
        ASN1Sequence asn1Sequence = null;
        if (array != null) {
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            for (int i = 0; i < array.length; ++i) {
                asn1EncodableVector.add(array[i]);
            }
            asn1Sequence = new DERSequence(asn1EncodableVector);
        }
        return asn1Sequence;
    }
    
    public PKIHeader build() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.pvno);
        asn1EncodableVector.add(this.sender);
        asn1EncodableVector.add(this.recipient);
        this.addOptional(asn1EncodableVector, 0, this.messageTime);
        this.addOptional(asn1EncodableVector, 1, this.protectionAlg);
        this.addOptional(asn1EncodableVector, 2, this.senderKID);
        this.addOptional(asn1EncodableVector, 3, this.recipKID);
        this.addOptional(asn1EncodableVector, 4, this.transactionID);
        this.addOptional(asn1EncodableVector, 5, this.senderNonce);
        this.addOptional(asn1EncodableVector, 6, this.recipNonce);
        this.addOptional(asn1EncodableVector, 7, this.freeText);
        this.addOptional(asn1EncodableVector, 8, this.generalInfo);
        this.messageTime = null;
        this.protectionAlg = null;
        this.senderKID = null;
        this.recipKID = null;
        this.transactionID = null;
        this.senderNonce = null;
        this.recipNonce = null;
        this.freeText = null;
        this.generalInfo = null;
        return PKIHeader.getInstance(new DERSequence(asn1EncodableVector));
    }
    
    private void addOptional(final ASN1EncodableVector asn1EncodableVector, final int n, final ASN1Encodable asn1Encodable) {
        if (asn1Encodable != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, n, asn1Encodable));
        }
    }
}
