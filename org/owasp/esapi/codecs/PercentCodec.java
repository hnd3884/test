package org.owasp.esapi.codecs;

import org.owasp.esapi.util.CollectionsUtil;
import java.io.UnsupportedEncodingException;
import java.util.Set;

public class PercentCodec extends Codec
{
    private static final String ALPHA_NUMERIC_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String RFC3986_RESERVED_STR = ":/?#[]@!$&'()*+,;=";
    private static final String RFC3986_NON_ALPHANUMERIC_UNRESERVED_STR = "-._~";
    private static final boolean ENCODED_NON_ALPHA_NUMERIC_UNRESERVED = true;
    private static final String UNENCODED_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Set<Character> UNENCODED_SET;
    
    private static byte[] toUtf8Bytes(final String str) {
        try {
            return str.getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException("The Java spec requires UTF-8 support.", e);
        }
    }
    
    private static StringBuilder appendTwoUpperHex(final StringBuilder sb, int b) {
        if (b < -128 || b > 127) {
            throw new IllegalArgumentException("b is not a byte (was " + b + ')');
        }
        b &= 0xFF;
        if (b < 16) {
            sb.append('0');
        }
        return sb.append(Integer.toHexString(b).toUpperCase());
    }
    
    @Override
    public String encodeCharacter(final char[] immune, final Character c) {
        final String cStr = String.valueOf((char)c);
        if (immune != null && Codec.containsCharacter(c, immune)) {
            return cStr;
        }
        if (PercentCodec.UNENCODED_SET.contains(c)) {
            return cStr;
        }
        final byte[] bytes = toUtf8Bytes(cStr);
        final StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (final byte b : bytes) {
            appendTwoUpperHex(sb.append('%'), b);
        }
        return sb.toString();
    }
    
    @Override
    public Character decodeCharacter(final PushbackString input) {
        input.mark();
        final Character first = input.next();
        if (first == null) {
            input.reset();
            return null;
        }
        if (first != '%') {
            input.reset();
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2; ++i) {
            final Character c = input.nextHex();
            if (c != null) {
                sb.append(c);
            }
        }
        if (sb.length() == 2) {
            try {
                final int i = Integer.parseInt(sb.toString(), 16);
                if (Character.isValidCodePoint(i)) {
                    return (char)i;
                }
            }
            catch (final NumberFormatException ex) {}
        }
        input.reset();
        return null;
    }
    
    static {
        UNENCODED_SET = CollectionsUtil.strToUnmodifiableSet("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    }
}
