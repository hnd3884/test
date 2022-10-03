package org.bouncycastle.pqc.jcajce.provider.xmss;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;
import java.security.KeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.pqc.jcajce.spec.XMSSParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import org.bouncycastle.pqc.crypto.xmss.XMSSKeyPairGenerator;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.pqc.crypto.xmss.XMSSKeyGenerationParameters;
import java.security.KeyPairGenerator;

public class XMSSKeyPairGeneratorSpi extends KeyPairGenerator
{
    private XMSSKeyGenerationParameters param;
    private ASN1ObjectIdentifier treeDigest;
    private XMSSKeyPairGenerator engine;
    private SecureRandom random;
    private boolean initialised;
    
    public XMSSKeyPairGeneratorSpi() {
        super("XMSS");
        this.engine = new XMSSKeyPairGenerator();
        this.random = new SecureRandom();
        this.initialised = false;
    }
    
    @Override
    public void initialize(final int n, final SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof XMSSParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a XMSSParameterSpec");
        }
        final XMSSParameterSpec xmssParameterSpec = (XMSSParameterSpec)algorithmParameterSpec;
        if (xmssParameterSpec.getTreeDigest().equals("SHA256")) {
            this.treeDigest = NISTObjectIdentifiers.id_sha256;
            this.param = new XMSSKeyGenerationParameters(new XMSSParameters(xmssParameterSpec.getHeight(), new SHA256Digest()), secureRandom);
        }
        else if (xmssParameterSpec.getTreeDigest().equals("SHA512")) {
            this.treeDigest = NISTObjectIdentifiers.id_sha512;
            this.param = new XMSSKeyGenerationParameters(new XMSSParameters(xmssParameterSpec.getHeight(), new SHA512Digest()), secureRandom);
        }
        else if (xmssParameterSpec.getTreeDigest().equals("SHAKE128")) {
            this.treeDigest = NISTObjectIdentifiers.id_shake128;
            this.param = new XMSSKeyGenerationParameters(new XMSSParameters(xmssParameterSpec.getHeight(), new SHAKEDigest(128)), secureRandom);
        }
        else if (xmssParameterSpec.getTreeDigest().equals("SHAKE256")) {
            this.treeDigest = NISTObjectIdentifiers.id_shake256;
            this.param = new XMSSKeyGenerationParameters(new XMSSParameters(xmssParameterSpec.getHeight(), new SHAKEDigest(256)), secureRandom);
        }
        this.engine.init(this.param);
        this.initialised = true;
    }
    
    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = new XMSSKeyGenerationParameters(new XMSSParameters(10, new SHA512Digest()), this.random);
            this.engine.init(this.param);
            this.initialised = true;
        }
        final AsymmetricCipherKeyPair generateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCXMSSPublicKey(this.treeDigest, (XMSSPublicKeyParameters)generateKeyPair.getPublic()), new BCXMSSPrivateKey(this.treeDigest, (XMSSPrivateKeyParameters)generateKeyPair.getPrivate()));
    }
}
