package org.bouncycastle.pqc.jcajce.provider.mceliece;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.McElieceCCA2PublicKey;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PublicKeyParameters;
import java.security.PublicKey;
import org.bouncycastle.crypto.CipherParameters;

public class BCMcElieceCCA2PublicKey implements CipherParameters, PublicKey
{
    private static final long serialVersionUID = 1L;
    private McElieceCCA2PublicKeyParameters params;
    
    public BCMcElieceCCA2PublicKey(final McElieceCCA2PublicKeyParameters params) {
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
        return this.params.getT();
    }
    
    public GF2Matrix getG() {
        return this.params.getG();
    }
    
    @Override
    public String toString() {
        return "McEliecePublicKey:\n" + " length of the code         : " + this.params.getN() + "\n" + " error correction capability: " + this.params.getT() + "\n" + " generator matrix           : " + this.params.getG().toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof BCMcElieceCCA2PublicKey)) {
            return false;
        }
        final BCMcElieceCCA2PublicKey bcMcElieceCCA2PublicKey = (BCMcElieceCCA2PublicKey)o;
        return this.params.getN() == bcMcElieceCCA2PublicKey.getN() && this.params.getT() == bcMcElieceCCA2PublicKey.getT() && this.params.getG().equals(bcMcElieceCCA2PublicKey.getG());
    }
    
    @Override
    public int hashCode() {
        return 37 * (this.params.getN() + 37 * this.params.getT()) + this.params.getG().hashCode();
    }
    
    public byte[] getEncoded() {
        final McElieceCCA2PublicKey mcElieceCCA2PublicKey = new McElieceCCA2PublicKey(this.params.getN(), this.params.getT(), this.params.getG(), Utils.getDigAlgId(this.params.getDigest()));
        final AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcElieceCca2);
        try {
            return new SubjectPublicKeyInfo(algorithmIdentifier, mcElieceCCA2PublicKey).getEncoded();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    AsymmetricKeyParameter getKeyParams() {
        return this.params;
    }
}
