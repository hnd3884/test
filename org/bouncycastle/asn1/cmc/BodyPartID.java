package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Integer;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;

public class BodyPartID extends ASN1Object
{
    public static final long bodyIdMax = 4294967295L;
    private final long id;
    
    public BodyPartID(final long id) {
        if (id < 0L || id > 4294967295L) {
            throw new IllegalArgumentException("id out of range");
        }
        this.id = id;
    }
    
    private static long convert(final BigInteger bigInteger) {
        if (bigInteger.bitLength() > 32) {
            throw new IllegalArgumentException("id out of range");
        }
        return bigInteger.longValue();
    }
    
    private BodyPartID(final ASN1Integer asn1Integer) {
        this(convert(asn1Integer.getValue()));
    }
    
    public static BodyPartID getInstance(final Object o) {
        if (o instanceof BodyPartID) {
            return (BodyPartID)o;
        }
        if (o != null) {
            return new BodyPartID(ASN1Integer.getInstance(o));
        }
        return null;
    }
    
    public long getID() {
        return this.id;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.id);
    }
}
