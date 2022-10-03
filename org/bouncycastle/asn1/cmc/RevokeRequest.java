package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Object;

public class RevokeRequest extends ASN1Object
{
    private final X500Name name;
    private final ASN1Integer serialNumber;
    private final CRLReason reason;
    private ASN1GeneralizedTime invalidityDate;
    private ASN1OctetString passphrase;
    private DERUTF8String comment;
    
    public RevokeRequest(final X500Name name, final ASN1Integer serialNumber, final CRLReason reason, final ASN1GeneralizedTime invalidityDate, final ASN1OctetString passphrase, final DERUTF8String comment) {
        this.name = name;
        this.serialNumber = serialNumber;
        this.reason = reason;
        this.invalidityDate = invalidityDate;
        this.passphrase = passphrase;
        this.comment = comment;
    }
    
    private RevokeRequest(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 3 || asn1Sequence.size() > 6) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.name = X500Name.getInstance(asn1Sequence.getObjectAt(0));
        this.serialNumber = ASN1Integer.getInstance(asn1Sequence.getObjectAt(1));
        this.reason = CRLReason.getInstance(asn1Sequence.getObjectAt(2));
        int n = 3;
        if (asn1Sequence.size() > n && asn1Sequence.getObjectAt(n).toASN1Primitive() instanceof ASN1GeneralizedTime) {
            this.invalidityDate = ASN1GeneralizedTime.getInstance(asn1Sequence.getObjectAt(n++));
        }
        if (asn1Sequence.size() > n && asn1Sequence.getObjectAt(n).toASN1Primitive() instanceof ASN1OctetString) {
            this.passphrase = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(n++));
        }
        if (asn1Sequence.size() > n && asn1Sequence.getObjectAt(n).toASN1Primitive() instanceof DERUTF8String) {
            this.comment = DERUTF8String.getInstance(asn1Sequence.getObjectAt(n));
        }
    }
    
    public static RevokeRequest getInstance(final Object o) {
        if (o instanceof RevokeRequest) {
            return (RevokeRequest)o;
        }
        if (o != null) {
            return new RevokeRequest(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public X500Name getName() {
        return this.name;
    }
    
    public BigInteger getSerialNumber() {
        return this.serialNumber.getValue();
    }
    
    public CRLReason getReason() {
        return this.reason;
    }
    
    public ASN1GeneralizedTime getInvalidityDate() {
        return this.invalidityDate;
    }
    
    public void setInvalidityDate(final ASN1GeneralizedTime invalidityDate) {
        this.invalidityDate = invalidityDate;
    }
    
    public ASN1OctetString getPassphrase() {
        return this.passphrase;
    }
    
    public void setPassphrase(final ASN1OctetString passphrase) {
        this.passphrase = passphrase;
    }
    
    public DERUTF8String getComment() {
        return this.comment;
    }
    
    public void setComment(final DERUTF8String comment) {
        this.comment = comment;
    }
    
    public byte[] getPassPhrase() {
        if (this.passphrase != null) {
            return Arrays.clone(this.passphrase.getOctets());
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.name);
        asn1EncodableVector.add(this.serialNumber);
        asn1EncodableVector.add(this.reason);
        if (this.invalidityDate != null) {
            asn1EncodableVector.add(this.invalidityDate);
        }
        if (this.passphrase != null) {
            asn1EncodableVector.add(this.passphrase);
        }
        if (this.comment != null) {
            asn1EncodableVector.add(this.comment);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
