package org.bouncycastle.pqc.jcajce.provider.xmss;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.util.Arrays;
import java.io.IOException;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.asn1.XMSSPublicKey;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.pqc.jcajce.interfaces.XMSSMTKey;
import java.security.PublicKey;

public class BCXMSSMTPublicKey implements PublicKey, XMSSMTKey
{
    private final ASN1ObjectIdentifier treeDigest;
    private final XMSSMTPublicKeyParameters keyParams;
    
    public BCXMSSMTPublicKey(final ASN1ObjectIdentifier treeDigest, final XMSSMTPublicKeyParameters keyParams) {
        this.treeDigest = treeDigest;
        this.keyParams = keyParams;
    }
    
    public BCXMSSMTPublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        final XMSSMTKeyParams instance = XMSSMTKeyParams.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
        this.treeDigest = instance.getTreeDigest().getAlgorithm();
        final XMSSPublicKey instance2 = XMSSPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
        this.keyParams = new XMSSMTPublicKeyParameters.Builder(new XMSSMTParameters(instance.getHeight(), instance.getLayers(), DigestUtil.getDigest(this.treeDigest))).withPublicSeed(instance2.getPublicSeed()).withRoot(instance2.getRoot()).build();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BCXMSSMTPublicKey) {
            final BCXMSSMTPublicKey bcxmssmtPublicKey = (BCXMSSMTPublicKey)o;
            return this.treeDigest.equals(bcxmssmtPublicKey.treeDigest) && Arrays.areEqual(this.keyParams.toByteArray(), bcxmssmtPublicKey.keyParams.toByteArray());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.treeDigest.hashCode() + 37 * Arrays.hashCode(this.keyParams.toByteArray());
    }
    
    public final String getAlgorithm() {
        return "XMSSMT";
    }
    
    public byte[] getEncoded() {
        try {
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt, new XMSSMTKeyParams(this.keyParams.getParameters().getHeight(), this.keyParams.getParameters().getLayers(), new AlgorithmIdentifier(this.treeDigest))), new XMSSPublicKey(this.keyParams.getPublicSeed(), this.keyParams.getRoot())).getEncoded();
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
    
    public int getHeight() {
        return this.keyParams.getParameters().getHeight();
    }
    
    public int getLayers() {
        return this.keyParams.getParameters().getLayers();
    }
    
    public String getTreeDigest() {
        return DigestUtil.getXMSSDigestName(this.treeDigest);
    }
}
