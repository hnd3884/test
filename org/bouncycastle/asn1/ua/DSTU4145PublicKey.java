package org.bouncycastle.asn1.ua;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Object;

public class DSTU4145PublicKey extends ASN1Object
{
    private ASN1OctetString pubKey;
    
    public DSTU4145PublicKey(final ECPoint ecPoint) {
        this.pubKey = new DEROctetString(DSTU4145PointEncoder.encodePoint(ecPoint));
    }
    
    private DSTU4145PublicKey(final ASN1OctetString pubKey) {
        this.pubKey = pubKey;
    }
    
    public static DSTU4145PublicKey getInstance(final Object o) {
        if (o instanceof DSTU4145PublicKey) {
            return (DSTU4145PublicKey)o;
        }
        if (o != null) {
            return new DSTU4145PublicKey(ASN1OctetString.getInstance(o));
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.pubKey;
    }
}
