package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.HC256Engine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;

public final class HC256
{
    private HC256() {
    }
    
    public static class Base extends BaseStreamCipher
    {
        public Base() {
            super(new HC256Engine(), 32);
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("HC256", 256, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.HC256", Mappings.PREFIX + "$Base");
            configurableProvider.addAlgorithm("KeyGenerator.HC256", Mappings.PREFIX + "$KeyGen");
        }
        
        static {
            PREFIX = HC256.class.getName();
        }
    }
}
