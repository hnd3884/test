package org.bouncycastle.pqc.jcajce.provider.mceliece;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.pqc.asn1.McElieceCCA2PrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import java.security.PrivateKey;

public class BCMcElieceCCA2PrivateKey implements PrivateKey
{
    private static final long serialVersionUID = 1L;
    private McElieceCCA2PrivateKeyParameters params;
    
    public BCMcElieceCCA2PrivateKey(final McElieceCCA2PrivateKeyParameters params) {
        this.params = params;
    }
    
    public String getAlgorithm() {
        return "McEliece-CCA2";
    }
    
    public int getN() {
        return this.params.getN();
    }
    
    public int getK() {
        return this.params.getK();
    }
    
    public int getT() {
        return this.params.getGoppaPoly().getDegree();
    }
    
    public GF2mField getField() {
        return this.params.getField();
    }
    
    public PolynomialGF2mSmallM getGoppaPoly() {
        return this.params.getGoppaPoly();
    }
    
    public Permutation getP() {
        return this.params.getP();
    }
    
    public GF2Matrix getH() {
        return this.params.getH();
    }
    
    public PolynomialGF2mSmallM[] getQInv() {
        return this.params.getQInv();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof BCMcElieceCCA2PrivateKey)) {
            return false;
        }
        final BCMcElieceCCA2PrivateKey bcMcElieceCCA2PrivateKey = (BCMcElieceCCA2PrivateKey)o;
        return this.getN() == bcMcElieceCCA2PrivateKey.getN() && this.getK() == bcMcElieceCCA2PrivateKey.getK() && this.getField().equals(bcMcElieceCCA2PrivateKey.getField()) && this.getGoppaPoly().equals(bcMcElieceCCA2PrivateKey.getGoppaPoly()) && this.getP().equals(bcMcElieceCCA2PrivateKey.getP()) && this.getH().equals(bcMcElieceCCA2PrivateKey.getH());
    }
    
    @Override
    public int hashCode() {
        return ((((this.params.getK() * 37 + this.params.getN()) * 37 + this.params.getField().hashCode()) * 37 + this.params.getGoppaPoly().hashCode()) * 37 + this.params.getP().hashCode()) * 37 + this.params.getH().hashCode();
    }
    
    public byte[] getEncoded() {
        try {
            return new PrivateKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.mcElieceCca2), new McElieceCCA2PrivateKey(this.getN(), this.getK(), this.getField(), this.getGoppaPoly(), this.getP(), Utils.getDigAlgId(this.params.getDigest()))).getEncoded();
        }
        catch (final IOException ex) {
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
