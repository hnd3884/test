package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.modes.G3413OFBBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.modes.G3413CFBBlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.modes.G3413CTRBlockCipher;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.G3413CBCBlockCipher;
import org.bouncycastle.crypto.engines.GOST3412_2015Engine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;

public class GOST3412_2015
{
    public static class CBC extends BaseBlockCipher
    {
        public CBC() {
            super(new G3413CBCBlockCipher(new GOST3412_2015Engine()), false, 128);
        }
    }
    
    public static class CTR extends BaseBlockCipher
    {
        public CTR() {
            super(new BufferedBlockCipher(new G3413CTRBlockCipher(new GOST3412_2015Engine())), 128);
        }
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new GOST3412_2015Engine());
        }
    }
    
    public static class GCFB extends BaseBlockCipher
    {
        public GCFB() {
            super(new BufferedBlockCipher(new G3413CFBBlockCipher(new GOST3412_2015Engine())), false, 128);
        }
    }
    
    public static class GCFB8 extends BaseBlockCipher
    {
        public GCFB8() {
            super(new BufferedBlockCipher(new G3413CFBBlockCipher(new GOST3412_2015Engine(), 8)), false, 128);
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            this(256);
        }
        
        public KeyGen(final int n) {
            super("GOST3412-2015", n, new CipherKeyGenerator());
        }
    }
    
    public static class Mac extends BaseMac
    {
        public Mac() {
            super(new CMac(new GOST3412_2015Engine()));
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.GOST3412-2015", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher.GOST3412-2015/CFB", Mappings.PREFIX + "$GCFB");
            configurableProvider.addAlgorithm("Cipher.GOST3412-2015/CFB8", Mappings.PREFIX + "$GCFB8");
            configurableProvider.addAlgorithm("Cipher.GOST3412-2015/OFB", Mappings.PREFIX + "$OFB");
            configurableProvider.addAlgorithm("Cipher.GOST3412-2015/CBC", Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher.GOST3412-2015/CTR", Mappings.PREFIX + "$CTR");
            configurableProvider.addAlgorithm("KeyGenerator.GOST3412-2015", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("Mac.GOST3412MAC", Mappings.PREFIX + "$Mac");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.GOST3412-2015", "GOST3412MAC");
        }
        
        static {
            PREFIX = GOST3412_2015.class.getName();
        }
    }
    
    public static class OFB extends BaseBlockCipher
    {
        public OFB() {
            super(new BufferedBlockCipher(new G3413OFBBlockCipher(new GOST3412_2015Engine())), false, 128);
        }
    }
}
