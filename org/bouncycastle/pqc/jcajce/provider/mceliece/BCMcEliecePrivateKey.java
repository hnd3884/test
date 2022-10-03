package org.bouncycastle.pqc.jcajce.provider.mceliece;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.McEliecePrivateKey;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import java.security.PrivateKey;
import org.bouncycastle.crypto.CipherParameters;

public class BCMcEliecePrivateKey implements CipherParameters, PrivateKey
{
    private static final long serialVersionUID = 1L;
    private McEliecePrivateKeyParameters params;
    
    public BCMcEliecePrivateKey(final McEliecePrivateKeyParameters params) {
        this.params = params;
    }
    
    public String getAlgorithm() {
        return "McEliece";
    }
    
    public int getN() {
        return this.params.getN();
    }
    
    public int getK() {
        return this.params.getK();
    }
    
    public GF2mField getField() {
        return this.params.getField();
    }
    
    public PolynomialGF2mSmallM getGoppaPoly() {
        return this.params.getGoppaPoly();
    }
    
    public GF2Matrix getSInv() {
        return this.params.getSInv();
    }
    
    public Permutation getP1() {
        return this.params.getP1();
    }
    
    public Permutation getP2() {
        return this.params.getP2();
    }
    
    public GF2Matrix getH() {
        return this.params.getH();
    }
    
    public PolynomialGF2mSmallM[] getQInv() {
        return this.params.getQInv();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BCMcEliecePrivateKey)) {
            return false;
        }
        final BCMcEliecePrivateKey bcMcEliecePrivateKey = (BCMcEliecePrivateKey)o;
        return this.getN() == bcMcEliecePrivateKey.getN() && this.getK() == bcMcEliecePrivateKey.getK() && this.getField().equals(bcMcEliecePrivateKey.getField()) && this.getGoppaPoly().equals(bcMcEliecePrivateKey.getGoppaPoly()) && this.getSInv().equals(bcMcEliecePrivateKey.getSInv()) && this.getP1().equals(bcMcEliecePrivateKey.getP1()) && this.getP2().equals(bcMcEliecePrivateKey.getP2());
    }
    
    @Override
    public int hashCode() {
        return (((((this.params.getK() * 37 + this.params.getN()) * 37 + this.params.getField().hashCode()) * 37 + this.params.getGoppaPoly().hashCode()) * 37 + this.params.getP1().hashCode()) * 37 + this.params.getP2().hashCode()) * 37 + this.params.getSInv().hashCode();
    }
    
    public byte[] getEncoded() {
        final McEliecePrivateKey mcEliecePrivateKey = new McEliecePrivateKey(this.params.getN(), this.params.getK(), this.params.getField(), this.params.getGoppaPoly(), this.params.getP1(), this.params.getP2(), this.params.getSInv());
        PrivateKeyInfo privateKeyInfo;
        try {
            privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.mcEliece), mcEliecePrivateKey);
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
    
    AsymmetricKeyParameter getKeyParams() {
        return this.params;
    }
}
