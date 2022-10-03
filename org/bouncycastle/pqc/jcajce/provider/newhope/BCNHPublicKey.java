package org.bouncycastle.pqc.jcajce.provider.newhope;

import org.bouncycastle.crypto.CipherParameters;
import java.io.IOException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.interfaces.NHPublicKey;

public class BCNHPublicKey implements NHPublicKey
{
    private static final long serialVersionUID = 1L;
    private final NHPublicKeyParameters params;
    
    public BCNHPublicKey(final NHPublicKeyParameters params) {
        this.params = params;
    }
    
    public BCNHPublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this.params = new NHPublicKeyParameters(subjectPublicKeyInfo.getPublicKeyData().getBytes());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof BCNHPublicKey && Arrays.areEqual(this.params.getPubData(), ((BCNHPublicKey)o).params.getPubData());
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.params.getPubData());
    }
    
    public final String getAlgorithm() {
        return "NH";
    }
    
    public byte[] getEncoded() {
        try {
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.newHope), this.params.getPubData()).getEncoded();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    public byte[] getPublicData() {
        return this.params.getPubData();
    }
    
    CipherParameters getKeyParams() {
        return this.params;
    }
}
