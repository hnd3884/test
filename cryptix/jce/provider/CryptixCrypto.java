package cryptix.jce.provider;

import java.security.Provider;

public final class CryptixCrypto extends Provider
{
    private static final String NAME = "CryptixCrypto";
    private static final String INFO = "Cryptix JCE Strong Crypto Provider";
    private static final double VERSION = 1.2;
    
    public CryptixCrypto() {
        super("CryptixCrypto", 1.2, "Cryptix JCE Strong Crypto Provider");
        this.put("Cipher.Blowfish", "cryptix.jce.provider.cipher.Blowfish");
        this.put("KeyGenerator.Blowfish", "cryptix.jce.provider.key.BlowfishKeyGenerator");
        this.put("Cipher.CAST5", "cryptix.jce.provider.cipher.CAST5");
        this.put("KeyGenerator.CAST5", "cryptix.jce.provider.key.CAST5KeyGenerator");
        this.put("Cipher.DES", "cryptix.jce.provider.cipher.DES");
        this.put("KeyGenerator.DES", "cryptix.jce.provider.key.DESKeyGenerator");
        this.put("SecretKeyFactory.DES", "cryptix.jce.provider.keyfactory.DESKeyFactory");
        this.put("Cipher.IDEA", "cryptix.jce.provider.cipher.IDEA");
        this.put("KeyGenerator.IDEA", "cryptix.jce.provider.key.IDEAKeyGenerator");
        this.put("Cipher.MARS", "cryptix.jce.provider.cipher.MARS");
        this.put("KeyGenerator.MARS", "cryptix.jce.provider.key.MARSKeyGenerator");
        this.put("Cipher.Null", "cryptix.jce.provider.cipher.Null");
        this.put("Cipher.RC2", "cryptix.jce.provider.cipher.RC2");
        this.put("KeyGenerator.RC2", "cryptix.jce.provider.key.RC2KeyGenerator");
        this.put("Cipher.RC4", "cryptix.jce.provider.cipher.RC4");
        this.put("KeyGenerator.RC4", "cryptix.jce.provider.key.RC4KeyGenerator");
        this.put("Cipher.RC6", "cryptix.jce.provider.cipher.RC6");
        this.put("KeyGenerator.RC6", "cryptix.jce.provider.key.RC6KeyGenerator");
        this.put("Cipher.Rijndael", "cryptix.jce.provider.cipher.Rijndael");
        this.put("KeyGenerator.Rijndael", "cryptix.jce.provider.key.RijndaelKeyGenerator");
        this.put("Cipher.Serpent", "cryptix.jce.provider.cipher.Serpent");
        this.put("KeyGenerator.Serpent", "cryptix.jce.provider.key.SerpentKeyGenerator");
        this.put("Cipher.SKIPJACK", "cryptix.jce.provider.cipher.SKIPJACK");
        this.put("KeyGenerator.SKIPJACK", "cryptix.jce.provider.key.SKIPJACKKeyGenerator");
        this.put("Cipher.Square", "cryptix.jce.provider.cipher.Square");
        this.put("KeyGenerator.Square", "cryptix.jce.provider.key.SquareKeyGenerator");
        this.put("Cipher.TripleDES", "cryptix.jce.provider.cipher.TripleDES");
        this.put("Alg.Alias.Cipher.DESede", "TripleDES");
        this.put("KeyGenerator.TripleDES", "cryptix.jce.provider.key.TripleDESKeyGenerator");
        this.put("Alg.Alias.KeyGenerator.DESede", "TripleDES");
        this.put("Cipher.Twofish", "cryptix.jce.provider.cipher.Twofish");
        this.put("KeyGenerator.Twofish", "cryptix.jce.provider.key.TwofishKeyGenerator");
        this.put("Mac.HMAC-MD5", "cryptix.jce.provider.mac.HMAC_MD5");
        this.put("Alg.Alias.Mac.HmacMD5", "HMAC-MD5");
        this.put("Mac.HMAC-RIPEMD", "cryptix.jce.provider.mac.HMAC_RIPEMD");
        this.put("Alg.Alias.Mac.HmacRIPEMD", "HMAC-RIPEMD");
        this.put("Mac.HMAC-RIPEMD128", "cryptix.jce.provider.mac.HMAC_RIPEMD128");
        this.put("Alg.Alias.Mac.HmacRIPEMD128", "HMAC-RIPEMD128");
        this.put("Mac.HMAC-RIPEMD160", "cryptix.jce.provider.mac.HMAC_RIPEMD160");
        this.put("Alg.Alias.Mac.HmacRIPEMD160", "HMAC-RIPEMD160");
        this.put("Mac.HMAC-SHA0", "cryptix.jce.provider.mac.HMAC_SHA0");
        this.put("Alg.Alias.Mac.HMAC-SHA-0", "HMAC-SHA0");
        this.put("Alg.Alias.Mac.HmacSHA0", "HMAC-SHA0");
        this.put("Alg.Alias.Mac.HmacSHA-0", "HMAC-SHA0");
        this.put("Mac.HMAC-SHA", "cryptix.jce.provider.mac.HMAC_SHA1");
        this.put("Alg.Alias.Mac.HMAC-SHA-1", "HMAC-SHA");
        this.put("Alg.Alias.Mac.HMAC-SHA1", "HMAC-SHA");
        this.put("Alg.Alias.Mac.HmacSHA", "HMAC-SHA");
        this.put("Alg.Alias.Mac.HmacSHA-1", "HMAC-SHA");
        this.put("Mac.HMAC-Tiger", "cryptix.jce.provider.mac.HMAC_Tiger");
        this.put("Mac.Null", "cryptix.jce.provider.mac.Null");
        this.put("KeyGenerator.HMAC", "cryptix.jce.provider.key.HMACKeyGenerator");
        this.put("MessageDigest.MD2", "cryptix.jce.provider.md.MD2");
        this.put("MessageDigest.MD4", "cryptix.jce.provider.md.MD4");
        this.put("MessageDigest.MD5", "cryptix.jce.provider.md.MD5");
        this.put("MessageDigest.RIPEMD", "cryptix.jce.provider.md.RIPEMD");
        this.put("MessageDigest.RIPEMD128", "cryptix.jce.provider.md.RIPEMD128");
        this.put("Alg.Alias.MessageDigest.RIPEMD-128", "RIPEMD128");
        this.put("MessageDigest.RIPEMD160", "cryptix.jce.provider.md.RIPEMD160");
        this.put("Alg.Alias.MessageDigest.RIPEMD-160", "RIPEMD160");
        this.put("MessageDigest.SHA1", "cryptix.jce.provider.md.SHA1");
        this.put("MessageDigest.SHA-1", "cryptix.jce.provider.md.SHA1");
        this.put("Alg.Alias.MessageDigest.SHA", "SHA1");
        this.put("MessageDigest.SHA0", "cryptix.jce.provider.md.SHA0");
        this.put("Alg.Alias.MessageDigest.SHA-0", "SHA0");
        this.put("MessageDigest.SHA-256", "cryptix.jce.provider.md.SHA256");
        this.put("MessageDigest.SHA-384", "cryptix.jce.provider.md.SHA384");
        this.put("MessageDigest.SHA-512", "cryptix.jce.provider.md.SHA512");
        this.put("MessageDigest.Tiger", "cryptix.jce.provider.md.Tiger");
        this.put("Signature.RSASSA-PSS/SHA-1", "cryptix.jce.provider.rsa.RSASignature_PSS_SHA1");
        this.put("Signature.RSASSA-PSS/SHA-256", "cryptix.jce.provider.rsa.RSASignature_PSS_SHA256");
        this.put("Signature.RSASSA-PSS/SHA-384", "cryptix.jce.provider.rsa.RSASignature_PSS_SHA384");
        this.put("Signature.RSASSA-PSS/SHA-512", "cryptix.jce.provider.rsa.RSASignature_PSS_SHA512");
        this.put("Signature.MD2withRSA", "cryptix.jce.provider.rsa.RSASignature_PKCS1_MD2");
        this.put("Alg.Alias.Signature.MD2/RSA/PKCS#1", "MD2withRSA");
        this.put("Signature.MD4withRSA", "cryptix.jce.provider.rsa.RSASignature_PKCS1_MD4");
        this.put("Alg.Alias.Signature.MD4/RSA/PKCS#1", "MD4withRSA");
        this.put("Signature.MD5withRSA", "cryptix.jce.provider.rsa.RSASignature_PKCS1_MD5");
        this.put("Alg.Alias.Signature.MD5/RSA/PKCS#1", "MD5withRSA");
        this.put("Signature.RIPEMD128withRSA", "cryptix.jce.provider.rsa.RSASignature_PKCS1_RIPEMD128");
        this.put("Alg.Alias.Signature.RIPEMD-128/RSA/PKCS#1", "RIPEMD128withRSA");
        this.put("Signature.RIPEMD160withRSA", "cryptix.jce.provider.rsa.RSASignature_PKCS1_RIPEMD160");
        this.put("Alg.Alias.Signature.RIPEMD-160/RSA/PKCS#1", "RIPEMD160withRSA");
        this.put("Signature.SHA1withRSA", "cryptix.jce.provider.rsa.RSASignature_PKCS1_SHA1");
        this.put("Alg.Alias.Signature.SHA-1/RSA/PKCS#1", "SHA1withRSA");
        this.put("Signature.SHA-256/RSA/PKCS#1", "cryptix.jce.provider.rsa.RSASignature_PKCS1_SHA256");
        this.put("Signature.SHA-384/RSA/PKCS#1", "cryptix.jce.provider.rsa.RSASignature_PKCS1_SHA384");
        this.put("Signature.SHA-512/RSA/PKCS#1", "cryptix.jce.provider.rsa.RSASignature_PKCS1_SHA512");
        this.put("Signature.SHA/DSA", "cryptix.jce.provider.dsa.DSASignature");
        this.put("Signature.RawDSA", "cryptix.jce.provider.dsa.RawDSASignature");
        this.put("AlgorithmParameters.DES", "cryptix.jce.provider.parameters.BlockParameters");
        this.put("KeyFactory.RSA", "cryptix.jce.provider.rsa.RSAKeyFactory");
        this.put("KeyPairGenerator.RSA", "cryptix.jce.provider.rsa.RSAKeyPairGenerator");
        this.put("Cipher.RSA/ECB/PKCS#1", "cryptix.jce.provider.rsa.RSACipher_ECB_PKCS1");
        this.put("KeyPairGenerator.ElGamal", "cryptix.jce.provider.elgamal.ElGamalKeyPairGenerator");
        this.put("Cipher.ElGamal/ECB/PKCS#1", "cryptix.jce.provider.elgamal.ElGamalCipher");
        this.put("KeyAgreement.DH", "cryptix.jce.provider.dh.DHKeyAgreement");
        this.put("KeyPairGenerator.DH", "cryptix.jce.provider.dh.DHKeyPairGenerator");
    }
}
