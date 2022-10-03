package org.bouncycastle.pqc.jcajce.provider.mceliece;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.McEliecePublicKey;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;
import java.security.PublicKey;

public class BCMcEliecePublicKey implements PublicKey
{
    private static final long serialVersionUID = 1L;
    private McEliecePublicKeyParameters params;
    
    public BCMcEliecePublicKey(final McEliecePublicKeyParameters params) {
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
    
    public int getT() {
        return this.params.getT();
    }
    
    public GF2Matrix getG() {
        return this.params.getG();
    }
    
    @Override
    public String toString() {
        return "McEliecePublicKey:\n" + " length of the code         : " + this.params.getN() + "\n" + " error correction capability: " + this.params.getT() + "\n" + " generator matrix           : " + this.params.getG();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof BCMcEliecePublicKey) {
            final BCMcEliecePublicKey bcMcEliecePublicKey = (BCMcEliecePublicKey)o;
            return this.params.getN() == bcMcEliecePublicKey.getN() && this.params.getT() == bcMcEliecePublicKey.getT() && this.params.getG().equals(bcMcEliecePublicKey.getG());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return 37 * (this.params.getN() + 37 * this.params.getT()) + this.params.getG().hashCode();
    }
    
    public byte[] getEncoded() {
        final McEliecePublicKey mcEliecePublicKey = new McEliecePublicKey(this.params.getN(), this.params.getT(), this.params.getG());
        final AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcEliece);
        try {
            return new SubjectPublicKeyInfo(algorithmIdentifier, mcEliecePublicKey).getEncoded();
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
