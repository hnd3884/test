package org.owasp.esapi.codecs;

public class UnixCodec extends Codec
{
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
        return "\\" + c;
    }
    
    @Override
    public Character decodeCharacter(final PushbackString input) {
        input.mark();
        final Character first = input.next();
        if (first == null) {
            input.reset();
            return null;
        }
        if (first != '\\') {
            input.reset();
            return null;
        }
        final Character second = input.next();
        return second;
    }
}
