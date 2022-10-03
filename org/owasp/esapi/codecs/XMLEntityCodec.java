package org.owasp.esapi.codecs;

import org.owasp.esapi.util.CollectionsUtil;
import java.util.Map;
import java.util.Set;

public class XMLEntityCodec extends Codec
{
    private static final String ALPHA_NUMERIC_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String UNENCODED_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 \t";
    private static final Set<Character> UNENCODED_SET;
    private static final HashTrie<Character> entityToCharacterMap;
    
    @Override
    public String encodeCharacter(final char[] immune, final Character c) {
        if (Codec.containsCharacter(c, immune)) {
            return c.toString();
        }
        if (XMLEntityCodec.UNENCODED_SET.contains(c)) {
            return c.toString();
        }
        return "&#x" + Integer.toHexString(c) + ";";
    }
    
    @Override
    public Character decodeCharacter(final PushbackString input) {
        Character ret = null;
        input.mark();
        try {
            final Character first = input.next();
            if (first == null) {
                return null;
            }
            if (first != '&') {
                return null;
            }
            final Character second = input.next();
            if (second == null) {
                return null;
            }
            if (second == '#') {
                ret = getNumericEntity(input);
            }
            else if (Character.isLetter(second)) {
                input.pushback(second);
                ret = this.getNamedEntity(input);
            }
        }
        finally {
            if (ret == null) {
                input.reset();
            }
        }
        return ret;
    }
    
    private static Character getNumericEntity(final PushbackString input) {
        final Character first = input.peek();
        if (first == null) {
            return null;
        }
        if (first == 'x' || first == 'X') {
            input.next();
            return parseHex(input);
        }
        return parseNumber(input);
    }
    
    private static Character int2char(final int i) {
        if (!Character.isValidCodePoint(i)) {
            return null;
        }
        if (0 > i || i > 65535) {
            return null;
        }
        return (char)i;
    }
    
    private static Character parseNumber(final PushbackString input) {
        final StringBuilder sb = new StringBuilder();
        Character c;
        while ((c = input.next()) != null && c != ';') {
            if (!Character.isDigit(c)) {
                return null;
            }
            sb.append(c);
        }
        if (c == null) {
            return null;
        }
        if (sb.length() <= 0) {
            return null;
        }
        try {
            return int2char(Integer.parseInt(sb.toString()));
        }
        catch (final NumberFormatException e) {
            return null;
        }
    }
    
    private static Character parseHex(final PushbackString input) {
        final StringBuilder sb = new StringBuilder();
        Character c = null;
    Label_0270:
        while ((c = input.next()) != null) {
            switch ((char)c) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f': {
                    sb.append(c);
                    continue;
                }
                case ';': {
                    break Label_0270;
                }
                default: {
                    return null;
                }
            }
        }
        if (c == null) {
            return null;
        }
        if (sb.length() <= 0) {
            return null;
        }
        try {
            return int2char(Integer.parseInt(sb.toString(), 16));
        }
        catch (final NumberFormatException e) {
            return null;
        }
    }
    
    private Character getNamedEntity(final PushbackString input) {
        final StringBuilder possible = new StringBuilder();
        for (int len = Math.min(input.remainder().length(), XMLEntityCodec.entityToCharacterMap.getMaxKeyLength() + 1), i = 0; i < len; ++i) {
            possible.append(Character.toLowerCase(input.next()));
        }
        final Map.Entry<CharSequence, Character> entry = XMLEntityCodec.entityToCharacterMap.getLongestMatch(possible);
        if (entry == null) {
            return null;
        }
        final int len = entry.getKey().length();
        if (possible.length() <= len || possible.charAt(len) != ';') {
            return null;
        }
        input.reset();
        input.next();
        for (int i = 0; i < len; ++i) {
            input.next();
        }
        input.next();
        return entry.getValue();
    }
    
    static {
        UNENCODED_SET = CollectionsUtil.strToUnmodifiableSet("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 \t");
        (entityToCharacterMap = new HashTrie<Character>()).put("lt", '<');
        XMLEntityCodec.entityToCharacterMap.put((CharSequence)"gt", Character.valueOf('>'));
        XMLEntityCodec.entityToCharacterMap.put((CharSequence)"amp", Character.valueOf('&'));
        XMLEntityCodec.entityToCharacterMap.put((CharSequence)"apos", Character.valueOf('\''));
        XMLEntityCodec.entityToCharacterMap.put((CharSequence)"quot", Character.valueOf('\"'));
    }
}
