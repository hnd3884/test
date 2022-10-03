package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.engines.TnepresEngine;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.asn1.gnu.GNUObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.SerpentEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;

public final class Serpent
{
    private Serpent() {
    }
    
    public static class AlgParams extends IvAlgorithmParameters
    {
        @Override
        protected String engineToString() {
            return "Serpent IV";
        }
    }
    
    public static class CBC extends BaseBlockCipher
    {
        public CBC() {
            super(new CBCBlockCipher(new SerpentEngine()), 128);
        }
    }
    
    public static class CFB extends BaseBlockCipher
    {
        public CFB() {
            super(new BufferedBlockCipher(new CFBBlockCipher(new SerpentEngine(), 128)), 128);
        }
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new BlockCipherProvider() {
                public BlockCipher get() {
                    return new SerpentEngine();
                }
            });
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("Serpent", 192, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends SymmetricAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.Serpent", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("KeyGenerator.Serpent", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("AlgorithmParameters.Serpent", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Cipher.Tnepres", Mappings.PREFIX + "$TECB");
            configurableProvider.addAlgorithm("KeyGenerator.Tnepres", Mappings.PREFIX + "$TKeyGen");
            configurableProvider.addAlgorithm("AlgorithmParameters.Tnepres", Mappings.PREFIX + "$TAlgParams");
            configurableProvider.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_128_ECB, Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_192_ECB, Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_256_ECB, Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_128_CBC, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_192_CBC, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_256_CBC, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_128_CFB, Mappings.PREFIX + "$CFB");
            configurableProvider.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_192_CFB, Mappings.PREFIX + "$CFB");
            configurableProvider.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_256_CFB, Mappings.PREFIX + "$CFB");
            configurableProvider.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_128_OFB, Mappings.PREFIX + "$OFB");
            configurableProvider.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_192_OFB, Mappings.PREFIX + "$OFB");
            configurableProvider.addAlgorithm("Cipher", GNUObjectIdentifiers.Serpent_256_OFB, Mappings.PREFIX + "$OFB");
            this.addGMacAlgorithm(configurableProvider, "SERPENT", Mappings.PREFIX + "$SerpentGMAC", Mappings.PREFIX + "$KeyGen");
            this.addGMacAlgorithm(configurableProvider, "TNEPRES", Mappings.PREFIX + "$TSerpentGMAC", Mappings.PREFIX + "$TKeyGen");
            this.addPoly1305Algorithm(configurableProvider, "SERPENT", Mappings.PREFIX + "$Poly1305", Mappings.PREFIX + "$Poly1305KeyGen");
        }
        
        static {
            PREFIX = Serpent.class.getName();
        }
    }
    
    public static class OFB extends BaseBlockCipher
    {
        public OFB() {
            super(new BufferedBlockCipher(new OFBBlockCipher(new SerpentEngine(), 128)), 128);
        }
    }
    
    public static class Poly1305 extends BaseMac
    {
        public Poly1305() {
            super(new org.bouncycastle.crypto.macs.Poly1305(new TwofishEngine()));
        }
    }
    
    public static class Poly1305KeyGen extends BaseKeyGenerator
    {
        public Poly1305KeyGen() {
            super("Poly1305-Serpent", 256, new Poly1305KeyGenerator());
        }
    }
    
    public static class SerpentGMAC extends BaseMac
    {
        public SerpentGMAC() {
            super(new GMac(new GCMBlockCipher(new SerpentEngine())));
        }
    }
    
    public static class TAlgParams extends IvAlgorithmParameters
    {
        @Override
        protected String engineToString() {
            return "Tnepres IV";
        }
    }
    
    public static class TECB extends BaseBlockCipher
    {
        public TECB() {
            super(new BlockCipherProvider() {
                public BlockCipher get() {
                    return new TnepresEngine();
                }
            });
        }
    }
    
    public static class TKeyGen extends BaseKeyGenerator
    {
        public TKeyGen() {
            super("Tnepres", 192, new CipherKeyGenerator());
        }
    }
    
    public static class TSerpentGMAC extends BaseMac
    {
        public TSerpentGMAC() {
            super(new GMac(new GCMBlockCipher(new TnepresEngine())));
        }
    }
}
