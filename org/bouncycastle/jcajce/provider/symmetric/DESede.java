package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.engines.DESedeWrapEngine;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;

public final class DESede
{
    private DESede() {
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
            super(new CBCBlockCipher(new DESedeEngine()), 64);
        }
    }
    
    public static class CBCMAC extends BaseMac
    {
        public CBCMAC() {
            super(new CBCBlockCipherMac(new DESedeEngine()));
        }
    }
    
    public static class CMAC extends BaseMac
    {
        public CMAC() {
            super(new CMac(new DESedeEngine()));
        }
    }
    
    public static class DESede64 extends BaseMac
    {
        public DESede64() {
            super(new CBCBlockCipherMac(new DESedeEngine(), 64));
        }
    }
    
    public static class DESede64with7816d4 extends BaseMac
    {
        public DESede64with7816d4() {
            super(new CBCBlockCipherMac(new DESedeEngine(), 64, new ISO7816d4Padding()));
        }
    }
    
    public static class DESedeCFB8 extends BaseMac
    {
        public DESedeCFB8() {
            super(new CFBBlockCipherMac(new DESedeEngine()));
        }
    }
    
    public static class ECB extends BaseBlockCipher
    {
        public ECB() {
            super(new DESedeEngine());
        }
    }
    
    public static class KeyFactory extends BaseSecretKeyFactory
    {
        public KeyFactory() {
            super("DESede", null);
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
            if (DESedeKeySpec.class.isAssignableFrom(clazz)) {
                final byte[] encoded = secretKey.getEncoded();
                try {
                    if (encoded.length == 16) {
                        final byte[] array = new byte[24];
                        System.arraycopy(encoded, 0, array, 0, 16);
                        System.arraycopy(encoded, 0, array, 16, 8);
                        return new DESedeKeySpec(array);
                    }
                    return new DESedeKeySpec(encoded);
                }
                catch (final Exception ex) {
                    throw new InvalidKeySpecException(ex.toString());
                }
            }
            throw new InvalidKeySpecException("Invalid KeySpec");
        }
        
        @Override
        protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
            if (keySpec instanceof DESedeKeySpec) {
                return new SecretKeySpec(((DESedeKeySpec)keySpec).getKey(), "DESede");
            }
            return super.engineGenerateSecret(keySpec);
        }
    }
    
    public static class KeyGenerator extends BaseKeyGenerator
    {
        private boolean keySizeSet;
        
        public KeyGenerator() {
            super("DESede", 192, new DESedeKeyGenerator());
            this.keySizeSet = false;
        }
        
        @Override
        protected void engineInit(final int n, final SecureRandom secureRandom) {
            super.engineInit(n, secureRandom);
            this.keySizeSet = true;
        }
        
        @Override
        protected SecretKey engineGenerateKey() {
            if (this.uninitialised) {
                this.engine.init(new KeyGenerationParameters(new SecureRandom(), this.defaultKeySize));
                this.uninitialised = false;
            }
            if (!this.keySizeSet) {
                final byte[] generateKey = this.engine.generateKey();
                System.arraycopy(generateKey, 0, generateKey, 16, 8);
                return new SecretKeySpec(generateKey, this.algName);
            }
            return new SecretKeySpec(this.engine.generateKey(), this.algName);
        }
    }
    
    public static class KeyGenerator3 extends BaseKeyGenerator
    {
        public KeyGenerator3() {
            super("DESede3", 192, new DESedeKeyGenerator());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        private static final String PACKAGE = "org.bouncycastle.jcajce.provider.symmetric";
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.DESEDE", Mappings.PREFIX + "$ECB");
            configurableProvider.addAlgorithm("Cipher", PKCSObjectIdentifiers.des_EDE3_CBC, Mappings.PREFIX + "$CBC");
            configurableProvider.addAlgorithm("Cipher.DESEDEWRAP", Mappings.PREFIX + "$Wrap");
            configurableProvider.addAlgorithm("Cipher", PKCSObjectIdentifiers.id_alg_CMS3DESwrap, Mappings.PREFIX + "$Wrap");
            configurableProvider.addAlgorithm("Cipher.DESEDERFC3211WRAP", Mappings.PREFIX + "$RFC3211");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.DESEDERFC3217WRAP", "DESEDEWRAP");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.TDEA", "DESEDE");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.TDEAWRAP", "DESEDEWRAP");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.TDEA", "DESEDE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.TDEA", "DESEDE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.TDEA", "DESEDE");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.TDEA", "DESEDE");
            if (configurableProvider.hasAlgorithm("MessageDigest", "SHA-1")) {
                configurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND3-KEYTRIPLEDES-CBC", Mappings.PREFIX + "$PBEWithSHAAndDES3Key");
                configurableProvider.addAlgorithm("Cipher.BROKENPBEWITHSHAAND3-KEYTRIPLEDES-CBC", Mappings.PREFIX + "$BrokePBEWithSHAAndDES3Key");
                configurableProvider.addAlgorithm("Cipher.OLDPBEWITHSHAAND3-KEYTRIPLEDES-CBC", Mappings.PREFIX + "$OldPBEWithSHAAndDES3Key");
                configurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND2-KEYTRIPLEDES-CBC", Mappings.PREFIX + "$PBEWithSHAAndDES2Key");
                configurableProvider.addAlgorithm("Cipher.BROKENPBEWITHSHAAND2-KEYTRIPLEDES-CBC", Mappings.PREFIX + "$BrokePBEWithSHAAndDES2Key");
                configurableProvider.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
                configurableProvider.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1ANDDESEDE", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND3-KEYTRIPLEDES-CBC", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND2-KEYTRIPLEDES-CBC", "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND3-KEYDESEDE-CBC", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND2-KEYDESEDE-CBC", "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND3-KEYDESEDE-CBC", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND2-KEYDESEDE-CBC", "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
                configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1ANDDESEDE-CBC", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
            }
            configurableProvider.addAlgorithm("KeyGenerator.DESEDE", Mappings.PREFIX + "$KeyGenerator");
            configurableProvider.addAlgorithm("KeyGenerator." + PKCSObjectIdentifiers.des_EDE3_CBC, Mappings.PREFIX + "$KeyGenerator3");
            configurableProvider.addAlgorithm("KeyGenerator.DESEDEWRAP", Mappings.PREFIX + "$KeyGenerator");
            configurableProvider.addAlgorithm("SecretKeyFactory.DESEDE", Mappings.PREFIX + "$KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory", OIWObjectIdentifiers.desEDE, Mappings.PREFIX + "$KeyFactory");
            configurableProvider.addAlgorithm("Mac.DESEDECMAC", Mappings.PREFIX + "$CMAC");
            configurableProvider.addAlgorithm("Mac.DESEDEMAC", Mappings.PREFIX + "$CBCMAC");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDE", "DESEDEMAC");
            configurableProvider.addAlgorithm("Mac.DESEDEMAC/CFB8", Mappings.PREFIX + "$DESedeCFB8");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDE/CFB8", "DESEDEMAC/CFB8");
            configurableProvider.addAlgorithm("Mac.DESEDEMAC64", Mappings.PREFIX + "$DESede64");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDE64", "DESEDEMAC64");
            configurableProvider.addAlgorithm("Mac.DESEDEMAC64WITHISO7816-4PADDING", Mappings.PREFIX + "$DESede64with7816d4");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDE64WITHISO7816-4PADDING", "DESEDEMAC64WITHISO7816-4PADDING");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDEISO9797ALG1MACWITHISO7816-4PADDING", "DESEDEMAC64WITHISO7816-4PADDING");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.DESEDEISO9797ALG1WITHISO7816-4PADDING", "DESEDEMAC64WITHISO7816-4PADDING");
            configurableProvider.addAlgorithm("AlgorithmParameters.DESEDE", "org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.des_EDE3_CBC, "DESEDE");
            configurableProvider.addAlgorithm("AlgorithmParameterGenerator.DESEDE", Mappings.PREFIX + "$AlgParamGen");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + PKCSObjectIdentifiers.des_EDE3_CBC, "DESEDE");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND3-KEYTRIPLEDES-CBC", Mappings.PREFIX + "$PBEWithSHAAndDES3KeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND2-KEYTRIPLEDES-CBC", Mappings.PREFIX + "$PBEWithSHAAndDES2KeyFactory");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND3-KEYTRIPLEDES", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND2-KEYTRIPLEDES", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND3-KEYTRIPLEDES-CBC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND2-KEYTRIPLEDES-CBC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDDES3KEY-CBC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDDES2KEY-CBC", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.12.1.3", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.12.1.4", "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWithSHAAnd3KeyTripleDES", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.1.2.840.113549.1.12.1.3", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.1.2.840.113549.1.12.1.4", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWithSHAAnd3KeyTripleDES", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
        }
        
        static {
            PREFIX = DESede.class.getName();
        }
    }
    
    public static class PBEWithSHAAndDES2Key extends BaseBlockCipher
    {
        public PBEWithSHAAndDES2Key() {
            super(new CBCBlockCipher(new DESedeEngine()), 2, 1, 128, 8);
        }
    }
    
    public static class PBEWithSHAAndDES2KeyFactory extends DES.DESPBEKeyFactory
    {
        public PBEWithSHAAndDES2KeyFactory() {
            super("PBEwithSHAandDES2Key-CBC", PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, true, 2, 1, 128, 64);
        }
    }
    
    public static class PBEWithSHAAndDES3Key extends BaseBlockCipher
    {
        public PBEWithSHAAndDES3Key() {
            super(new CBCBlockCipher(new DESedeEngine()), 2, 1, 192, 8);
        }
    }
    
    public static class PBEWithSHAAndDES3KeyFactory extends DES.DESPBEKeyFactory
    {
        public PBEWithSHAAndDES3KeyFactory() {
            super("PBEwithSHAandDES3Key-CBC", PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, true, 2, 1, 192, 64);
        }
    }
    
    public static class RFC3211 extends BaseWrapCipher
    {
        public RFC3211() {
            super(new RFC3211WrapEngine(new DESedeEngine()), 8);
        }
    }
    
    public static class Wrap extends BaseWrapCipher
    {
        public Wrap() {
            super(new DESedeWrapEngine());
        }
    }
}
