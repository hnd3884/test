package com.zoho.tools;

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

public class AES256Util
{
    private static final Logger LOGGER;
    private static final String ENCODING = "UTF-8";
    private static final char[] HEX;
    private static int bit;
    
    public static String encrypt(final String plainText, final String cryptKey) {
        try {
            final SecureRandom random = new SecureRandom();
            final byte[] bytes = new byte[20];
            random.nextBytes(bytes);
            final SecretKeyFactory KeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            final PBEKeySpec spec = new PBEKeySpec(cryptKey.toCharArray(), bytes, 65556, AES256Util.bit);
            final SecretKey secretkey = KeyFactory.generateSecret(spec);
            final SecretKeySpec skc = new SecretKeySpec(secretkey.getEncoded(), "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final byte[] iv = new byte[cipher.getBlockSize()];
            final IvParameterSpec ivpa = new IvParameterSpec(iv);
            cipher.init(1, skc, ivpa);
            final byte[] cleartext = plainText.getBytes("UTF-8");
            final byte[] ciphertext = cipher.doFinal(cleartext);
            final byte[] saltcipher = new byte[bytes.length + ciphertext.length];
            System.arraycopy(bytes, 0, saltcipher, 0, bytes.length);
            System.arraycopy(ciphertext, 0, saltcipher, bytes.length, ciphertext.length);
            return BASE16_ENCODE(saltcipher);
        }
        catch (final NoSuchAlgorithmException | InvalidKeySpecException nsa) {
            AES256Util.LOGGER.log(Level.SEVERE, "Key generation failed " + nsa.getMessage(), nsa);
        }
        catch (final Exception e) {
            AES256Util.LOGGER.log(Level.WARNING, "Encryption failed" + e.getMessage(), e);
        }
        return plainText;
    }
    
    public static String decrypt(final String cipherText, final String cryptKey) {
        try {
            final byte[] saltBytes = new byte[20];
            final ByteBuffer saltbuffer = ByteBuffer.wrap(BASE16_DECODE(cipherText));
            saltbuffer.get(saltBytes, 0, saltBytes.length);
            final byte[] cipherbytes = new byte[saltbuffer.capacity() - saltBytes.length];
            saltbuffer.get(cipherbytes);
            final SecretKeyFactory KeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            final PBEKeySpec spec = new PBEKeySpec(cryptKey.toCharArray(), saltBytes, 65556, AES256Util.bit);
            final SecretKey secretkey = KeyFactory.generateSecret(spec);
            final SecretKeySpec skc = new SecretKeySpec(secretkey.getEncoded(), "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final byte[] iv = new byte[cipher.getBlockSize()];
            final IvParameterSpec ivpa = new IvParameterSpec(iv);
            cipher.init(2, skc, ivpa);
            final byte[] decryptbytes = cipher.doFinal(cipherbytes);
            return B2S(decryptbytes);
        }
        catch (final NoSuchAlgorithmException | InvalidKeySpecException nsa) {
            AES256Util.LOGGER.log(Level.SEVERE, "Key generation failed" + nsa.getMessage(), nsa);
        }
        catch (final Exception e) {
            AES256Util.LOGGER.log(Level.WARNING, "Decryption failed" + e.getMessage(), e);
        }
        return cipherText;
    }
    
    private static String B2S(final byte[] bytes) {
        return new String(bytes);
    }
    
    private static String BASE16_ENCODE(final byte[] input) {
        final char[] b16 = new char[input.length * 2];
        int i = 0;
        for (final byte c : input) {
            final int low = c & 0xF;
            final int high = (c & 0xF0) >> 4;
            b16[i++] = AES256Util.HEX[high];
            b16[i++] = AES256Util.HEX[low];
        }
        return new String(b16);
    }
    
    private static byte[] BASE16_DECODE(final String b16str) {
        final int len = b16str.length();
        final byte[] out = new byte[len / 2];
        int j = 0;
        for (int i = 0; i < len; i += 2) {
            final int c1 = INT(b16str.charAt(i));
            final int c2 = INT(b16str.charAt(i + 1));
            final int bt = c1 << 4 | c2;
            out[j++] = (byte)bt;
        }
        return out;
    }
    
    private static int INT(final char c) {
        return Integer.decode("0x" + c);
    }
    
    static {
        LOGGER = Logger.getLogger(AES256Util.class.getName());
        HEX = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        AES256Util.bit = 256;
    }
}
