package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import java.math.BigInteger;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.ASN1Object;

public class GetCert extends ASN1Object
{
    private final GeneralName issuerName;
    private final BigInteger serialNumber;
    
    private GetCert(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.issuerName = GeneralName.getInstance(asn1Sequence.getObjectAt(0));
        this.serialNumber = ASN1Integer.getInstance(asn1Sequence.getObjectAt(1)).getValue();
    }
    
    public GetCert(final GeneralName issuerName, final BigInteger serialNumber) {
        this.issuerName = issuerName;
        this.serialNumber = serialNumber;
    }
    
    public static GetCert getInstance(final Object o) {
        if (o instanceof GetCert) {
            return (GetCert)o;
        }
        if (o != null) {
            return new GetCert(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public GeneralName getIssuerName() {
        return this.issuerName;
    }
    
    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.issuerName);
        asn1EncodableVector.add(new ASN1Integer(this.serialNumber));
        return new DERSequence(asn1EncodableVector);
    }
}
