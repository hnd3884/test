package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.asn1.ASN1Object;

public class McElieceCCA2PublicKey extends ASN1Object
{
    private final int n;
    private final int t;
    private final GF2Matrix g;
    private final AlgorithmIdentifier digest;
    
    public McElieceCCA2PublicKey(final int n, final int t, final GF2Matrix gf2Matrix, final AlgorithmIdentifier digest) {
        this.n = n;
        this.t = t;
        this.g = new GF2Matrix(gf2Matrix.getEncoded());
        this.digest = digest;
    }
    
    private McElieceCCA2PublicKey(final ASN1Sequence asn1Sequence) {
        this.n = ((ASN1Integer)asn1Sequence.getObjectAt(0)).getValue().intValue();
        this.t = ((ASN1Integer)asn1Sequence.getObjectAt(1)).getValue().intValue();
        this.g = new GF2Matrix(((ASN1OctetString)asn1Sequence.getObjectAt(2)).getOctets());
        this.digest = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(3));
    }
    
    public int getN() {
        return this.n;
    }
    
    public int getT() {
        return this.t;
    }
    
    public GF2Matrix getG() {
        return this.g;
    }
    
    public AlgorithmIdentifier getDigest() {
        return this.digest;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new ASN1Integer(this.n));
        asn1EncodableVector.add(new ASN1Integer(this.t));
        asn1EncodableVector.add(new DEROctetString(this.g.getEncoded()));
        asn1EncodableVector.add(this.digest);
        return new DERSequence(asn1EncodableVector);
    }
    
    public static McElieceCCA2PublicKey getInstance(final Object o) {
        if (o instanceof McElieceCCA2PublicKey) {
            return (McElieceCCA2PublicKey)o;
        }
        if (o != null) {
            return new McElieceCCA2PublicKey(ASN1Sequence.getInstance(o));
        }
        return null;
    }
}
