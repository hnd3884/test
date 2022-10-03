package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.engines.CamelliaWrapEngine;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import javax.crypto.spec.IvParameterSpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;

public final class Camellia
{
    private Camellia() {
    }
    
    public static class AlgParamGen extends BaseAlgorithmParameterGenerator
    {
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for Camellia parameter generation.");
        }
        
        @Override
        protected AlgorithmParameters engineGenerateParameters() {
            final byte[] array = new byte[16];
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            this.random.nextBytes(array);
            AlgorithmParameters parametersInstance;
            try {
                parametersInstance = this.createParametersInstance("Camellia");
                parametersInstance.init(new IvParameterSpec(array));
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
            return parametersInstance;
        }
    }
    
    public static class AlgParams extends IvAlgorithmParameters
    {
        @Override
        protected String engineToString() {
            return "Camellia IV";
        }
    }
    
    public static class CBC extends BaseBlockCipher
    {
        public CBC() {
            super(new CBCBlockCipher(new CamelliaEngine()), 128);
        }
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new BlockCipherProvider() {
                public BlockCipher get() {
                    return new CamelliaEngine();
                }
            });
        }
    }
    
    public static class GMAC extends BaseMac
    {
        public GMAC() {
            super(new GMac(new GCMBlockCipher(new CamelliaEngine())));
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            this(256);
        }
        
        public KeyGen(final int n) {
            super("Camellia", n, new CipherKeyGenerator());
        }
    }
    
    public static class KeyGen128 extends KeyGen
    {
        public KeyGen128() {
            super(128);
        }
    }
    
    public static class KeyGen192 extends KeyGen
    {
        public KeyGen192() {
            super(192);
        }
    }
    
    public static class KeyGen256 extends KeyGen
    {
        public KeyGen256() {
            super(256);
        }
    }
    
    public static class Mappings extends SymmetricAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("AlgorithmParameters.CAMELLIA", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NTTObjectIdentifiers.id_camellia128_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NTTObjectIdentifiers.id_camellia192_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NTTObjectIdentifiers.id_camellia256_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.CAMELLIA", Mappings.PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NTTObjectIdentifiers.id_camellia128_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NTTObjectIdentifiers.id_camellia192_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NTTObjectIdentifiers.id_camellia256_cbc, "CAMELLIA");
            configurableProvider.addAlgorithm("Cipher.CAMELLIA", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", NTTObjectIdentifiers.id_camellia128_cbc, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher", NTTObjectIdentifiers.id_camellia192_cbc, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher", NTTObjectIdentifiers.id_camellia256_cbc, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher.CAMELLIARFC3211WRAP", Mappings.PREFIX + "$RFC3211Wrap");
            configurableProvider.addAlgorithm("Cipher.CAMELLIAWRAP", Mappings.PREFIX + "$Wrap");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NTTObjectIdentifiers.id_camellia128_wrap, "CAMELLIAWRAP");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NTTObjectIdentifiers.id_camellia192_wrap, "CAMELLIAWRAP");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NTTObjectIdentifiers.id_camellia256_wrap, "CAMELLIAWRAP");
            configurableProvider.addAlgorithm("KeyGenerator.CAMELLIA", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator", NTTObjectIdentifiers.id_camellia128_wrap, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NTTObjectIdentifiers.id_camellia192_wrap, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NTTObjectIdentifiers.id_camellia256_wrap, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NTTObjectIdentifiers.id_camellia128_cbc, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NTTObjectIdentifiers.id_camellia192_cbc, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NTTObjectIdentifiers.id_camellia256_cbc, Mappings.PREFIX + "$KeyGen256");
            this.addGMacAlgorithm(configurableProvider, "CAMELLIA", Mappings.PREFIX + "$GMAC", Mappings.PREFIX + "$KeyGen");
            this.addPoly1305Algorithm(configurableProvider, "CAMELLIA", Mappings.PREFIX + "$Poly1305", Mappings.PREFIX + "$Poly1305KeyGen");
        }
        
        static {
            PREFIX = Camellia.class.getName();
        }
    }
    
    public static class Poly1305 extends BaseMac
    {
        public Poly1305() {
            super(new org.bouncycastle.crypto.macs.Poly1305(new CamelliaEngine()));
        }
    }
    
    public static class Poly1305KeyGen extends BaseKeyGenerator
    {
        public Poly1305KeyGen() {
            super("Poly1305-Camellia", 256, new Poly1305KeyGenerator());
        }
    }
    
    public static class RFC3211Wrap extends BaseWrapCipher
    {
        public RFC3211Wrap() {
            super(new RFC3211WrapEngine(new CamelliaEngine()), 16);
        }
    }
    
    public static class Wrap extends BaseWrapCipher
    {
        public Wrap() {
            super(new CamelliaWrapEngine());
        }
    }
}
