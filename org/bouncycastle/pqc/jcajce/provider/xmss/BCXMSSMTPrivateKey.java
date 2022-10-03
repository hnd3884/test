package org.bouncycastle.pqc.jcajce.provider.xmss;

import org.bouncycastle.pqc.asn1.XMSSMTPrivateKey;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import java.io.IOException;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.pqc.crypto.xmss.BDSStateMap;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.asn1.XMSSPrivateKey;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.pqc.jcajce.interfaces.XMSSMTKey;
import java.security.PrivateKey;

public class BCXMSSMTPrivateKey implements PrivateKey, XMSSMTKey
{
    private final ASN1ObjectIdentifier treeDigest;
    private final XMSSMTPrivateKeyParameters keyParams;
    
    public BCXMSSMTPrivateKey(final ASN1ObjectIdentifier treeDigest, final XMSSMTPrivateKeyParameters keyParams) {
        this.treeDigest = treeDigest;
        this.keyParams = keyParams;
    }
    
    public BCXMSSMTPrivateKey(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final XMSSMTKeyParams instance = XMSSMTKeyParams.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
        this.treeDigest = instance.getTreeDigest().getAlgorithm();
        final XMSSPrivateKey instance2 = XMSSPrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
        try {
            final XMSSMTPrivateKeyParameters.Builder withRoot = new XMSSMTPrivateKeyParameters.Builder(new XMSSMTParameters(instance.getHeight(), instance.getLayers(), DigestUtil.getDigest(this.treeDigest))).withIndex(instance2.getIndex()).withSecretKeySeed(instance2.getSecretKeySeed()).withSecretKeyPRF(instance2.getSecretKeyPRF()).withPublicSeed(instance2.getPublicSeed()).withRoot(instance2.getRoot());
            if (instance2.getBdsState() != null) {
                withRoot.withBDSState((BDSStateMap)XMSSUtil.deserialize(instance2.getBdsState()));
            }
            this.keyParams = withRoot.build();
        }
        catch (final ClassNotFoundException ex) {
            throw new IOException("ClassNotFoundException processing BDS state: " + ex.getMessage());
        }
    }
    
    public String getAlgorithm() {
        return "XMSSMT";
    }
    
    public String getFormat() {
        return "PKCS#8";
    }
    
    public byte[] getEncoded() {
        try {
            return new PrivateKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt, new XMSSMTKeyParams(this.keyParams.getParameters().getHeight(), this.keyParams.getParameters().getLayers(), new AlgorithmIdentifier(this.treeDigest))), this.createKeyStructure()).getEncoded();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    CipherParameters getKeyParams() {
        return this.keyParams;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BCXMSSMTPrivateKey) {
            final BCXMSSMTPrivateKey bcxmssmtPrivateKey = (BCXMSSMTPrivateKey)o;
            return this.treeDigest.equals(bcxmssmtPrivateKey.treeDigest) && Arrays.areEqual(this.keyParams.toByteArray(), bcxmssmtPrivateKey.keyParams.toByteArray());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.treeDigest.hashCode() + 37 * Arrays.hashCode(this.keyParams.toByteArray());
    }
    
    private XMSSMTPrivateKey createKeyStructure() {
        final byte[] byteArray = this.keyParams.toByteArray();
        final int digestSize = this.keyParams.getParameters().getDigestSize();
        final int height = this.keyParams.getParameters().getHeight();
        final int n = (height + 7) / 8;
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
        return new XMSSMTPrivateKey(n7, bytesAtOffset, bytesAtOffset2, bytesAtOffset3, bytesAtOffset4, XMSSUtil.extractBytesAtOffset(byteArray, n12, byteArray.length - n12));
    }
    
    ASN1ObjectIdentifier getTreeDigestOID() {
        return this.treeDigest;
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
