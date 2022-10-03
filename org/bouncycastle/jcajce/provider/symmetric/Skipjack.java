package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.SkipjackEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;

public final class Skipjack
{
    private Skipjack() {
    }
    
    public static class AlgParams extends IvAlgorithmParameters
    {
        @Override
        protected String engineToString() {
            return "Skipjack IV";
        }
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new SkipjackEngine());
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("Skipjack", 80, new CipherKeyGenerator());
        }
    }
    
    public static class Mac extends BaseMac
    {
        public Mac() {
            super(new CBCBlockCipherMac(new SkipjackEngine()));
        }
    }
    
    public static class MacCFB8 extends BaseMac
    {
        public MacCFB8() {
            super(new CFBBlockCipherMac(new SkipjackEngine()));
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.SKIPJACK", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("KeyGenerator.SKIPJACK", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("AlgorithmParameters.SKIPJACK", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Mac.SKIPJACKMAC", Mappings.PREFIX + "$Mac");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.SKIPJACK", "SKIPJACKMAC");
            configurableProvider.addAlgorithm("Mac.SKIPJACKMAC/CFB8", Mappings.PREFIX + "$MacCFB8");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.SKIPJACK/CFB8", "SKIPJACKMAC/CFB8");
        }
        
        static {
            PREFIX = Skipjack.class.getName();
        }
    }
}
