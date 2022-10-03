package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.engines.RC564Engine;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.RC532Engine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import javax.crypto.spec.IvParameterSpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;

public final class RC5
{
    private RC5() {
    }
    
    public static class AlgParamGen extends BaseAlgorithmParameterGenerator
    {
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for RC5 parameter generation.");
        }
        
        @Override
        protected AlgorithmParameters engineGenerateParameters() {
            final byte[] array = new byte[8];
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            this.random.nextBytes(array);
            AlgorithmParameters parametersInstance;
            try {
                parametersInstance = this.createParametersInstance("RC5");
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
            return "RC5 IV";
        }
    }
    
    public static class CBC32 extends BaseBlockCipher
    {
        public CBC32() {
            super(new CBCBlockCipher(new RC532Engine()), 64);
        }
    }
    
    public static class CFB8Mac32 extends BaseMac
    {
        public CFB8Mac32() {
            super(new CFBBlockCipherMac(new RC532Engine()));
        }
    }
    
    public static class ECB32 extends BaseBlockCipher
    {
        public ECB32() {
            super(new RC532Engine());
        }
    }
    
    public static class ECB64 extends BaseBlockCipher
    {
        public ECB64() {
            super(new RC564Engine());
        }
    }
    
    public static class KeyGen32 extends BaseKeyGenerator
    {
        public KeyGen32() {
            super("RC5", 128, new CipherKeyGenerator());
        }
    }
    
    public static class KeyGen64 extends BaseKeyGenerator
    {
        public KeyGen64() {
            super("RC5-64", 256, new CipherKeyGenerator());
        }
    }
    
    public static class Mac32 extends BaseMac
    {
        public Mac32() {
            super(new CBCBlockCipherMac(new RC532Engine()));
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.RC5", Mappings.PREFIX + "$ECB32");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.RC5-32", "RC5");
            configurableProvider.addAlgorithm("Cipher.RC5-64", Mappings.PREFIX + "$ECB64");
            configurableProvider.addAlgorithm("KeyGenerator.RC5", Mappings.PREFIX + "$KeyGen32");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.RC5-32", "RC5");
            configurableProvider.addAlgorithm("KeyGenerator.RC5-64", Mappings.PREFIX + "$KeyGen64");
            configurableProvider.addAlgorithm("AlgorithmParameters.RC5", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("AlgorithmParameters.RC5-64", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Mac.RC5MAC", Mappings.PREFIX + "$Mac32");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.RC5", "RC5MAC");
            configurableProvider.addAlgorithm("Mac.RC5MAC/CFB8", Mappings.PREFIX + "$CFB8Mac32");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.RC5/CFB8", "RC5MAC/CFB8");
        }
        
        static {
            PREFIX = RC5.class.getName();
        }
    }
}
