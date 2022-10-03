package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.CAST5Engine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.asn1.ASN1InputStream;
import java.security.spec.InvalidParameterSpecException;
import java.io.IOException;
import org.bouncycastle.asn1.misc.CAST5CBCParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import javax.crypto.spec.IvParameterSpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;

public final class CAST5
{
    private CAST5() {
    }
    
    public static class AlgParamGen extends BaseAlgorithmParameterGenerator
    {
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for CAST5 parameter generation.");
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
                parametersInstance = this.createParametersInstance("CAST5");
                parametersInstance.init(new IvParameterSpec(array));
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
            return parametersInstance;
        }
    }
    
    public static class AlgParams extends BaseAlgorithmParameters
    {
        private byte[] iv;
        private int keyLength;
        
        public AlgParams() {
            this.keyLength = 128;
        }
        
        @Override
        protected byte[] engineGetEncoded() {
            final byte[] array = new byte[this.iv.length];
            System.arraycopy(this.iv, 0, array, 0, this.iv.length);
            return array;
        }
        
        @Override
        protected byte[] engineGetEncoded(final String s) throws IOException {
            if (this.isASN1FormatString(s)) {
                return new CAST5CBCParameters(this.engineGetEncoded(), this.keyLength).getEncoded();
            }
            if (s.equals("RAW")) {
                return this.engineGetEncoded();
            }
            return null;
        }
        
        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
            if (clazz == IvParameterSpec.class) {
                return new IvParameterSpec(this.iv);
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to CAST5 parameters object.");
        }
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (algorithmParameterSpec instanceof IvParameterSpec) {
                this.iv = ((IvParameterSpec)algorithmParameterSpec).getIV();
                return;
            }
            throw new InvalidParameterSpecException("IvParameterSpec required to initialise a CAST5 parameters algorithm parameters object");
        }
        
        @Override
        protected void engineInit(final byte[] array) throws IOException {
            System.arraycopy(array, 0, this.iv = new byte[array.length], 0, this.iv.length);
        }
        
        @Override
        protected void engineInit(final byte[] array, final String s) throws IOException {
            if (this.isASN1FormatString(s)) {
                final CAST5CBCParameters instance = CAST5CBCParameters.getInstance(new ASN1InputStream(array).readObject());
                this.keyLength = instance.getKeyLength();
                this.iv = instance.getIV();
                return;
            }
            if (s.equals("RAW")) {
                this.engineInit(array);
                return;
            }
            throw new IOException("Unknown parameters format in IV parameters object");
        }
        
        @Override
        protected String engineToString() {
            return "CAST5 Parameters";
        }
    }
    
    public static class CBC extends BaseBlockCipher
    {
        public CBC() {
            super(new CBCBlockCipher(new CAST5Engine()), 64);
        }
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new CAST5Engine());
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("CAST5", 128, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("AlgorithmParameters.CAST5", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.1.2.840.113533.7.66.10", "CAST5");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.CAST5", Mappings.PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.1.2.840.113533.7.66.10", "CAST5");
            configurableProvider.addAlgorithm("Cipher.CAST5", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", MiscObjectIdentifiers.cast5CBC, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("KeyGenerator.CAST5", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator", MiscObjectIdentifiers.cast5CBC, "CAST5");
        }
        
        static {
            PREFIX = CAST5.class.getName();
        }
    }
}
