package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;

public class OtherCertID extends ASN1Object
{
    private ASN1Encodable otherCertHash;
    private IssuerSerial issuerSerial;
    
    public static OtherCertID getInstance(final Object o) {
        if (o instanceof OtherCertID) {
            return (OtherCertID)o;
        }
        if (o != null) {
            return new OtherCertID(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private OtherCertID(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 1 || asn1Sequence.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        if (asn1Sequence.getObjectAt(0).toASN1Primitive() instanceof ASN1OctetString) {
            this.otherCertHash = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(0));
        }
        else {
            this.otherCertHash = DigestInfo.getInstance(asn1Sequence.getObjectAt(0));
        }
        if (asn1Sequence.size() > 1) {
            this.issuerSerial = IssuerSerial.getInstance(asn1Sequence.getObjectAt(1));
        }
    }
    
    public OtherCertID(final AlgorithmIdentifier algorithmIdentifier, final byte[] array) {
        this.otherCertHash = new DigestInfo(algorithmIdentifier, array);
    }
    
    public OtherCertID(final AlgorithmIdentifier algorithmIdentifier, final byte[] array, final IssuerSerial issuerSerial) {
        this.otherCertHash = new DigestInfo(algorithmIdentifier, array);
        this.issuerSerial = issuerSerial;
    }
    
    public AlgorithmIdentifier getAlgorithmHash() {
        if (this.otherCertHash.toASN1Primitive() instanceof ASN1OctetString) {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
        }
        return DigestInfo.getInstance(this.otherCertHash).getAlgorithmId();
    }
    
    public byte[] getCertHash() {
        if (this.otherCertHash.toASN1Primitive() instanceof ASN1OctetString) {
            return ((ASN1OctetString)this.otherCertHash.toASN1Primitive()).getOctets();
        }
        return DigestInfo.getInstance(this.otherCertHash).getDigest();
    }
    
    public IssuerSerial getIssuerSerial() {
        return this.issuerSerial;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.otherCertHash);
        if (this.issuerSerial != null) {
            asn1EncodableVector.add(this.issuerSerial);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
