package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;

public class NHKeyPairGenerator implements AsymmetricCipherKeyPairGenerator
{
    private SecureRandom random;
    
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        this.random = keyGenerationParameters.getRandom();
    }
    
    public AsymmetricCipherKeyPair generateKeyPair() {
        final byte[] array = new byte[1824];
        final short[] array2 = new short[1024];
        NewHope.keygen(this.random, array, array2);
        return new AsymmetricCipherKeyPair(new NHPublicKeyParameters(array), new NHPrivateKeyParameters(array2));
    }
}
