package sun.security.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.security.spec.ECParameterSpec;

public final class NamedCurve extends ECParameterSpec
{
    private final String name;
    private final String oid;
    private final byte[] encoded;
    
    NamedCurve(final String name, final String oid, final EllipticCurve ellipticCurve, final ECPoint ecPoint, final BigInteger bigInteger, final int n) {
        super(ellipticCurve, ecPoint, bigInteger, n);
        this.name = name;
        this.oid = oid;
        final DerOutputStream derOutputStream = new DerOutputStream();
        try {
            derOutputStream.putOID(new ObjectIdentifier(oid));
        }
        catch (final IOException ex) {
            throw new RuntimeException("Internal error", ex);
        }
        this.encoded = derOutputStream.toByteArray();
    }
    
    public String getName() {
        return this.name;
    }
    
    public byte[] getEncoded() {
        return this.encoded.clone();
    }
    
    public String getObjectId() {
        return this.oid;
    }
    
    @Override
    public String toString() {
        return this.name + " (" + this.oid + ")";
    }
}
