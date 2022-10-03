package org.bouncycastle.pqc.jcajce.provider.sphincs;

import org.bouncycastle.crypto.CipherParameters;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.pqc.jcajce.interfaces.SPHINCSKey;
import java.security.PublicKey;

public class BCSphincs256PublicKey implements PublicKey, SPHINCSKey
{
    private static final long serialVersionUID = 1L;
    private final ASN1ObjectIdentifier treeDigest;
    private final SPHINCSPublicKeyParameters params;
    
    public BCSphincs256PublicKey(final ASN1ObjectIdentifier treeDigest, final SPHINCSPublicKeyParameters params) {
        this.treeDigest = treeDigest;
        this.params = params;
    }
    
    public BCSphincs256PublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this.treeDigest = SPHINCS256KeyParams.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters()).getTreeDigest().getAlgorithm();
        this.params = new SPHINCSPublicKeyParameters(subjectPublicKeyInfo.getPublicKeyData().getBytes());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BCSphincs256PublicKey) {
            final BCSphincs256PublicKey bcSphincs256PublicKey = (BCSphincs256PublicKey)o;
            return this.treeDigest.equals(bcSphincs256PublicKey.treeDigest) && Arrays.areEqual(this.params.getKeyData(), bcSphincs256PublicKey.params.getKeyData());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.treeDigest.hashCode() + 37 * Arrays.hashCode(this.params.getKeyData());
    }
    
    public final String getAlgorithm() {
        return "SPHINCS-256";
    }
    
    public byte[] getEncoded() {
        try {
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(new AlgorithmIdentifier(this.treeDigest))), this.params.getKeyData()).getEncoded();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    public byte[] getKeyData() {
        return this.params.getKeyData();
    }
    
    CipherParameters getKeyParams() {
        return this.params;
    }
}
