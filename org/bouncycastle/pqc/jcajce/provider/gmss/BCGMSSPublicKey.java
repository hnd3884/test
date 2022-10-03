package org.bouncycastle.pqc.jcajce.provider.gmss;

import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.pqc.asn1.GMSSPublicKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.ParSet;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.pqc.crypto.gmss.GMSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.gmss.GMSSParameters;
import java.security.PublicKey;
import org.bouncycastle.crypto.CipherParameters;

public class BCGMSSPublicKey implements CipherParameters, PublicKey
{
    private static final long serialVersionUID = 1L;
    private byte[] publicKeyBytes;
    private GMSSParameters gmssParameterSet;
    private GMSSParameters gmssParams;
    
    public BCGMSSPublicKey(final byte[] publicKeyBytes, final GMSSParameters gmssParameterSet) {
        this.gmssParameterSet = gmssParameterSet;
        this.publicKeyBytes = publicKeyBytes;
    }
    
    public BCGMSSPublicKey(final GMSSPublicKeyParameters gmssPublicKeyParameters) {
        this(gmssPublicKeyParameters.getPublicKey(), gmssPublicKeyParameters.getParameters());
    }
    
    public String getAlgorithm() {
        return "GMSS";
    }
    
    public byte[] getPublicKeyBytes() {
        return this.publicKeyBytes;
    }
    
    public GMSSParameters getParameterSet() {
        return this.gmssParameterSet;
    }
    
    @Override
    public String toString() {
        String s = "GMSS public key : " + new String(Hex.encode(this.publicKeyBytes)) + "\nHeight of Trees: \n";
        for (int i = 0; i < this.gmssParameterSet.getHeightOfTrees().length; ++i) {
            s = s + "Layer " + i + " : " + this.gmssParameterSet.getHeightOfTrees()[i] + " WinternitzParameter: " + this.gmssParameterSet.getWinternitzParameter()[i] + " K: " + this.gmssParameterSet.getK()[i] + "\n";
        }
        return s;
    }
    
    public byte[] getEncoded() {
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.gmss, new ParSet(this.gmssParameterSet.getNumOfLayers(), this.gmssParameterSet.getHeightOfTrees(), this.gmssParameterSet.getWinternitzParameter(), this.gmssParameterSet.getK()).toASN1Primitive()), new GMSSPublicKey(this.publicKeyBytes));
    }
    
    public String getFormat() {
        return "X.509";
    }
}
