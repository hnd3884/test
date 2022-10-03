package org.bouncycastle.pqc.jcajce.provider.rainbow;

import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.pqc.asn1.RainbowPublicKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.rainbow.util.RainbowUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.RainbowPublicKeySpec;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import java.security.PublicKey;

public class BCRainbowPublicKey implements PublicKey
{
    private static final long serialVersionUID = 1L;
    private short[][] coeffquadratic;
    private short[][] coeffsingular;
    private short[] coeffscalar;
    private int docLength;
    private RainbowParameters rainbowParams;
    
    public BCRainbowPublicKey(final int docLength, final short[][] coeffquadratic, final short[][] coeffsingular, final short[] coeffscalar) {
        this.docLength = docLength;
        this.coeffquadratic = coeffquadratic;
        this.coeffsingular = coeffsingular;
        this.coeffscalar = coeffscalar;
    }
    
    public BCRainbowPublicKey(final RainbowPublicKeySpec rainbowPublicKeySpec) {
        this(rainbowPublicKeySpec.getDocLength(), rainbowPublicKeySpec.getCoeffQuadratic(), rainbowPublicKeySpec.getCoeffSingular(), rainbowPublicKeySpec.getCoeffScalar());
    }
    
    public BCRainbowPublicKey(final RainbowPublicKeyParameters rainbowPublicKeyParameters) {
        this(rainbowPublicKeyParameters.getDocLength(), rainbowPublicKeyParameters.getCoeffQuadratic(), rainbowPublicKeyParameters.getCoeffSingular(), rainbowPublicKeyParameters.getCoeffScalar());
    }
    
    public int getDocLength() {
        return this.docLength;
    }
    
    public short[][] getCoeffQuadratic() {
        return this.coeffquadratic;
    }
    
    public short[][] getCoeffSingular() {
        final short[][] array = new short[this.coeffsingular.length][];
        for (int i = 0; i != this.coeffsingular.length; ++i) {
            array[i] = Arrays.clone(this.coeffsingular[i]);
        }
        return array;
    }
    
    public short[] getCoeffScalar() {
        return Arrays.clone(this.coeffscalar);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof BCRainbowPublicKey)) {
            return false;
        }
        final BCRainbowPublicKey bcRainbowPublicKey = (BCRainbowPublicKey)o;
        return this.docLength == bcRainbowPublicKey.getDocLength() && RainbowUtil.equals(this.coeffquadratic, bcRainbowPublicKey.getCoeffQuadratic()) && RainbowUtil.equals(this.coeffsingular, bcRainbowPublicKey.getCoeffSingular()) && RainbowUtil.equals(this.coeffscalar, bcRainbowPublicKey.getCoeffScalar());
    }
    
    @Override
    public int hashCode() {
        return ((this.docLength * 37 + Arrays.hashCode(this.coeffquadratic)) * 37 + Arrays.hashCode(this.coeffsingular)) * 37 + Arrays.hashCode(this.coeffscalar);
    }
    
    public final String getAlgorithm() {
        return "Rainbow";
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    public byte[] getEncoded() {
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.rainbow, DERNull.INSTANCE), new RainbowPublicKey(this.docLength, this.coeffquadratic, this.coeffsingular, this.coeffscalar));
    }
}
