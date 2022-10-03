package org.apache.lucene.queryparser.flexible.standard.parser;

import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.util.UnescapedCharSequence;
import java.util.Locale;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class EscapeQuerySyntaxImpl implements EscapeQuerySyntax
{
    private static final char[] wildcardChars;
    private static final String[] escapableTermExtraFirstChars;
    private static final String[] escapableTermChars;
    private static final String[] escapableQuotedChars;
    private static final String[] escapableWhiteChars;
    private static final String[] escapableWordTokens;
    
    private static final CharSequence escapeChar(final CharSequence str, final Locale locale) {
        if (str == null || str.length() == 0) {
            return str;
        }
        CharSequence buffer = str;
        for (int i = 0; i < EscapeQuerySyntaxImpl.escapableTermChars.length; ++i) {
            buffer = replaceIgnoreCase(buffer, EscapeQuerySyntaxImpl.escapableTermChars[i].toLowerCase(locale), "\\", locale);
        }
        for (int i = 0; i < EscapeQuerySyntaxImpl.escapableTermExtraFirstChars.length; ++i) {
            if (buffer.charAt(0) == EscapeQuerySyntaxImpl.escapableTermExtraFirstChars[i].charAt(0)) {
                buffer = "\\" + buffer.charAt(0) + (Object)buffer.subSequence(1, buffer.length());
                break;
            }
        }
        return buffer;
    }
    
    private final CharSequence escapeQuoted(final CharSequence str, final Locale locale) {
        if (str == null || str.length() == 0) {
            return str;
        }
        CharSequence buffer = str;
        for (int i = 0; i < EscapeQuerySyntaxImpl.escapableQuotedChars.length; ++i) {
            buffer = replaceIgnoreCase(buffer, EscapeQuerySyntaxImpl.escapableTermChars[i].toLowerCase(locale), "\\", locale);
        }
        return buffer;
    }
    
    private static final CharSequence escapeTerm(CharSequence term, final Locale locale) {
        if (term == null) {
            return term;
        }
        term = escapeChar(term, locale);
        term = escapeWhiteChar(term, locale);
        for (int i = 0; i < EscapeQuerySyntaxImpl.escapableWordTokens.length; ++i) {
            if (EscapeQuerySyntaxImpl.escapableWordTokens[i].equalsIgnoreCase(term.toString())) {
                return "\\" + (Object)term;
            }
        }
        return term;
    }
    
    private static CharSequence replaceIgnoreCase(final CharSequence string, final CharSequence sequence1, final CharSequence escapeChar, final Locale locale) {
        if (escapeChar == null || sequence1 == null || string == null) {
            throw new NullPointerException();
        }
        final int count = string.length();
        final int sequence1Length = sequence1.length();
        if (sequence1Length == 0) {
            final StringBuilder result = new StringBuilder((count + 1) * escapeChar.length());
            result.append(escapeChar);
            for (int i = 0; i < count; ++i) {
                result.append(string.charAt(i));
                result.append(escapeChar);
            }
            return result.toString();
        }
        final StringBuilder result = new StringBuilder();
        final char first = sequence1.charAt(0);
        int start = 0;
        int copyStart = 0;
        while (start < count) {
            final int firstIndex;
            if ((firstIndex = string.toString().toLowerCase(locale).indexOf(first, start)) == -1) {
                break;
            }
            boolean found = true;
            if (sequence1.length() > 1) {
                if (firstIndex + sequence1Length > count) {
                    break;
                }
                for (int j = 1; j < sequence1Length; ++j) {
                    if (string.toString().toLowerCase(locale).charAt(firstIndex + j) != sequence1.charAt(j)) {
                        found = false;
                        break;
                    }
                }
            }
            if (found) {
                result.append(string.toString().substring(copyStart, firstIndex));
                result.append(escapeChar);
                result.append(string.toString().substring(firstIndex, firstIndex + sequence1Length));
                start = (copyStart = firstIndex + sequence1Length);
            }
            else {
                start = firstIndex + 1;
            }
        }
        if (result.length() == 0 && copyStart == 0) {
            return string;
        }
        result.append(string.toString().substring(copyStart));
        return result.toString();
    }
    
    private static final CharSequence escapeWhiteChar(final CharSequence str, final Locale locale) {
        if (str == null || str.length() == 0) {
            return str;
        }
        CharSequence buffer = str;
        for (int i = 0; i < EscapeQuerySyntaxImpl.escapableWhiteChars.length; ++i) {
            buffer = replaceIgnoreCase(buffer, EscapeQuerySyntaxImpl.escapableWhiteChars[i].toLowerCase(locale), "\\", locale);
        }
        return buffer;
    }
    
    @Override
    public CharSequence escape(CharSequence text, final Locale locale, final Type type) {
        if (text == null || text.length() == 0) {
            return text;
        }
        if (text instanceof UnescapedCharSequence) {
            text = ((UnescapedCharSequence)text).toStringEscaped(EscapeQuerySyntaxImpl.wildcardChars);
        }
        else {
            text = new UnescapedCharSequence(text).toStringEscaped(EscapeQuerySyntaxImpl.wildcardChars);
        }
        if (type == Type.STRING) {
            return this.escapeQuoted(text, locale);
        }
        return escapeTerm(text, locale);
    }
    
    public static UnescapedCharSequence discardEscapeChar(final CharSequence input) throws ParseException {
        final char[] output = new char[input.length()];
        final boolean[] wasEscaped = new boolean[input.length()];
        int length = 0;
        boolean lastCharWasEscapeChar = false;
        int codePointMultiplier = 0;
        int codePoint = 0;
        for (int i = 0; i < input.length(); ++i) {
            final char curChar = input.charAt(i);
            if (codePointMultiplier > 0) {
                codePoint += hexToInt(curChar) * codePointMultiplier;
                codePointMultiplier >>>= 4;
                if (codePointMultiplier == 0) {
                    output[length++] = (char)codePoint;
                    codePoint = 0;
                }
            }
            else if (lastCharWasEscapeChar) {
                if (curChar == 'u') {
                    codePointMultiplier = 4096;
                }
                else {
                    output[length] = curChar;
                    wasEscaped[length] = true;
                    ++length;
                }
                lastCharWasEscapeChar = false;
            }
            else if (curChar == '\\') {
                lastCharWasEscapeChar = true;
            }
            else {
                output[length] = curChar;
                ++length;
            }
        }
        if (codePointMultiplier > 0) {
            throw new ParseException(new MessageImpl(QueryParserMessages.INVALID_SYNTAX_ESCAPE_UNICODE_TRUNCATION));
        }
        if (lastCharWasEscapeChar) {
            throw new ParseException(new MessageImpl(QueryParserMessages.INVALID_SYNTAX_ESCAPE_CHARACTER));
        }
        return new UnescapedCharSequence(output, wasEscaped, 0, length);
    }
    
    private static final int hexToInt(final char c) throws ParseException {
        if ('0' <= c && c <= '9') {
            return c - '0';
        }
        if ('a' <= c && c <= 'f') {
            return c - 'a' + 10;
        }
        if ('A' <= c && c <= 'F') {
            return c - 'A' + 10;
        }
        throw new ParseException(new MessageImpl(QueryParserMessages.INVALID_SYNTAX_ESCAPE_NONE_HEX_UNICODE, new Object[] { c }));
    }
    
    static {
        wildcardChars = new char[] { '*', '?' };
        escapableTermExtraFirstChars = new String[] { "+", "-", "@" };
        escapableTermChars = new String[] { "\"", "<", ">", "=", "!", "(", ")", "^", "[", "{", ":", "]", "}", "~", "/" };
        escapableQuotedChars = new String[] { "\"" };
        escapableWhiteChars = new String[] { " ", "\t", "\n", "\r", "\f", "\b", "\u3000" };
        escapableWordTokens = new String[] { "AND", "OR", "NOT", "TO", "WITHIN", "SENTENCE", "PARAGRAPH", "INORDER" };
    }
}
