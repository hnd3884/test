package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.IDEAEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.asn1.misc.IDEACBCPar;
import java.io.IOException;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import javax.crypto.spec.IvParameterSpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;

public final class IDEA
{
    private IDEA() {
    }
    
    public static class AlgParamGen extends BaseAlgorithmParameterGenerator
    {
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for IDEA parameter generation.");
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
                parametersInstance = this.createParametersInstance("IDEA");
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
        
        @Override
        protected byte[] engineGetEncoded() throws IOException {
            return this.engineGetEncoded("ASN.1");
        }
        
        @Override
        protected byte[] engineGetEncoded(final String s) throws IOException {
            if (this.isASN1FormatString(s)) {
                return new IDEACBCPar(this.engineGetEncoded("RAW")).getEncoded();
            }
            if (s.equals("RAW")) {
                final byte[] array = new byte[this.iv.length];
                System.arraycopy(this.iv, 0, array, 0, this.iv.length);
                return array;
            }
            return null;
        }
        
        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
            if (clazz == IvParameterSpec.class) {
                return new IvParameterSpec(this.iv);
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to IV parameters object.");
        }
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (!(algorithmParameterSpec instanceof IvParameterSpec)) {
                throw new InvalidParameterSpecException("IvParameterSpec required to initialise a IV parameters algorithm parameters object");
            }
            this.iv = ((IvParameterSpec)algorithmParameterSpec).getIV();
        }
        
        @Override
        protected void engineInit(final byte[] array) throws IOException {
            System.arraycopy(array, 0, this.iv = new byte[array.length], 0, this.iv.length);
        }
        
        @Override
        protected void engineInit(final byte[] array, final String s) throws IOException {
            if (s.equals("RAW")) {
                this.engineInit(array);
                return;
            }
            if (s.equals("ASN.1")) {
                this.engineInit(new IDEACBCPar((ASN1Sequence)new ASN1InputStream(array).readObject()).getIV());
                return;
            }
            throw new IOException("Unknown parameters format in IV parameters object");
        }
        
        @Override
        protected String engineToString() {
            return "IDEA Parameters";
        }
    }
    
    public static class CBC extends BaseBlockCipher
    {
        public CBC() {
            super(new CBCBlockCipher(new IDEAEngine()), 64);
        }
    }
    
    public static class CFB8Mac extends BaseMac
    {
        public CFB8Mac() {
            super(new CFBBlockCipherMac(new IDEAEngine()));
        }
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new IDEAEngine());
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("IDEA", 128, new CipherKeyGenerator());
        }
    }
    
    public static class Mac extends BaseMac
    {
        public Mac() {
            super(new CBCBlockCipherMac(new IDEAEngine()));
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.IDEA", Mappings.PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.1.3.6.1.4.1.188.7.1.1.2", Mappings.PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("AlgorithmParameters.IDEA", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("AlgorithmParameters.1.3.6.1.4.1.188.7.1.1.2", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDIDEA", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDIDEA-CBC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Cipher.IDEA", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", MiscObjectIdentifiers.as_sys_sec_alg_ideaCBC, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHAANDIDEA-CBC", Mappings.PREFIX + "$PBEWithSHAAndIDEA");
            configurableProvider.addAlgorithm("KeyGenerator.IDEA", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator", MiscObjectIdentifiers.as_sys_sec_alg_ideaCBC, Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAANDIDEA-CBC", Mappings.PREFIX + "$PBEWithSHAAndIDEAKeyGen");
            configurableProvider.addAlgorithm("Mac.IDEAMAC", Mappings.PREFIX + "$Mac");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.IDEA", "IDEAMAC");
            configurableProvider.addAlgorithm("Mac.IDEAMAC/CFB8", Mappings.PREFIX + "$CFB8Mac");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.IDEA/CFB8", "IDEAMAC/CFB8");
        }
        
        static {
            PREFIX = IDEA.class.getName();
        }
    }
    
    public static class PBEWithSHAAndIDEA extends BaseBlockCipher
    {
        public PBEWithSHAAndIDEA() {
            super(new CBCBlockCipher(new IDEAEngine()));
        }
    }
    
    public static class PBEWithSHAAndIDEAKeyGen extends PBESecretKeyFactory
    {
        public PBEWithSHAAndIDEAKeyGen() {
            super("PBEwithSHAandIDEA-CBC", null, true, 2, 1, 128, 64);
        }
    }
}
