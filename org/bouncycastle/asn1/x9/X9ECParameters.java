package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.math.ec.ECCurve;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;

public class X9ECParameters extends ASN1Object implements X9ObjectIdentifiers
{
    private static final BigInteger ONE;
    private X9FieldID fieldID;
    private ECCurve curve;
    private X9ECPoint g;
    private BigInteger n;
    private BigInteger h;
    private byte[] seed;
    
    private X9ECParameters(final ASN1Sequence asn1Sequence) {
        if (!(asn1Sequence.getObjectAt(0) instanceof ASN1Integer) || !((ASN1Integer)asn1Sequence.getObjectAt(0)).getValue().equals(X9ECParameters.ONE)) {
            throw new IllegalArgumentException("bad version in X9ECParameters");
        }
        final X9Curve x9Curve = new X9Curve(X9FieldID.getInstance(asn1Sequence.getObjectAt(1)), ASN1Sequence.getInstance(asn1Sequence.getObjectAt(2)));
        this.curve = x9Curve.getCurve();
        final ASN1Encodable object = asn1Sequence.getObjectAt(3);
        if (object instanceof X9ECPoint) {
            this.g = (X9ECPoint)object;
        }
        else {
            this.g = new X9ECPoint(this.curve, (ASN1OctetString)object);
        }
        this.n = ((ASN1Integer)asn1Sequence.getObjectAt(4)).getValue();
        this.seed = x9Curve.getSeed();
        if (asn1Sequence.size() == 6) {
            this.h = ((ASN1Integer)asn1Sequence.getObjectAt(5)).getValue();
        }
    }
    
    public static X9ECParameters getInstance(final Object o) {
        if (o instanceof X9ECParameters) {
            return (X9ECParameters)o;
        }
        if (o != null) {
            return new X9ECParameters(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public X9ECParameters(final ECCurve ecCurve, final ECPoint ecPoint, final BigInteger bigInteger) {
        this(ecCurve, ecPoint, bigInteger, null, null);
    }
    
    public X9ECParameters(final ECCurve ecCurve, final X9ECPoint x9ECPoint, final BigInteger bigInteger, final BigInteger bigInteger2) {
        this(ecCurve, x9ECPoint, bigInteger, bigInteger2, null);
    }
    
    public X9ECParameters(final ECCurve ecCurve, final ECPoint ecPoint, final BigInteger bigInteger, final BigInteger bigInteger2) {
        this(ecCurve, ecPoint, bigInteger, bigInteger2, null);
    }
    
    public X9ECParameters(final ECCurve ecCurve, final ECPoint ecPoint, final BigInteger bigInteger, final BigInteger bigInteger2, final byte[] array) {
        this(ecCurve, new X9ECPoint(ecPoint), bigInteger, bigInteger2, array);
    }
    
    public X9ECParameters(final ECCurve curve, final X9ECPoint g, final BigInteger n, final BigInteger h, final byte[] seed) {
        this.curve = curve;
        this.g = g;
        this.n = n;
        this.h = h;
        this.seed = seed;
        if (ECAlgorithms.isFpCurve(curve)) {
            this.fieldID = new X9FieldID(curve.getField().getCharacteristic());
        }
        else {
            if (!ECAlgorithms.isF2mCurve(curve)) {
                throw new IllegalArgumentException("'curve' is of an unsupported type");
            }
            final int[] exponentsPresent = ((PolynomialExtensionField)curve.getField()).getMinimalPolynomial().getExponentsPresent();
            if (exponentsPresent.length == 3) {
                this.fieldID = new X9FieldID(exponentsPresent[2], exponentsPresent[1]);
            }
            else {
                if (exponentsPresent.length != 5) {
                    throw new IllegalArgumentException("Only trinomial and pentomial curves are supported");
                }
                this.fieldID = new X9FieldID(exponentsPresent[4], exponentsPresent[1], exponentsPresent[2], exponentsPresent[3]);
            }
        }
    }
    
    public ECCurve getCurve() {
        return this.curve;
    }
    
    public ECPoint getG() {
        return this.g.getPoint();
    }
    
    public BigInteger getN() {
        return this.n;
    }
    
    public BigInteger getH() {
        return this.h;
    }
    
    public byte[] getSeed() {
        return this.seed;
    }
    
    public X9Curve getCurveEntry() {
        return new X9Curve(this.curve, this.seed);
    }
    
    public X9FieldID getFieldIDEntry() {
        return this.fieldID;
    }
    
    public X9ECPoint getBaseEntry() {
        return this.g;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new ASN1Integer(X9ECParameters.ONE));
        asn1EncodableVector.add(this.fieldID);
        asn1EncodableVector.add(new X9Curve(this.curve, this.seed));
        asn1EncodableVector.add(this.g);
        asn1EncodableVector.add(new ASN1Integer(this.n));
        if (this.h != null) {
            asn1EncodableVector.add(new ASN1Integer(this.h));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
    }
}
