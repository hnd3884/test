package org.bouncycastle.cert.crmf.jcajce;

import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.util.HashMap;
import org.bouncycastle.asn1.DERNull;
import javax.crypto.spec.RC2ParameterSpec;
import java.security.SecureRandom;
import javax.crypto.SecretKey;
import java.security.AlgorithmParameterGenerator;
import javax.crypto.Mac;
import java.security.MessageDigest;
import java.security.KeyFactory;
import java.security.NoSuchProviderException;
import javax.crypto.NoSuchPaddingException;
import java.security.spec.InvalidParameterSpecException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.AlgorithmParameters;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.cms.CMSAlgorithm;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.Key;
import javax.crypto.KeyGenerator;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.util.Map;

class CRMFHelper
{
    protected static final Map BASE_CIPHER_NAMES;
    protected static final Map CIPHER_ALG_NAMES;
    protected static final Map DIGEST_ALG_NAMES;
    protected static final Map KEY_ALG_NAMES;
    protected static final Map MAC_ALG_NAMES;
    private JcaJceHelper helper;
    
    CRMFHelper(final JcaJceHelper helper) {
        this.helper = helper;
    }
    
    PublicKey toPublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws CRMFException {
        try {
            return this.createKeyFactory(subjectPublicKeyInfo.getAlgorithm().getAlgorithm()).generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
        }
        catch (final Exception ex) {
            throw new CRMFException("invalid key: " + ex.getMessage(), ex);
        }
    }
    
