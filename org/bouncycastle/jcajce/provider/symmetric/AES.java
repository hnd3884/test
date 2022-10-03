package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.engines.AESWrapPadEngine;
import org.bouncycastle.crypto.engines.AESWrapEngine;
import org.bouncycastle.crypto.engines.RFC5649WrapEngine;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import java.io.IOException;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.asn1.cms.GCMParameters;
import org.bouncycastle.asn1.cms.CCMParameters;
import javax.crypto.spec.IvParameterSpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import java.util.HashMap;
import java.util.Map;

public final class AES
{
    private static final Map<String, String> generalAesAttributes;
    
    private AES() {
    }
    
    static {
        (generalAesAttributes = new HashMap<String, String>()).put("SupportedKeyClasses", "javax.crypto.SecretKey");
        AES.generalAesAttributes.put("SupportedKeyFormats", "RAW");
    }
    
    public static class AESCCMMAC extends BaseMac
    {
        public AESCCMMAC() {
            super(new CCMMac());
        }
        
        private static class CCMMac implements Mac
        {
            private final CCMBlockCipher ccm;
            private int macLength;
            
            private CCMMac() {
                this.ccm = new CCMBlockCipher(new AESEngine());
                this.macLength = 8;
            }
            
            public void init(final CipherParameters cipherParameters) throws IllegalArgumentException {
                this.ccm.init(true, cipherParameters);
                this.macLength = this.ccm.getMac().length;
            }
            
            public String getAlgorithmName() {
                return this.ccm.getAlgorithmName() + "Mac";
            }
            
            public int getMacSize() {
                return this.macLength;
            }
            
            public void update(final byte b) throws IllegalStateException {
                this.ccm.processAADByte(b);
            }
            
            public void update(final byte[] array, final int n, final int n2) throws DataLengthException, IllegalStateException {
                this.ccm.processAADBytes(array, n, n2);
            }
            
            public int doFinal(final byte[] array, final int n) throws DataLengthException, IllegalStateException {
                try {
                    return this.ccm.doFinal(array, 0);
                }
                catch (final InvalidCipherTextException ex) {
                    throw new IllegalStateException("exception on doFinal(): " + ex.toString());
                }
            }
            
            public void reset() {
                this.ccm.reset();
            }
        }
    }
    
    public static class AESCMAC extends BaseMac
    {
        public AESCMAC() {
            super(new CMac(new AESEngine()));
        }
    }
    
    public static class AESGMAC extends BaseMac
    {
        public AESGMAC() {
            super(new GMac(new GCMBlockCipher(new AESEngine())));
        }
    }
    
