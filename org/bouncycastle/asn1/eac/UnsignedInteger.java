package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1TaggedObject;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;

public class UnsignedInteger extends ASN1Object
{
    private int tagNo;
    private BigInteger value;
    
    public UnsignedInteger(final int tagNo, final BigInteger value) {
        this.tagNo = tagNo;
        this.value = value;
    }
    
    private UnsignedInteger(final ASN1TaggedObject asn1TaggedObject) {
        this.tagNo = asn1TaggedObject.getTagNo();
        this.value = new BigInteger(1, ASN1OctetString.getInstance(asn1TaggedObject, false).getOctets());
    }
    
    public static UnsignedInteger getInstance(final Object o) {
        if (o instanceof UnsignedInteger) {
            return (UnsignedInteger)o;
        }
        if (o != null) {
            return new UnsignedInteger(ASN1TaggedObject.getInstance(o));
        }
        return null;
    }
    
    private byte[] convertValue() {
        final byte[] byteArray = this.value.toByteArray();
        if (byteArray[0] == 0) {
            final byte[] array = new byte[byteArray.length - 1];
            System.arraycopy(byteArray, 1, array, 0, array.length);
            return array;
        }
        return byteArray;
    }
    
    public int getTagNo() {
        return this.tagNo;
    }
    
    public BigInteger getValue() {
        return this.value;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.tagNo, new DEROctetString(this.convertValue()));
    }
}
