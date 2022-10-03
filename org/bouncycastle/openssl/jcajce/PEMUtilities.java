package org.bouncycastle.openssl.jcajce;

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.util.Integers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import java.util.HashSet;
import java.util.HashMap;
import java.security.GeneralSecurityException;
import org.bouncycastle.openssl.PEMException;
import javax.crypto.Cipher;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import org.bouncycastle.openssl.EncryptionException;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Set;
import java.util.Map;

class PEMUtilities
{
    private static final Map KEYSIZES;
    private static final Set PKCS5_SCHEME_1;
    private static final Set PKCS5_SCHEME_2;
    private static final Map PRFS;
    private static final Map PRFS_SALT;
    
    static int getKeySize(final String s) {
        if (!PEMUtilities.KEYSIZES.containsKey(s)) {
            throw new IllegalStateException("no key size for algorithm: " + s);
        }
        return PEMUtilities.KEYSIZES.get(s);
    }
    
    static int getSaltSize(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        if (!PEMUtilities.PRFS_SALT.containsKey(asn1ObjectIdentifier)) {
            throw new IllegalStateException("no salt size for algorithm: " + asn1ObjectIdentifier);
        }
        return PEMUtilities.PRFS_SALT.get(asn1ObjectIdentifier);
    }
    
    static boolean isHmacSHA1(final AlgorithmIdentifier algorithmIdentifier) {
        return algorithmIdentifier == null || algorithmIdentifier.getAlgorithm().equals((Object)PKCSObjectIdentifiers.id_hmacWithSHA1);
    }
    
    static boolean isPKCS5Scheme1(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return PEMUtilities.PKCS5_SCHEME_1.contains(asn1ObjectIdentifier);
    }
    
    static boolean isPKCS5Scheme2(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return PEMUtilities.PKCS5_SCHEME_2.contains(asn1ObjectIdentifier);
    }
    
    public static boolean isPKCS12(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return asn1ObjectIdentifier.getId().startsWith(PKCSObjectIdentifiers.pkcs_12PbeIds.getId());
    }
    
