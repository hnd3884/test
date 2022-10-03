package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.engines.ARIAWrapPadEngine;
import org.bouncycastle.crypto.engines.ARIAWrapEngine;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.asn1.nsri.NSRIObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.ARIAEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.asn1.cms.GCMParameters;
import java.io.IOException;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;
import org.bouncycastle.asn1.cms.CCMParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import javax.crypto.spec.IvParameterSpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;

public final class ARIA
{
    private ARIA() {
    }
    
    public static class AlgParamGen extends BaseAlgorithmParameterGenerator
    {
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for ARIA parameter generation.");
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
                parametersInstance = this.createParametersInstance("ARIA");
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
            return "ARIA IV";
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
            super(new CBCBlockCipher(new ARIAEngine()), 128);
        }
    }
    
    public static class CFB extends BaseBlockCipher
    {
        public CFB() {
            super(new BufferedBlockCipher(new CFBBlockCipher(new ARIAEngine(), 128)), 128);
        }
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new BlockCipherProvider() {
                public BlockCipher get() {
                    return new ARIAEngine();
                }
            });
        }
    }
    
    public static class GMAC extends BaseMac
    {
        public GMAC() {
            super(new GMac(new GCMBlockCipher(new ARIAEngine())));
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            this(256);
        }
        
        public KeyGen(final int n) {
            super("ARIA", n, new CipherKeyGenerator());
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
            configurableProvider.addAlgorithm("AlgorithmParameters.ARIA", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria128_cbc, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria192_cbc, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria256_cbc, "ARIA");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.ARIA", Mappings.PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_cbc, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_cbc, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_cbc, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_ofb, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_ofb, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_ofb, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_cfb, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_cfb, "ARIA");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_cfb, "ARIA");
            configurableProvider.addAlgorithm("Cipher.ARIA", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_ecb, Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_ecb, Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_ecb, Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_cbc, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_cbc, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_cbc, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_cfb, Mappings.PREFIX + "$CFB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_cfb, Mappings.PREFIX + "$CFB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_cfb, Mappings.PREFIX + "$CFB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_ofb, Mappings.PREFIX + "$OFB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_ofb, Mappings.PREFIX + "$OFB");
            configurableProvider.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_ofb, Mappings.PREFIX + "$OFB");
            configurableProvider.addAlgorithm("Cipher.ARIARFC3211WRAP", Mappings.PREFIX + "$RFC3211Wrap");
            configurableProvider.addAlgorithm("Cipher.ARIAWRAP", Mappings.PREFIX + "$Wrap");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_kw, "ARIAWRAP");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_kw, "ARIAWRAP");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_kw, "ARIAWRAP");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.ARIAKW", "ARIAWRAP");
            configurableProvider.addAlgorithm("Cipher.ARIAWRAPPAD", Mappings.PREFIX + "$WrapPad");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_kwp, "ARIAWRAPPAD");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_kwp, "ARIAWRAPPAD");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_kwp, "ARIAWRAPPAD");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.ARIAKWP", "ARIAWRAPPAD");
            configurableProvider.addAlgorithm("KeyGenerator.ARIA", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_kw, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_kw, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_kw, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_kwp, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_kwp, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_kwp, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ecb, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ecb, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ecb, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_cbc, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_cbc, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_cbc, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_cfb, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_cfb, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_cfb, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ofb, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ofb, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ofb, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ccm, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ccm, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ccm, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_gcm, Mappings.PREFIX + "$KeyGen128");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_gcm, Mappings.PREFIX + "$KeyGen192");
            configurableProvider.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_gcm, Mappings.PREFIX + "$KeyGen256");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.ARIACCM", Mappings.PREFIX + "$AlgParamGenCCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria128_ccm, "CCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria192_ccm, "CCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria256_ccm, "CCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_ccm, "CCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_ccm, "CCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_ccm, "CCM");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.ARIAGCM", Mappings.PREFIX + "$AlgParamGenGCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria128_gcm, "GCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria192_gcm, "GCM");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria256_gcm, "GCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_gcm, "GCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_gcm, "GCM");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_gcm, "GCM");
            this.addGMacAlgorithm(configurableProvider, "ARIA", Mappings.PREFIX + "$GMAC", Mappings.PREFIX + "$KeyGen");
            this.addPoly1305Algorithm(configurableProvider, "ARIA", Mappings.PREFIX + "$Poly1305", Mappings.PREFIX + "$Poly1305KeyGen");
        }
        
        static {
            PREFIX = ARIA.class.getName();
        }
    }
    
    public static class OFB extends BaseBlockCipher
    {
        public OFB() {
            super(new BufferedBlockCipher(new OFBBlockCipher(new ARIAEngine(), 128)), 128);
        }
    }
    
    public static class Poly1305 extends BaseMac
    {
        public Poly1305() {
            super(new org.bouncycastle.crypto.macs.Poly1305(new ARIAEngine()));
        }
    }
    
    public static class Poly1305KeyGen extends BaseKeyGenerator
    {
        public Poly1305KeyGen() {
            super("Poly1305-ARIA", 256, new Poly1305KeyGenerator());
        }
    }
    
    public static class RFC3211Wrap extends BaseWrapCipher
    {
        public RFC3211Wrap() {
            super(new RFC3211WrapEngine(new ARIAEngine()), 16);
        }
    }
    
    public static class Wrap extends BaseWrapCipher
    {
        public Wrap() {
            super(new ARIAWrapEngine());
        }
    }
    
    public static class WrapPad extends BaseWrapCipher
    {
        public WrapPad() {
            super(new ARIAWrapPadEngine());
        }
    }
}
