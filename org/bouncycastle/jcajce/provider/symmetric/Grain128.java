package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.Grain128Engine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;

public final class Grain128
{
    private Grain128() {
    }
    
    public static class Base extends BaseStreamCipher
    {
        public Base() {
            super(new Grain128Engine(), 12);
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("Grain128", 128, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.Grain128", Mappings.PREFIX + "$Base");
            configurableProvider.addAlgorithm("KeyGenerator.Grain128", Mappings.PREFIX + "$KeyGen");
        }
        
        static {
            PREFIX = Grain128.class.getName();
        }
    }
}
