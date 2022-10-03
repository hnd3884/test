package org.bouncycastle.pqc.jcajce.provider.sphincs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.util.Arrays;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.pqc.jcajce.interfaces.SPHINCSKey;
import java.security.PrivateKey;

public class BCSphincs256PrivateKey implements PrivateKey, SPHINCSKey
{
    private static final long serialVersionUID = 1L;
    private final ASN1ObjectIdentifier treeDigest;
    private final SPHINCSPrivateKeyParameters params;
    
    public BCSphincs256PrivateKey(final ASN1ObjectIdentifier treeDigest, final SPHINCSPrivateKeyParameters params) {
        this.treeDigest = treeDigest;
        this.params = params;
    }
    
    public BCSphincs256PrivateKey(final PrivateKeyInfo privateKeyInfo) throws IOException {
        this.treeDigest = SPHINCS256KeyParams.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters()).getTreeDigest().getAlgorithm();
        this.params = new SPHINCSPrivateKeyParameters(ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BCSphincs256PrivateKey) {
            final BCSphincs256PrivateKey bcSphincs256PrivateKey = (BCSphincs256PrivateKey)o;
            return this.treeDigest.equals(bcSphincs256PrivateKey.treeDigest) && Arrays.areEqual(this.params.getKeyData(), bcSphincs256PrivateKey.params.getKeyData());
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
            return new PrivateKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(new AlgorithmIdentifier(this.treeDigest))), new DEROctetString(this.params.getKeyData())).getEncoded();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public String getFormat() {
        return "PKCS#8";
    }
    
    public byte[] getKeyData() {
        return this.params.getKeyData();
    }
    
    CipherParameters getKeyParams() {
        return this.params;
    }
}
