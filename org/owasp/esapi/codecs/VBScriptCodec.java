package org.owasp.esapi.codecs;

import org.owasp.esapi.EncoderConstants;

public class VBScriptCodec extends Codec
{
    @Override
    public String encode(final char[] immune, final String input) {
        final StringBuilder sb = new StringBuilder();
        boolean encoding = false;
        boolean inquotes = false;
        for (int i = 0; i < input.length(); ++i) {
            final char c = input.charAt(i);
            if (Codec.containsCharacter(c, EncoderConstants.CHAR_ALPHANUMERICS) || Codec.containsCharacter(c, immune)) {
                if (encoding && i > 0) {
                    sb.append("&");
                }
                if (!inquotes && i > 0) {
                    sb.append("\"");
                }
                sb.append(c);
                inquotes = true;
                encoding = false;
            }
            else {
                if (inquotes && i < input.length()) {
                    sb.append("\"");
                }
                if (i > 0) {
                    sb.append("&");
                }
                sb.append(this.encodeCharacter(immune, c));
                inquotes = false;
                encoding = true;
            }
        }
        return sb.toString();
    }
    
    @Override
    public String encodeCharacter(final char[] immune, final Character c) {
        final char ch = c;
        if (Codec.containsCharacter(ch, immune)) {
            return "" + ch;
        }
        final String hex = Codec.getHexForNonAlphanumeric(ch);
        if (hex == null) {
            return "" + ch;
        }
        return "chrw(" + (int)c + ")";
    }
    
    @Override
    public Character decodeCharacter(final PushbackString input) {
        input.mark();
        final Character first = input.next();
        if (first == null) {
            input.reset();
            return null;
        }
        if (first != '\"') {
            input.reset();
            return null;
        }
        final Character second = input.next();
        return second;
    }
}
