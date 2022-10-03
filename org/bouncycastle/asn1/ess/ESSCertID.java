package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Object;

public class ESSCertID extends ASN1Object
{
    private ASN1OctetString certHash;
    private IssuerSerial issuerSerial;
    
    public static ESSCertID getInstance(final Object o) {
        if (o instanceof ESSCertID) {
            return (ESSCertID)o;
        }
        if (o != null) {
            return new ESSCertID(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private ESSCertID(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 1 || asn1Sequence.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.certHash = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() > 1) {
            this.issuerSerial = IssuerSerial.getInstance(asn1Sequence.getObjectAt(1));
        }
    }
    
    public ESSCertID(final byte[] array) {
        this.certHash = new DEROctetString(array);
    }
    
    public ESSCertID(final byte[] array, final IssuerSerial issuerSerial) {
        this.certHash = new DEROctetString(array);
        this.issuerSerial = issuerSerial;
    }
    
    public byte[] getCertHash() {
        return this.certHash.getOctets();
    }
    
    public IssuerSerial getIssuerSerial() {
        return this.issuerSerial;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.certHash);
        if (this.issuerSerial != null) {
            asn1EncodableVector.add(this.issuerSerial);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
