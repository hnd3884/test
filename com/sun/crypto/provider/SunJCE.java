package com.sun.crypto.provider;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.security.SecureRandom;
import java.security.Provider;

public final class SunJCE extends Provider
{
    private static final long serialVersionUID = 6812507587804302833L;
    private static final String info = "SunJCE Provider (implements RSA, DES, Triple DES, AES, Blowfish, ARCFOUR, RC2, PBE, Diffie-Hellman, HMAC)";
    static final boolean debug = false;
    private static volatile SunJCE instance;
    
    static SecureRandom getRandom() {
        return SecureRandomHolder.RANDOM;
    }
    
    private void ps(final String s, final String s2, final String s3, final List<String> list, final HashMap<String, String> hashMap) {
        this.putService(new Service(this, s, s2, s3, list, hashMap));
    }
    
    public SunJCE() {
        super("SunJCE", 1.8, "SunJCE Provider (implements RSA, DES, Triple DES, AES, Blowfish, ARCFOUR, RC2, PBE, Diffie-Hellman, HMAC)");
        if (System.getSecurityManager() == null) {
            this.putEntries();
        }
        else {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    SunJCE.this.putEntries();
                    return null;
                }
            });
        }
        if (SunJCE.instance == null) {
            SunJCE.instance = this;
        }
    }
    
    void putEntries() {
        final List aliases = SunEntries.createAliases(new String[] { "Rijndael" });
        final List aliases2 = SunEntries.createAliases(new String[] { "TripleDES" });
        final List aliases3 = SunEntries.createAliases(new String[] { "RC4" });
        final List aliases4 = SunEntries.createAliases(new String[] { "SunTls12MasterSecret", "SunTlsExtendedMasterSecret" });
        final List aliases5 = SunEntries.createAliases(new String[] { "SunTls12KeyMaterial" });
        final List aliases6 = SunEntries.createAliases(new String[] { "SunTls12RsaPremasterSecret" });
        final String s = "2.16.840.1.101.3.4.1.";
        final String s2 = "2.16.840.1.101.3.4.1.2";
        final String s3 = "2.16.840.1.101.3.4.1.4";
        final List aliasesWithOid = SunEntries.createAliasesWithOid(new String[] { "1.2.840.113549.1.12.1.1" });
        final List aliasesWithOid2 = SunEntries.createAliasesWithOid(new String[] { "1.2.840.113549.1.12.1.2" });
        final List aliasesWithOid3 = SunEntries.createAliasesWithOid(new String[] { "1.2.840.113549.1.12.1.3" });
        final List aliasesWithOid4 = SunEntries.createAliasesWithOid(new String[] { "1.2.840.113549.1.12.1.5" });
        final List aliasesWithOid5 = SunEntries.createAliasesWithOid(new String[] { "1.2.840.113549.1.12.1.6" });
        final List aliasesWithOid6 = SunEntries.createAliasesWithOid(new String[] { "1.2.840.113549.1.5.3" });
        final List aliasesWithOid7 = SunEntries.createAliasesWithOid(new String[] { "1.2.840.113549.1.5.3", "PBE" });
        final List aliasesWithOid8 = SunEntries.createAliasesWithOid(new String[] { "1.2.840.113549.1.5.12" });
        final List aliasesWithOid9 = SunEntries.createAliasesWithOid(new String[] { "1.2.840.113549.1.5.13" });
        final List aliasesWithOid10 = SunEntries.createAliasesWithOid(new String[] { "1.2.840.113549.1.3.1", "DH" });
        final String s4 = "1.2.840.113549.2.";
        final List aliasesWithOid11 = SunEntries.createAliasesWithOid(new String[] { s4 + "7" });
        final List aliasesWithOid12 = SunEntries.createAliasesWithOid(new String[] { s4 + "8" });
        final List aliasesWithOid13 = SunEntries.createAliasesWithOid(new String[] { s4 + "9" });
        final List aliasesWithOid14 = SunEntries.createAliasesWithOid(new String[] { s4 + "10" });
        final List aliasesWithOid15 = SunEntries.createAliasesWithOid(new String[] { s4 + "11" });
        final HashMap hashMap = new HashMap(3);
        hashMap.put("SupportedModes", "ECB");
        hashMap.put("SupportedPaddings", "NOPADDING|PKCS1PADDING|OAEPPADDING|OAEPWITHMD5ANDMGF1PADDING|OAEPWITHSHA1ANDMGF1PADDING|OAEPWITHSHA-1ANDMGF1PADDING|OAEPWITHSHA-224ANDMGF1PADDING|OAEPWITHSHA-256ANDMGF1PADDING|OAEPWITHSHA-384ANDMGF1PADDING|OAEPWITHSHA-512ANDMGF1PADDING|OAEPWITHSHA-512/224ANDMGF1PADDING|OAEPWITHSHA-512/256ANDMGF1PADDING");
        hashMap.put("SupportedKeyClasses", "java.security.interfaces.RSAPublicKey|java.security.interfaces.RSAPrivateKey");
        this.ps("Cipher", "RSA", "com.sun.crypto.provider.RSACipher", null, hashMap);
        hashMap.clear();
        hashMap.put("SupportedModes", "ECB|CBC|PCBC|CTR|CTS|CFB|OFB|CFB8|CFB16|CFB24|CFB32|CFB40|CFB48|CFB56|CFB64|OFB8|OFB16|OFB24|OFB32|OFB40|OFB48|OFB56|OFB64");
        hashMap.put("SupportedPaddings", "NOPADDING|PKCS5PADDING|ISO10126PADDING");
        hashMap.put("SupportedKeyFormats", "RAW");
        this.ps("Cipher", "DES", "com.sun.crypto.provider.DESCipher", null, hashMap);
        this.ps("Cipher", "DESede", "com.sun.crypto.provider.DESedeCipher", aliases2, hashMap);
        this.ps("Cipher", "Blowfish", "com.sun.crypto.provider.BlowfishCipher", null, hashMap);
        this.ps("Cipher", "RC2", "com.sun.crypto.provider.RC2Cipher", null, hashMap);
        hashMap.clear();
        hashMap.put("SupportedModes", "ECB|CBC|PCBC|CTR|CTS|CFB|OFB|CFB8|CFB16|CFB24|CFB32|CFB40|CFB48|CFB56|CFB64|OFB8|OFB16|OFB24|OFB32|OFB40|OFB48|OFB56|OFB64|GCM|CFB72|CFB80|CFB88|CFB96|CFB104|CFB112|CFB120|CFB128|OFB72|OFB80|OFB88|OFB96|OFB104|OFB112|OFB120|OFB128");
        hashMap.put("SupportedPaddings", "NOPADDING|PKCS5PADDING|ISO10126PADDING");
        hashMap.put("SupportedKeyFormats", "RAW");
        this.ps("Cipher", "AES", "com.sun.crypto.provider.AESCipher$General", aliases, hashMap);
        hashMap.clear();
        hashMap.put("SupportedKeyFormats", "RAW");
        this.ps("Cipher", "AES_128/ECB/NoPadding", "com.sun.crypto.provider.AESCipher$AES128_ECB_NoPadding", SunEntries.createAliasesWithOid(new String[] { s + "1" }), hashMap);
        this.ps("Cipher", "AES_128/CBC/NoPadding", "com.sun.crypto.provider.AESCipher$AES128_CBC_NoPadding", SunEntries.createAliasesWithOid(new String[] { s + "2" }), hashMap);
        this.ps("Cipher", "AES_128/OFB/NoPadding", "com.sun.crypto.provider.AESCipher$AES128_OFB_NoPadding", SunEntries.createAliasesWithOid(new String[] { s + "3" }), hashMap);
        this.ps("Cipher", "AES_128/CFB/NoPadding", "com.sun.crypto.provider.AESCipher$AES128_CFB_NoPadding", SunEntries.createAliasesWithOid(new String[] { s + "4" }), hashMap);
        this.ps("Cipher", "AES_128/GCM/NoPadding", "com.sun.crypto.provider.AESCipher$AES128_GCM_NoPadding", SunEntries.createAliasesWithOid(new String[] { s + "6" }), hashMap);
        this.ps("Cipher", "AES_192/ECB/NoPadding", "com.sun.crypto.provider.AESCipher$AES192_ECB_NoPadding", SunEntries.createAliasesWithOid(new String[] { s2 + "1" }), hashMap);
        this.ps("Cipher", "AES_192/CBC/NoPadding", "com.sun.crypto.provider.AESCipher$AES192_CBC_NoPadding", SunEntries.createAliasesWithOid(new String[] { s2 + "2" }), hashMap);
        this.ps("Cipher", "AES_192/OFB/NoPadding", "com.sun.crypto.provider.AESCipher$AES192_OFB_NoPadding", SunEntries.createAliasesWithOid(new String[] { s2 + "3" }), hashMap);
        this.ps("Cipher", "AES_192/CFB/NoPadding", "com.sun.crypto.provider.AESCipher$AES192_CFB_NoPadding", SunEntries.createAliasesWithOid(new String[] { s2 + "4" }), hashMap);
        this.ps("Cipher", "AES_192/GCM/NoPadding", "com.sun.crypto.provider.AESCipher$AES192_GCM_NoPadding", SunEntries.createAliasesWithOid(new String[] { s2 + "6" }), hashMap);
        this.ps("Cipher", "AES_256/ECB/NoPadding", "com.sun.crypto.provider.AESCipher$AES256_ECB_NoPadding", SunEntries.createAliasesWithOid(new String[] { s3 + "1" }), hashMap);
        this.ps("Cipher", "AES_256/CBC/NoPadding", "com.sun.crypto.provider.AESCipher$AES256_CBC_NoPadding", SunEntries.createAliasesWithOid(new String[] { s3 + "2" }), hashMap);
        this.ps("Cipher", "AES_256/OFB/NoPadding", "com.sun.crypto.provider.AESCipher$AES256_OFB_NoPadding", SunEntries.createAliasesWithOid(new String[] { s3 + "3" }), hashMap);
        this.ps("Cipher", "AES_256/CFB/NoPadding", "com.sun.crypto.provider.AESCipher$AES256_CFB_NoPadding", SunEntries.createAliasesWithOid(new String[] { s3 + "4" }), hashMap);
        this.ps("Cipher", "AES_256/GCM/NoPadding", "com.sun.crypto.provider.AESCipher$AES256_GCM_NoPadding", SunEntries.createAliasesWithOid(new String[] { s3 + "6" }), hashMap);
        hashMap.clear();
        hashMap.put("SupportedModes", "CBC");
        hashMap.put("SupportedPaddings", "NOPADDING");
        hashMap.put("SupportedKeyFormats", "RAW");
        this.ps("Cipher", "DESedeWrap", "com.sun.crypto.provider.DESedeWrapCipher", null, hashMap);
        hashMap.clear();
        hashMap.put("SupportedModes", "ECB");
        hashMap.put("SupportedPaddings", "NOPADDING");
        hashMap.put("SupportedKeyFormats", "RAW");
        this.ps("Cipher", "ARCFOUR", "com.sun.crypto.provider.ARCFOURCipher", aliases3, hashMap);
        this.ps("Cipher", "AESWrap", "com.sun.crypto.provider.AESWrapCipher$General", null, hashMap);
        this.ps("Cipher", "AESWrap_128", "com.sun.crypto.provider.AESWrapCipher$AES128", SunEntries.createAliasesWithOid(new String[] { s + "5" }), hashMap);
        this.ps("Cipher", "AESWrap_192", "com.sun.crypto.provider.AESWrapCipher$AES192", SunEntries.createAliasesWithOid(new String[] { s2 + "5" }), hashMap);
        this.ps("Cipher", "AESWrap_256", "com.sun.crypto.provider.AESWrapCipher$AES256", SunEntries.createAliasesWithOid(new String[] { s3 + "5" }), hashMap);
        this.ps("Cipher", "PBEWithMD5AndDES", "com.sun.crypto.provider.PBEWithMD5AndDESCipher", aliasesWithOid6, null);
        this.ps("Cipher", "PBEWithMD5AndTripleDES", "com.sun.crypto.provider.PBEWithMD5AndTripleDESCipher", null, null);
        this.ps("Cipher", "PBEWithSHA1AndDESede", "com.sun.crypto.provider.PKCS12PBECipherCore$PBEWithSHA1AndDESede", aliasesWithOid3, null);
        this.ps("Cipher", "PBEWithSHA1AndRC2_40", "com.sun.crypto.provider.PKCS12PBECipherCore$PBEWithSHA1AndRC2_40", aliasesWithOid5, null);
        this.ps("Cipher", "PBEWithSHA1AndRC2_128", "com.sun.crypto.provider.PKCS12PBECipherCore$PBEWithSHA1AndRC2_128", aliasesWithOid4, null);
        this.ps("Cipher", "PBEWithSHA1AndRC4_40", "com.sun.crypto.provider.PKCS12PBECipherCore$PBEWithSHA1AndRC4_40", aliasesWithOid2, null);
        this.ps("Cipher", "PBEWithSHA1AndRC4_128", "com.sun.crypto.provider.PKCS12PBECipherCore$PBEWithSHA1AndRC4_128", aliasesWithOid, null);
        this.ps("Cipher", "PBEWithHmacSHA1AndAES_128", "com.sun.crypto.provider.PBES2Core$HmacSHA1AndAES_128", null, null);
        this.ps("Cipher", "PBEWithHmacSHA224AndAES_128", "com.sun.crypto.provider.PBES2Core$HmacSHA224AndAES_128", null, null);
        this.ps("Cipher", "PBEWithHmacSHA256AndAES_128", "com.sun.crypto.provider.PBES2Core$HmacSHA256AndAES_128", null, null);
        this.ps("Cipher", "PBEWithHmacSHA384AndAES_128", "com.sun.crypto.provider.PBES2Core$HmacSHA384AndAES_128", null, null);
        this.ps("Cipher", "PBEWithHmacSHA512AndAES_128", "com.sun.crypto.provider.PBES2Core$HmacSHA512AndAES_128", null, null);
        this.ps("Cipher", "PBEWithHmacSHA1AndAES_256", "com.sun.crypto.provider.PBES2Core$HmacSHA1AndAES_256", null, null);
        this.ps("Cipher", "PBEWithHmacSHA224AndAES_256", "com.sun.crypto.provider.PBES2Core$HmacSHA224AndAES_256", null, null);
        this.ps("Cipher", "PBEWithHmacSHA256AndAES_256", "com.sun.crypto.provider.PBES2Core$HmacSHA256AndAES_256", null, null);
        this.ps("Cipher", "PBEWithHmacSHA384AndAES_256", "com.sun.crypto.provider.PBES2Core$HmacSHA384AndAES_256", null, null);
        this.ps("Cipher", "PBEWithHmacSHA512AndAES_256", "com.sun.crypto.provider.PBES2Core$HmacSHA512AndAES_256", null, null);
        this.ps("KeyGenerator", "DES", "com.sun.crypto.provider.DESKeyGenerator", null, null);
        this.ps("KeyGenerator", "DESede", "com.sun.crypto.provider.DESedeKeyGenerator", aliases2, null);
        this.ps("KeyGenerator", "Blowfish", "com.sun.crypto.provider.BlowfishKeyGenerator", null, null);
        this.ps("KeyGenerator", "AES", "com.sun.crypto.provider.AESKeyGenerator", aliases, null);
        this.ps("KeyGenerator", "RC2", "com.sun.crypto.provider.KeyGeneratorCore$RC2KeyGenerator", null, null);
        this.ps("KeyGenerator", "ARCFOUR", "com.sun.crypto.provider.KeyGeneratorCore$ARCFOURKeyGenerator", aliases3, null);
        this.ps("KeyGenerator", "HmacMD5", "com.sun.crypto.provider.HmacMD5KeyGenerator", null, null);
        this.ps("KeyGenerator", "HmacSHA1", "com.sun.crypto.provider.HmacSHA1KeyGenerator", aliasesWithOid11, null);
        this.ps("KeyGenerator", "HmacSHA224", "com.sun.crypto.provider.KeyGeneratorCore$HmacSHA2KG$SHA224", aliasesWithOid12, null);
        this.ps("KeyGenerator", "HmacSHA256", "com.sun.crypto.provider.KeyGeneratorCore$HmacSHA2KG$SHA256", aliasesWithOid13, null);
        this.ps("KeyGenerator", "HmacSHA384", "com.sun.crypto.provider.KeyGeneratorCore$HmacSHA2KG$SHA384", aliasesWithOid14, null);
        this.ps("KeyGenerator", "HmacSHA512", "com.sun.crypto.provider.KeyGeneratorCore$HmacSHA2KG$SHA512", aliasesWithOid15, null);
        this.ps("KeyPairGenerator", "DiffieHellman", "com.sun.crypto.provider.DHKeyPairGenerator", aliasesWithOid10, null);
        this.ps("AlgorithmParameterGenerator", "DiffieHellman", "com.sun.crypto.provider.DHParameterGenerator", aliasesWithOid10, null);
        hashMap.clear();
        hashMap.put("SupportedKeyClasses", "javax.crypto.interfaces.DHPublicKey|javax.crypto.interfaces.DHPrivateKey");
        this.ps("KeyAgreement", "DiffieHellman", "com.sun.crypto.provider.DHKeyAgreement", aliasesWithOid10, hashMap);
        this.ps("AlgorithmParameters", "DiffieHellman", "com.sun.crypto.provider.DHParameters", aliasesWithOid10, null);
        this.ps("AlgorithmParameters", "DES", "com.sun.crypto.provider.DESParameters", null, null);
        this.ps("AlgorithmParameters", "DESede", "com.sun.crypto.provider.DESedeParameters", aliases2, null);
        this.ps("AlgorithmParameters", "PBE", "com.sun.crypto.provider.PBEParameters", null, null);
        this.ps("AlgorithmParameters", "PBEWithMD5AndDES", "com.sun.crypto.provider.PBEParameters", aliasesWithOid6, null);
        this.ps("AlgorithmParameters", "PBEWithMD5AndTripleDES", "com.sun.crypto.provider.PBEParameters", null, null);
        this.ps("AlgorithmParameters", "PBEWithSHA1AndDESede", "com.sun.crypto.provider.PBEParameters", aliasesWithOid3, null);
        this.ps("AlgorithmParameters", "PBEWithSHA1AndRC2_40", "com.sun.crypto.provider.PBEParameters", aliasesWithOid5, null);
        this.ps("AlgorithmParameters", "PBEWithSHA1AndRC2_128", "com.sun.crypto.provider.PBEParameters", aliasesWithOid4, null);
        this.ps("AlgorithmParameters", "PBEWithSHA1AndRC4_40", "com.sun.crypto.provider.PBEParameters", aliasesWithOid2, null);
        this.ps("AlgorithmParameters", "PBEWithSHA1AndRC4_128", "com.sun.crypto.provider.PBEParameters", aliasesWithOid, null);
        this.ps("AlgorithmParameters", "PBES2", "com.sun.crypto.provider.PBES2Parameters$General", aliasesWithOid9, null);
        this.ps("AlgorithmParameters", "PBEWithHmacSHA1AndAES_128", "com.sun.crypto.provider.PBES2Parameters$HmacSHA1AndAES_128", null, null);
        this.ps("AlgorithmParameters", "PBEWithHmacSHA224AndAES_128", "com.sun.crypto.provider.PBES2Parameters$HmacSHA224AndAES_128", null, null);
        this.ps("AlgorithmParameters", "PBEWithHmacSHA256AndAES_128", "com.sun.crypto.provider.PBES2Parameters$HmacSHA256AndAES_128", null, null);
        this.ps("AlgorithmParameters", "PBEWithHmacSHA384AndAES_128", "com.sun.crypto.provider.PBES2Parameters$HmacSHA384AndAES_128", null, null);
        this.ps("AlgorithmParameters", "PBEWithHmacSHA512AndAES_128", "com.sun.crypto.provider.PBES2Parameters$HmacSHA512AndAES_128", null, null);
        this.ps("AlgorithmParameters", "PBEWithHmacSHA1AndAES_256", "com.sun.crypto.provider.PBES2Parameters$HmacSHA1AndAES_256", null, null);
        this.ps("AlgorithmParameters", "PBEWithHmacSHA224AndAES_256", "com.sun.crypto.provider.PBES2Parameters$HmacSHA224AndAES_256", null, null);
        this.ps("AlgorithmParameters", "PBEWithHmacSHA256AndAES_256", "com.sun.crypto.provider.PBES2Parameters$HmacSHA256AndAES_256", null, null);
        this.ps("AlgorithmParameters", "PBEWithHmacSHA384AndAES_256", "com.sun.crypto.provider.PBES2Parameters$HmacSHA384AndAES_256", null, null);
        this.ps("AlgorithmParameters", "PBEWithHmacSHA512AndAES_256", "com.sun.crypto.provider.PBES2Parameters$HmacSHA512AndAES_256", null, null);
        this.ps("AlgorithmParameters", "Blowfish", "com.sun.crypto.provider.BlowfishParameters", null, null);
        this.ps("AlgorithmParameters", "AES", "com.sun.crypto.provider.AESParameters", aliases, null);
        this.ps("AlgorithmParameters", "GCM", "com.sun.crypto.provider.GCMParameters", null, null);
        this.ps("AlgorithmParameters", "RC2", "com.sun.crypto.provider.RC2Parameters", null, null);
        this.ps("AlgorithmParameters", "OAEP", "com.sun.crypto.provider.OAEPParameters", null, null);
        this.ps("KeyFactory", "DiffieHellman", "com.sun.crypto.provider.DHKeyFactory", aliasesWithOid10, null);
        this.ps("SecretKeyFactory", "DES", "com.sun.crypto.provider.DESKeyFactory", null, null);
        this.ps("SecretKeyFactory", "DESede", "com.sun.crypto.provider.DESedeKeyFactory", aliases2, null);
        this.ps("SecretKeyFactory", "PBEWithMD5AndDES", "com.sun.crypto.provider.PBEKeyFactory$PBEWithMD5AndDES", aliasesWithOid7, null);
        this.ps("SecretKeyFactory", "PBEWithMD5AndTripleDES", "com.sun.crypto.provider.PBEKeyFactory$PBEWithMD5AndTripleDES", null, null);
        this.ps("SecretKeyFactory", "PBEWithSHA1AndDESede", "com.sun.crypto.provider.PBEKeyFactory$PBEWithSHA1AndDESede", aliasesWithOid3, null);
        this.ps("SecretKeyFactory", "PBEWithSHA1AndRC2_40", "com.sun.crypto.provider.PBEKeyFactory$PBEWithSHA1AndRC2_40", aliasesWithOid5, null);
        this.ps("SecretKeyFactory", "PBEWithSHA1AndRC2_128", "com.sun.crypto.provider.PBEKeyFactory$PBEWithSHA1AndRC2_128", aliasesWithOid4, null);
        this.ps("SecretKeyFactory", "PBEWithSHA1AndRC4_40", "com.sun.crypto.provider.PBEKeyFactory$PBEWithSHA1AndRC4_40", aliasesWithOid2, null);
        this.ps("SecretKeyFactory", "PBEWithSHA1AndRC4_128", "com.sun.crypto.provider.PBEKeyFactory$PBEWithSHA1AndRC4_128", aliasesWithOid, null);
        this.ps("SecretKeyFactory", "PBEWithHmacSHA1AndAES_128", "com.sun.crypto.provider.PBEKeyFactory$PBEWithHmacSHA1AndAES_128", null, null);
        this.ps("SecretKeyFactory", "PBEWithHmacSHA224AndAES_128", "com.sun.crypto.provider.PBEKeyFactory$PBEWithHmacSHA224AndAES_128", null, null);
        this.ps("SecretKeyFactory", "PBEWithHmacSHA256AndAES_128", "com.sun.crypto.provider.PBEKeyFactory$PBEWithHmacSHA256AndAES_128", null, null);
        this.ps("SecretKeyFactory", "PBEWithHmacSHA384AndAES_128", "com.sun.crypto.provider.PBEKeyFactory$PBEWithHmacSHA384AndAES_128", null, null);
        this.ps("SecretKeyFactory", "PBEWithHmacSHA512AndAES_128", "com.sun.crypto.provider.PBEKeyFactory$PBEWithHmacSHA512AndAES_128", null, null);
        this.ps("SecretKeyFactory", "PBEWithHmacSHA1AndAES_256", "com.sun.crypto.provider.PBEKeyFactory$PBEWithHmacSHA1AndAES_256", null, null);
        this.ps("SecretKeyFactory", "PBEWithHmacSHA224AndAES_256", "com.sun.crypto.provider.PBEKeyFactory$PBEWithHmacSHA224AndAES_256", null, null);
        this.ps("SecretKeyFactory", "PBEWithHmacSHA256AndAES_256", "com.sun.crypto.provider.PBEKeyFactory$PBEWithHmacSHA256AndAES_256", null, null);
        this.ps("SecretKeyFactory", "PBEWithHmacSHA384AndAES_256", "com.sun.crypto.provider.PBEKeyFactory$PBEWithHmacSHA384AndAES_256", null, null);
        this.ps("SecretKeyFactory", "PBEWithHmacSHA512AndAES_256", "com.sun.crypto.provider.PBEKeyFactory$PBEWithHmacSHA512AndAES_256", null, null);
        this.ps("SecretKeyFactory", "PBKDF2WithHmacSHA1", "com.sun.crypto.provider.PBKDF2Core$HmacSHA1", aliasesWithOid8, null);
        this.ps("SecretKeyFactory", "PBKDF2WithHmacSHA224", "com.sun.crypto.provider.PBKDF2Core$HmacSHA224", null, null);
        this.ps("SecretKeyFactory", "PBKDF2WithHmacSHA256", "com.sun.crypto.provider.PBKDF2Core$HmacSHA256", null, null);
        this.ps("SecretKeyFactory", "PBKDF2WithHmacSHA384", "com.sun.crypto.provider.PBKDF2Core$HmacSHA384", null, null);
        this.ps("SecretKeyFactory", "PBKDF2WithHmacSHA512", "com.sun.crypto.provider.PBKDF2Core$HmacSHA512", null, null);
        hashMap.clear();
        hashMap.put("SupportedKeyFormats", "RAW");
        this.ps("Mac", "HmacMD5", "com.sun.crypto.provider.HmacMD5", null, hashMap);
        this.ps("Mac", "HmacSHA1", "com.sun.crypto.provider.HmacSHA1", aliasesWithOid11, hashMap);
        this.ps("Mac", "HmacSHA224", "com.sun.crypto.provider.HmacCore$HmacSHA224", aliasesWithOid12, hashMap);
        this.ps("Mac", "HmacSHA256", "com.sun.crypto.provider.HmacCore$HmacSHA256", aliasesWithOid13, hashMap);
        this.ps("Mac", "HmacSHA384", "com.sun.crypto.provider.HmacCore$HmacSHA384", aliasesWithOid14, hashMap);
        this.ps("Mac", "HmacSHA512", "com.sun.crypto.provider.HmacCore$HmacSHA512", aliasesWithOid15, hashMap);
        this.ps("Mac", "HmacPBESHA1", "com.sun.crypto.provider.HmacPKCS12PBESHA1", null, hashMap);
        this.ps("Mac", "PBEWithHmacSHA1", "com.sun.crypto.provider.PBMAC1Core$HmacSHA1", null, hashMap);
        this.ps("Mac", "PBEWithHmacSHA224", "com.sun.crypto.provider.PBMAC1Core$HmacSHA224", null, hashMap);
        this.ps("Mac", "PBEWithHmacSHA256", "com.sun.crypto.provider.PBMAC1Core$HmacSHA256", null, hashMap);
        this.ps("Mac", "PBEWithHmacSHA384", "com.sun.crypto.provider.PBMAC1Core$HmacSHA384", null, hashMap);
        this.ps("Mac", "PBEWithHmacSHA512", "com.sun.crypto.provider.PBMAC1Core$HmacSHA512", null, hashMap);
        this.ps("Mac", "SslMacMD5", "com.sun.crypto.provider.SslMacCore$SslMacMD5", null, hashMap);
        this.ps("Mac", "SslMacSHA1", "com.sun.crypto.provider.SslMacCore$SslMacSHA1", null, hashMap);
        this.ps("KeyStore", "JCEKS", "com.sun.crypto.provider.JceKeyStore", null, null);
        this.ps("KeyGenerator", "SunTlsPrf", "com.sun.crypto.provider.TlsPrfGenerator$V10", null, null);
        this.ps("KeyGenerator", "SunTls12Prf", "com.sun.crypto.provider.TlsPrfGenerator$V12", null, null);
        this.ps("KeyGenerator", "SunTlsMasterSecret", "com.sun.crypto.provider.TlsMasterSecretGenerator", aliases4, null);
        this.ps("KeyGenerator", "SunTlsKeyMaterial", "com.sun.crypto.provider.TlsKeyMaterialGenerator", aliases5, null);
        this.ps("KeyGenerator", "SunTlsRsaPremasterSecret", "com.sun.crypto.provider.TlsRsaPremasterSecretGenerator", aliases6, null);
    }
    
    static SunJCE getInstance() {
        if (SunJCE.instance == null) {
            return new SunJCE();
        }
        return SunJCE.instance;
    }
    
    static {
        SunJCE.instance = null;
    }
    
    private static class SecureRandomHolder
    {
        static final SecureRandom RANDOM;
        
        static {
            RANDOM = new SecureRandom();
        }
    }
}
