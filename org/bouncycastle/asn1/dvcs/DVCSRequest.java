package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.ASN1Object;

public class DVCSRequest extends ASN1Object
{
    private DVCSRequestInformation requestInformation;
    private Data data;
    private GeneralName transactionIdentifier;
    
    public DVCSRequest(final DVCSRequestInformation dvcsRequestInformation, final Data data) {
        this(dvcsRequestInformation, data, null);
    }
    
    public DVCSRequest(final DVCSRequestInformation requestInformation, final Data data, final GeneralName transactionIdentifier) {
        this.requestInformation = requestInformation;
        this.data = data;
        this.transactionIdentifier = transactionIdentifier;
    }
    
    private DVCSRequest(final ASN1Sequence asn1Sequence) {
        this.requestInformation = DVCSRequestInformation.getInstance(asn1Sequence.getObjectAt(0));
        this.data = Data.getInstance(asn1Sequence.getObjectAt(1));
        if (asn1Sequence.size() > 2) {
            this.transactionIdentifier = GeneralName.getInstance(asn1Sequence.getObjectAt(2));
        }
    }
    
    public static DVCSRequest getInstance(final Object o) {
        if (o instanceof DVCSRequest) {
            return (DVCSRequest)o;
        }
        if (o != null) {
            return new DVCSRequest(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static DVCSRequest getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.requestInformation);
        asn1EncodableVector.add(this.data);
        if (this.transactionIdentifier != null) {
            asn1EncodableVector.add(this.transactionIdentifier);
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    @Override
    public String toString() {
        return "DVCSRequest {\nrequestInformation: " + this.requestInformation + "\ndata: " + this.data + "\n" + ((this.transactionIdentifier != null) ? ("transactionIdentifier: " + this.transactionIdentifier + "\n") : "") + "}\n";
    }
    
    public Data getData() {
        return this.data;
    }
    
    public DVCSRequestInformation getRequestInformation() {
        return this.requestInformation;
    }
    
    public GeneralName getTransactionIdentifier() {
        return this.transactionIdentifier;
    }
}
