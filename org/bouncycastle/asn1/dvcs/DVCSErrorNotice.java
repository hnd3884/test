package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.ASN1Object;

public class DVCSErrorNotice extends ASN1Object
{
    private PKIStatusInfo transactionStatus;
    private GeneralName transactionIdentifier;
    
    public DVCSErrorNotice(final PKIStatusInfo pkiStatusInfo) {
        this(pkiStatusInfo, null);
    }
    
    public DVCSErrorNotice(final PKIStatusInfo transactionStatus, final GeneralName transactionIdentifier) {
        this.transactionStatus = transactionStatus;
        this.transactionIdentifier = transactionIdentifier;
    }
    
    private DVCSErrorNotice(final ASN1Sequence asn1Sequence) {
        this.transactionStatus = PKIStatusInfo.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() > 1) {
            this.transactionIdentifier = GeneralName.getInstance(asn1Sequence.getObjectAt(1));
        }
    }
    
    public static DVCSErrorNotice getInstance(final Object o) {
        if (o instanceof DVCSErrorNotice) {
            return (DVCSErrorNotice)o;
        }
        if (o != null) {
            return new DVCSErrorNotice(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static DVCSErrorNotice getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.transactionStatus);
        if (this.transactionIdentifier != null) {
            asn1EncodableVector.add(this.transactionIdentifier);
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    @Override
    public String toString() {
        return "DVCSErrorNotice {\ntransactionStatus: " + this.transactionStatus + "\n" + ((this.transactionIdentifier != null) ? ("transactionIdentifier: " + this.transactionIdentifier + "\n") : "") + "}\n";
    }
    
    public PKIStatusInfo getTransactionStatus() {
        return this.transactionStatus;
    }
    
    public GeneralName getTransactionIdentifier() {
        return this.transactionIdentifier;
    }
}
