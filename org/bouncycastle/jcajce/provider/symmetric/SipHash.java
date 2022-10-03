package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;

public final class SipHash
{
    private SipHash() {
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("SipHash", 128, new CipherKeyGenerator());
        }
    }
    
    public static class Mac24 extends BaseMac
    {
        public Mac24() {
            super(new org.bouncycastle.crypto.macs.SipHash());
        }
    }
    
    public static class Mac48 extends BaseMac
    {
        public Mac48() {
            super(new org.bouncycastle.crypto.macs.SipHash(4, 8));
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Mac.SIPHASH-2-4", Mappings.PREFIX + "$Mac24");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.SIPHASH", "SIPHASH-2-4");
            configurableProvider.addAlgorithm("Mac.SIPHASH-4-8", Mappings.PREFIX + "$Mac48");
            configurableProvider.addAlgorithm("KeyGenerator.SIPHASH", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.SIPHASH-2-4", "SIPHASH");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.SIPHASH-4-8", "SIPHASH");
        }
        
        static {
            PREFIX = SipHash.class.getName();
        }
    }
}