    public static class AlgParamGen extends BaseAlgorithmParameterGenerator
    {
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
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
                parametersInstance = this.createParametersInstance("AES");
                parametersInstance.init(new IvParameterSpec(array));
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
            return parametersInstance;
        }
    }
    
    public static class AlgParamGenCCM extends BaseAlgorithmParameterGenerator
    {
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
        }
        
        @Override
        protected AlgorithmParameters engineGenerateParameters() {
            final byte[] array = new byte[12];
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            this.random.nextBytes(array);
            AlgorithmParameters parametersInstance;
            try {
                parametersInstance = this.createParametersInstance("CCM");
                parametersInstance.init(new CCMParameters(array, 12).getEncoded());
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
            return parametersInstance;
        }
    }
    
    public static class AlgParamGenGCM extends BaseAlgorithmParameterGenerator
    {
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
        }
        
        @Override
        protected AlgorithmParameters engineGenerateParameters() {
            final byte[] array = new byte[12];
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            this.random.nextBytes(array);
            AlgorithmParameters parametersInstance;
            try {
                parametersInstance = this.createParametersInstance("GCM");
                parametersInstance.init(new GCMParameters(array, 16).getEncoded());
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
            return "AES IV";
        }
    }
    
    public static class AlgParamsCCM extends BaseAlgorithmParameters
    {
        private CCMParameters ccmParams;
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (GcmSpecUtil.isGcmSpec(algorithmParameterSpec)) {
                this.ccmParams = CCMParameters.getInstance(GcmSpecUtil.extractGcmParameters(algorithmParameterSpec));
            }
            else {
                if (!(algorithmParameterSpec instanceof AEADParameterSpec)) {
                    throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + algorithmParameterSpec.getClass().getName());
                }
                this.ccmParams = new CCMParameters(((AEADParameterSpec)algorithmParameterSpec).getNonce(), ((AEADParameterSpec)algorithmParameterSpec).getMacSizeInBits() / 8);
            }
        }
        
        @Override
        protected void engineInit(final byte[] array) throws IOException {
            this.ccmParams = CCMParameters.getInstance(array);
        }
        
        @Override
        protected void engineInit(final byte[] array, final String s) throws IOException {
            if (!this.isASN1FormatString(s)) {
                throw new IOException("unknown format specified");
            }
            this.ccmParams = CCMParameters.getInstance(array);
        }
        
        @Override
        protected byte[] engineGetEncoded() throws IOException {
            return this.ccmParams.getEncoded();
        }
        
        @Override
        protected byte[] engineGetEncoded(final String s) throws IOException {
            if (!this.isASN1FormatString(s)) {
                throw new IOException("unknown format specified");
            }
            return this.ccmParams.getEncoded();
        }
        
        @Override
        protected String engineToString() {
            return "CCM";
        }
        
        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
            if (clazz == AlgorithmParameterSpec.class || GcmSpecUtil.isGcmSpec(clazz)) {
                if (GcmSpecUtil.gcmSpecExists()) {
                    return GcmSpecUtil.extractGcmSpec(this.ccmParams.toASN1Primitive());
                }
                return new AEADParameterSpec(this.ccmParams.getNonce(), this.ccmParams.getIcvLen() * 8);
            }
            else {
                if (clazz == AEADParameterSpec.class) {
                    return new AEADParameterSpec(this.ccmParams.getNonce(), this.ccmParams.getIcvLen() * 8);
                }
                if (clazz == IvParameterSpec.class) {
                    return new IvParameterSpec(this.ccmParams.getNonce());
                }
                throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + clazz.getName());
            }
        }
    }
    
    public static class AlgParamsGCM extends BaseAlgorithmParameters
    {
        private GCMParameters gcmParams;
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (GcmSpecUtil.isGcmSpec(algorithmParameterSpec)) {
                this.gcmParams = GcmSpecUtil.extractGcmParameters(algorithmParameterSpec);
            }
            else {
                if (!(algorithmParameterSpec instanceof AEADParameterSpec)) {
                    throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + algorithmParameterSpec.getClass().getName());
                }
                this.gcmParams = new GCMParameters(((AEADParameterSpec)algorithmParameterSpec).getNonce(), ((AEADParameterSpec)algorithmParameterSpec).getMacSizeInBits() / 8);
            }
        }
        
        @Override
        protected void engineInit(final byte[] array) throws IOException {
            this.gcmParams = GCMParameters.getInstance(array);
        }
        
        @Override
        protected void engineInit(final byte[] array, final String s) throws IOException {
            if (!this.isASN1FormatString(s)) {
                throw new IOException("unknown format specified");
            }
            this.gcmParams = GCMParameters.getInstance(array);
        }
        
        @Override
        protected byte[] engineGetEncoded() throws IOException {
            return this.gcmParams.getEncoded();
        }
        
        @Override
        protected byte[] engineGetEncoded(final String s) throws IOException {
            if (!this.isASN1FormatString(s)) {
                throw new IOException("unknown format specified");
            }
            return this.gcmParams.getEncoded();
        }
        
        @Override
        protected String engineToString() {
            return "GCM";
        }
        
        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
            if (clazz == AlgorithmParameterSpec.class || GcmSpecUtil.isGcmSpec(clazz)) {
                if (GcmSpecUtil.gcmSpecExists()) {
                    return GcmSpecUtil.extractGcmSpec(this.gcmParams.toASN1Primitive());
                }
                return new AEADParameterSpec(this.gcmParams.getNonce(), this.gcmParams.getIcvLen() * 8);
            }
            else {
                if (clazz == AEADParameterSpec.class) {
                    return new AEADParameterSpec(this.gcmParams.getNonce(), this.gcmParams.getIcvLen() * 8);
                }
                if (clazz == IvParameterSpec.class) {
                    return new IvParameterSpec(this.gcmParams.getNonce());
                }
                throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + clazz.getName());
            }
        }
    }
    
    public static class CBC extends BaseBlockCipher
    {
        public CBC() {
            super(new CBCBlockCipher(new AESEngine()), 128);
        }
    }
    
    public static class CCM extends BaseBlockCipher
    {
        public CCM() {
            super(new CCMBlockCipher(new AESEngine()), false, 16);
        }
    }
    
    public static class CFB extends BaseBlockCipher
    {
        public CFB() {
            super(new BufferedBlockCipher(new CFBBlockCipher(new AESEngine(), 128)), 128);
        }
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new BlockCipherProvider() {
                public BlockCipher get() {
                    return new AESEngine();
                }
            });
        }
    }
    
    public static class GCM extends BaseBlockCipher
    {
        public GCM() {
            super(new GCMBlockCipher(new AESEngine()));
        }
    }
    
    public static class KeyFactory extends BaseSecretKeyFactory
    {
        public KeyFactory() {
            super("AES", null);
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            this(192);
        }
        
        public KeyGen(final int n) {
            super("AES", n, new CipherKeyGenerator());
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
        private static final String wrongAES128 = "2.16.840.1.101.3.4.2";
        private static final String wrongAES192 = "2.16.840.1.101.3.4.22";
        private static final String wrongAES256 = "2.16.840.1.101.3.4.42";
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("AlgorithmParameters.AES", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.2", "AES");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.22", "AES");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.42", "AES");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_CBC, "AES");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_CBC, "AES");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_CBC, "AES");
            configurableProvider.addAlgorithm("AlgorithmParameters.GCM", Mappings.PREFIX + "$AlgParamsGCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_GCM, "GCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_GCM, "GCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_GCM, "GCM");
            configurableProvider.addAlgorithm("AlgorithmParameters.CCM", Mappings.PREFIX + "$AlgParamsCCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_CCM, "CCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_CCM, "CCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_CCM, "CCM");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.AES", Mappings.PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.2", "AES");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.22", "AES");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.42", "AES");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_CBC, "AES");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_CBC, "AES");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_CBC, "AES");
            configurableProvider.addAttributes("Cipher.AES", AES.generalAesAttributes);
            configurableProvider.addAlgorithm("Cipher.AES", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.2", "AES");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.22", "AES");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.42", "AES");
            configurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_ECB, Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_ECB, Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_ECB, Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_CBC, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_CBC, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_CBC, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_OFB, Mappings.PREFIX + "$OFB");
            configurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_OFB, Mappings.PREFIX + "$OFB");
            configurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_OFB, Mappings.PREFIX + "$OFB");
            configurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_CFB, Mappings.PREFIX + "$CFB");
            configurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_CFB, Mappings.PREFIX + "$CFB");
            configurableProvider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_CFB, Mappings.PREFIX + "$CFB");
            configurableProvider.addAttributes("Cipher.AESWRAP", AES.generalAesAttributes);
            configurableProvider.addAlgorithm("Cipher.AESWRAP", Mappings.PREFIX + "$Wrap");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_wrap, "AESWRAP");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_wrap, "AESWRAP");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_wrap, "AESWRAP");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.AESKW", "AESWRAP");
            configurableProvider.addAttributes("Cipher.AESWRAPPAD", AES.generalAesAttributes);
            configurableProvider.addAlgorithm("Cipher.AESWRAPPAD", Mappings.PREFIX + "$WrapPad");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_wrap_pad, "AESWRAPPAD");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_wrap_pad, "AESWRAPPAD");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_wrap_pad, "AESWRAPPAD");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.AESKWP", "AESWRAPPAD");
            configurableProvider.addAlgorithm("Cipher.AESRFC3211WRAP", Mappings.PREFIX + "$RFC3211Wrap");
            configurableProvider.addAlgorithm("Cipher.AESRFC5649WRAP", Mappings.PREFIX + "$RFC5649Wrap");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.CCM", Mappings.PREFIX + "$AlgParamGenCCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_CCM, "CCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_CCM, "CCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_CCM, "CCM");
            configurableProvider.addAttributes("Cipher.CCM", AES.generalAesAttributes);
            configurableProvider.addAlgorithm("Cipher.CCM", Mappings.PREFIX + "$CCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_CCM, "CCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_CCM, "CCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_CCM, "CCM");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.GCM", Mappings.PREFIX + "$AlgParamGenGCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_GCM, "GCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_GCM, "GCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_GCM, "GCM");
            configurableProvider.addAttributes("Cipher.GCM", AES.generalAesAttributes);
            configurableProvider.addAlgorithm("Cipher.GCM", Mappings.PREFIX + "$GCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_GCM, "GCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_GCM, "GCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_GCM, "GCM");
            configurableProvider.addAlgorithm("KeyGenerator.AES", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.2", Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.22", Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.42", Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_ECB, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CBC, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_OFB, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CFB, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_ECB, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CBC, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_OFB, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CFB, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_ECB, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CBC, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_OFB, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CFB, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator.AESWRAP", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_wrap, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_wrap, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_wrap, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_GCM, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_GCM, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_GCM, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CCM, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CCM, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CCM, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator.AESWRAPPAD", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_wrap_pad, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_wrap_pad, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_wrap_pad, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("Mac.AESCMAC", Mappings.PREFIX + "$AESCMAC");
            configurableProvider.addAlgorithm("Mac.AESCCMMAC", Mappings.PREFIX + "$AESCCMMAC");
            configurableProvider.addAlgorithm("Alg.Alias.Mac." + NISTObjectIdentifiers.id_aes128_CCM.getId(), "AESCCMMAC");
            configurableProvider.addAlgorithm("Alg.Alias.Mac." + NISTObjectIdentifiers.id_aes192_CCM.getId(), "AESCCMMAC");
            configurableProvider.addAlgorithm("Alg.Alias.Mac." + NISTObjectIdentifiers.id_aes256_CCM.getId(), "AESCCMMAC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc, "PBEWITHSHAAND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc, "PBEWITHSHAAND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc, "PBEWITHSHAAND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc, "PBEWITHSHA256AND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc, "PBEWITHSHA256AND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc, "PBEWITHSHA256AND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND128BITAES-CBC-BC", Mappings.PREFIX + "$PBEWithSHA1AESCBC128");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND192BITAES-CBC-BC", Mappings.PREFIX + "$PBEWithSHA1AESCBC192");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND256BITAES-CBC-BC", Mappings.PREFIX + "$PBEWithSHA1AESCBC256");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHA256AND128BITAES-CBC-BC", Mappings.PREFIX + "$PBEWithSHA256AESCBC128");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHA256AND192BITAES-CBC-BC", Mappings.PREFIX + "$PBEWithSHA256AESCBC192");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHA256AND256BITAES-CBC-BC", Mappings.PREFIX + "$PBEWithSHA256AESCBC256");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND128BITAES-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND192BITAES-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND256BITAES-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND128BITAES-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND192BITAES-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND256BITAES-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND128BITAES-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND192BITAES-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND256BITAES-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND128BITAES-CBC-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND192BITAES-CBC-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND256BITAES-CBC-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Cipher.PBEWITHMD5AND128BITAES-CBC-OPENSSL", Mappings.PREFIX + "$PBEWithAESCBC");
            configurableProvider.addAlgorithm("Cipher.PBEWITHMD5AND192BITAES-CBC-OPENSSL", Mappings.PREFIX + "$PBEWithAESCBC");
            configurableProvider.addAlgorithm("Cipher.PBEWITHMD5AND256BITAES-CBC-OPENSSL", Mappings.PREFIX + "$PBEWithAESCBC");
            configurableProvider.addAlgorithm("SecretKeyFactory.AES", Mappings.PREFIX + "$KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.aes, Mappings.PREFIX + "$KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND128BITAES-CBC-OPENSSL", Mappings.PREFIX + "$PBEWithMD5And128BitAESCBCOpenSSL");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND192BITAES-CBC-OPENSSL", Mappings.PREFIX + "$PBEWithMD5And192BitAESCBCOpenSSL");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND256BITAES-CBC-OPENSSL", Mappings.PREFIX + "$PBEWithMD5And256BitAESCBCOpenSSL");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND128BITAES-CBC-BC", Mappings.PREFIX + "$PBEWithSHAAnd128BitAESBC");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND192BITAES-CBC-BC", Mappings.PREFIX + "$PBEWithSHAAnd192BitAESBC");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND256BITAES-CBC-BC", Mappings.PREFIX + "$PBEWithSHAAnd256BitAESBC");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND128BITAES-CBC-BC", Mappings.PREFIX + "$PBEWithSHA256And128BitAESBC");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND192BITAES-CBC-BC", Mappings.PREFIX + "$PBEWithSHA256And192BitAESBC");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND256BITAES-CBC-BC", Mappings.PREFIX + "$PBEWithSHA256And256BitAESBC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND128BITAES-CBC-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND192BITAES-CBC-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND256BITAES-CBC-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc, "PBEWITHSHAAND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc, "PBEWITHSHAAND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc, "PBEWITHSHAAND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc, "PBEWITHSHA256AND128BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc, "PBEWITHSHA256AND192BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc, "PBEWITHSHA256AND256BITAES-CBC-BC");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND128BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND192BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND256BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND128BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND192BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND256BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND128BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND192BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND256BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND128BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND192BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND256BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND128BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND192BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND256BITAES-CBC-BC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc.getId(), "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc.getId(), "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc.getId(), "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc.getId(), "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc.getId(), "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc.getId(), "PKCS12PBE");
            this.addGMacAlgorithm(configurableProvider, "AES", Mappings.PREFIX + "$AESGMAC", Mappings.PREFIX + "$KeyGen128");
            this.addPoly1305Algorithm(configurableProvider, "AES", Mappings.PREFIX + "$Poly1305", Mappings.PREFIX + "$Poly1305KeyGen");
        }
        
        static {
            PREFIX = AES.class.getName();
        }
    }
    
    public static class OFB extends BaseBlockCipher
    {
        public OFB() {
            super(new BufferedBlockCipher(new OFBBlockCipher(new AESEngine(), 128)), 128);
        }
    }
    
    public static class PBEWithAESCBC extends BaseBlockCipher
    {
        public PBEWithAESCBC() {
            super(new CBCBlockCipher(new AESEngine()));
        }
    }
    
    public static class PBEWithMD5And128BitAESCBCOpenSSL extends PBESecretKeyFactory
    {
        public PBEWithMD5And128BitAESCBCOpenSSL() {
            super("PBEWithMD5And128BitAES-CBC-OpenSSL", null, true, 3, 0, 128, 128);
        }
    }
    
    public static class PBEWithMD5And192BitAESCBCOpenSSL extends PBESecretKeyFactory
    {
        public PBEWithMD5And192BitAESCBCOpenSSL() {
            super("PBEWithMD5And192BitAES-CBC-OpenSSL", null, true, 3, 0, 192, 128);
        }
    }
    
    public static class PBEWithMD5And256BitAESCBCOpenSSL extends PBESecretKeyFactory
    {
        public PBEWithMD5And256BitAESCBCOpenSSL() {
            super("PBEWithMD5And256BitAES-CBC-OpenSSL", null, true, 3, 0, 256, 128);
        }
    }
    
    public static class PBEWithSHA1AESCBC128 extends BaseBlockCipher
    {
        public PBEWithSHA1AESCBC128() {
            super(new CBCBlockCipher(new AESEngine()), 2, 1, 128, 16);
        }
    }
    
    public static class PBEWithSHA1AESCBC192 extends BaseBlockCipher
    {
        public PBEWithSHA1AESCBC192() {
            super(new CBCBlockCipher(new AESEngine()), 2, 1, 192, 16);
        }
    }
    
    public static class PBEWithSHA1AESCBC256 extends BaseBlockCipher
    {
        public PBEWithSHA1AESCBC256() {
            super(new CBCBlockCipher(new AESEngine()), 2, 1, 256, 16);
        }
    }
    
    public static class PBEWithSHA256AESCBC128 extends BaseBlockCipher
    {
        public PBEWithSHA256AESCBC128() {
            super(new CBCBlockCipher(new AESEngine()), 2, 4, 128, 16);
        }
    }
    
    public static class PBEWithSHA256AESCBC192 extends BaseBlockCipher
    {
        public PBEWithSHA256AESCBC192() {
            super(new CBCBlockCipher(new AESEngine()), 2, 4, 192, 16);
        }
    }
    
    public static class PBEWithSHA256AESCBC256 extends BaseBlockCipher
    {
        public PBEWithSHA256AESCBC256() {
            super(new CBCBlockCipher(new AESEngine()), 2, 4, 256, 16);
        }
    }
    
    public static class PBEWithSHA256And128BitAESBC extends PBESecretKeyFactory
    {
        public PBEWithSHA256And128BitAESBC() {
            super("PBEWithSHA256And128BitAES-CBC-BC", null, true, 2, 4, 128, 128);
        }
    }
    
    public static class PBEWithSHA256And192BitAESBC extends PBESecretKeyFactory
    {
        public PBEWithSHA256And192BitAESBC() {
            super("PBEWithSHA256And192BitAES-CBC-BC", null, true, 2, 4, 192, 128);
        }
    }
    
    public static class PBEWithSHA256And256BitAESBC extends PBESecretKeyFactory
    {
        public PBEWithSHA256And256BitAESBC() {
            super("PBEWithSHA256And256BitAES-CBC-BC", null, true, 2, 4, 256, 128);
        }
    }
    
    public static class PBEWithSHAAnd128BitAESBC extends PBESecretKeyFactory
    {
        public PBEWithSHAAnd128BitAESBC() {
            super("PBEWithSHA1And128BitAES-CBC-BC", null, true, 2, 1, 128, 128);
        }
    }
    
    public static class PBEWithSHAAnd192BitAESBC extends PBESecretKeyFactory
    {
        public PBEWithSHAAnd192BitAESBC() {
            super("PBEWithSHA1And192BitAES-CBC-BC", null, true, 2, 1, 192, 128);
        }
    }
    
    public static class PBEWithSHAAnd256BitAESBC extends PBESecretKeyFactory
    {
        public PBEWithSHAAnd256BitAESBC() {
            super("PBEWithSHA1And256BitAES-CBC-BC", null, true, 2, 1, 256, 128);
        }
    }
    
    public static class Poly1305 extends BaseMac
    {
        public Poly1305() {
            super(new org.bouncycastle.crypto.macs.Poly1305(new AESEngine()));
        }
    }
    
    public static class Poly1305KeyGen extends BaseKeyGenerator
    {
        public Poly1305KeyGen() {
            super("Poly1305-AES", 256, new Poly1305KeyGenerator());
        }
    }
    
    public static class RFC3211Wrap extends BaseWrapCipher
    {
        public RFC3211Wrap() {
            super(new RFC3211WrapEngine(new AESEngine()), 16);
        }
    }
    
    public static class RFC5649Wrap extends BaseWrapCipher
    {
        public RFC5649Wrap() {
            super(new RFC5649WrapEngine(new AESEngine()));
        }
    }
    
    public static class Wrap extends BaseWrapCipher
    {
        public Wrap() {
            super(new AESWrapEngine());
        }
    }
    
    public static class WrapPad extends BaseWrapCipher
    {
        public WrapPad() {
            super(new AESWrapPadEngine());
        }
    }
}
