package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Object;

public class X9ECPoint extends ASN1Object
{
    private final ASN1OctetString encoding;
    private ECCurve c;
    private ECPoint p;
    
    public X9ECPoint(final ECPoint ecPoint) {
        this(ecPoint, false);
    }
    
    public X9ECPoint(final ECPoint ecPoint, final boolean b) {
        this.p = ecPoint.normalize();
        this.encoding = new DEROctetString(ecPoint.getEncoded(b));
    }
    
    public X9ECPoint(final ECCurve c, final byte[] array) {
        this.c = c;
        this.encoding = new DEROctetString(Arrays.clone(array));
    }
    
    public X9ECPoint(final ECCurve ecCurve, final ASN1OctetString asn1OctetString) {
        this(ecCurve, asn1OctetString.getOctets());
    }
    
    public byte[] getPointEncoding() {
        return Arrays.clone(this.encoding.getOctets());
    }
    
    public synchronized ECPoint getPoint() {
        if (this.p == null) {
            this.p = this.c.decodePoint(this.encoding.getOctets()).normalize();
        }
        return this.p;
    }
    
    public boolean isPointCompressed() {
        final byte[] octets = this.encoding.getOctets();
        return octets != null && octets.length > 0 && (octets[0] == 2 || octets[0] == 3);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.encoding;
    }
}
