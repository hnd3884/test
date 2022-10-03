package org.bouncycastle.asn1.ua;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Integer;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;

public class DSTU4145ECBinary extends ASN1Object
{
    BigInteger version;
    DSTU4145BinaryField f;
    ASN1Integer a;
    ASN1OctetString b;
    ASN1Integer n;
    ASN1OctetString bp;
    
    public DSTU4145ECBinary(final ECDomainParameters ecDomainParameters) {
        this.version = BigInteger.valueOf(0L);
        final ECCurve curve = ecDomainParameters.getCurve();
        if (!ECAlgorithms.isF2mCurve(curve)) {
            throw new IllegalArgumentException("only binary domain is possible");
        }
        final int[] exponentsPresent = ((PolynomialExtensionField)curve.getField()).getMinimalPolynomial().getExponentsPresent();
        if (exponentsPresent.length == 3) {
            this.f = new DSTU4145BinaryField(exponentsPresent[2], exponentsPresent[1]);
        }
        else {
            if (exponentsPresent.length != 5) {
                throw new IllegalArgumentException("curve must have a trinomial or pentanomial basis");
            }
            this.f = new DSTU4145BinaryField(exponentsPresent[4], exponentsPresent[1], exponentsPresent[2], exponentsPresent[3]);
        }
        this.a = new ASN1Integer(curve.getA().toBigInteger());
        this.b = new DEROctetString(curve.getB().getEncoded());
        this.n = new ASN1Integer(ecDomainParameters.getN());
        this.bp = new DEROctetString(DSTU4145PointEncoder.encodePoint(ecDomainParameters.getG()));
    }
    
    private DSTU4145ECBinary(final ASN1Sequence asn1Sequence) {
        this.version = BigInteger.valueOf(0L);
        int n = 0;
        if (asn1Sequence.getObjectAt(n) instanceof ASN1TaggedObject) {
            final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)asn1Sequence.getObjectAt(n);
            if (!asn1TaggedObject.isExplicit() || 0 != asn1TaggedObject.getTagNo()) {
                throw new IllegalArgumentException("object parse error");
            }
            this.version = ASN1Integer.getInstance(asn1TaggedObject.getLoadedObject()).getValue();
            ++n;
        }
        this.f = DSTU4145BinaryField.getInstance(asn1Sequence.getObjectAt(n));
        ++n;
        this.a = ASN1Integer.getInstance(asn1Sequence.getObjectAt(n));
        ++n;
        this.b = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(n));
        ++n;
        this.n = ASN1Integer.getInstance(asn1Sequence.getObjectAt(n));
        ++n;
        this.bp = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(n));
    }
    
    public static DSTU4145ECBinary getInstance(final Object o) {
        if (o instanceof DSTU4145ECBinary) {
            return (DSTU4145ECBinary)o;
        }
        if (o != null) {
            return new DSTU4145ECBinary(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public DSTU4145BinaryField getField() {
        return this.f;
    }
    
    public BigInteger getA() {
        return this.a.getValue();
    }
    
    public byte[] getB() {
        return Arrays.clone(this.b.getOctets());
    }
    
    public BigInteger getN() {
        return this.n.getValue();
    }
    
    public byte[] getG() {
        return Arrays.clone(this.bp.getOctets());
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (0 != this.version.compareTo(BigInteger.valueOf(0L))) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, new ASN1Integer(this.version)));
        }
        asn1EncodableVector.add(this.f);
        asn1EncodableVector.add(this.a);
        asn1EncodableVector.add(this.b);
        asn1EncodableVector.add(this.n);
        asn1EncodableVector.add(this.bp);
        return new DERSequence(asn1EncodableVector);
    }
}
