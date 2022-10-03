package com.zoho.security.util;

import com.zoho.jedis.v320.util.MurmurHash;
import java.util.logging.Level;
import java.security.MessageDigest;
import java.util.logging.Logger;

public class HashUtil
{
    private static final Logger LOGGER;
    public static final char[] HEX;
    public static final String ENCODING = "UTF-8";
    
    public static String SHA512(final String plaintext) {
        return hash(plaintext, null, "SHA512");
    }
    
    public static String hash(final String plaintext, final String salt, final String algorithm) {
        try {
            final byte[] p = plaintext.getBytes("UTF-8");
            MessageDigest md = null;
            if ("SHA512".equals(algorithm)) {
                md = MessageDigest.getInstance("SHA-512");
            }
            else {
                md = MessageDigest.getInstance("MD5");
            }
            md.reset();
            md.update(p);
            if (salt != null) {
                final byte[] s = salt.getBytes("UTF-8");
                md.update(s);
            }
            final byte[] digest = md.digest();
            return BASE16_ENCODE(digest);
        }
        catch (final Exception e) {
            HashUtil.LOGGER.log(Level.WARNING, "Exception occurred while generating Hash Value : {0}", e);
            return plaintext;
        }
    }
    
    public static Long murmurHash(final String plaintext) {
        return MurmurHash.hash64A(plaintext.getBytes(), 0);
    }
    
    public static String BASE16_ENCODE(final byte[] input) {
        final char[] b16 = new char[input.length * 2];
        int i = 0;
        for (final byte c : input) {
            final int low = c & 0xF;
            final int high = (c & 0xF0) >> 4;
            b16[i++] = HashUtil.HEX[high];
            b16[i++] = HashUtil.HEX[low];
        }
        return new String(b16);
    }
    
    static {
        LOGGER = Logger.getLogger(HashUtil.class.getName());
        HEX = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
