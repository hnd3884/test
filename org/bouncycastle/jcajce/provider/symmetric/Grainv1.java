package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.Grainv1Engine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;

public final class Grainv1
{
    private Grainv1() {
    }
    
    public static class Base extends BaseStreamCipher
    {
        public Base() {
            super(new Grainv1Engine(), 8);
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("Grainv1", 80, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.Grainv1", Mappings.PREFIX + "$Base");
            configurableProvider.addAlgorithm("KeyGenerator.Grainv1", Mappings.PREFIX + "$KeyGen");
        }
        
        static {
            PREFIX = Grainv1.class.getName();
        }
    }
}
