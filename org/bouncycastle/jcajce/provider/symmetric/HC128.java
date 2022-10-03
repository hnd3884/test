package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.HC128Engine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;

public final class HC128
{
    private HC128() {
    }
    
    public static class Base extends BaseStreamCipher
    {
        public Base() {
            super(new HC128Engine(), 16);
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("HC128", 128, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.HC128", Mappings.PREFIX + "$Base");
            configurableProvider.addAlgorithm("KeyGenerator.HC128", Mappings.PREFIX + "$KeyGen");
        }
        
        static {
            PREFIX = HC128.class.getName();
        }
    }
}
