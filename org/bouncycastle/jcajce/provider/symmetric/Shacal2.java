package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.Shacal2Engine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import javax.crypto.spec.IvParameterSpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;

public final class Shacal2
{
    private Shacal2() {
    }
    
    public static class AlgParamGen extends BaseAlgorithmParameterGenerator
    {
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for Shacal2 parameter generation.");
        }
        
        @Override
        protected AlgorithmParameters engineGenerateParameters() {
            final byte[] array = new byte[32];
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            this.random.nextBytes(array);
            AlgorithmParameters parametersInstance;
            try {
                parametersInstance = this.createParametersInstance("Shacal2");
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
            return "Shacal2 IV";
        }
    }
    
    public static class CBC extends BaseBlockCipher
    {
        public CBC() {
            super(new CBCBlockCipher(new Shacal2Engine()), 256);
        }
    }
    
    public static class CMAC extends BaseMac
    {
        public CMAC() {
            super(new CMac(new Shacal2Engine()));
        }
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new BlockCipherProvider() {
                public BlockCipher get() {
                    return new Shacal2Engine();
                }
            });
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("SHACAL-2", 128, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends SymmetricAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Mac.Shacal-2CMAC", Mappings.PREFIX + "$CMAC");
            configurableProvider.addAlgorithm("Cipher.Shacal2", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher.SHACAL-2", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("KeyGenerator.Shacal2", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.Shacal2", Mappings.PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("AlgorithmParameters.Shacal2", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("KeyGenerator.SHACAL-2", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.SHACAL-2", Mappings.PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("AlgorithmParameters.SHACAL-2", Mappings.PREFIX + "$AlgParams");
        }
        
        static {
            PREFIX = Shacal2.class.getName();
        }
    }
}
