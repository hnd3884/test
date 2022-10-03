package com.zoho.framework.utils.crypto;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import javax.crypto.SecretKey;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.security.SecureRandom;
import java.util.logging.Logger;

public class EnDecryptBlowfishImpl implements EnDecrypt
{
    private static final Logger LOGGER;
    private static final String ENCODING = "UTF-8";
    
    @Override
    public String encrypt(final String plainText) {
        return this.encrypt(plainText, null);
    }
    
    @Override
    public String decrypt(final String cipherText) {
        return this.decrypt(cipherText, null);
    }
    
    @Override
    public String encrypt(final String plainText, final String cryptTag) {
        try {
            final String encryptionKey = (cryptTag == null) ? EnDecryptUtil.getCryptTag() : cryptTag;
            final SecureRandom random = new SecureRandom();
            final byte[] bytes = new byte[20];
            random.nextBytes(bytes);
            final byte[] saltBytes = bytes;
            final SecretKeyFactory KeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            final PBEKeySpec spec = new PBEKeySpec(encryptionKey.toCharArray(), saltBytes, 4096, 128);
            final SecretKey secretkey = KeyFactory.generateSecret(spec);
            final SecretKeySpec skc = new SecretKeySpec(secretkey.getEncoded(), "Blowfish");
            final Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
            final byte[] iv = new byte[cipher.getBlockSize()];
            final IvParameterSpec ivpa = new IvParameterSpec(iv);
            cipher.init(1, skc, ivpa);
            final byte[] cleartext = plainText.getBytes("UTF-8");
            final byte[] ciphertext = cipher.doFinal(cleartext);
            final byte[] saltcipher = new byte[saltBytes.length + ciphertext.length];
            System.arraycopy(saltBytes, 0, saltcipher, 0, saltBytes.length);
            System.arraycopy(ciphertext, 0, saltcipher, saltBytes.length, ciphertext.length);
            final String encryptedString = EnDecryptUtil.BASE16_ENCODE(saltcipher);
            return encryptedString;
        }
        catch (final NoSuchAlgorithmException | InvalidKeySpecException nsa) {
            EnDecryptBlowfishImpl.LOGGER.log(Level.SEVERE, "Key generation failed" + nsa.getMessage());
            throw new IllegalArgumentException(nsa.getMessage(), nsa);
        }
        catch (final Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
    
    @Override
    public String decrypt(final String cipherText, final String cryptTag) {
        try {
            final String encryptionKey = (cryptTag == null) ? EnDecryptUtil.getCryptTag() : cryptTag;
            final byte[] saltBytes = new byte[20];
            final ByteBuffer saltbuffer = ByteBuffer.wrap(EnDecryptUtil.BASE16_DECODE(cipherText));
            saltbuffer.get(saltBytes, 0, saltBytes.length);
            final byte[] cipherbytes = new byte[saltbuffer.capacity() - saltBytes.length];
            saltbuffer.get(cipherbytes);
            final SecretKeyFactory KeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            final PBEKeySpec spec = new PBEKeySpec(encryptionKey.toCharArray(), saltBytes, 4096, 128);
            final SecretKey secretkey = KeyFactory.generateSecret(spec);
            final SecretKeySpec skc = new SecretKeySpec(secretkey.getEncoded(), "Blowfish");
            final Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
            final byte[] iv = new byte[cipher.getBlockSize()];
            final IvParameterSpec ivpa = new IvParameterSpec(iv);
            cipher.init(2, skc, ivpa);
            final byte[] decryptedbytes = cipher.doFinal(cipherbytes);
            final String decryptedString = EnDecryptUtil.B2S(decryptedbytes);
            return decryptedString;
        }
        catch (final NoSuchAlgorithmException | InvalidKeySpecException nsa) {
            EnDecryptBlowfishImpl.LOGGER.log(Level.SEVERE, "Key generation failed" + nsa.getMessage());
        }
        catch (final Exception e) {
            EnDecryptBlowfishImpl.LOGGER.log(Level.SEVERE, "Decryption failed" + e.getMessage());
        }
        return cipherText;
    }
    
    static {
        LOGGER = Logger.getLogger(EnDecryptBlowfishImpl.class.getName());
    }
}