    public static SecretKey generateSecretKeyForPKCS5Scheme2(final JcaJceHelper jcaJceHelper, final String s, final char[] array, final byte[] array2, final int n) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        return new SecretKeySpec(jcaJceHelper.createSecretKeyFactory("PBKDF2with8BIT").generateSecret(new PBEKeySpec(array, array2, n, getKeySize(s))).getEncoded(), s);
    }
    
    public static SecretKey generateSecretKeyForPKCS5Scheme2(final JcaJceHelper jcaJceHelper, final String s, final char[] array, final byte[] array2, final int n, final AlgorithmIdentifier algorithmIdentifier) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        final String s2 = PEMUtilities.PRFS.get(algorithmIdentifier.getAlgorithm());
        if (s2 == null) {
            throw new NoSuchAlgorithmException("unknown PRF in PKCS#2: " + algorithmIdentifier.getAlgorithm());
        }
        return new SecretKeySpec(jcaJceHelper.createSecretKeyFactory(s2).generateSecret(new PBEKeySpec(array, array2, n, getKeySize(s))).getEncoded(), s);
    }
    
    static byte[] crypt(final boolean b, final JcaJceHelper jcaJceHelper, final byte[] array, final char[] array2, final String s, final byte[] array3) throws PEMException {
        AlgorithmParameterSpec algorithmParameterSpec = new IvParameterSpec(array3);
        String s2 = "CBC";
        String s3 = "PKCS5Padding";
        if (s.endsWith("-CFB")) {
            s2 = "CFB";
            s3 = "NoPadding";
        }
        if (s.endsWith("-ECB") || "DES-EDE".equals(s) || "DES-EDE3".equals(s)) {
            s2 = "ECB";
            algorithmParameterSpec = null;
        }
        if (s.endsWith("-OFB")) {
            s2 = "OFB";
            s3 = "NoPadding";
        }
        String s4;
        SecretKey secretKey;
        if (s.startsWith("DES-EDE")) {
            s4 = "DESede";
            secretKey = getKey(jcaJceHelper, array2, s4, 24, array3, !s.startsWith("DES-EDE3"));
        }
        else if (s.startsWith("DES-")) {
            s4 = "DES";
            secretKey = getKey(jcaJceHelper, array2, s4, 8, array3);
        }
        else if (s.startsWith("BF-")) {
            s4 = "Blowfish";
            secretKey = getKey(jcaJceHelper, array2, s4, 16, array3);
        }
        else if (s.startsWith("RC2-")) {
            s4 = "RC2";
            int n = 128;
            if (s.startsWith("RC2-40-")) {
                n = 40;
            }
            else if (s.startsWith("RC2-64-")) {
                n = 64;
            }
            secretKey = getKey(jcaJceHelper, array2, s4, n / 8, array3);
            if (algorithmParameterSpec == null) {
                algorithmParameterSpec = new RC2ParameterSpec(n);
            }
            else {
                algorithmParameterSpec = new RC2ParameterSpec(n, array3);
            }
        }
        else {
            if (!s.startsWith("AES-")) {
                throw new EncryptionException("unknown encryption with private key");
            }
            s4 = "AES";
            byte[] array4 = array3;
            if (array4.length > 8) {
                array4 = new byte[8];
                System.arraycopy(array3, 0, array4, 0, 8);
            }
            int n2;
            if (s.startsWith("AES-128-")) {
                n2 = 128;
            }
            else if (s.startsWith("AES-192-")) {
                n2 = 192;
            }
            else {
                if (!s.startsWith("AES-256-")) {
                    throw new EncryptionException("unknown AES encryption with private key");
                }
                n2 = 256;
            }
            secretKey = getKey(jcaJceHelper, array2, "AES", n2 / 8, array4);
        }
        final String string = s4 + "/" + s2 + "/" + s3;
        try {
            final Cipher cipher = jcaJceHelper.createCipher(string);
            final int n3 = b ? 1 : 2;
            if (algorithmParameterSpec == null) {
                cipher.init(n3, secretKey);
            }
            else {
                cipher.init(n3, secretKey, algorithmParameterSpec);
            }
            return cipher.doFinal(array);
        }
        catch (final Exception ex) {
            throw new EncryptionException("exception using cipher - please check password and data.", (Throwable)ex);
        }
    }
    
    private static SecretKey getKey(final JcaJceHelper jcaJceHelper, final char[] array, final String s, final int n, final byte[] array2) throws PEMException {
        return getKey(jcaJceHelper, array, s, n, array2, false);
    }
    
    private static SecretKey getKey(final JcaJceHelper jcaJceHelper, final char[] array, final String s, final int n, final byte[] array2, final boolean b) throws PEMException {
        try {
            final byte[] encoded = jcaJceHelper.createSecretKeyFactory("PBKDF-OpenSSL").generateSecret(new PBEKeySpec(array, array2, 1, n * 8)).getEncoded();
            if (b && encoded.length >= 24) {
                System.arraycopy(encoded, 0, encoded, 16, 8);
            }
            return new SecretKeySpec(encoded, s);
        }
        catch (final GeneralSecurityException ex) {
            throw new PEMException("Unable to create OpenSSL PBDKF: " + ex.getMessage(), ex);
        }
    }
    
    static {
        KEYSIZES = new HashMap();
        PKCS5_SCHEME_1 = new HashSet();
        PKCS5_SCHEME_2 = new HashSet();
        PRFS = new HashMap();
        PRFS_SALT = new HashMap();
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndRC2_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndRC2_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndRC2_CBC);
        PEMUtilities.PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.id_PBES2);
        PEMUtilities.PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.des_EDE3_CBC);
        PEMUtilities.PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes128_CBC);
        PEMUtilities.PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes192_CBC);
        PEMUtilities.PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes256_CBC);
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), Integers.valueOf(192));
        PEMUtilities.KEYSIZES.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), Integers.valueOf(128));
        PEMUtilities.KEYSIZES.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), Integers.valueOf(192));
        PEMUtilities.KEYSIZES.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), Integers.valueOf(256));
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4.getId(), Integers.valueOf(128));
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, Integers.valueOf(40));
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, Integers.valueOf(128));
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, Integers.valueOf(192));
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, Integers.valueOf(128));
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, Integers.valueOf(40));
        PEMUtilities.PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA1, "PBKDF2withHMACSHA1");
        PEMUtilities.PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA256, "PBKDF2withHMACSHA256");
        PEMUtilities.PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA512, "PBKDF2withHMACSHA512");
        PEMUtilities.PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA224, "PBKDF2withHMACSHA224");
        PEMUtilities.PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA384, "PBKDF2withHMACSHA384");
        PEMUtilities.PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, "PBKDF2withHMACSHA3-224");
        PEMUtilities.PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, "PBKDF2withHMACSHA3-256");
        PEMUtilities.PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, "PBKDF2withHMACSHA3-384");
        PEMUtilities.PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, "PBKDF2withHMACSHA3-512");
        PEMUtilities.PRFS.put(CryptoProObjectIdentifiers.gostR3411Hmac, "PBKDF2withHMACGOST3411");
        PEMUtilities.PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA1, Integers.valueOf(20));
        PEMUtilities.PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA256, Integers.valueOf(32));
        PEMUtilities.PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA512, Integers.valueOf(64));
        PEMUtilities.PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA224, Integers.valueOf(28));
        PEMUtilities.PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA384, Integers.valueOf(48));
        PEMUtilities.PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, Integers.valueOf(28));
        PEMUtilities.PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, Integers.valueOf(32));
        PEMUtilities.PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, Integers.valueOf(48));
        PEMUtilities.PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, Integers.valueOf(64));
        PEMUtilities.PRFS_SALT.put(CryptoProObjectIdentifiers.gostR3411Hmac, Integers.valueOf(32));
    }
}
