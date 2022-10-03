package org.bouncycastle.pqc.jcajce.provider.xmss;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import java.security.KeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.pqc.jcajce.spec.XMSSMTParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTKeyPairGenerator;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTKeyGenerationParameters;
import java.security.KeyPairGenerator;

public class XMSSMTKeyPairGeneratorSpi extends KeyPairGenerator
{
    private XMSSMTKeyGenerationParameters param;
    private XMSSMTKeyPairGenerator engine;
    private ASN1ObjectIdentifier treeDigest;
    private SecureRandom random;
    private boolean initialised;
    
    public XMSSMTKeyPairGeneratorSpi() {
        super("XMSSMT");
        this.engine = new XMSSMTKeyPairGenerator();
        this.random = new SecureRandom();
        this.initialised = false;
    }
    
    @Override
    public void initialize(final int n, final SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof XMSSMTParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a XMSSMTParameterSpec");
        }
        final XMSSMTParameterSpec xmssmtParameterSpec = (XMSSMTParameterSpec)algorithmParameterSpec;
        if (xmssmtParameterSpec.getTreeDigest().equals("SHA256")) {
            this.treeDigest = NISTObjectIdentifiers.id_sha256;
            this.param = new XMSSMTKeyGenerationParameters(new XMSSMTParameters(xmssmtParameterSpec.getHeight(), xmssmtParameterSpec.getLayers(), new SHA256Digest()), secureRandom);
        }
        else if (xmssmtParameterSpec.getTreeDigest().equals("SHA512")) {
            this.treeDigest = NISTObjectIdentifiers.id_sha512;
            this.param = new XMSSMTKeyGenerationParameters(new XMSSMTParameters(xmssmtParameterSpec.getHeight(), xmssmtParameterSpec.getLayers(), new SHA512Digest()), secureRandom);
        }
        else if (xmssmtParameterSpec.getTreeDigest().equals("SHAKE128")) {
            this.treeDigest = NISTObjectIdentifiers.id_shake128;
            this.param = new XMSSMTKeyGenerationParameters(new XMSSMTParameters(xmssmtParameterSpec.getHeight(), xmssmtParameterSpec.getLayers(), new SHAKEDigest(128)), secureRandom);
        }
        else if (xmssmtParameterSpec.getTreeDigest().equals("SHAKE256")) {
            this.treeDigest = NISTObjectIdentifiers.id_shake256;
            this.param = new XMSSMTKeyGenerationParameters(new XMSSMTParameters(xmssmtParameterSpec.getHeight(), xmssmtParameterSpec.getLayers(), new SHAKEDigest(256)), secureRandom);
        }
        this.engine.init(this.param);
        this.initialised = true;
    }
    
    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = new XMSSMTKeyGenerationParameters(new XMSSMTParameters(10, 20, new SHA512Digest()), this.random);
            this.engine.init(this.param);
            this.initialised = true;
        }
        final AsymmetricCipherKeyPair generateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCXMSSMTPublicKey(this.treeDigest, (XMSSMTPublicKeyParameters)generateKeyPair.getPublic()), new BCXMSSMTPrivateKey(this.treeDigest, (XMSSMTPrivateKeyParameters)generateKeyPair.getPrivate()));
    }
}
