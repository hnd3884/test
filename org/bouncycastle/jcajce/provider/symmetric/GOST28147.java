package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.GOST28147Mac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.engines.GOST28147WrapEngine;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.CryptoProWrapEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Map;

public final class GOST28147
{
    private static Map<ASN1ObjectIdentifier, String> oidMappings;
    private static Map<String, ASN1ObjectIdentifier> nameMappings;
    
    private GOST28147() {
    }
    
    static {
        GOST28147.oidMappings = new HashMap<ASN1ObjectIdentifier, String>();
        GOST28147.nameMappings = new HashMap<String, ASN1ObjectIdentifier>();
        GOST28147.oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_TestParamSet, "E-TEST");
        GOST28147.oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, "E-A");
        GOST28147.oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_B_ParamSet, "E-B");
        GOST28147.oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_C_ParamSet, "E-C");
        GOST28147.oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_D_ParamSet, "E-D");
        GOST28147.nameMappings.put("E-A", CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet);
        GOST28147.nameMappings.put("E-B", CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_B_ParamSet);
        GOST28147.nameMappings.put("E-C", CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_C_ParamSet);
        GOST28147.nameMappings.put("E-D", CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_D_ParamSet);
    }
    
    public static class AlgParamGen extends BaseAlgorithmParameterGenerator
    {
        byte[] iv;
        byte[] sBox;
        
        public AlgParamGen() {
            this.iv = new byte[8];
            this.sBox = GOST28147Engine.getSBox("E-A");
        }
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            if (algorithmParameterSpec instanceof GOST28147ParameterSpec) {
                this.sBox = ((GOST28147ParameterSpec)algorithmParameterSpec).getSBox();
                return;
            }
            throw new InvalidAlgorithmParameterException("parameter spec not supported");
        }
        
        @Override
        protected AlgorithmParameters engineGenerateParameters() {
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            this.random.nextBytes(this.iv);
            AlgorithmParameters parametersInstance;
            try {
                parametersInstance = this.createParametersInstance("GOST28147");
                parametersInstance.init(new GOST28147ParameterSpec(this.sBox, this.iv));
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
            return parametersInstance;
        }
    }
    
    public static class AlgParams extends BaseAlgParams
    {
        private ASN1ObjectIdentifier sBox;
        private byte[] iv;
        
        public AlgParams() {
            this.sBox = CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet;
        }
        
        @Override
        protected byte[] localGetEncoded() throws IOException {
            return new GOST28147Parameters(this.iv, this.sBox).getEncoded();
        }
        
        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
            if (clazz == IvParameterSpec.class) {
                return new IvParameterSpec(this.iv);
            }
            if (clazz == GOST28147ParameterSpec.class || clazz == AlgorithmParameterSpec.class) {
                return new GOST28147ParameterSpec(this.sBox, this.iv);
            }
            throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + clazz.getName());
        }
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (!(algorithmParameterSpec instanceof IvParameterSpec)) {
                if (algorithmParameterSpec instanceof GOST28147ParameterSpec) {
                    this.iv = ((GOST28147ParameterSpec)algorithmParameterSpec).getIV();
                    try {
                        this.sBox = BaseAlgParams.getSBoxOID(((GOST28147ParameterSpec)algorithmParameterSpec).getSBox());
                        return;
                    }
                    catch (final IllegalArgumentException ex) {
                        throw new InvalidParameterSpecException(ex.getMessage());
                    }
                }
                throw new InvalidParameterSpecException("IvParameterSpec required to initialise a IV parameters algorithm parameters object");
            }
            this.iv = ((IvParameterSpec)algorithmParameterSpec).getIV();
        }
        
        protected void localInit(final byte[] array) throws IOException {
            final ASN1Primitive fromByteArray = ASN1Primitive.fromByteArray(array);
            if (fromByteArray instanceof ASN1OctetString) {
                this.iv = ASN1OctetString.getInstance(fromByteArray).getOctets();
            }
            else {
                if (!(fromByteArray instanceof ASN1Sequence)) {
                    throw new IOException("Unable to recognize parameters");
                }
                final GOST28147Parameters instance = GOST28147Parameters.getInstance(fromByteArray);
                this.sBox = instance.getEncryptionParamSet();
                this.iv = instance.getIV();
            }
        }
        
        @Override
        protected String engineToString() {
            return "GOST 28147 IV Parameters";
        }
    }
    
    public abstract static class BaseAlgParams extends BaseAlgorithmParameters
    {
        private ASN1ObjectIdentifier sBox;
        private byte[] iv;
        
        public BaseAlgParams() {
            this.sBox = CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet;
        }
        
        @Override
        protected final void engineInit(final byte[] array) throws IOException {
            this.engineInit(array, "ASN.1");
        }
        
        @Override
        protected final byte[] engineGetEncoded() throws IOException {
            return this.engineGetEncoded("ASN.1");
        }
        
        @Override
        protected final byte[] engineGetEncoded(final String s) throws IOException {
            if (this.isASN1FormatString(s)) {
                return this.localGetEncoded();
            }
            throw new IOException("Unknown parameter format: " + s);
        }
        
        @Override
        protected final void engineInit(final byte[] array, final String s) throws IOException {
            if (array == null) {
                throw new NullPointerException("Encoded parameters cannot be null");
            }
            if (this.isASN1FormatString(s)) {
                try {
                    this.localInit(array);
                    return;
                }
                catch (final IOException ex) {
                    throw ex;
                }
                catch (final Exception ex2) {
                    throw new IOException("Parameter parsing failed: " + ex2.getMessage());
                }
                throw new IOException("Unknown parameter format: " + s);
            }
            throw new IOException("Unknown parameter format: " + s);
        }
        
        protected byte[] localGetEncoded() throws IOException {
            return new GOST28147Parameters(this.iv, this.sBox).getEncoded();
        }
        
        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
            if (clazz == IvParameterSpec.class) {
                return new IvParameterSpec(this.iv);
            }
            if (clazz == GOST28147ParameterSpec.class || clazz == AlgorithmParameterSpec.class) {
                return new GOST28147ParameterSpec(this.sBox, this.iv);
            }
            throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + clazz.getName());
        }
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (!(algorithmParameterSpec instanceof IvParameterSpec)) {
                if (algorithmParameterSpec instanceof GOST28147ParameterSpec) {
                    this.iv = ((GOST28147ParameterSpec)algorithmParameterSpec).getIV();
                    try {
                        this.sBox = getSBoxOID(((GOST28147ParameterSpec)algorithmParameterSpec).getSBox());
                        return;
                    }
                    catch (final IllegalArgumentException ex) {
                        throw new InvalidParameterSpecException(ex.getMessage());
                    }
                }
                throw new InvalidParameterSpecException("IvParameterSpec required to initialise a IV parameters algorithm parameters object");
            }
            this.iv = ((IvParameterSpec)algorithmParameterSpec).getIV();
        }
        
        protected static ASN1ObjectIdentifier getSBoxOID(final String s) {
            final ASN1ObjectIdentifier asn1ObjectIdentifier = GOST28147.nameMappings.get(s);
            if (asn1ObjectIdentifier == null) {
                throw new IllegalArgumentException("Unknown SBOX name: " + s);
            }
            return asn1ObjectIdentifier;
        }
        
        protected static ASN1ObjectIdentifier getSBoxOID(final byte[] array) {
            return getSBoxOID(GOST28147Engine.getSBoxName(array));
        }
        
        abstract void localInit(final byte[] p0) throws IOException;
    }
    
    public static class CBC extends BaseBlockCipher
    {
        public CBC() {
            super(new CBCBlockCipher(new GOST28147Engine()), 64);
        }
    }
    
    public static class CryptoProWrap extends BaseWrapCipher
    {
        public CryptoProWrap() {
            super(new CryptoProWrapEngine());
        }
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new GOST28147Engine());
        }
    }
    
    public static class GCFB extends BaseBlockCipher
    {
        public GCFB() {
            super(new BufferedBlockCipher(new GCFBBlockCipher(new GOST28147Engine())), 64);
        }
    }
    
    public static class GostWrap extends BaseWrapCipher
    {
        public GostWrap() {
            super(new GOST28147WrapEngine());
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            this(256);
        }
        
        public KeyGen(final int n) {
            super("GOST28147", n, new CipherKeyGenerator());
        }
    }
    
    public static class Mac extends BaseMac
    {
        public Mac() {
            super(new GOST28147Mac());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.GOST28147", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.GOST", "GOST28147");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.GOST-28147", "GOST28147");
            configurableProvider.addAlgorithm("Cipher." + CryptoProObjectIdentifiers.gostR28147_gcfb, Mappings.PREFIX + "$GCFB");
            configurableProvider.addAlgorithm("KeyGenerator.GOST28147", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.GOST", "GOST28147");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.GOST-28147", "GOST28147");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + CryptoProObjectIdentifiers.gostR28147_gcfb, "GOST28147");
            configurableProvider.addAlgorithm("AlgorithmParameters.GOST28147", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.GOST28147", Mappings.PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + CryptoProObjectIdentifiers.gostR28147_gcfb, "GOST28147");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + CryptoProObjectIdentifiers.gostR28147_gcfb, "GOST28147");
            configurableProvider.addAlgorithm("Cipher." + CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap, Mappings.PREFIX + "$CryptoProWrap");
            configurableProvider.addAlgorithm("Cipher." + CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap, Mappings.PREFIX + "$GostWrap");
            configurableProvider.addAlgorithm("Mac.GOST28147MAC", Mappings.PREFIX + "$Mac");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.GOST28147", "GOST28147MAC");
        }
        
        static {
            PREFIX = GOST28147.class.getName();
        }
    }
}
