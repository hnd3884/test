package org.bouncycastle.pqc.jcajce.provider.xmss;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import java.io.IOException;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.pqc.crypto.xmss.BDS;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.asn1.XMSSPrivateKey;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.jcajce.interfaces.XMSSKey;
import java.security.PrivateKey;

public class BCXMSSPrivateKey implements PrivateKey, XMSSKey
{
    private final XMSSPrivateKeyParameters keyParams;
    private final ASN1ObjectIdentifier treeDigest;
    
    public BCXMSSPrivateKey(final ASN1ObjectIdentifier treeDigest, final XMSSPrivateKeyParameters keyParams) {
        this.treeDigest = treeDigest;
        this.keyParams = keyParams;
    }
    
    public BCXMSSPrivateKey(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final XMSSKeyParams instance = XMSSKeyParams.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
        this.treeDigest = instance.getTreeDigest().getAlgorithm();
        final XMSSPrivateKey instance2 = XMSSPrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
        try {
            final XMSSPrivateKeyParameters.Builder withRoot = new XMSSPrivateKeyParameters.Builder(new XMSSParameters(instance.getHeight(), DigestUtil.getDigest(this.treeDigest))).withIndex(instance2.getIndex()).withSecretKeySeed(instance2.getSecretKeySeed()).withSecretKeyPRF(instance2.getSecretKeyPRF()).withPublicSeed(instance2.getPublicSeed()).withRoot(instance2.getRoot());
            if (instance2.getBdsState() != null) {
                withRoot.withBDSState((BDS)XMSSUtil.deserialize(instance2.getBdsState()));
            }
            this.keyParams = withRoot.build();
        }
        catch (final ClassNotFoundException ex) {
            throw new IOException("ClassNotFoundException processing BDS state: " + ex.getMessage());
        }
    }
    
    public String getAlgorithm() {
        return "XMSS";
    }
    
    public String getFormat() {
        return "PKCS#8";
    }
    
    public byte[] getEncoded() {
        try {
            return new PrivateKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.xmss, new XMSSKeyParams(this.keyParams.getParameters().getHeight(), new AlgorithmIdentifier(this.treeDigest))), this.createKeyStructure()).getEncoded();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BCXMSSPrivateKey) {
            final BCXMSSPrivateKey bcxmssPrivateKey = (BCXMSSPrivateKey)o;
            return this.treeDigest.equals(bcxmssPrivateKey.treeDigest) && Arrays.areEqual(this.keyParams.toByteArray(), bcxmssPrivateKey.keyParams.toByteArray());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.treeDigest.hashCode() + 37 * Arrays.hashCode(this.keyParams.toByteArray());
    }
    
    CipherParameters getKeyParams() {
        return this.keyParams;
    }
    
    private XMSSPrivateKey createKeyStructure() {
        final byte[] byteArray = this.keyParams.toByteArray();
        final int digestSize = this.keyParams.getParameters().getDigestSize();
        final int height = this.keyParams.getParameters().getHeight();
        final int n = 4;
        final int n2 = digestSize;
        final int n3 = digestSize;
        final int n4 = digestSize;
        final int n5 = digestSize;
        final int n6 = 0;
        final int n7 = (int)XMSSUtil.bytesToXBigEndian(byteArray, n6, n);
        if (!XMSSUtil.isIndexValid(height, n7)) {
            throw new IllegalArgumentException("index out of bounds");
        }
        final int n8 = n6 + n;
        final byte[] bytesAtOffset = XMSSUtil.extractBytesAtOffset(byteArray, n8, n2);
        final int n9 = n8 + n2;
        final byte[] bytesAtOffset2 = XMSSUtil.extractBytesAtOffset(byteArray, n9, n3);
        final int n10 = n9 + n3;
        final byte[] bytesAtOffset3 = XMSSUtil.extractBytesAtOffset(byteArray, n10, n4);
        final int n11 = n10 + n4;
        final byte[] bytesAtOffset4 = XMSSUtil.extractBytesAtOffset(byteArray, n11, n5);
        final int n12 = n11 + n5;
        return new XMSSPrivateKey(n7, bytesAtOffset, bytesAtOffset2, bytesAtOffset3, bytesAtOffset4, XMSSUtil.extractBytesAtOffset(byteArray, n12, byteArray.length - n12));
    }
    
    ASN1ObjectIdentifier getTreeDigestOID() {
        return this.treeDigest;
    }
    
    public int getHeight() {
        return this.keyParams.getParameters().getHeight();
    }
    
    public String getTreeDigest() {
        return DigestUtil.getXMSSDigestName(this.treeDigest);
    }
}
