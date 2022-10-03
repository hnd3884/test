package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import javax.crypto.spec.IvParameterSpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;

public final class SM4
{
    private SM4() {
    }
    
    public static class AlgParamGen extends BaseAlgorithmParameterGenerator
    {
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for SM4 parameter generation.");
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
                parametersInstance = this.createParametersInstance("SM4");
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
            return "SM4 IV";
        }
    }
    
    public static class CMAC extends BaseMac
    {
        public CMAC() {
            super(new CMac(new SM4Engine()));
        }
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new BlockCipherProvider() {
                public BlockCipher get() {
                    return new SM4Engine();
                }
            });
        }
    }
    
    public static class GMAC extends BaseMac
    {
        public GMAC() {
            super(new GMac(new GCMBlockCipher(new SM4Engine())));
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("SM4", 128, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends SymmetricAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("AlgorithmParameters.SM4", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.SM4", Mappings.PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("Cipher.SM4", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("KeyGenerator.SM4", Mappings.PREFIX + "$KeyGen");
            this.addCMacAlgorithm(configurableProvider, "SM4", Mappings.PREFIX + "$CMAC", Mappings.PREFIX + "$KeyGen");
            this.addGMacAlgorithm(configurableProvider, "SM4", Mappings.PREFIX + "$GMAC", Mappings.PREFIX + "$KeyGen");
            this.addPoly1305Algorithm(configurableProvider, "SM4", Mappings.PREFIX + "$Poly1305", Mappings.PREFIX + "$Poly1305KeyGen");
        }
        
        static {
            PREFIX = SM4.class.getName();
        }
    }
    
    public static class Poly1305 extends BaseMac
    {
        public Poly1305() {
            super(new org.bouncycastle.crypto.macs.Poly1305(new SM4Engine()));
        }
    }
    
    public static class Poly1305KeyGen extends BaseKeyGenerator
    {
        public Poly1305KeyGen() {
            super("Poly1305-SM4", 256, new Poly1305KeyGenerator());
        }
    }
}
