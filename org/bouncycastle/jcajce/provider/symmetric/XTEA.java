package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.XTEAEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;

public final class XTEA
{
    private XTEA() {
    }
    
    public static class AlgParams extends IvAlgorithmParameters
    {
        @Override
        protected String engineToString() {
            return "XTEA IV";
        }
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new XTEAEngine());
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("XTEA", 128, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.XTEA", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("KeyGenerator.XTEA", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("AlgorithmParameters.XTEA", Mappings.PREFIX + "$AlgParams");
        }
        
        static {
            PREFIX = XTEA.class.getName();
        }
    }
}
