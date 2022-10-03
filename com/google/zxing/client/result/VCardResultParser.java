package com.google.zxing.client.result;

import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.Collection;
import java.util.List;
import com.google.zxing.Result;
import java.util.regex.Pattern;

public final class VCardResultParser extends ResultParser
{
    private static final Pattern BEGIN_VCARD;
    private static final Pattern VCARD_LIKE_DATE;
    private static final Pattern CR_LF_SPACE_TAB;
    private static final Pattern NEWLINE_ESCAPE;
    private static final Pattern VCARD_ESCAPES;
    private static final Pattern EQUALS;
    private static final Pattern SEMICOLON;
    
    @Override
    public AddressBookParsedResult parse(final Result result) {
        final String rawText = result.getText();
        final Matcher m = VCardResultParser.BEGIN_VCARD.matcher(rawText);
        if (!m.find() || m.start() != 0) {
            return null;
        }
        List<List<String>> names = matchVCardPrefixedField("FN", rawText, true);
        if (names == null) {
            names = matchVCardPrefixedField("N", rawText, true);
            formatNames(names);
        }
        final List<List<String>> phoneNumbers = matchVCardPrefixedField("TEL", rawText, true);
        final List<List<String>> emails = matchVCardPrefixedField("EMAIL", rawText, true);
        final List<String> note = matchSingleVCardPrefixedField("NOTE", rawText, false);
        final List<List<String>> addresses = matchVCardPrefixedField("ADR", rawText, true);
        if (addresses != null) {
            for (final List<String> list : addresses) {
                list.set(0, list.get(0));
            }
        }
        final List<String> org = matchSingleVCardPrefixedField("ORG", rawText, true);
        List<String> birthday = matchSingleVCardPrefixedField("BDAY", rawText, true);
        if (birthday != null && !isLikeVCardDate(birthday.get(0))) {
            birthday = null;
        }
        final List<String> title = matchSingleVCardPrefixedField("TITLE", rawText, true);
        final List<String> url = matchSingleVCardPrefixedField("URL", rawText, true);
        final List<String> instantMessenger = matchSingleVCardPrefixedField("IMPP", rawText, true);
        return new AddressBookParsedResult(toPrimaryValues(names), null, toPrimaryValues(phoneNumbers), toTypes(phoneNumbers), toPrimaryValues(emails), toTypes(emails), toPrimaryValue(instantMessenger), toPrimaryValue(note), toPrimaryValues(addresses), toTypes(addresses), toPrimaryValue(org), toPrimaryValue(birthday), toPrimaryValue(title), toPrimaryValue(url));
    }
    
    private static List<List<String>> matchVCardPrefixedField(final CharSequence prefix, final String rawText, final boolean trim) {
        List<List<String>> matches = null;
        int i = 0;
        final int max = rawText.length();
        while (i < max) {
            final Matcher matcher = Pattern.compile("(?:^|\n)" + (Object)prefix + "(?:;([^:]*))?:", 2).matcher(rawText);
            if (i > 0) {
                --i;
            }
            if (!matcher.find(i)) {
                break;
            }
            i = matcher.end(0);
            final String metadataString = matcher.group(1);
            List<String> metadata = null;
            boolean quotedPrintable = false;
            String quotedPrintableCharset = null;
            if (metadataString != null) {
                for (final String metadatum : VCardResultParser.SEMICOLON.split(metadataString)) {
                    if (metadata == null) {
                        metadata = new ArrayList<String>(1);
                    }
                    metadata.add(metadatum);
                    final String[] metadatumTokens = VCardResultParser.EQUALS.split(metadatum, 2);
                    if (metadatumTokens.length > 1) {
                        final String key = metadatumTokens[0];
                        final String value = metadatumTokens[1];
                        if ("ENCODING".equalsIgnoreCase(key) && "QUOTED-PRINTABLE".equalsIgnoreCase(value)) {
                            quotedPrintable = true;
                        }
                        else if ("CHARSET".equalsIgnoreCase(key)) {
                            quotedPrintableCharset = value;
                        }
                    }
                }
            }
            final int matchStart = i;
            while ((i = rawText.indexOf(10, i)) >= 0) {
                if (i < rawText.length() - 1 && (rawText.charAt(i + 1) == ' ' || rawText.charAt(i + 1) == '\t')) {
                    i += 2;
                }
                else {
                    if (!quotedPrintable || (rawText.charAt(i - 1) != '=' && rawText.charAt(i - 2) != '=')) {
                        break;
                    }
                    ++i;
                }
            }
            if (i < 0) {
                i = max;
            }
            else if (i > matchStart) {
                if (matches == null) {
                    matches = new ArrayList<List<String>>(1);
                }
                if (rawText.charAt(i - 1) == '\r') {
                    --i;
                }
                String element = rawText.substring(matchStart, i);
                if (trim) {
                    element = element.trim();
                }
                if (quotedPrintable) {
                    element = decodeQuotedPrintable(element, quotedPrintableCharset);
                }
                else {
                    element = VCardResultParser.CR_LF_SPACE_TAB.matcher(element).replaceAll("");
                    element = VCardResultParser.NEWLINE_ESCAPE.matcher(element).replaceAll("\n");
                    element = VCardResultParser.VCARD_ESCAPES.matcher(element).replaceAll("$1");
                }
                if (metadata == null) {
                    final List<String> match = new ArrayList<String>(1);
                    match.add(element);
                    matches.add(match);
                }
                else {
                    metadata.add(0, element);
                    matches.add(metadata);
                }
                ++i;
            }
            else {
                ++i;
            }
        }
        return matches;
    }
    
