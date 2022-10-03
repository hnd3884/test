package com.google.zxing.client.result;

import java.util.List;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import com.google.zxing.Result;
import java.util.regex.Pattern;

public abstract class ResultParser
{
    private static final ResultParser[] PARSERS;
    private static final Pattern DIGITS;
    private static final Pattern ALPHANUM;
    private static final Pattern AMPERSAND;
    private static final Pattern EQUALS;
    
    public abstract ParsedResult parse(final Result p0);
    
    public static ParsedResult parseResult(final Result theResult) {
        for (final ResultParser parser : ResultParser.PARSERS) {
            final ParsedResult result = parser.parse(theResult);
            if (result != null) {
                return result;
            }
        }
        return new TextParsedResult(theResult.getText(), null);
    }
    
    protected static void maybeAppend(final String value, final StringBuilder result) {
        if (value != null) {
            result.append('\n');
            result.append(value);
        }
    }
    
    protected static void maybeAppend(final String[] value, final StringBuilder result) {
        if (value != null) {
            for (final String s : value) {
                result.append('\n');
                result.append(s);
            }
        }
    }
    
    protected static String[] maybeWrap(final String value) {
        return (String[])((value == null) ? null : new String[] { value });
    }
    
    protected static String unescapeBackslash(final String escaped) {
        final int backslash = escaped.indexOf(92);
        if (backslash < 0) {
            return escaped;
        }
        final int max = escaped.length();
        final StringBuilder unescaped = new StringBuilder(max - 1);
        unescaped.append(escaped.toCharArray(), 0, backslash);
        boolean nextIsEscaped = false;
        for (int i = backslash; i < max; ++i) {
            final char c = escaped.charAt(i);
            if (nextIsEscaped || c != '\\') {
                unescaped.append(c);
                nextIsEscaped = false;
            }
            else {
                nextIsEscaped = true;
            }
        }
        return unescaped.toString();
    }
    
    protected static int parseHexDigit(final char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return 10 + (c - 'a');
        }
        if (c >= 'A' && c <= 'F') {
            return 10 + (c - 'A');
        }
        return -1;
    }
    
    protected static boolean isStringOfDigits(final CharSequence value, final int length) {
        return value != null && length == value.length() && ResultParser.DIGITS.matcher(value).matches();
    }
    
    protected static boolean isSubstringOfDigits(final CharSequence value, final int offset, final int length) {
        if (value == null) {
            return false;
        }
        final int max = offset + length;
        return value.length() >= max && ResultParser.DIGITS.matcher(value.subSequence(offset, max)).matches();
    }
    
    protected static boolean isSubstringOfAlphaNumeric(final CharSequence value, final int offset, final int length) {
        if (value == null) {
            return false;
        }
        final int max = offset + length;
        return value.length() >= max && ResultParser.ALPHANUM.matcher(value.subSequence(offset, max)).matches();
    }
    
    static Map<String, String> parseNameValuePairs(final String uri) {
        final int paramStart = uri.indexOf(63);
        if (paramStart < 0) {
            return null;
        }
        final Map<String, String> result = new HashMap<String, String>(3);
        for (final String keyValue : ResultParser.AMPERSAND.split(uri.substring(paramStart + 1))) {
            appendKeyValue(keyValue, result);
        }
        return result;
    }
    
    private static void appendKeyValue(final CharSequence keyValue, final Map<String, String> result) {
        final String[] keyValueTokens = ResultParser.EQUALS.split(keyValue, 2);
        if (keyValueTokens.length == 2) {
            final String key = keyValueTokens[0];
            String value = keyValueTokens[1];
            try {
                value = URLDecoder.decode(value, "UTF-8");
            }
            catch (final UnsupportedEncodingException uee) {
                throw new IllegalStateException(uee);
            }
            result.put(key, value);
        }
    }
    
    static String[] matchPrefixedField(final String prefix, final String rawText, final char endChar, final boolean trim) {
        List<String> matches = null;
        int i = 0;
        final int max = rawText.length();
        while (i < max) {
            i = rawText.indexOf(prefix, i);
            if (i < 0) {
                break;
            }
            final int start;
            i = (start = i + prefix.length());
            boolean more = true;
            while (more) {
                i = rawText.indexOf(endChar, i);
                if (i < 0) {
                    i = rawText.length();
                    more = false;
                }
                else if (rawText.charAt(i - 1) == '\\') {
                    ++i;
                }
                else {
                    if (matches == null) {
                        matches = new ArrayList<String>(3);
                    }
                    String element = unescapeBackslash(rawText.substring(start, i));
                    if (trim) {
                        element = element.trim();
                    }
                    matches.add(element);
                    ++i;
                    more = false;
                }
            }
        }
        if (matches == null || matches.isEmpty()) {
            return null;
        }
        return matches.toArray(new String[matches.size()]);
    }
    
    static String matchSinglePrefixedField(final String prefix, final String rawText, final char endChar, final boolean trim) {
        final String[] matches = matchPrefixedField(prefix, rawText, endChar, trim);
        return (matches == null) ? null : matches[0];
    }
    
    static {
        PARSERS = new ResultParser[] { new BookmarkDoCoMoResultParser(), new AddressBookDoCoMoResultParser(), new EmailDoCoMoResultParser(), new AddressBookAUResultParser(), new VCardResultParser(), new BizcardResultParser(), new VEventResultParser(), new EmailAddressResultParser(), new SMTPResultParser(), new TelResultParser(), new SMSMMSResultParser(), new SMSTOMMSTOResultParser(), new GeoResultParser(), new WifiResultParser(), new URLTOResultParser(), new URIResultParser(), new ISBNResultParser(), new ProductResultParser(), new ExpandedProductResultParser() };
        DIGITS = Pattern.compile("\\d*");
        ALPHANUM = Pattern.compile("[a-zA-Z0-9]*");
        AMPERSAND = Pattern.compile("&");
        EQUALS = Pattern.compile("=");
    }
}
