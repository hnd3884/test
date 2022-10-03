package org.bouncycastle.pqc.jcajce.provider.rainbow;

import java.io.IOException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.RainbowPrivateKey;
import java.util.Arrays;
import org.bouncycastle.pqc.crypto.rainbow.util.RainbowUtil;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.RainbowPrivateKeySpec;
import org.bouncycastle.pqc.crypto.rainbow.Layer;
import java.security.PrivateKey;

public class BCRainbowPrivateKey implements PrivateKey
{
    private static final long serialVersionUID = 1L;
    private short[][] A1inv;
    private short[] b1;
    private short[][] A2inv;
    private short[] b2;
    private Layer[] layers;
    private int[] vi;
    
    public BCRainbowPrivateKey(final short[][] a1inv, final short[] b1, final short[][] a2inv, final short[] b2, final int[] vi, final Layer[] layers) {
        this.A1inv = a1inv;
        this.b1 = b1;
        this.A2inv = a2inv;
        this.b2 = b2;
        this.vi = vi;
        this.layers = layers;
    }
    
    public BCRainbowPrivateKey(final RainbowPrivateKeySpec rainbowPrivateKeySpec) {
        this(rainbowPrivateKeySpec.getInvA1(), rainbowPrivateKeySpec.getB1(), rainbowPrivateKeySpec.getInvA2(), rainbowPrivateKeySpec.getB2(), rainbowPrivateKeySpec.getVi(), rainbowPrivateKeySpec.getLayers());
    }
    
    public BCRainbowPrivateKey(final RainbowPrivateKeyParameters rainbowPrivateKeyParameters) {
        this(rainbowPrivateKeyParameters.getInvA1(), rainbowPrivateKeyParameters.getB1(), rainbowPrivateKeyParameters.getInvA2(), rainbowPrivateKeyParameters.getB2(), rainbowPrivateKeyParameters.getVi(), rainbowPrivateKeyParameters.getLayers());
    }
    
    public short[][] getInvA1() {
        return this.A1inv;
    }
    
    public short[] getB1() {
        return this.b1;
    }
    
    public short[] getB2() {
        return this.b2;
    }
    
    public short[][] getInvA2() {
        return this.A2inv;
    }
    
    public Layer[] getLayers() {
        return this.layers;
    }
    
    public int[] getVi() {
        return this.vi;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof BCRainbowPrivateKey)) {
            return false;
        }
        final BCRainbowPrivateKey bcRainbowPrivateKey = (BCRainbowPrivateKey)o;
        boolean b = true && RainbowUtil.equals(this.A1inv, bcRainbowPrivateKey.getInvA1()) && RainbowUtil.equals(this.A2inv, bcRainbowPrivateKey.getInvA2()) && RainbowUtil.equals(this.b1, bcRainbowPrivateKey.getB1()) && RainbowUtil.equals(this.b2, bcRainbowPrivateKey.getB2()) && Arrays.equals(this.vi, bcRainbowPrivateKey.getVi());
        if (this.layers.length != bcRainbowPrivateKey.getLayers().length) {
            return false;
        }
        for (int i = this.layers.length - 1; i >= 0; --i) {
            b &= this.layers[i].equals(bcRainbowPrivateKey.getLayers()[i]);
        }
        return b;
    }
    
    @Override
    public int hashCode() {
        int n = ((((this.layers.length * 37 + org.bouncycastle.util.Arrays.hashCode(this.A1inv)) * 37 + org.bouncycastle.util.Arrays.hashCode(this.b1)) * 37 + org.bouncycastle.util.Arrays.hashCode(this.A2inv)) * 37 + org.bouncycastle.util.Arrays.hashCode(this.b2)) * 37 + org.bouncycastle.util.Arrays.hashCode(this.vi);
        for (int i = this.layers.length - 1; i >= 0; --i) {
            n = n * 37 + this.layers[i].hashCode();
        }
        return n;
    }
    
    public final String getAlgorithm() {
        return "Rainbow";
    }
    
    public byte[] getEncoded() {
        final RainbowPrivateKey rainbowPrivateKey = new RainbowPrivateKey(this.A1inv, this.b1, this.A2inv, this.b2, this.vi, this.layers);
        PrivateKeyInfo privateKeyInfo;
        try {
            privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.rainbow, DERNull.INSTANCE), rainbowPrivateKey);
        }
        catch (final IOException ex) {
            return null;
        }
        try {
            return privateKeyInfo.getEncoded();
        }
        catch (final IOException ex2) {
            return null;
        }
    }
    
    public String getFormat() {
        return "PKCS#8";
    }
}