    private static String decodeQuotedPrintable(final CharSequence value, final String charset) {
        final int length = value.length();
        final StringBuilder result = new StringBuilder(length);
        final ByteArrayOutputStream fragmentBuffer = new ByteArrayOutputStream();
        for (int i = 0; i < length; ++i) {
            final char c = value.charAt(i);
            switch (c) {
                case '\n':
                case '\r': {
                    break;
                }
                case '=': {
                    if (i < length - 2) {
                        final char nextChar = value.charAt(i + 1);
                        if (nextChar != '\r') {
                            if (nextChar != '\n') {
                                final char nextNextChar = value.charAt(i + 2);
                                final int firstDigit = ResultParser.parseHexDigit(nextChar);
                                final int secondDigit = ResultParser.parseHexDigit(nextNextChar);
                                if (firstDigit >= 0 && secondDigit >= 0) {
                                    fragmentBuffer.write((firstDigit << 4) + secondDigit);
                                }
                                i += 2;
                            }
                        }
                        break;
                    }
                    break;
                }
                default: {
                    maybeAppendFragment(fragmentBuffer, charset, result);
                    result.append(c);
                    break;
                }
            }
        }
        maybeAppendFragment(fragmentBuffer, charset, result);
        return result.toString();
    }
    
    private static void maybeAppendFragment(final ByteArrayOutputStream fragmentBuffer, final String charset, final StringBuilder result) {
        if (fragmentBuffer.size() > 0) {
            final byte[] fragmentBytes = fragmentBuffer.toByteArray();
            String fragment;
            if (charset == null) {
                fragment = new String(fragmentBytes);
            }
            else {
                try {
                    fragment = new String(fragmentBytes, charset);
                }
                catch (final UnsupportedEncodingException e) {
                    fragment = new String(fragmentBytes);
                }
            }
            fragmentBuffer.reset();
            result.append(fragment);
        }
    }
    
    static List<String> matchSingleVCardPrefixedField(final CharSequence prefix, final String rawText, final boolean trim) {
        final List<List<String>> values = matchVCardPrefixedField(prefix, rawText, trim);
        return (values == null || values.isEmpty()) ? null : values.get(0);
    }
    
    private static String toPrimaryValue(final List<String> list) {
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }
    
    private static String[] toPrimaryValues(final Collection<List<String>> lists) {
        if (lists == null || lists.isEmpty()) {
            return null;
        }
        final List<String> result = new ArrayList<String>(lists.size());
        for (final List<String> list : lists) {
            result.add(list.get(0));
        }
        return result.toArray(new String[lists.size()]);
    }
    
    private static String[] toTypes(final Collection<List<String>> lists) {
        if (lists == null || lists.isEmpty()) {
            return null;
        }
        final List<String> result = new ArrayList<String>(lists.size());
        for (final List<String> list : lists) {
            String type = null;
            for (int i = 1; i < list.size(); ++i) {
                final String metadatum = list.get(i);
                final int equals = metadatum.indexOf(61);
                if (equals < 0) {
                    type = metadatum;
                    break;
                }
                if ("TYPE".equalsIgnoreCase(metadatum.substring(0, equals))) {
                    type = metadatum.substring(equals + 1);
                    break;
                }
            }
            result.add(type);
        }
        return result.toArray(new String[lists.size()]);
    }
    
    private static boolean isLikeVCardDate(final CharSequence value) {
        return value == null || VCardResultParser.VCARD_LIKE_DATE.matcher(value).matches();
    }
    
    private static void formatNames(final Iterable<List<String>> names) {
        if (names != null) {
            for (final List<String> list : names) {
                final String name = list.get(0);
                final String[] components = new String[5];
                int start = 0;
                int componentIndex = 0;
                int end;
                while ((end = name.indexOf(59, start)) > 0) {
                    components[componentIndex] = name.substring(start, end);
                    ++componentIndex;
                    start = end + 1;
                }
                components[componentIndex] = name.substring(start);
                final StringBuilder newName = new StringBuilder(100);
                maybeAppendComponent(components, 3, newName);
                maybeAppendComponent(components, 1, newName);
                maybeAppendComponent(components, 2, newName);
                maybeAppendComponent(components, 0, newName);
                maybeAppendComponent(components, 4, newName);
                list.set(0, newName.toString().trim());
            }
        }
    }
    
    private static void maybeAppendComponent(final String[] components, final int i, final StringBuilder newName) {
        if (components[i] != null) {
            newName.append(' ');
            newName.append(components[i]);
        }
    }
    
    static {
        BEGIN_VCARD = Pattern.compile("BEGIN:VCARD", 2);
        VCARD_LIKE_DATE = Pattern.compile("\\d{4}-?\\d{2}-?\\d{2}");
        CR_LF_SPACE_TAB = Pattern.compile("\r\n[ \t]");
        NEWLINE_ESCAPE = Pattern.compile("\\\\[nN]");
        VCARD_ESCAPES = Pattern.compile("\\\\([,;\\\\])");
        EQUALS = Pattern.compile("=");
        SEMICOLON = Pattern.compile(";");
    }
}