    Cipher createCipher(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CRMFException {
        try {
            final String s = CRMFHelper.CIPHER_ALG_NAMES.get(asn1ObjectIdentifier);
            if (s != null) {
                try {
                    return this.helper.createCipher(s);
                }
                catch (final NoSuchAlgorithmException ex) {}
            }
            return this.helper.createCipher(asn1ObjectIdentifier.getId());
        }
        catch (final GeneralSecurityException ex2) {
            throw new CRMFException("cannot create cipher: " + ex2.getMessage(), ex2);
        }
    }
    
    public KeyGenerator createKeyGenerator(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CRMFException {
        try {
            final String s = CRMFHelper.BASE_CIPHER_NAMES.get(asn1ObjectIdentifier);
            if (s != null) {
                try {
                    return this.helper.createKeyGenerator(s);
                }
                catch (final NoSuchAlgorithmException ex) {}
            }
            return this.helper.createKeyGenerator(asn1ObjectIdentifier.getId());
        }
        catch (final GeneralSecurityException ex2) {
            throw new CRMFException("cannot create key generator: " + ex2.getMessage(), ex2);
        }
    }
    
    Cipher createContentCipher(final Key key, final AlgorithmIdentifier algorithmIdentifier) throws CRMFException {
        return (Cipher)execute(new JCECallback() {
            public Object doInJCE() throws CRMFException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
                final Cipher cipher = CRMFHelper.this.createCipher(algorithmIdentifier.getAlgorithm());
                final ASN1Primitive asn1Primitive = (ASN1Primitive)algorithmIdentifier.getParameters();
                final ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
                if (asn1Primitive != null && !(asn1Primitive instanceof ASN1Null)) {
                    try {
                        final AlgorithmParameters algorithmParameters = CRMFHelper.this.createAlgorithmParameters(algorithmIdentifier.getAlgorithm());
                        try {
                            AlgorithmParametersUtils.loadParameters(algorithmParameters, (ASN1Encodable)asn1Primitive);
                        }
                        catch (final IOException ex) {
                            throw new CRMFException("error decoding algorithm parameters.", ex);
                        }
                        cipher.init(2, key, algorithmParameters);
                    }
                    catch (final NoSuchAlgorithmException ex2) {
                        if (!algorithm.equals((Object)CMSAlgorithm.DES_EDE3_CBC) && !algorithm.equals((Object)CMSAlgorithm.IDEA_CBC) && !algorithm.equals((Object)CMSAlgorithm.AES128_CBC) && !algorithm.equals((Object)CMSAlgorithm.AES192_CBC) && !algorithm.equals((Object)CMSAlgorithm.AES256_CBC)) {
                            throw ex2;
                        }
                        cipher.init(2, key, new IvParameterSpec(ASN1OctetString.getInstance((Object)asn1Primitive).getOctets()));
                    }
                }
                else if (algorithm.equals((Object)CMSAlgorithm.DES_EDE3_CBC) || algorithm.equals((Object)CMSAlgorithm.IDEA_CBC) || algorithm.equals((Object)CMSAlgorithm.CAST5_CBC)) {
                    cipher.init(2, key, new IvParameterSpec(new byte[8]));
                }
                else {
                    cipher.init(2, key);
                }
                return cipher;
            }
        });
    }
    
    AlgorithmParameters createAlgorithmParameters(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws NoSuchAlgorithmException, NoSuchProviderException {
        final String s = CRMFHelper.BASE_CIPHER_NAMES.get(asn1ObjectIdentifier);
        if (s != null) {
            try {
                return this.helper.createAlgorithmParameters(s);
            }
            catch (final NoSuchAlgorithmException ex) {}
        }
        return this.helper.createAlgorithmParameters(asn1ObjectIdentifier.getId());
    }
    
    KeyFactory createKeyFactory(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CRMFException {
        try {
            final String s = CRMFHelper.KEY_ALG_NAMES.get(asn1ObjectIdentifier);
            if (s != null) {
                try {
                    return this.helper.createKeyFactory(s);
                }
                catch (final NoSuchAlgorithmException ex) {}
            }
            return this.helper.createKeyFactory(asn1ObjectIdentifier.getId());
        }
        catch (final GeneralSecurityException ex2) {
            throw new CRMFException("cannot create cipher: " + ex2.getMessage(), ex2);
        }
    }
    
    MessageDigest createDigest(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CRMFException {
        try {
            final String s = CRMFHelper.DIGEST_ALG_NAMES.get(asn1ObjectIdentifier);
            if (s != null) {
                try {
                    return this.helper.createDigest(s);
                }
                catch (final NoSuchAlgorithmException ex) {}
            }
            return this.helper.createDigest(asn1ObjectIdentifier.getId());
        }
        catch (final GeneralSecurityException ex2) {
            throw new CRMFException("cannot create cipher: " + ex2.getMessage(), ex2);
        }
    }
    
    Mac createMac(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CRMFException {
        try {
            final String s = CRMFHelper.MAC_ALG_NAMES.get(asn1ObjectIdentifier);
            if (s != null) {
                try {
                    return this.helper.createMac(s);
                }
                catch (final NoSuchAlgorithmException ex) {}
            }
            return this.helper.createMac(asn1ObjectIdentifier.getId());
        }
        catch (final GeneralSecurityException ex2) {
            throw new CRMFException("cannot create mac: " + ex2.getMessage(), ex2);
        }
    }
    
    AlgorithmParameterGenerator createAlgorithmParameterGenerator(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws GeneralSecurityException {
        final String s = CRMFHelper.BASE_CIPHER_NAMES.get(asn1ObjectIdentifier);
        if (s != null) {
            try {
                return this.helper.createAlgorithmParameterGenerator(s);
            }
            catch (final NoSuchAlgorithmException ex) {}
        }
        return this.helper.createAlgorithmParameterGenerator(asn1ObjectIdentifier.getId());
    }
    
    AlgorithmParameters generateParameters(final ASN1ObjectIdentifier asn1ObjectIdentifier, final SecretKey secretKey, final SecureRandom secureRandom) throws CRMFException {
        try {
            final AlgorithmParameterGenerator algorithmParameterGenerator = this.createAlgorithmParameterGenerator(asn1ObjectIdentifier);
            if (asn1ObjectIdentifier.equals((Object)CMSAlgorithm.RC2_CBC)) {
                final byte[] array = new byte[8];
                secureRandom.nextBytes(array);
                try {
                    algorithmParameterGenerator.init(new RC2ParameterSpec(secretKey.getEncoded().length * 8, array), secureRandom);
                }
                catch (final InvalidAlgorithmParameterException ex) {
                    throw new CRMFException("parameters generation error: " + ex, ex);
                }
            }
            return algorithmParameterGenerator.generateParameters();
        }
        catch (final NoSuchAlgorithmException ex2) {
            return null;
        }
        catch (final GeneralSecurityException ex3) {
            throw new CRMFException("exception creating algorithm parameter generator: " + ex3, ex3);
        }
    }
    
    AlgorithmIdentifier getAlgorithmIdentifier(final ASN1ObjectIdentifier asn1ObjectIdentifier, final AlgorithmParameters algorithmParameters) throws CRMFException {
        if (algorithmParameters != null) {
            try {
                final Object o = AlgorithmParametersUtils.extractParameters(algorithmParameters);
                return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)o);
            }
            catch (final IOException ex) {
                throw new CRMFException("cannot encode parameters: " + ex.getMessage(), ex);
            }
        }
        final Object o = DERNull.INSTANCE;
        return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)o);
    }
    
    static Object execute(final JCECallback jceCallback) throws CRMFException {
        try {
            return jceCallback.doInJCE();
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new CRMFException("can't find algorithm.", ex);
        }
        catch (final InvalidKeyException ex2) {
            throw new CRMFException("key invalid in message.", ex2);
        }
        catch (final NoSuchProviderException ex3) {
            throw new CRMFException("can't find provider.", ex3);
        }
        catch (final NoSuchPaddingException ex4) {
            throw new CRMFException("required padding not supported.", ex4);
        }
        catch (final InvalidAlgorithmParameterException ex5) {
            throw new CRMFException("algorithm parameters invalid.", ex5);
        }
        catch (final InvalidParameterSpecException ex6) {
            throw new CRMFException("MAC algorithm parameter spec invalid.", ex6);
        }
    }
    
    static {
        BASE_CIPHER_NAMES = new HashMap();
        CIPHER_ALG_NAMES = new HashMap();
        DIGEST_ALG_NAMES = new HashMap();
        KEY_ALG_NAMES = new HashMap();
        MAC_ALG_NAMES = new HashMap();
        CRMFHelper.BASE_CIPHER_NAMES.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESEDE");
        CRMFHelper.BASE_CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes128_CBC, "AES");
        CRMFHelper.BASE_CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes192_CBC, "AES");
        CRMFHelper.BASE_CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes256_CBC, "AES");
        CRMFHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE/CBC/PKCS5Padding");
        CRMFHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AES/CBC/PKCS5Padding");
        CRMFHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AES/CBC/PKCS5Padding");
        CRMFHelper.CIPHER_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AES/CBC/PKCS5Padding");
        CRMFHelper.CIPHER_ALG_NAMES.put(new ASN1ObjectIdentifier(PKCSObjectIdentifiers.rsaEncryption.getId()), "RSA/ECB/PKCS1Padding");
        CRMFHelper.DIGEST_ALG_NAMES.put(OIWObjectIdentifiers.idSHA1, "SHA1");
        CRMFHelper.DIGEST_ALG_NAMES.put(NISTObjectIdentifiers.id_sha224, "SHA224");
        CRMFHelper.DIGEST_ALG_NAMES.put(NISTObjectIdentifiers.id_sha256, "SHA256");
        CRMFHelper.DIGEST_ALG_NAMES.put(NISTObjectIdentifiers.id_sha384, "SHA384");
        CRMFHelper.DIGEST_ALG_NAMES.put(NISTObjectIdentifiers.id_sha512, "SHA512");
        CRMFHelper.MAC_ALG_NAMES.put(IANAObjectIdentifiers.hmacSHA1, "HMACSHA1");
        CRMFHelper.MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA1, "HMACSHA1");
        CRMFHelper.MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA224, "HMACSHA224");
        CRMFHelper.MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA256, "HMACSHA256");
        CRMFHelper.MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA384, "HMACSHA384");
        CRMFHelper.MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA512, "HMACSHA512");
        CRMFHelper.KEY_ALG_NAMES.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        CRMFHelper.KEY_ALG_NAMES.put(X9ObjectIdentifiers.id_dsa, "DSA");
    }
    
    interface JCECallback
    {
        Object doInJCE() throws CRMFException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException;
    }
}
