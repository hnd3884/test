package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;

public final class Salsa20
{
    private Salsa20() {
    }
    
    public static class Base extends BaseStreamCipher
    {
        public Base() {
            super(new Salsa20Engine(), 8);
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("Salsa20", 128, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.SALSA20", Mappings.PREFIX + "$Base");
            configurableProvider.addAlgorithm("KeyGenerator.SALSA20", Mappings.PREFIX + "$KeyGen");
        }
        
        static {
            PREFIX = Salsa20.class.getName();
        }
    }
}
