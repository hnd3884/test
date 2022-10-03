package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class RC2CBCParameter extends ASN1Object
{
    ASN1Integer version;
    ASN1OctetString iv;
    
    public static RC2CBCParameter getInstance(final Object o) {
        if (o instanceof RC2CBCParameter) {
            return (RC2CBCParameter)o;
        }
        if (o != null) {
            return new RC2CBCParameter(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public RC2CBCParameter(final byte[] array) {
        this.version = null;
        this.iv = new DEROctetString(array);
    }
    
    public RC2CBCParameter(final int n, final byte[] array) {
        this.version = new ASN1Integer(n);
        this.iv = new DEROctetString(array);
    }
    
    private RC2CBCParameter(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() == 1) {
            this.version = null;
            this.iv = (ASN1OctetString)asn1Sequence.getObjectAt(0);
        }
        else {
            this.version = (ASN1Integer)asn1Sequence.getObjectAt(0);
            this.iv = (ASN1OctetString)asn1Sequence.getObjectAt(1);
        }
    }
    
    public BigInteger getRC2ParameterVersion() {
        if (this.version == null) {
            return null;
        }
        return this.version.getValue();
    }
    
    public byte[] getIV() {
        return this.iv.getOctets();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.version != null) {
            asn1EncodableVector.add(this.version);
        }
        asn1EncodableVector.add(this.iv);
        return new DERSequence(asn1EncodableVector);
    }
}
