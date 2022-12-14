package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Object;

public class CertStatus extends ASN1Object
{
    private ASN1OctetString certHash;
    private ASN1Integer certReqId;
    private PKIStatusInfo statusInfo;
    
    private CertStatus(final ASN1Sequence asn1Sequence) {
        this.certHash = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(0));
        this.certReqId = ASN1Integer.getInstance(asn1Sequence.getObjectAt(1));
        if (asn1Sequence.size() > 2) {
            this.statusInfo = PKIStatusInfo.getInstance(asn1Sequence.getObjectAt(2));
        }
    }
    
    public CertStatus(final byte[] array, final BigInteger bigInteger) {
        this.certHash = new DEROctetString(array);
        this.certReqId = new ASN1Integer(bigInteger);
    }
    
    public CertStatus(final byte[] array, final BigInteger bigInteger, final PKIStatusInfo statusInfo) {
        this.certHash = new DEROctetString(array);
        this.certReqId = new ASN1Integer(bigInteger);
        this.statusInfo = statusInfo;
    }
    
    public static CertStatus getInstance(final Object o) {
        if (o instanceof CertStatus) {
            return (CertStatus)o;
        }
        if (o != null) {
            return new CertStatus(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1OctetString getCertHash() {
        return this.certHash;
    }
    
    public ASN1Integer getCertReqId() {
        return this.certReqId;
    }
    
    public PKIStatusInfo getStatusInfo() {
        return this.statusInfo;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.certHash);
        asn1EncodableVector.add(this.certReqId);
        if (this.statusInfo != null) {
            asn1EncodableVector.add(this.statusInfo);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
