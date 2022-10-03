package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.asn1.ASN1Object;

public class McEliecePrivateKey extends ASN1Object
{
    private int n;
    private int k;
    private byte[] encField;
    private byte[] encGp;
    private byte[] encSInv;
    private byte[] encP1;
    private byte[] encP2;
    
    public McEliecePrivateKey(final int n, final int k, final GF2mField gf2mField, final PolynomialGF2mSmallM polynomialGF2mSmallM, final Permutation permutation, final Permutation permutation2, final GF2Matrix gf2Matrix) {
        this.n = n;
        this.k = k;
        this.encField = gf2mField.getEncoded();
        this.encGp = polynomialGF2mSmallM.getEncoded();
        this.encSInv = gf2Matrix.getEncoded();
        this.encP1 = permutation.getEncoded();
        this.encP2 = permutation2.getEncoded();
    }
    
    public static McEliecePrivateKey getInstance(final Object o) {
        if (o instanceof McEliecePrivateKey) {
            return (McEliecePrivateKey)o;
        }
        if (o != null) {
            return new McEliecePrivateKey(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private McEliecePrivateKey(final ASN1Sequence asn1Sequence) {
        this.n = ((ASN1Integer)asn1Sequence.getObjectAt(0)).getValue().intValue();
        this.k = ((ASN1Integer)asn1Sequence.getObjectAt(1)).getValue().intValue();
        this.encField = ((ASN1OctetString)asn1Sequence.getObjectAt(2)).getOctets();
        this.encGp = ((ASN1OctetString)asn1Sequence.getObjectAt(3)).getOctets();
        this.encP1 = ((ASN1OctetString)asn1Sequence.getObjectAt(4)).getOctets();
        this.encP2 = ((ASN1OctetString)asn1Sequence.getObjectAt(5)).getOctets();
        this.encSInv = ((ASN1OctetString)asn1Sequence.getObjectAt(6)).getOctets();
    }
    
    public int getN() {
        return this.n;
    }
    
    public int getK() {
        return this.k;
    }
    
    public GF2mField getField() {
        return new GF2mField(this.encField);
    }
    
    public PolynomialGF2mSmallM getGoppaPoly() {
        return new PolynomialGF2mSmallM(this.getField(), this.encGp);
    }
    
    public GF2Matrix getSInv() {
        return new GF2Matrix(this.encSInv);
    }
    
    public Permutation getP1() {
        return new Permutation(this.encP1);
    }
    
    public Permutation getP2() {
        return new Permutation(this.encP2);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new ASN1Integer(this.n));
        asn1EncodableVector.add(new ASN1Integer(this.k));
        asn1EncodableVector.add(new DEROctetString(this.encField));
        asn1EncodableVector.add(new DEROctetString(this.encGp));
        asn1EncodableVector.add(new DEROctetString(this.encP1));
        asn1EncodableVector.add(new DEROctetString(this.encP2));
        asn1EncodableVector.add(new DEROctetString(this.encSInv));
        return new DERSequence(asn1EncodableVector);
    }
}
