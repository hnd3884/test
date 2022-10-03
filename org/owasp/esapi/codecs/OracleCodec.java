package org.owasp.esapi.codecs;

public class OracleCodec extends Codec
{
    @Override
    public String encodeCharacter(final char[] immune, final Character c) {
        if (c == '\'') {
            return "''";
        }
        return "" + c;
    }
    
    @Override
    public Character decodeCharacter(final PushbackString input) {
        input.mark();
        final Character first = input.next();
        if (first == null) {
            input.reset();
            return null;
        }
        if (first != '\'') {
            input.reset();
            return null;
        }
        final Character second = input.next();
        if (second == null) {
            input.reset();
            return null;
        }
        if (second != '\'') {
            input.reset();
            return null;
        }
        return '\'';
    }
}
