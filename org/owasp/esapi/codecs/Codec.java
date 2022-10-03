package org.owasp.esapi.codecs;

public abstract class Codec
{
    private static final String[] hex;
    
    public String encode(final char[] immune, final String input) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            final char c = input.charAt(i);
            sb.append(this.encodeCharacter(immune, c));
        }
        return sb.toString();
    }
    
    public String encodeCharacter(final char[] immune, final Character c) {
        return "" + c;
    }
    
    public String decode(final String input) {
        final StringBuilder sb = new StringBuilder();
        final PushbackString pbs = new PushbackString(input);
        while (pbs.hasNext()) {
            final Character c = this.decodeCharacter(pbs);
            if (c != null) {
                sb.append(c);
            }
            else {
                sb.append(pbs.next());
            }
        }
        return sb.toString();
    }
    
    public Character decodeCharacter(final PushbackString input) {
        return input.next();
    }
    
    public static String getHexForNonAlphanumeric(final char c) {
        if (c < '\u00ff') {
            return Codec.hex[c];
        }
        return toHex(c);
    }
    
    public static String toOctal(final char c) {
        return Integer.toOctalString(c);
    }
    
    public static String toHex(final char c) {
        return Integer.toHexString(c);
    }
    
    public static boolean containsCharacter(final char c, final char[] array) {
        for (final char ch : array) {
            if (c == ch) {
                return true;
            }
        }
        return false;
    }
    
    static {
        hex = new String[256];
        for (char c = '\0'; c < '\u00ff'; ++c) {
            if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                Codec.hex[c] = null;
            }
            else {
                Codec.hex[c] = toHex(c).intern();
            }
        }
    }
}
