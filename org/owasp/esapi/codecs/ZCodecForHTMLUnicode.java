package org.owasp.esapi.codecs;

public class ZCodecForHTMLUnicode extends Codec
{
    @Override
    public String encode(final char[] immune, final String input) {
        final StringBuilder sb = new StringBuilder();
        int codePoint;
        for (int i = 0; i < input.length(); i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            sb.append(this.encodeCharacter(immune, codePoint));
        }
        return sb.toString();
    }
    
    public String encodeCharacter(final char[] immune, final int c) {
        return "" + c;
    }
    
    public static String getHexForNonAlphanumeric(final char c, final int codePoint) {
        if (c < '\u00ff') {
            return Codec.getHexForNonAlphanumeric(c);
        }
        return Integer.toHexString(codePoint);
    }
}
