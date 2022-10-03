package org.bouncycastle.asn1.icao;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class DataGroupHash extends ASN1Object
{
    ASN1Integer dataGroupNumber;
    ASN1OctetString dataGroupHashValue;
    
    public static DataGroupHash getInstance(final Object o) {
        if (o instanceof DataGroupHash) {
            return (DataGroupHash)o;
        }
        if (o != null) {
            return new DataGroupHash(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private DataGroupHash(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.dataGroupNumber = ASN1Integer.getInstance(objects.nextElement());
        this.dataGroupHashValue = ASN1OctetString.getInstance(objects.nextElement());
    }
    
    public DataGroupHash(final int n, final ASN1OctetString dataGroupHashValue) {
        this.dataGroupNumber = new ASN1Integer(n);
        this.dataGroupHashValue = dataGroupHashValue;
    }
    
    public int getDataGroupNumber() {
        return this.dataGroupNumber.getValue().intValue();
    }
    
    public ASN1OctetString getDataGroupHashValue() {
        return this.dataGroupHashValue;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.dataGroupNumber);
        asn1EncodableVector.add(this.dataGroupHashValue);
        return new DERSequence(asn1EncodableVector);
    }
}
