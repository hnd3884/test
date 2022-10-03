package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.engines.CAST6Engine;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;

public final class CAST6
{
    private CAST6() {
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new BlockCipherProvider() {
                public BlockCipher get() {
                    return new CAST6Engine();
                }
            });
        }
    }
    
    public static class GMAC extends BaseMac
    {
        public GMAC() {
            super(new GMac(new GCMBlockCipher(new CAST6Engine())));
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("CAST6", 256, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends SymmetricAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.CAST6", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("KeyGenerator.CAST6", Mappings.PREFIX + "$KeyGen");
            this.addGMacAlgorithm(configurableProvider, "CAST6", Mappings.PREFIX + "$GMAC", Mappings.PREFIX + "$KeyGen");
            this.addPoly1305Algorithm(configurableProvider, "CAST6", Mappings.PREFIX + "$Poly1305", Mappings.PREFIX + "$Poly1305KeyGen");
        }
        
        static {
            PREFIX = CAST6.class.getName();
        }
    }
    
    public static class Poly1305 extends BaseMac
    {
        public Poly1305() {
            super(new org.bouncycastle.crypto.macs.Poly1305(new CAST6Engine()));
        }
    }
    
    public static class Poly1305KeyGen extends BaseKeyGenerator
    {
        public Poly1305KeyGen() {
            super("Poly1305-CAST6", 256, new Poly1305KeyGenerator());
        }
    }
}
