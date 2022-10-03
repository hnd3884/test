package org.bouncycastle.cms.jcajce;

import org.bouncycastle.cms.PasswordRecipient;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.util.HashMap;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import javax.crypto.SecretKeyFactory;
import org.bouncycastle.asn1.pkcs.RC2CBCParameter;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import java.security.PrivateKey;
import java.security.KeyFactory;
import org.bouncycastle.asn1.DERNull;
import javax.crypto.spec.RC2ParameterSpec;
import java.security.SecureRandom;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import java.security.KeyPairGenerator;
import java.security.NoSuchProviderException;
import javax.crypto.NoSuchPaddingException;
import java.security.spec.InvalidParameterSpecException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.AlgorithmParameters;
import org.bouncycastle.asn1.ASN1Encodable;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.asn1.ASN1Null;
import java.security.AlgorithmParameterGenerator;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Map;
import org.bouncycastle.operator.SecretKeySizeProvider;

public class EnvelopedDataHelper
{
    protected static final SecretKeySizeProvider KEY_SIZE_PROVIDER;
    protected static final Map BASE_CIPHER_NAMES;
    protected static final Map CIPHER_ALG_NAMES;
    protected static final Map MAC_ALG_NAMES;
    private static final Map PBKDF2_ALG_NAMES;
    private static final short[] rc2Table;
    private static final short[] rc2Ekb;
    private JcaJceExtHelper helper;
    
    EnvelopedDataHelper(final JcaJceExtHelper helper) {
        this.helper = helper;
    }
    
