package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.generators.DESKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.jcajce.PBKDF1Key;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.macs.ISO9797Alg3Mac;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;

public final class DES
{
    private DES() {
    }
    
    public static class AlgParamGen extends BaseAlgorithmParameterGenerator
    {
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for DES parameter generation.");
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
                parametersInstance = this.createParametersInstance("DES");
                parametersInstance.init(new IvParameterSpec(array));
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
            return parametersInstance;
        }
    }
    
    public static class CBC extends BaseBlockCipher
    {
        public CBC() {
            super(new CBCBlockCipher(new DESEngine()), 64);
        }
    }
    
    public static class CBCMAC extends BaseMac
    {
        public CBCMAC() {
            super(new CBCBlockCipherMac(new DESEngine()));
        }
    }
    
    public static class CMAC extends BaseMac
    {
        public CMAC() {
            super(new CMac(new DESEngine()));
        }
    }
    
    public static class DES64 extends BaseMac
    {
        public DES64() {
            super(new CBCBlockCipherMac(new DESEngine(), 64));
        }
    }
    
    public static class DES64with7816d4 extends BaseMac
    {
        public DES64with7816d4() {
            super(new CBCBlockCipherMac(new DESEngine(), 64, new ISO7816d4Padding()));
        }
    }
    
    public static class DES9797Alg3 extends BaseMac
    {
        public DES9797Alg3() {
            super(new ISO9797Alg3Mac(new DESEngine()));
        }
    }
    
    public static class DES9797Alg3with7816d4 extends BaseMac
    {
        public DES9797Alg3with7816d4() {
            super(new ISO9797Alg3Mac(new DESEngine(), new ISO7816d4Padding()));
        }
    }
    
    public static class DESCFB8 extends BaseMac
    {
        public DESCFB8() {
            super(new CFBBlockCipherMac(new DESEngine()));
        }
    }
    
    public static class DESPBEKeyFactory extends BaseSecretKeyFactory
    {
        private boolean forCipher;
        private int scheme;
        private int digest;
        private int keySize;
        private int ivSize;
        
        public DESPBEKeyFactory(final String s, final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean forCipher, final int scheme, final int digest, final int keySize, final int ivSize) {
            super(s, asn1ObjectIdentifier);
            this.forCipher = forCipher;
            this.scheme = scheme;
            this.digest = digest;
            this.keySize = keySize;
            this.ivSize = ivSize;
        }
        
        @Override
        protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
            if (!(keySpec instanceof PBEKeySpec)) {
                throw new InvalidKeySpecException("Invalid KeySpec");
            }
            final PBEKeySpec pbeKeySpec = (PBEKeySpec)keySpec;
            if (pbeKeySpec.getSalt() != null) {
                CipherParameters cipherParameters;
                if (this.forCipher) {
                    cipherParameters = PBE.Util.makePBEParameters(pbeKeySpec, this.scheme, this.digest, this.keySize, this.ivSize);
                }
                else {
                    cipherParameters = PBE.Util.makePBEMacParameters(pbeKeySpec, this.scheme, this.digest, this.keySize);
                }
                KeyParameter keyParameter;
                if (cipherParameters instanceof ParametersWithIV) {
                    keyParameter = (KeyParameter)((ParametersWithIV)cipherParameters).getParameters();
                }
                else {
                    keyParameter = (KeyParameter)cipherParameters;
                }
                DESParameters.setOddParity(keyParameter.getKey());
                return new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, pbeKeySpec, cipherParameters);
            }
            if (this.scheme == 0 || this.scheme == 4) {
                return new PBKDF1Key(pbeKeySpec.getPassword(), (this.scheme == 0) ? PasswordConverter.ASCII : PasswordConverter.UTF8);
            }
            return new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, pbeKeySpec, null);
        }
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new DESEngine());
        }
    }
    
    public static class KeyFactory extends BaseSecretKeyFactory
    {
        public KeyFactory() {
            super("DES", null);
        }
        
        @Override
        protected KeySpec engineGetKeySpec(final SecretKey secretKey, final Class clazz) throws InvalidKeySpecException {
            if (clazz == null) {
                throw new InvalidKeySpecException("keySpec parameter is null");
            }
            if (secretKey == null) {
                throw new InvalidKeySpecException("key parameter is null");
            }
            if (SecretKeySpec.class.isAssignableFrom(clazz)) {
                return new SecretKeySpec(secretKey.getEncoded(), this.algName);
            }
            if (DESKeySpec.class.isAssignableFrom(clazz)) {
                final byte[] encoded = secretKey.getEncoded();
                try {
                    return new DESKeySpec(encoded);
                }
                catch (final Exception ex) {
                    throw new InvalidKeySpecException(ex.toString());
                }
            }
            throw new InvalidKeySpecException("Invalid KeySpec");
        }
        
        @Override
        protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
            if (keySpec instanceof DESKeySpec) {
                return new SecretKeySpec(((DESKeySpec)keySpec).getKey(), "DES");
            }
            return super.engineGenerateSecret(keySpec);
        }
    }
    
    public static class KeyGenerator extends BaseKeyGenerator
    {
        public KeyGenerator() {
            super("DES", 64, new DESKeyGenerator());
        }
        
        @Override
        protected void engineInit(final int n, final SecureRandom secureRandom) {
            super.engineInit(n, secureRandom);
        }
        
        @Override
        protected SecretKey engineGenerateKey() {
            if (this.uninitialised) {
                this.engine.init(new KeyGenerationParameters(new SecureRandom(), this.defaultKeySize));
                this.uninitialised = false;
            }
            return new SecretKeySpec(this.engine.generateKey(), this.algName);
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        private static final String PACKAGE = "org.bouncycastle.jcajce.provider.symmetric";
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.DES", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", OIWObjectIdentifiers.desCBC, Mappings.PREFIX + "$CBC");
            this.addAlias(configurableProvider, OIWObjectIdentifiers.desCBC, "DES");
            configurableProvider.addAlgorithm("Cipher.DESRFC3211WRAP", Mappings.PREFIX + "$RFC3211");
            configurableProvider.addAlgorithm("KeyGenerator.DES", Mappings.PREFIX + "$KeyGenerator");
            configurableProvider.addAlgorithm("SecretKeyFactory.DES", Mappings.PREFIX + "$KeyFactory");
            configurableProvider.addAlgorithm("Mac.DESCMAC", Mappings.PREFIX + "$CMAC");
            configurableProvider.addAlgorithm("Mac.DESMAC", Mappings.PREFIX + "$CBCMAC");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DES", "DESMAC");
            configurableProvider.addAlgorithm("Mac.DESMAC/CFB8", Mappings.PREFIX + "$DESCFB8");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DES/CFB8", "DESMAC/CFB8");
            configurableProvider.addAlgorithm("Mac.DESMAC64", Mappings.PREFIX + "$DES64");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DES64", "DESMAC64");
            configurableProvider.addAlgorithm("Mac.DESMAC64WITHISO7816-4PADDING", Mappings.PREFIX + "$DES64with7816d4");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DES64WITHISO7816-4PADDING", "DESMAC64WITHISO7816-4PADDING");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESISO9797ALG1MACWITHISO7816-4PADDING", "DESMAC64WITHISO7816-4PADDING");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESISO9797ALG1WITHISO7816-4PADDING", "DESMAC64WITHISO7816-4PADDING");
            configurableProvider.addAlgorithm("Mac.DESWITHISO9797", Mappings.PREFIX + "$DES9797Alg3");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESISO9797MAC", "DESWITHISO9797");
            configurableProvider.addAlgorithm("Mac.ISO9797ALG3MAC", Mappings.PREFIX + "$DES9797Alg3");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.ISO9797ALG3", "ISO9797ALG3MAC");
            configurableProvider.addAlgorithm("Mac.ISO9797ALG3WITHISO7816-4PADDING", Mappings.PREFIX + "$DES9797Alg3with7816d4");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.ISO9797ALG3MACWITHISO7816-4PADDING", "ISO9797ALG3WITHISO7816-4PADDING");
            configurableProvider.addAlgorithm("AlgorithmParameters.DES", "org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters", OIWObjectIdentifiers.desCBC, "DES");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.DES", Mappings.PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + OIWObjectIdentifiers.desCBC, "DES");
            configurableProvider.addAlgorithm("Cipher.PBEWITHMD2ANDDES", Mappings.PREFIX + "$PBEWithMD2");
            configurableProvider.addAlgorithm("Cipher.PBEWITHMD5ANDDES", Mappings.PREFIX + "$PBEWithMD5");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHA1ANDDES", Mappings.PREFIX + "$PBEWithSHA1");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC, "PBEWITHMD2ANDDES");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, "PBEWITHMD5ANDDES");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, "PBEWITHSHA1ANDDES");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHMD2ANDDES-CBC", "PBEWITHMD2ANDDES");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHMD5ANDDES-CBC", "PBEWITHMD5ANDDES");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1ANDDES-CBC", "PBEWITHSHA1ANDDES");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD2ANDDES", Mappings.PREFIX + "$PBEWithMD2KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHMD5ANDDES", Mappings.PREFIX + "$PBEWithMD5KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHA1ANDDES", Mappings.PREFIX + "$PBEWithSHA1KeyFactory");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHMD2ANDDES-CBC", "PBEWITHMD2ANDDES");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHMD5ANDDES-CBC", "PBEWITHMD5ANDDES");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1ANDDES-CBC", "PBEWITHSHA1ANDDES");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC, "PBEWITHMD2ANDDES");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, "PBEWITHMD5ANDDES");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, "PBEWITHSHA1ANDDES");
        }
        
        private void addAlias(final ConfigurableProvider configurableProvider, final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + asn1ObjectIdentifier.getId(), s);
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory." + asn1ObjectIdentifier.getId(), s);
        }
        
        static {
            PREFIX = DES.class.getName();
        }
    }
    
    public static class PBEWithMD2 extends BaseBlockCipher
    {
        public PBEWithMD2() {
            super(new CBCBlockCipher(new DESEngine()), 0, 5, 64, 8);
        }
    }
    
    public static class PBEWithMD2KeyFactory extends DESPBEKeyFactory
    {
        public PBEWithMD2KeyFactory() {
            super("PBEwithMD2andDES", PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC, true, 0, 5, 64, 64);
        }
    }
    
    public static class PBEWithMD5 extends BaseBlockCipher
    {
        public PBEWithMD5() {
            super(new CBCBlockCipher(new DESEngine()), 0, 0, 64, 8);
        }
    }
    
    public static class PBEWithMD5KeyFactory extends DESPBEKeyFactory
    {
        public PBEWithMD5KeyFactory() {
            super("PBEwithMD5andDES", PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, true, 0, 0, 64, 64);
        }
    }
    
    public static class PBEWithSHA1 extends BaseBlockCipher
    {
        public PBEWithSHA1() {
            super(new CBCBlockCipher(new DESEngine()), 0, 1, 64, 8);
        }
    }
    
    public static class PBEWithSHA1KeyFactory extends DESPBEKeyFactory
    {
        public PBEWithSHA1KeyFactory() {
            super("PBEwithSHA1andDES", PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, true, 0, 1, 64, 64);
        }
    }
    
    public static class RFC3211 extends BaseWrapCipher
    {
        public RFC3211() {
            super(new RFC3211WrapEngine(new DESEngine()), 8);
        }
    }
}
