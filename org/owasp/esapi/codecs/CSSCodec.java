package org.owasp.esapi.codecs;

public class CSSCodec extends Codec
{
    private static final Character REPLACEMENT;
    
    @Override
    public String encodeCharacter(final char[] immune, final Character c) {
        if (Codec.containsCharacter(c, immune)) {
            return "" + c;
        }
        final String hex = Codec.getHexForNonAlphanumeric(c);
        if (hex == null) {
            return "" + c;
        }
        return "\\" + hex + " ";
    }
    
    @Override
    public Character decodeCharacter(final PushbackString input) {
        input.mark();
        final Character first = input.next();
        if (first == null || first != '\\') {
            input.reset();
            return null;
        }
        final Character second = input.next();
        if (second == null) {
            input.reset();
            return null;
        }
        switch ((char)second) {
            case '\r': {
                if (input.peek('\n')) {
                    input.next();
                    return this.decodeCharacter(input);
                }
                return this.decodeCharacter(input);
            }
            case '\0':
            case '\n':
            case '\f': {
                return this.decodeCharacter(input);
            }
            default: {
                if (!PushbackString.isHexDigit(second)) {
                    return second;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append(second);
                for (int i = 0; i < 5; ++i) {
                    final Character c = input.next();
                    if (c == null) {
                        break;
                    }
                    if (Character.isWhitespace(c)) {
                        break;
                    }
                    if (!PushbackString.isHexDigit(c)) {
                        input.pushback(c);
                        break;
                    }
                    sb.append(c);
                }
                try {
                    final int i = Integer.parseInt(sb.toString(), 16);
                    if (Character.isValidCodePoint(i)) {
                        return (char)i;
                    }
                    return CSSCodec.REPLACEMENT;
                }
                catch (final NumberFormatException e) {
                    throw new IllegalStateException("Received a NumberFormateException parsing a string verified to be hex", e);
                }
                break;
            }
        }
    }
    
    static {
        REPLACEMENT = '\ufffd';
    }
}
