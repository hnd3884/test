package HTTPClient;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

class MD5
{
    private static final char[] hex;
    
    public static final String toHex(final byte[] hash) {
        final StringBuffer buf = new StringBuffer(hash.length * 2);
        for (int idx = 0; idx < hash.length; ++idx) {
            buf.append(MD5.hex[hash[idx] >> 4 & 0xF]).append(MD5.hex[hash[idx] & 0xF]);
        }
        return buf.toString();
    }
    
    public static final byte[] digest(final byte[] input) {
        try {
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5.digest(input);
        }
        catch (final NoSuchAlgorithmException nsae) {
            throw new Error(nsae.toString());
        }
    }
    
    public static final byte[] digest(final byte[] input1, final byte[] input2) {
        try {
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(input1);
            return md5.digest(input2);
        }
        catch (final NoSuchAlgorithmException nsae) {
            throw new Error(nsae.toString());
        }
    }
    
    public static final String hexDigest(final byte[] input) {
        return toHex(digest(input));
    }
    
    public static final String hexDigest(final byte[] input1, final byte[] input2) {
        return toHex(digest(input1, input2));
    }
    
    public static final byte[] digest(final String input) {
        try {
            return digest(input.getBytes("8859_1"));
        }
        catch (final UnsupportedEncodingException uee) {
            throw new Error(uee.toString());
        }
    }
    
    public static final String hexDigest(final String input) {
        try {
            return toHex(digest(input.getBytes("8859_1")));
        }
        catch (final UnsupportedEncodingException uee) {
            throw new Error(uee.toString());
        }
    }
    
    static {
        hex = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