    String getBaseCipherName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final String s = EnvelopedDataHelper.BASE_CIPHER_NAMES.get(asn1ObjectIdentifier);
        if (s == null) {
            return asn1ObjectIdentifier.getId();
        }
        return s;
    }
    
    Key getJceKey(final GenericKey genericKey) {
        if (genericKey.getRepresentation() instanceof Key) {
            return (Key)genericKey.getRepresentation();
        }
        if (genericKey.getRepresentation() instanceof byte[]) {
            return new SecretKeySpec((byte[])genericKey.getRepresentation(), "ENC");
        }
        throw new IllegalArgumentException("unknown generic key type");
    }
    
    public Key getJceKey(final ASN1ObjectIdentifier asn1ObjectIdentifier, final GenericKey genericKey) {
        if (genericKey.getRepresentation() instanceof Key) {
            return (Key)genericKey.getRepresentation();
        }
        if (genericKey.getRepresentation() instanceof byte[]) {
            return new SecretKeySpec((byte[])genericKey.getRepresentation(), this.getBaseCipherName(asn1ObjectIdentifier));
        }
        throw new IllegalArgumentException("unknown generic key type");
    }
    
    public void keySizeCheck(final AlgorithmIdentifier algorithmIdentifier, final Key key) throws CMSException {
        final int keySize = EnvelopedDataHelper.KEY_SIZE_PROVIDER.getKeySize(algorithmIdentifier);
        if (keySize > 0) {
            byte[] encoded = null;
            try {
                encoded = key.getEncoded();
            }
            catch (final Exception ex) {}
            if (encoded != null && encoded.length * 8 != keySize) {
                throw new CMSException("Expected key size for algorithm OID not found in recipient.");
            }
        }
    }
    
    Cipher createCipher(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CMSException {
        try {
            final String s = EnvelopedDataHelper.CIPHER_ALG_NAMES.get(asn1ObjectIdentifier);
            if (s != null) {
                try {
                    return this.helper.createCipher(s);
                }
                catch (final NoSuchAlgorithmException ex) {}
            }
            return this.helper.createCipher(asn1ObjectIdentifier.getId());
        }
        catch (final GeneralSecurityException ex2) {
            throw new CMSException("cannot create cipher: " + ex2.getMessage(), ex2);
        }
    }
    
    Mac createMac(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CMSException {
        try {
            final String s = EnvelopedDataHelper.MAC_ALG_NAMES.get(asn1ObjectIdentifier);
            if (s != null) {
                try {
                    return this.helper.createMac(s);
                }
                catch (final NoSuchAlgorithmException ex) {}
            }
            return this.helper.createMac(asn1ObjectIdentifier.getId());
        }
        catch (final GeneralSecurityException ex2) {
            throw new CMSException("cannot create mac: " + ex2.getMessage(), ex2);
        }
    }
    
    Cipher createRFC3211Wrapper(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CMSException {
        final String s = EnvelopedDataHelper.BASE_CIPHER_NAMES.get(asn1ObjectIdentifier);
        if (s == null) {
            throw new CMSException("no name for " + asn1ObjectIdentifier);
        }
        final String string = s + "RFC3211Wrap";
        try {
            return this.helper.createCipher(string);
        }
        catch (final GeneralSecurityException ex) {
            throw new CMSException("cannot create cipher: " + ex.getMessage(), ex);
        }
    }
    
    KeyAgreement createKeyAgreement(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CMSException {
        try {
            final String s = EnvelopedDataHelper.BASE_CIPHER_NAMES.get(asn1ObjectIdentifier);
            if (s != null) {
                try {
                    return this.helper.createKeyAgreement(s);
                }
                catch (final NoSuchAlgorithmException ex) {}
            }
            return this.helper.createKeyAgreement(asn1ObjectIdentifier.getId());
        }
        catch (final GeneralSecurityException ex2) {
            throw new CMSException("cannot create key agreement: " + ex2.getMessage(), ex2);
        }
    }
    
    AlgorithmParameterGenerator createAlgorithmParameterGenerator(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws GeneralSecurityException {
        final String s = EnvelopedDataHelper.BASE_CIPHER_NAMES.get(asn1ObjectIdentifier);
        if (s != null) {
            try {
                return this.helper.createAlgorithmParameterGenerator(s);
            }
            catch (final NoSuchAlgorithmException ex) {}
        }
        return this.helper.createAlgorithmParameterGenerator(asn1ObjectIdentifier.getId());
    }
    
    public Cipher createContentCipher(final Key key, final AlgorithmIdentifier algorithmIdentifier) throws CMSException {
        return (Cipher)execute(new JCECallback() {
            public Object doInJCE() throws CMSException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
                final Cipher cipher = EnvelopedDataHelper.this.createCipher(algorithmIdentifier.getAlgorithm());
                final ASN1Encodable parameters = algorithmIdentifier.getParameters();
                final String id = algorithmIdentifier.getAlgorithm().getId();
                if (parameters != null && !(parameters instanceof ASN1Null)) {
                    try {
                        final AlgorithmParameters algorithmParameters = EnvelopedDataHelper.this.createAlgorithmParameters(algorithmIdentifier.getAlgorithm());
                        CMSUtils.loadParameters(algorithmParameters, parameters);
                        cipher.init(2, key, algorithmParameters);
                    }
                    catch (final NoSuchAlgorithmException ex) {
                        if (!id.equals(CMSAlgorithm.DES_CBC.getId()) && !id.equals(CMSEnvelopedDataGenerator.DES_EDE3_CBC) && !id.equals("1.3.6.1.4.1.188.7.1.1.2") && !id.equals(CMSEnvelopedDataGenerator.AES128_CBC) && !id.equals(CMSEnvelopedDataGenerator.AES192_CBC) && !id.equals(CMSEnvelopedDataGenerator.AES256_CBC)) {
                            throw ex;
                        }
                        cipher.init(2, key, new IvParameterSpec(ASN1OctetString.getInstance((Object)parameters).getOctets()));
                    }
                }
                else if (id.equals(CMSAlgorithm.DES_CBC.getId()) || id.equals(CMSEnvelopedDataGenerator.DES_EDE3_CBC) || id.equals("1.3.6.1.4.1.188.7.1.1.2") || id.equals("1.2.840.113533.7.66.10")) {
                    cipher.init(2, key, new IvParameterSpec(new byte[8]));
                }
                else {
                    cipher.init(2, key);
                }
                return cipher;
            }
        });
    }
    
    Mac createContentMac(final Key key, final AlgorithmIdentifier algorithmIdentifier) throws CMSException {
        return (Mac)execute(new JCECallback() {
            public Object doInJCE() throws CMSException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
                final Mac mac = EnvelopedDataHelper.this.createMac(algorithmIdentifier.getAlgorithm());
                final ASN1Encodable parameters = algorithmIdentifier.getParameters();
                algorithmIdentifier.getAlgorithm().getId();
                if (parameters != null && !(parameters instanceof ASN1Null)) {
                    try {
                        final AlgorithmParameters algorithmParameters = EnvelopedDataHelper.this.createAlgorithmParameters(algorithmIdentifier.getAlgorithm());
                        CMSUtils.loadParameters(algorithmParameters, parameters);
                        mac.init(key, algorithmParameters.getParameterSpec(AlgorithmParameterSpec.class));
                        return mac;
                    }
                    catch (final NoSuchAlgorithmException ex) {
                        throw ex;
                    }
                }
                mac.init(key);
                return mac;
            }
        });
    }
    
    AlgorithmParameters createAlgorithmParameters(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws NoSuchAlgorithmException, NoSuchProviderException {
        final String s = EnvelopedDataHelper.BASE_CIPHER_NAMES.get(asn1ObjectIdentifier);
        if (s != null) {
            try {
                return this.helper.createAlgorithmParameters(s);
            }
            catch (final NoSuchAlgorithmException ex) {}
        }
        return this.helper.createAlgorithmParameters(asn1ObjectIdentifier.getId());
    }
    
    KeyPairGenerator createKeyPairGenerator(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CMSException {
        try {
            final String s = EnvelopedDataHelper.BASE_CIPHER_NAMES.get(asn1ObjectIdentifier);
            if (s != null) {
                try {
                    return this.helper.createKeyPairGenerator(s);
                }
                catch (final NoSuchAlgorithmException ex) {}
            }
            return this.helper.createKeyPairGenerator(asn1ObjectIdentifier.getId());
        }
        catch (final GeneralSecurityException ex2) {
            throw new CMSException("cannot create key pair generator: " + ex2.getMessage(), ex2);
        }
    }
    
    public KeyGenerator createKeyGenerator(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CMSException {
        try {
            final String s = EnvelopedDataHelper.BASE_CIPHER_NAMES.get(asn1ObjectIdentifier);
            if (s != null) {
                try {
                    return this.helper.createKeyGenerator(s);
                }
                catch (final NoSuchAlgorithmException ex) {}
            }
            return this.helper.createKeyGenerator(asn1ObjectIdentifier.getId());
        }
        catch (final GeneralSecurityException ex2) {
            throw new CMSException("cannot create key generator: " + ex2.getMessage(), ex2);
        }
    }
    
    AlgorithmParameters generateParameters(final ASN1ObjectIdentifier asn1ObjectIdentifier, final SecretKey secretKey, final SecureRandom secureRandom) throws CMSException {
        try {
            final AlgorithmParameterGenerator algorithmParameterGenerator = this.createAlgorithmParameterGenerator(asn1ObjectIdentifier);
            if (asn1ObjectIdentifier.equals((Object)CMSAlgorithm.RC2_CBC)) {
                final byte[] array = new byte[8];
                secureRandom.nextBytes(array);
                try {
                    algorithmParameterGenerator.init(new RC2ParameterSpec(secretKey.getEncoded().length * 8, array), secureRandom);
                }
                catch (final InvalidAlgorithmParameterException ex) {
                    throw new CMSException("parameters generation error: " + ex, ex);
                }
            }
            return algorithmParameterGenerator.generateParameters();
        }
        catch (final NoSuchAlgorithmException ex2) {
            return null;
        }
        catch (final GeneralSecurityException ex3) {
            throw new CMSException("exception creating algorithm parameter generator: " + ex3, ex3);
        }
    }
    
    AlgorithmIdentifier getAlgorithmIdentifier(final ASN1ObjectIdentifier asn1ObjectIdentifier, final AlgorithmParameters algorithmParameters) throws CMSException {
        Object o;
        if (algorithmParameters != null) {
            o = CMSUtils.extractParameters(algorithmParameters);
        }
        else {
            o = DERNull.INSTANCE;
        }
        return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)o);
    }
    
    static Object execute(final JCECallback jceCallback) throws CMSException {
        try {
            return jceCallback.doInJCE();
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new CMSException("can't find algorithm.", ex);
        }
        catch (final InvalidKeyException ex2) {
            throw new CMSException("key invalid in message.", ex2);
        }
        catch (final NoSuchProviderException ex3) {
            throw new CMSException("can't find provider.", ex3);
        }
        catch (final NoSuchPaddingException ex4) {
            throw new CMSException("required padding not supported.", ex4);
        }
        catch (final InvalidAlgorithmParameterException ex5) {
            throw new CMSException("algorithm parameters invalid.", ex5);
        }
        catch (final InvalidParameterSpecException ex6) {
            throw new CMSException("MAC algorithm parameter spec invalid.", ex6);
        }
    }
    
    public KeyFactory createKeyFactory(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CMSException {
        try {
            final String s = EnvelopedDataHelper.BASE_CIPHER_NAMES.get(asn1ObjectIdentifier);
            if (s != null) {
                try {
                    return this.helper.createKeyFactory(s);
                }
                catch (final NoSuchAlgorithmException ex) {}
            }
            return this.helper.createKeyFactory(asn1ObjectIdentifier.getId());
        }
        catch (final GeneralSecurityException ex2) {
            throw new CMSException("cannot create key factory: " + ex2.getMessage(), ex2);
        }
    }
    
    public JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final PrivateKey privateKey) {
        return this.helper.createAsymmetricUnwrapper(algorithmIdentifier, privateKey);
    }
    
    public JceKTSKeyUnwrapper createAsymmetricUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final PrivateKey privateKey, final byte[] array, final byte[] array2) {
        return this.helper.createAsymmetricUnwrapper(algorithmIdentifier, privateKey, array, array2);
    }
    
    public SymmetricKeyUnwrapper createSymmetricUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final SecretKey secretKey) {
        return this.helper.createSymmetricUnwrapper(algorithmIdentifier, secretKey);
    }
    
    public AlgorithmIdentifier getAlgorithmIdentifier(final ASN1ObjectIdentifier asn1ObjectIdentifier, final AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof IvParameterSpec) {
            return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)new DEROctetString(((IvParameterSpec)algorithmParameterSpec).getIV()));
        }
        if (!(algorithmParameterSpec instanceof RC2ParameterSpec)) {
            throw new IllegalStateException("unknown parameter spec: " + algorithmParameterSpec);
        }
        final RC2ParameterSpec rc2ParameterSpec = (RC2ParameterSpec)algorithmParameterSpec;
        final int effectiveKeyBits = ((RC2ParameterSpec)algorithmParameterSpec).getEffectiveKeyBits();
        if (effectiveKeyBits != -1) {
            int n;
            if (effectiveKeyBits < 256) {
                n = EnvelopedDataHelper.rc2Table[effectiveKeyBits];
            }
            else {
                n = effectiveKeyBits;
            }
            return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)new RC2CBCParameter(n, rc2ParameterSpec.getIV()));
        }
        return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)new RC2CBCParameter(rc2ParameterSpec.getIV()));
    }
    
    SecretKeyFactory createSecretKeyFactory(final String s) throws NoSuchProviderException, NoSuchAlgorithmException {
        return this.helper.createSecretKeyFactory(s);
    }
    
    byte[] calculateDerivedKey(final int n, final char[] array, final AlgorithmIdentifier algorithmIdentifier, final int n2) throws CMSException {
        final PBKDF2Params instance = PBKDF2Params.getInstance((Object)algorithmIdentifier.getParameters());
        try {
            SecretKeyFactory secretKeyFactory;
            if (n == 0) {
                secretKeyFactory = this.helper.createSecretKeyFactory("PBKDF2with8BIT");
            }
            else {
                secretKeyFactory = this.helper.createSecretKeyFactory((String)EnvelopedDataHelper.PBKDF2_ALG_NAMES.get(instance.getPrf()));
            }
            return secretKeyFactory.generateSecret(new PBEKeySpec(array, instance.getSalt(), instance.getIterationCount().intValue(), n2)).getEncoded();
        }
        catch (final GeneralSecurityException ex) {
            throw new CMSException("Unable to calculate derived key from password: " + ex.getMessage(), ex);
        }
    }
    
    static {
        KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
        BASE_CIPHER_NAMES = new HashMap();
        CIPHER_ALG_NAMES = new HashMap();
        MAC_ALG_NAMES = new HashMap();
        PBKDF2_ALG_NAMES = new HashMap();
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.DES_CBC, "DES");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.AES128_CBC, "AES");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.AES192_CBC, "AES");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.AES256_CBC, "AES");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.CAST5_CBC, "CAST5");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.CAMELLIA128_CBC, "Camellia");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.CAMELLIA192_CBC, "Camellia");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.CAMELLIA256_CBC, "Camellia");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CMSAlgorithm.SEED_CBC, "SEED");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(PKCSObjectIdentifiers.rc4, "RC4");
        EnvelopedDataHelper.BASE_CIPHER_NAMES.put(CryptoProObjectIdentifiers.gostR28147_gcfb, "GOST28147");
        EnvelopedDataHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.DES_CBC, "DES/CBC/PKCS5Padding");
        EnvelopedDataHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2/CBC/PKCS5Padding");
        EnvelopedDataHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE/CBC/PKCS5Padding");
        EnvelopedDataHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AES/CBC/PKCS5Padding");
        EnvelopedDataHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AES/CBC/PKCS5Padding");
        EnvelopedDataHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AES/CBC/PKCS5Padding");
        EnvelopedDataHelper.CIPHER_ALG_NAMES.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
        EnvelopedDataHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.CAST5_CBC, "CAST5/CBC/PKCS5Padding");
        EnvelopedDataHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.CAMELLIA128_CBC, "Camellia/CBC/PKCS5Padding");
        EnvelopedDataHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.CAMELLIA192_CBC, "Camellia/CBC/PKCS5Padding");
        EnvelopedDataHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.CAMELLIA256_CBC, "Camellia/CBC/PKCS5Padding");
        EnvelopedDataHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.SEED_CBC, "SEED/CBC/PKCS5Padding");
        EnvelopedDataHelper.CIPHER_ALG_NAMES.put(PKCSObjectIdentifiers.rc4, "RC4");
        EnvelopedDataHelper.MAC_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDEMac");
        EnvelopedDataHelper.MAC_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AESMac");
        EnvelopedDataHelper.MAC_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AESMac");
        EnvelopedDataHelper.MAC_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AESMac");
        EnvelopedDataHelper.MAC_ALG_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2Mac");
        EnvelopedDataHelper.PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA1.getAlgorithmID(), "PBKDF2WITHHMACSHA1");
        EnvelopedDataHelper.PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA224.getAlgorithmID(), "PBKDF2WITHHMACSHA224");
        EnvelopedDataHelper.PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA256.getAlgorithmID(), "PBKDF2WITHHMACSHA256");
        EnvelopedDataHelper.PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA384.getAlgorithmID(), "PBKDF2WITHHMACSHA384");
        EnvelopedDataHelper.PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA512.getAlgorithmID(), "PBKDF2WITHHMACSHA512");
        rc2Table = new short[] { 189, 86, 234, 242, 162, 241, 172, 42, 176, 147, 209, 156, 27, 51, 253, 208, 48, 4, 182, 220, 125, 223, 50, 75, 247, 203, 69, 155, 49, 187, 33, 90, 65, 159, 225, 217, 74, 77, 158, 218, 160, 104, 44, 195, 39, 95, 128, 54, 62, 238, 251, 149, 26, 254, 206, 168, 52, 169, 19, 240, 166, 63, 216, 12, 120, 36, 175, 35, 82, 193, 103, 23, 245, 102, 144, 231, 232, 7, 184, 96, 72, 230, 30, 83, 243, 146, 164, 114, 140, 8, 21, 110, 134, 0, 132, 250, 244, 127, 138, 66, 25, 246, 219, 205, 20, 141, 80, 18, 186, 60, 6, 78, 236, 179, 53, 17, 161, 136, 142, 43, 148, 153, 183, 113, 116, 211, 228, 191, 58, 222, 150, 14, 188, 10, 237, 119, 252, 55, 107, 3, 121, 137, 98, 198, 215, 192, 210, 124, 106, 139, 34, 163, 91, 5, 93, 2, 117, 213, 97, 227, 24, 143, 85, 81, 173, 31, 11, 94, 133, 229, 194, 87, 99, 202, 61, 108, 180, 197, 204, 112, 178, 145, 89, 13, 71, 32, 200, 79, 88, 224, 1, 226, 22, 56, 196, 111, 59, 15, 101, 70, 190, 126, 45, 123, 130, 249, 64, 181, 29, 115, 248, 235, 38, 199, 135, 151, 37, 84, 177, 40, 170, 152, 157, 165, 100, 109, 122, 212, 16, 129, 68, 239, 73, 214, 174, 46, 221, 118, 92, 47, 167, 28, 201, 9, 105, 154, 131, 207, 41, 57, 185, 233, 76, 255, 67, 171 };
        rc2Ekb = new short[] { 93, 190, 155, 139, 17, 153, 110, 77, 89, 243, 133, 166, 63, 183, 131, 197, 228, 115, 107, 58, 104, 90, 192, 71, 160, 100, 52, 12, 241, 208, 82, 165, 185, 30, 150, 67, 65, 216, 212, 44, 219, 248, 7, 119, 42, 202, 235, 239, 16, 28, 22, 13, 56, 114, 47, 137, 193, 249, 128, 196, 109, 174, 48, 61, 206, 32, 99, 254, 230, 26, 199, 184, 80, 232, 36, 23, 252, 37, 111, 187, 106, 163, 68, 83, 217, 162, 1, 171, 188, 182, 31, 152, 238, 154, 167, 45, 79, 158, 142, 172, 224, 198, 73, 70, 41, 244, 148, 138, 175, 225, 91, 195, 179, 123, 87, 209, 124, 156, 237, 135, 64, 140, 226, 203, 147, 20, 201, 97, 46, 229, 204, 246, 94, 168, 92, 214, 117, 141, 98, 149, 88, 105, 118, 161, 74, 181, 85, 9, 120, 51, 130, 215, 221, 121, 245, 27, 11, 222, 38, 33, 40, 116, 4, 151, 86, 223, 60, 240, 55, 57, 220, 255, 6, 164, 234, 66, 8, 218, 180, 113, 176, 207, 18, 122, 78, 250, 108, 29, 132, 0, 200, 127, 145, 69, 170, 43, 194, 177, 143, 213, 186, 242, 173, 25, 178, 103, 54, 247, 15, 10, 146, 125, 227, 157, 233, 144, 62, 35, 39, 102, 19, 236, 129, 21, 189, 34, 191, 159, 126, 169, 81, 75, 76, 251, 2, 211, 112, 134, 49, 231, 59, 5, 3, 84, 96, 72, 101, 24, 210, 205, 95, 50, 136, 14, 53, 253 };
    }
    
    interface JCECallback
    {
        Object doInJCE() throws CMSException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException;
    }
}
