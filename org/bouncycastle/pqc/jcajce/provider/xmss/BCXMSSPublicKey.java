package org.bouncycastle.pqc.jcajce.provider.xmss;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import java.io.IOException;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.asn1.XMSSPublicKey;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.interfaces.XMSSKey;
import java.security.PublicKey;

public class BCXMSSPublicKey implements PublicKey, XMSSKey
{
    private final XMSSPublicKeyParameters keyParams;
    private final ASN1ObjectIdentifier treeDigest;
    
    public BCXMSSPublicKey(final ASN1ObjectIdentifier treeDigest, final XMSSPublicKeyParameters keyParams) {
        this.treeDigest = treeDigest;
        this.keyParams = keyParams;
    }
    
    public BCXMSSPublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        final XMSSKeyParams instance = XMSSKeyParams.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
        this.treeDigest = instance.getTreeDigest().getAlgorithm();
        final XMSSPublicKey instance2 = XMSSPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
        this.keyParams = new XMSSPublicKeyParameters.Builder(new XMSSParameters(instance.getHeight(), DigestUtil.getDigest(this.treeDigest))).withPublicSeed(instance2.getPublicSeed()).withRoot(instance2.getRoot()).build();
    }
    
    public final String getAlgorithm() {
        return "XMSS";
    }
    
    public byte[] getEncoded() {
        try {
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.xmss, new XMSSKeyParams(this.keyParams.getParameters().getHeight(), new AlgorithmIdentifier(this.treeDigest))), new XMSSPublicKey(this.keyParams.getPublicSeed(), this.keyParams.getRoot())).getEncoded();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    CipherParameters getKeyParams() {
        return this.keyParams;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BCXMSSPublicKey) {
            final BCXMSSPublicKey bcxmssPublicKey = (BCXMSSPublicKey)o;
            return this.treeDigest.equals(bcxmssPublicKey.treeDigest) && Arrays.areEqual(this.keyParams.toByteArray(), bcxmssPublicKey.keyParams.toByteArray());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.treeDigest.hashCode() + 37 * Arrays.hashCode(this.keyParams.toByteArray());
    }
    
    public int getHeight() {
        return this.keyParams.getParameters().getHeight();
    }
    
    public String getTreeDigest() {
        return DigestUtil.getXMSSDigestName(this.treeDigest);
    }
}
