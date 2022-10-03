package org.owasp.esapi.codecs;

public class JavaScriptCodec extends Codec
{
    @Override
    public String encodeCharacter(final char[] immune, final Character c) {
        if (Codec.containsCharacter(c, immune)) {
            return "" + c;
        }
        final String hex = Codec.getHexForNonAlphanumeric(c);
        if (hex == null) {
            return "" + c;
        }
        final String temp = Integer.toHexString(c);
        if (c < '\u0100') {
            final String pad = "00".substring(temp.length());
            return "\\x" + pad + temp.toUpperCase();
        }
        final String pad = "0000".substring(temp.length());
        return "\\u" + pad + temp.toUpperCase();
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
        if (second == null) {
            input.reset();
            return null;
        }
        if (second == 'b') {
            return '\b';
        }
        if (second == 't') {
            return '\t';
        }
        if (second == 'n') {
            return '\n';
        }
        if (second == 'v') {
            return '\u000b';
        }
        if (second == 'f') {
            return '\f';
        }
        if (second == 'r') {
            return '\r';
        }
        if (second == '\"') {
            return '\"';
        }
        if (second == '\'') {
            return '\'';
        }
        if (second == '\\') {
            return '\\';
        }
        if (Character.toLowerCase(second) == 'x') {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 2; ++i) {
                final Character c = input.nextHex();
                if (c == null) {
                    input.reset();
                    return null;
                }
                sb.append(c);
            }
            try {
                final int i = Integer.parseInt(sb.toString(), 16);
                if (Character.isValidCodePoint(i)) {
                    return (char)i;
                }
            }
            catch (final NumberFormatException e) {
                input.reset();
                return null;
            }
        }
        else if (Character.toLowerCase(second) == 'u') {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4; ++i) {
                final Character c = input.nextHex();
                if (c == null) {
                    input.reset();
                    return null;
                }
                sb.append(c);
            }
            try {
                final int i = Integer.parseInt(sb.toString(), 16);
                if (Character.isValidCodePoint(i)) {
                    return (char)i;
                }
            }
            catch (final NumberFormatException e) {
                input.reset();
                return null;
            }
        }
        else if (PushbackString.isOctalDigit(second)) {
            final StringBuilder sb = new StringBuilder();
            sb.append(second);
            final Character c2 = input.next();
            if (!PushbackString.isOctalDigit(c2)) {
                input.pushback(c2);
            }
            else {
                sb.append(c2);
                final Character c3 = input.next();
                if (!PushbackString.isOctalDigit(c3)) {
                    input.pushback(c3);
                }
                else {
                    sb.append(c3);
                }
            }
            try {
                final int j = Integer.parseInt(sb.toString(), 8);
                if (Character.isValidCodePoint(j)) {
                    return (char)j;
                }
            }
            catch (final NumberFormatException e2) {
                input.reset();
                return null;
            }
        }
        return second;
    }
}
