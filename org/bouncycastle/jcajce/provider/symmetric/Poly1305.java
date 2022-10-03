package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;

public class Poly1305
{
    private Poly1305() {
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("Poly1305", 256, new Poly1305KeyGenerator());
        }
    }
    
    public static class Mac extends BaseMac
    {
        public Mac() {
            super(new org.bouncycastle.crypto.macs.Poly1305());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Mac.POLY1305", Mappings.PREFIX + "$Mac");
            configurableProvider.addAlgorithm("KeyGenerator.POLY1305", Mappings.PREFIX + "$KeyGen");
        }
        
        static {
            PREFIX = Poly1305.class.getName();
        }
    }
}
