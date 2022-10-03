package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;

public final class XMSSMTKeyPairGenerator
{
    private XMSSMTParameters params;
    private XMSSParameters xmssParams;
    private SecureRandom prng;
    
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        final XMSSMTKeyGenerationParameters xmssmtKeyGenerationParameters = (XMSSMTKeyGenerationParameters)keyGenerationParameters;
        this.prng = xmssmtKeyGenerationParameters.getRandom();
        this.params = xmssmtKeyGenerationParameters.getParameters();
        this.xmssParams = this.params.getXMSSParameters();
    }
    
    public AsymmetricCipherKeyPair generateKeyPair() {
        final XMSSMTPrivateKeyParameters generatePrivateKey = this.generatePrivateKey(new XMSSMTPrivateKeyParameters.Builder(this.params).build().getBDSState());
        this.xmssParams.getWOTSPlus().importKeys(new byte[this.params.getDigestSize()], generatePrivateKey.getPublicSeed());
        final int n = this.params.getLayers() - 1;
        final BDS bds = new BDS(this.xmssParams, generatePrivateKey.getPublicSeed(), generatePrivateKey.getSecretKeySeed(), (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(n).build());
        final XMSSNode root = bds.getRoot();
        generatePrivateKey.getBDSState().put(n, bds);
        final XMSSMTPrivateKeyParameters build = new XMSSMTPrivateKeyParameters.Builder(this.params).withSecretKeySeed(generatePrivateKey.getSecretKeySeed()).withSecretKeyPRF(generatePrivateKey.getSecretKeyPRF()).withPublicSeed(generatePrivateKey.getPublicSeed()).withRoot(root.getValue()).withBDSState(generatePrivateKey.getBDSState()).build();
        return new AsymmetricCipherKeyPair(new XMSSMTPublicKeyParameters.Builder(this.params).withRoot(root.getValue()).withPublicSeed(build.getPublicSeed()).build(), build);
    }
    
    private XMSSMTPrivateKeyParameters generatePrivateKey(final BDSStateMap bdsStateMap) {
        final int digestSize = this.params.getDigestSize();
        final byte[] array = new byte[digestSize];
        this.prng.nextBytes(array);
        final byte[] array2 = new byte[digestSize];
        this.prng.nextBytes(array2);
        final byte[] array3 = new byte[digestSize];
        this.prng.nextBytes(array3);
        return new XMSSMTPrivateKeyParameters.Builder(this.params).withSecretKeySeed(array).withSecretKeyPRF(array2).withPublicSeed(array3).withBDSState(bdsStateMap).build();
    }
}
