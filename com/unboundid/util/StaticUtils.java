package com.unboundid.util;

import java.util.Hashtable;
import java.util.Enumeration;
import java.net.NetworkInterface;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import java.net.InetAddress;
import com.unboundid.ldap.sdk.NameResolver;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import java.io.File;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import com.unboundid.ldap.sdk.Attribute;
import java.util.HashSet;
import java.lang.reflect.Array;
import java.util.UUID;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.ParseException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import com.unboundid.ldap.sdk.Control;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class StaticUtils
{
    public static final byte[] NO_BYTES;
    public static final char[] NO_CHARS;
    public static final Control[] NO_CONTROLS;
    public static final String[] NO_STRINGS;
    public static final String EOL;
    public static final byte[] EOL_BYTES;
    private static final boolean IS_WITHIN_UNIT_TESTS;
    public static final int TERMINAL_WIDTH_COLUMNS;
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTERS;
    private static final TimeZone UTC_TIME_ZONE;
    private static volatile Set<String> TO_CODE_SENSITIVE_ATTRIBUTE_NAMES;
    
    private StaticUtils() {
    }
    
    public static byte[] getBytes(final String s) {
        final int length;
        if (s == null || (length = s.length()) == 0) {
            return StaticUtils.NO_BYTES;
        }
        final byte[] b = new byte[length];
        for (int i = 0; i < length; ++i) {
            final char c = s.charAt(i);
            if (c > '\u007f') {
                return s.getBytes(StandardCharsets.UTF_8);
            }
            b[i] = (byte)(c & '\u007f');
        }
        return b;
    }
    
    public static boolean isASCIIString(final byte[] b) {
        for (final byte by : b) {
            if ((by & 0x80) == 0x80) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isPrintable(final char c) {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
            return true;
        }
        switch (c) {
            case ' ':
            case '\'':
            case '(':
            case ')':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case ':':
            case '=':
            case '?': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isPrintableString(final byte[] b) {
        for (final byte by : b) {
            if ((by & 0x80) == 0x80) {
                return false;
            }
            if ((by < 97 || by > 122) && (by < 65 || by > 90)) {
                if (by < 48 || by > 57) {
                    switch (by) {
                        case 32:
                        case 39:
                        case 40:
                        case 41:
                        case 43:
                        case 44:
                        case 45:
                        case 46:
                        case 47:
                        case 58:
                        case 61:
                        case 63: {
                            break;
                        }
                        default: {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public static boolean isValidUTF8(final byte[] b) {
        int i = 0;
        while (i < b.length) {
            final byte currentByte = b[i++];
            if ((currentByte & 0x80) == 0x0) {
                continue;
            }
            if ((currentByte & 0xE0) == 0xC0) {
                if (!hasExpectedSubsequentUTF8Bytes(b, i, 1)) {
                    return false;
                }
                ++i;
            }
            else if ((currentByte & 0xF0) == 0xE0) {
                if (!hasExpectedSubsequentUTF8Bytes(b, i, 2)) {
                    return false;
                }
                i += 2;
            }
            else if ((currentByte & 0xF8) == 0xF0) {
                if (!hasExpectedSubsequentUTF8Bytes(b, i, 3)) {
                    return false;
                }
                i += 3;
            }
            else if ((currentByte & 0xFC) == 0xF8) {
                if (!hasExpectedSubsequentUTF8Bytes(b, i, 4)) {
                    return false;
                }
                i += 4;
            }
            else {
                if ((currentByte & 0xFE) != 0xFC) {
                    return false;
                }
                if (!hasExpectedSubsequentUTF8Bytes(b, i, 5)) {
                    return false;
                }
                i += 5;
            }
        }
        return true;
    }
    
    private static boolean hasExpectedSubsequentUTF8Bytes(final byte[] b, final int p, final int n) {
        if (b.length < p + n) {
            return false;
        }
        for (int i = 0; i < n; ++i) {
            if ((b[p + i] & 0xC0) != 0x80) {
                return false;
            }
        }
        return true;
    }
    
    public static String toUTF8String(final byte[] b) {
        try {
            return new String(b, StandardCharsets.UTF_8);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return new String(b);
        }
    }
    
    public static String toUTF8String(final byte[] b, final int offset, final int length) {
        try {
            return new String(b, offset, length, StandardCharsets.UTF_8);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return new String(b, offset, length);
        }
    }
    
    public static String toInitialLowerCase(final String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        if (s.length() == 1) {
            return toLowerCase(s);
        }
        final char c = s.charAt(0);
        if ((c >= 'A' && c <= 'Z') || c < ' ' || c > '~') {
            final StringBuilder b = new StringBuilder(s);
            b.setCharAt(0, Character.toLowerCase(c));
            return b.toString();
        }
        return s;
    }
    
    public static String toLowerCase(final String s) {
        if (s == null) {
            return null;
        }
        final int length = s.length();
        final char[] charArray = s.toCharArray();
        for (int i = 0; i < length; ++i) {
            switch (charArray[i]) {
                case 'A': {
                    charArray[i] = 'a';
                    break;
                }
                case 'B': {
                    charArray[i] = 'b';
                    break;
                }
                case 'C': {
                    charArray[i] = 'c';
                    break;
                }
                case 'D': {
                    charArray[i] = 'd';
                    break;
                }
                case 'E': {
                    charArray[i] = 'e';
                    break;
                }
                case 'F': {
                    charArray[i] = 'f';
                    break;
                }
                case 'G': {
                    charArray[i] = 'g';
                    break;
                }
                case 'H': {
                    charArray[i] = 'h';
                    break;
                }
                case 'I': {
                    charArray[i] = 'i';
                    break;
                }
                case 'J': {
                    charArray[i] = 'j';
                    break;
                }
                case 'K': {
                    charArray[i] = 'k';
                    break;
                }
                case 'L': {
                    charArray[i] = 'l';
                    break;
                }
                case 'M': {
                    charArray[i] = 'm';
                    break;
                }
                case 'N': {
                    charArray[i] = 'n';
                    break;
                }
                case 'O': {
                    charArray[i] = 'o';
                    break;
                }
                case 'P': {
                    charArray[i] = 'p';
                    break;
                }
                case 'Q': {
                    charArray[i] = 'q';
                    break;
                }
                case 'R': {
                    charArray[i] = 'r';
                    break;
                }
                case 'S': {
                    charArray[i] = 's';
                    break;
                }
                case 'T': {
                    charArray[i] = 't';
                    break;
                }
                case 'U': {
                    charArray[i] = 'u';
                    break;
                }
                case 'V': {
                    charArray[i] = 'v';
                    break;
                }
                case 'W': {
                    charArray[i] = 'w';
                    break;
                }
                case 'X': {
                    charArray[i] = 'x';
                    break;
                }
                case 'Y': {
                    charArray[i] = 'y';
                    break;
                }
                case 'Z': {
                    charArray[i] = 'z';
                    break;
                }
                default: {
                    if (charArray[i] > '\u007f') {
                        return s.toLowerCase();
                    }
                    break;
                }
            }
        }
        return new String(charArray);
    }
    
    public static String toUpperCase(final String s) {
        if (s == null) {
            return null;
        }
        final int length = s.length();
        final char[] charArray = s.toCharArray();
        for (int i = 0; i < length; ++i) {
            switch (charArray[i]) {
                case 'a': {
                    charArray[i] = 'A';
                    break;
                }
                case 'b': {
                    charArray[i] = 'B';
                    break;
                }
                case 'c': {
                    charArray[i] = 'C';
                    break;
                }
                case 'd': {
                    charArray[i] = 'D';
                    break;
                }
                case 'e': {
                    charArray[i] = 'E';
                    break;
                }
                case 'f': {
                    charArray[i] = 'F';
                    break;
                }
                case 'g': {
                    charArray[i] = 'G';
                    break;
                }
                case 'h': {
                    charArray[i] = 'H';
                    break;
                }
                case 'i': {
                    charArray[i] = 'I';
                    break;
                }
                case 'j': {
                    charArray[i] = 'J';
                    break;
                }
                case 'k': {
                    charArray[i] = 'K';
                    break;
                }
                case 'l': {
                    charArray[i] = 'L';
                    break;
                }
                case 'm': {
                    charArray[i] = 'M';
                    break;
                }
                case 'n': {
                    charArray[i] = 'N';
                    break;
                }
                case 'o': {
                    charArray[i] = 'O';
                    break;
                }
                case 'p': {
                    charArray[i] = 'P';
                    break;
                }
                case 'q': {
                    charArray[i] = 'Q';
                    break;
                }
                case 'r': {
                    charArray[i] = 'R';
                    break;
                }
                case 's': {
                    charArray[i] = 'S';
                    break;
                }
                case 't': {
                    charArray[i] = 'T';
                    break;
                }
                case 'u': {
                    charArray[i] = 'U';
                    break;
                }
                case 'v': {
                    charArray[i] = 'V';
                    break;
                }
                case 'w': {
                    charArray[i] = 'W';
                    break;
                }
                case 'x': {
                    charArray[i] = 'X';
                    break;
                }
                case 'y': {
                    charArray[i] = 'Y';
                    break;
                }
                case 'z': {
                    charArray[i] = 'Z';
                    break;
                }
                default: {
                    if (charArray[i] > '\u007f') {
                        return s.toUpperCase();
                    }
                    break;
                }
            }
        }
        return new String(charArray);
    }
    
    public static boolean isHex(final char c) {
        switch (c) {
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
            case 'F':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static String toHex(final byte b) {
        final StringBuilder buffer = new StringBuilder(2);
        toHex(b, buffer);
        return buffer.toString();
    }
    
    public static void toHex(final byte b, final StringBuilder buffer) {
        switch (b & 0xF0) {
            case 0: {
                buffer.append('0');
                break;
            }
            case 16: {
                buffer.append('1');
                break;
            }
            case 32: {
                buffer.append('2');
                break;
            }
            case 48: {
                buffer.append('3');
                break;
            }
            case 64: {
                buffer.append('4');
                break;
            }
            case 80: {
                buffer.append('5');
                break;
            }
            case 96: {
                buffer.append('6');
                break;
            }
            case 112: {
                buffer.append('7');
                break;
            }
            case 128: {
                buffer.append('8');
                break;
            }
            case 144: {
                buffer.append('9');
                break;
            }
            case 160: {
                buffer.append('a');
                break;
            }
            case 176: {
                buffer.append('b');
                break;
            }
            case 192: {
                buffer.append('c');
                break;
            }
            case 208: {
                buffer.append('d');
                break;
            }
            case 224: {
                buffer.append('e');
                break;
            }
            case 240: {
                buffer.append('f');
                break;
            }
        }
        switch (b & 0xF) {
            case 0: {
                buffer.append('0');
                break;
            }
            case 1: {
                buffer.append('1');
                break;
            }
            case 2: {
                buffer.append('2');
                break;
            }
            case 3: {
                buffer.append('3');
                break;
            }
            case 4: {
                buffer.append('4');
                break;
            }
            case 5: {
                buffer.append('5');
                break;
            }
            case 6: {
                buffer.append('6');
                break;
            }
            case 7: {
                buffer.append('7');
                break;
            }
            case 8: {
                buffer.append('8');
                break;
            }
            case 9: {
                buffer.append('9');
                break;
            }
            case 10: {
                buffer.append('a');
                break;
            }
            case 11: {
                buffer.append('b');
                break;
            }
            case 12: {
                buffer.append('c');
                break;
            }
            case 13: {
                buffer.append('d');
                break;
            }
            case 14: {
                buffer.append('e');
                break;
            }
            case 15: {
                buffer.append('f');
                break;
            }
        }
    }
    
    public static String toHex(final byte[] b) {
        Validator.ensureNotNull(b);
        final StringBuilder buffer = new StringBuilder(2 * b.length);
        toHex(b, buffer);
        return buffer.toString();
    }
    
    public static void toHex(final byte[] b, final StringBuilder buffer) {
        toHex(b, null, buffer);
    }
    
    public static void toHex(final byte[] b, final String delimiter, final StringBuilder buffer) {
        boolean first = true;
        for (final byte bt : b) {
            if (first) {
                first = false;
            }
            else if (delimiter != null) {
                buffer.append(delimiter);
            }
            toHex(bt, buffer);
        }
    }
    
    public static String toHexPlusASCII(final byte[] array, final int indent) {
        final StringBuilder buffer = new StringBuilder();
        toHexPlusASCII(array, indent, buffer);
        return buffer.toString();
    }
    
    public static void toHexPlusASCII(final byte[] array, final int indent, final StringBuilder buffer) {
        if (array == null || array.length == 0) {
            return;
        }
        for (int i = 0; i < indent; ++i) {
            buffer.append(' ');
        }
        int pos = 0;
        int startPos = 0;
        while (pos < array.length) {
            toHex(array[pos++], buffer);
            buffer.append(' ');
            if (pos % 16 == 0) {
                buffer.append("  ");
                for (int j = startPos; j < pos; ++j) {
                    if (array[j] < 32 || array[j] > 126) {
                        buffer.append(' ');
                    }
                    else {
                        buffer.append((char)array[j]);
                    }
                }
                buffer.append(StaticUtils.EOL);
                if ((startPos = pos) >= array.length) {
                    continue;
                }
                for (int j = 0; j < indent; ++j) {
                    buffer.append(' ');
                }
            }
        }
        if (array.length % 16 != 0) {
            final int missingBytes = 16 - array.length % 16;
            if (missingBytes > 0) {
                for (int k = 0; k < missingBytes; ++k) {
                    buffer.append("   ");
                }
                buffer.append("  ");
                for (int k = startPos; k < array.length; ++k) {
                    if (array[k] < 32 || array[k] > 126) {
                        buffer.append(' ');
                    }
                    else {
                        buffer.append((char)array[k]);
                    }
                }
                buffer.append(StaticUtils.EOL);
            }
        }
    }
    
    public static byte[] fromHex(final String hexString) throws ParseException {
        if (hexString.length() % 2 != 0) {
            throw new ParseException(UtilityMessages.ERR_FROM_HEX_ODD_NUMBER_OF_CHARACTERS.get(hexString.length()), hexString.length());
        }
        final byte[] decodedBytes = new byte[hexString.length() / 2];
        for (int i = 0, j = 0; i < decodedBytes.length; ++i, j += 2) {
            switch (hexString.charAt(j)) {
                case '0': {
                    break;
                }
                case '1': {
                    decodedBytes[i] = 16;
                    break;
                }
                case '2': {
                    decodedBytes[i] = 32;
                    break;
                }
                case '3': {
                    decodedBytes[i] = 48;
                    break;
                }
                case '4': {
                    decodedBytes[i] = 64;
                    break;
                }
                case '5': {
                    decodedBytes[i] = 80;
                    break;
                }
                case '6': {
                    decodedBytes[i] = 96;
                    break;
                }
                case '7': {
                    decodedBytes[i] = 112;
                    break;
                }
                case '8': {
                    decodedBytes[i] = -128;
                    break;
                }
                case '9': {
                    decodedBytes[i] = -112;
                    break;
                }
                case 'A':
                case 'a': {
                    decodedBytes[i] = -96;
                    break;
                }
                case 'B':
                case 'b': {
                    decodedBytes[i] = -80;
                    break;
                }
                case 'C':
                case 'c': {
                    decodedBytes[i] = -64;
                    break;
                }
                case 'D':
                case 'd': {
                    decodedBytes[i] = -48;
                    break;
                }
                case 'E':
                case 'e': {
                    decodedBytes[i] = -32;
                    break;
                }
                case 'F':
                case 'f': {
                    decodedBytes[i] = -16;
                    break;
                }
                default: {
                    throw new ParseException(UtilityMessages.ERR_FROM_HEX_NON_HEX_CHARACTER.get(j), j);
                }
            }
            switch (hexString.charAt(j + 1)) {
                case '0': {
                    break;
                }
                case '1': {
                    final byte[] array = decodedBytes;
                    final int n = i;
                    array[n] |= 0x1;
                    break;
                }
                case '2': {
                    final byte[] array2 = decodedBytes;
                    final int n2 = i;
                    array2[n2] |= 0x2;
                    break;
                }
                case '3': {
                    final byte[] array3 = decodedBytes;
                    final int n3 = i;
                    array3[n3] |= 0x3;
                    break;
                }
                case '4': {
                    final byte[] array4 = decodedBytes;
                    final int n4 = i;
                    array4[n4] |= 0x4;
                    break;
                }
                case '5': {
                    final byte[] array5 = decodedBytes;
                    final int n5 = i;
                    array5[n5] |= 0x5;
                    break;
                }
                case '6': {
                    final byte[] array6 = decodedBytes;
                    final int n6 = i;
                    array6[n6] |= 0x6;
                    break;
                }
                case '7': {
                    final byte[] array7 = decodedBytes;
                    final int n7 = i;
                    array7[n7] |= 0x7;
                    break;
                }
                case '8': {
                    final byte[] array8 = decodedBytes;
                    final int n8 = i;
                    array8[n8] |= 0x8;
                    break;
                }
                case '9': {
                    final byte[] array9 = decodedBytes;
                    final int n9 = i;
                    array9[n9] |= 0x9;
                    break;
                }
                case 'A':
                case 'a': {
                    final byte[] array10 = decodedBytes;
                    final int n10 = i;
                    array10[n10] |= 0xA;
                    break;
                }
                case 'B':
                case 'b': {
                    final byte[] array11 = decodedBytes;
                    final int n11 = i;
                    array11[n11] |= 0xB;
                    break;
                }
                case 'C':
                case 'c': {
                    final byte[] array12 = decodedBytes;
                    final int n12 = i;
                    array12[n12] |= 0xC;
                    break;
                }
                case 'D':
                case 'd': {
                    final byte[] array13 = decodedBytes;
                    final int n13 = i;
                    array13[n13] |= 0xD;
                    break;
                }
                case 'E':
                case 'e': {
                    final byte[] array14 = decodedBytes;
                    final int n14 = i;
                    array14[n14] |= 0xE;
                    break;
                }
                case 'F':
                case 'f': {
                    final byte[] array15 = decodedBytes;
                    final int n15 = i;
                    array15[n15] |= 0xF;
                    break;
                }
                default: {
                    throw new ParseException(UtilityMessages.ERR_FROM_HEX_NON_HEX_CHARACTER.get(j + 1), j + 1);
                }
            }
        }
        return decodedBytes;
    }
    
    public static void hexEncode(final char c, final StringBuilder buffer) {
        byte[] charBytes;
        if (c <= '\u007f') {
            charBytes = new byte[] { (byte)(c & '\u007f') };
        }
        else {
            charBytes = getBytes(String.valueOf(c));
        }
        for (final byte b : charBytes) {
            buffer.append('\\');
            toHex(b, buffer);
        }
    }
    
    public static void hexEncode(final int codePoint, final StringBuilder buffer) {
        final byte[] arr$;
        final byte[] charBytes = arr$ = getBytes(new String(new int[] { codePoint }, 0, 1));
        for (final byte b : arr$) {
            buffer.append('\\');
            toHex(b, buffer);
        }
    }
    
    public static void byteArrayToCode(final byte[] array, final StringBuilder buffer) {
        buffer.append("new byte[] {");
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                buffer.append(',');
            }
            buffer.append(" (byte) 0x");
            toHex(array[i], buffer);
        }
        buffer.append(" }");
    }
    
    public static String getStackTrace(final Throwable t) {
        final StringBuilder buffer = new StringBuilder();
        getStackTrace(t, buffer);
        return buffer.toString();
    }
    
    public static void getStackTrace(final Throwable t, final StringBuilder buffer) {
        buffer.append(getUnqualifiedClassName(t.getClass()));
        buffer.append('(');
        final String message = t.getMessage();
        if (message != null) {
            buffer.append("message='");
            buffer.append(message);
            buffer.append("', ");
        }
        buffer.append("trace='");
        getStackTrace(t.getStackTrace(), buffer);
        buffer.append('\'');
        final Throwable cause = t.getCause();
        if (cause != null) {
            buffer.append(", cause=");
            getStackTrace(cause, buffer);
        }
        final String ldapSDKVersionString = ", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb";
        if (buffer.indexOf(", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb") < 0) {
            buffer.append(", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb");
        }
        buffer.append(')');
    }
    
    public static String getStackTrace(final StackTraceElement[] elements) {
        final StringBuilder buffer = new StringBuilder();
        getStackTrace(elements, buffer);
        return buffer.toString();
    }
    
    public static void getStackTrace(final StackTraceElement[] elements, final StringBuilder buffer) {
        getStackTrace(elements, buffer, -1);
    }
    
    public static void getStackTrace(final StackTraceElement[] elements, final StringBuilder buffer, final int maxPreSDKFrames) {
        boolean sdkElementFound = false;
        int numPreSDKElementsFound = 0;
        for (int i = 0; i < elements.length; ++i) {
            if (i > 0) {
                buffer.append(" / ");
            }
            if (elements[i].getClassName().startsWith("com.unboundid.")) {
                sdkElementFound = true;
            }
            else if (sdkElementFound) {
                if (maxPreSDKFrames >= 0 && numPreSDKElementsFound >= maxPreSDKFrames) {
                    buffer.append("...");
                    return;
                }
                ++numPreSDKElementsFound;
            }
            buffer.append(elements[i].getMethodName());
            buffer.append('(');
            buffer.append(elements[i].getFileName());
            final int lineNumber = elements[i].getLineNumber();
            if (lineNumber > 0) {
                buffer.append(':');
                buffer.append(lineNumber);
            }
            else if (elements[i].isNativeMethod()) {
                buffer.append(":native");
            }
            else {
                buffer.append(":unknown");
            }
            buffer.append(')');
        }
    }
    
    public static String getExceptionMessage(final Throwable t) {
        final boolean includeCause = Boolean.getBoolean("com.unboundid.ldap.sdk.debug.includeCauseInExceptionMessages");
        final boolean includeStackTrace = Boolean.getBoolean("com.unboundid.ldap.sdk.debug.includeStackTraceInExceptionMessages");
        return getExceptionMessage(t, includeCause, includeStackTrace);
    }
    
    public static String getExceptionMessage(final Throwable t, final boolean includeCause, final boolean includeStackTrace) {
        if (t == null) {
            return UtilityMessages.ERR_NO_EXCEPTION.get();
        }
        final StringBuilder buffer = new StringBuilder();
        if (t instanceof LDAPSDKException) {
            buffer.append(((LDAPSDKException)t).getExceptionMessage());
        }
        else if (t instanceof LDAPSDKRuntimeException) {
            buffer.append(((LDAPSDKRuntimeException)t).getExceptionMessage());
        }
        else if (t instanceof NullPointerException) {
            buffer.append("NullPointerException(");
            getStackTrace(t.getStackTrace(), buffer, 3);
            buffer.append(')');
        }
        else if (t.getMessage() == null || t.getMessage().isEmpty() || t.getMessage().equalsIgnoreCase("null")) {
            getStackTrace(t, buffer);
        }
        else {
            buffer.append(t.getClass().getSimpleName());
            buffer.append('(');
            buffer.append(t.getMessage());
            buffer.append(')');
            if (includeStackTrace) {
                buffer.append(" trace=");
                getStackTrace(t, buffer);
            }
            else if (includeCause) {
                final Throwable cause = t.getCause();
                if (cause != null) {
                    buffer.append(" caused by ");
                    buffer.append(getExceptionMessage(cause));
                }
            }
        }
        final String ldapSDKVersionString = ", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb";
        if (buffer.indexOf(", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb") < 0) {
            buffer.append(", ldapSDKVersion=4.0.14, revision=c0fb784eebf9d36a67c736d0428fb3577f2e25bb");
        }
        return buffer.toString();
    }
    
    public static String getUnqualifiedClassName(final Class<?> c) {
        final String className = c.getName();
        final int lastPeriodPos = className.lastIndexOf(46);
        if (lastPeriodPos > 0) {
            return className.substring(lastPeriodPos + 1);
        }
        return className;
    }
    
    public static TimeZone getUTCTimeZone() {
        return StaticUtils.UTC_TIME_ZONE;
    }
    
    public static String encodeGeneralizedTime(final long timestamp) {
        return encodeGeneralizedTime(new Date(timestamp));
    }
    
    public static String encodeGeneralizedTime(final Date d) {
        SimpleDateFormat dateFormat = StaticUtils.DATE_FORMATTERS.get();
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS'Z'");
            dateFormat.setTimeZone(StaticUtils.UTC_TIME_ZONE);
            StaticUtils.DATE_FORMATTERS.set(dateFormat);
        }
        return dateFormat.format(d);
    }
    
    public static Date decodeGeneralizedTime(final String t) throws ParseException {
        Validator.ensureNotNull(t);
        TimeZone tz;
        int tzPos;
        if (t.endsWith("Z")) {
            tz = TimeZone.getTimeZone("UTC");
            tzPos = t.length() - 1;
        }
        else {
            tzPos = t.lastIndexOf(45);
            if (tzPos < 0) {
                tzPos = t.lastIndexOf(43);
                if (tzPos < 0) {
                    throw new ParseException(UtilityMessages.ERR_GENTIME_DECODE_CANNOT_PARSE_TZ.get(t), 0);
                }
            }
            tz = TimeZone.getTimeZone("GMT" + t.substring(tzPos));
            if (tz.getRawOffset() == 0 && !t.endsWith("+0000") && !t.endsWith("-0000")) {
                throw new ParseException(UtilityMessages.ERR_GENTIME_DECODE_CANNOT_PARSE_TZ.get(t), tzPos);
            }
        }
        int periodPos = t.lastIndexOf(46, tzPos);
        String subSecFormatStr = null;
        String trimmedTimestamp = null;
        if (periodPos > 0) {
            final int subSecondLength = tzPos - periodPos - 1;
            switch (subSecondLength) {
                case 0: {
                    subSecFormatStr = "";
                    trimmedTimestamp = t.substring(0, periodPos);
                    break;
                }
                case 1: {
                    subSecFormatStr = ".SSS";
                    trimmedTimestamp = t.substring(0, periodPos + 2) + "00";
                    break;
                }
                case 2: {
                    subSecFormatStr = ".SSS";
                    trimmedTimestamp = t.substring(0, periodPos + 3) + '0';
                    break;
                }
                default: {
                    subSecFormatStr = ".SSS";
                    trimmedTimestamp = t.substring(0, periodPos + 4);
                    break;
                }
            }
        }
        else {
            subSecFormatStr = "";
            periodPos = tzPos;
            trimmedTimestamp = t.substring(0, tzPos);
        }
        String formatStr = null;
        switch (periodPos) {
            case 10: {
                formatStr = "yyyyMMddHH" + subSecFormatStr;
                break;
            }
            case 12: {
                formatStr = "yyyyMMddHHmm" + subSecFormatStr;
                break;
            }
            case 14: {
                formatStr = "yyyyMMddHHmmss" + subSecFormatStr;
                break;
            }
            default: {
                throw new ParseException(UtilityMessages.ERR_GENTIME_CANNOT_PARSE_INVALID_LENGTH.get(t), periodPos);
            }
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr);
        dateFormat.setTimeZone(tz);
        dateFormat.setLenient(false);
        return dateFormat.parse(trimmedTimestamp);
    }
    
    public static String trimLeading(final String s) {
        Validator.ensureNotNull(s);
        int nonSpacePos;
        int length;
        for (nonSpacePos = 0, length = s.length(); nonSpacePos < length && s.charAt(nonSpacePos) == ' '; ++nonSpacePos) {}
        if (nonSpacePos == 0) {
            return s;
        }
        if (nonSpacePos >= length) {
            return "";
        }
        return s.substring(nonSpacePos, length);
    }
    
    public static String trimTrailing(final String s) {
        Validator.ensureNotNull(s);
        int nonSpacePos;
        int lastPos;
        for (lastPos = (nonSpacePos = s.length() - 1); nonSpacePos >= 0 && s.charAt(nonSpacePos) == ' '; --nonSpacePos) {}
        if (nonSpacePos < 0) {
            return "";
        }
        if (nonSpacePos == lastPos) {
            return s;
        }
        return s.substring(0, nonSpacePos + 1);
    }
    
    public static List<String> wrapLine(final String line, final int maxWidth) {
        return wrapLine(line, maxWidth, maxWidth);
    }
    
    public static List<String> wrapLine(final String line, final int maxFirstLineWidth, final int maxSubsequentLineWidth) {
        if (maxFirstLineWidth > 0) {
            Validator.ensureTrue(maxSubsequentLineWidth > 0);
        }
        final int breakPos = line.indexOf(10);
        if (breakPos >= 0) {
            final ArrayList<String> lineList = new ArrayList<String>(10);
            final StringTokenizer tokenizer = new StringTokenizer(line, "\r\n");
            while (tokenizer.hasMoreTokens()) {
                lineList.addAll(wrapLine(tokenizer.nextToken(), maxFirstLineWidth, maxSubsequentLineWidth));
            }
            return lineList;
        }
        final int length = line.length();
        if (maxFirstLineWidth <= 0 || length < maxFirstLineWidth) {
            return Collections.singletonList(line);
        }
        int wrapPos = maxFirstLineWidth;
        int lastWrapPos = 0;
        final ArrayList<String> lineList2 = new ArrayList<String>(5);
        do {
            final int spacePos = line.lastIndexOf(32, wrapPos);
            if (spacePos > lastWrapPos) {
                final String s = trimTrailing(line.substring(lastWrapPos, spacePos));
                if (!s.isEmpty()) {
                    lineList2.add(s);
                }
                wrapPos = spacePos;
            }
            else {
                lineList2.add(line.substring(lastWrapPos, wrapPos));
            }
            while (wrapPos < length && line.charAt(wrapPos) == ' ') {
                ++wrapPos;
            }
            lastWrapPos = wrapPos;
            wrapPos += maxSubsequentLineWidth;
        } while (wrapPos < length);
        if (lastWrapPos < length) {
            final String s = line.substring(lastWrapPos);
            if (!s.isEmpty()) {
                lineList2.add(s);
            }
        }
        return lineList2;
    }
    
    public static String cleanExampleCommandLineArgument(final String s) {
        return ExampleCommandLineArgument.getCleanArgument(s).getLocalForm();
    }
    
    public static String concatenateStrings(final String... a) {
        return concatenateStrings((String)null, null, "  ", null, null, a);
    }
    
    public static String concatenateStrings(final List<String> l) {
        return concatenateStrings(null, null, "  ", null, null, l);
    }
    
    public static String concatenateStrings(final String beforeList, final String beforeElement, final String betweenElements, final String afterElement, final String afterList, final String... a) {
        return concatenateStrings(beforeList, beforeElement, betweenElements, afterElement, afterList, Arrays.asList(a));
    }
    
    public static String concatenateStrings(final String beforeList, final String beforeElement, final String betweenElements, final String afterElement, final String afterList, final List<String> l) {
        Validator.ensureNotNull(l);
        final StringBuilder buffer = new StringBuilder();
        if (beforeList != null) {
            buffer.append(beforeList);
        }
        final Iterator<String> iterator = l.iterator();
        while (iterator.hasNext()) {
            if (beforeElement != null) {
                buffer.append(beforeElement);
            }
            buffer.append(iterator.next());
            if (afterElement != null) {
                buffer.append(afterElement);
            }
            if (betweenElements != null && iterator.hasNext()) {
                buffer.append(betweenElements);
            }
        }
        if (afterList != null) {
            buffer.append(afterList);
        }
        return buffer.toString();
    }
    
    public static String secondsToHumanReadableDuration(final long s) {
        return millisToHumanReadableDuration(s * 1000L);
    }
    
    public static String millisToHumanReadableDuration(final long m) {
        final StringBuilder buffer = new StringBuilder();
        long numMillis = m;
        final long numDays = numMillis / 86400000L;
        if (numDays > 0L) {
            numMillis -= numDays * 86400000L;
            if (numDays == 1L) {
                buffer.append(UtilityMessages.INFO_NUM_DAYS_SINGULAR.get(numDays));
            }
            else {
                buffer.append(UtilityMessages.INFO_NUM_DAYS_PLURAL.get(numDays));
            }
        }
        final long numHours = numMillis / 3600000L;
        if (numHours > 0L) {
            numMillis -= numHours * 3600000L;
            if (buffer.length() > 0) {
                buffer.append(", ");
            }
            if (numHours == 1L) {
                buffer.append(UtilityMessages.INFO_NUM_HOURS_SINGULAR.get(numHours));
            }
            else {
                buffer.append(UtilityMessages.INFO_NUM_HOURS_PLURAL.get(numHours));
            }
        }
        final long numMinutes = numMillis / 60000L;
        if (numMinutes > 0L) {
            numMillis -= numMinutes * 60000L;
            if (buffer.length() > 0) {
                buffer.append(", ");
            }
            if (numMinutes == 1L) {
                buffer.append(UtilityMessages.INFO_NUM_MINUTES_SINGULAR.get(numMinutes));
            }
            else {
                buffer.append(UtilityMessages.INFO_NUM_MINUTES_PLURAL.get(numMinutes));
            }
        }
        if (numMillis == 1000L) {
            if (buffer.length() > 0) {
                buffer.append(", ");
            }
            buffer.append(UtilityMessages.INFO_NUM_SECONDS_SINGULAR.get(1));
        }
        else if (numMillis > 0L || buffer.length() == 0) {
            if (buffer.length() > 0) {
                buffer.append(", ");
            }
            final long numSeconds = numMillis / 1000L;
            numMillis -= numSeconds * 1000L;
            if (numMillis % 1000L != 0L) {
                final double numSecondsDouble = numSeconds + numMillis / 1000.0;
                final DecimalFormat decimalFormat = new DecimalFormat("0.000");
                buffer.append(UtilityMessages.INFO_NUM_SECONDS_WITH_DECIMAL.get(decimalFormat.format(numSecondsDouble)));
            }
            else {
                buffer.append(UtilityMessages.INFO_NUM_SECONDS_PLURAL.get(numSeconds));
            }
        }
        return buffer.toString();
    }
    
    public static long nanosToMillis(final long nanos) {
        return Math.max(0L, Math.round(nanos / 1000000.0));
    }
    
    public static long millisToNanos(final long millis) {
        return Math.max(0L, millis * 1000000L);
    }
    
    public static boolean isNumericOID(final String s) {
        boolean digitRequired = true;
        boolean periodFound = false;
        for (final char c : s.toCharArray()) {
            switch (c) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    digitRequired = false;
                    break;
                }
                case '.': {
                    if (digitRequired) {
                        return false;
                    }
                    digitRequired = true;
                    periodFound = true;
                    break;
                }
                default: {
                    return false;
                }
            }
        }
        return periodFound && !digitRequired;
    }
    
    public static String capitalize(final String s) {
        return capitalize(s, false);
    }
    
    public static String capitalize(final String s, final boolean allWords) {
        if (s == null) {
            return null;
        }
        switch (s.length()) {
            case 0: {
                return s;
            }
            case 1: {
                return s.toUpperCase();
            }
            default: {
                boolean capitalize = true;
                final char[] chars = s.toCharArray();
                final StringBuilder buffer = new StringBuilder(chars.length);
                for (final char c : chars) {
                    if (Character.isWhitespace(c) || (c >= '!' && c <= '.') || (c >= ':' && c <= '@') || (c >= '[' && c <= '`') || (c >= '{' && c <= '~')) {
                        buffer.append(c);
                        capitalize |= allWords;
                    }
                    else if (capitalize) {
                        buffer.append(Character.toUpperCase(c));
                        capitalize = false;
                    }
                    else {
                        buffer.append(c);
                    }
                }
                return buffer.toString();
            }
        }
    }
    
    public static byte[] encodeUUID(final UUID uuid) {
        final byte[] b = new byte[16];
        final long mostSignificantBits = uuid.getMostSignificantBits();
        b[0] = (byte)(mostSignificantBits >> 56 & 0xFFL);
        b[1] = (byte)(mostSignificantBits >> 48 & 0xFFL);
        b[2] = (byte)(mostSignificantBits >> 40 & 0xFFL);
        b[3] = (byte)(mostSignificantBits >> 32 & 0xFFL);
        b[4] = (byte)(mostSignificantBits >> 24 & 0xFFL);
        b[5] = (byte)(mostSignificantBits >> 16 & 0xFFL);
        b[6] = (byte)(mostSignificantBits >> 8 & 0xFFL);
        b[7] = (byte)(mostSignificantBits & 0xFFL);
        final long leastSignificantBits = uuid.getLeastSignificantBits();
        b[8] = (byte)(leastSignificantBits >> 56 & 0xFFL);
        b[9] = (byte)(leastSignificantBits >> 48 & 0xFFL);
        b[10] = (byte)(leastSignificantBits >> 40 & 0xFFL);
        b[11] = (byte)(leastSignificantBits >> 32 & 0xFFL);
        b[12] = (byte)(leastSignificantBits >> 24 & 0xFFL);
        b[13] = (byte)(leastSignificantBits >> 16 & 0xFFL);
        b[14] = (byte)(leastSignificantBits >> 8 & 0xFFL);
        b[15] = (byte)(leastSignificantBits & 0xFFL);
        return b;
    }
    
    public static UUID decodeUUID(final byte[] b) throws ParseException {
        if (b.length != 16) {
            throw new ParseException(UtilityMessages.ERR_DECODE_UUID_INVALID_LENGTH.get(toHex(b)), 0);
        }
        long mostSignificantBits = 0L;
        for (int i = 0; i < 8; ++i) {
            mostSignificantBits = (mostSignificantBits << 8 | (long)(b[i] & 0xFF));
        }
        long leastSignificantBits = 0L;
        for (int j = 8; j < 16; ++j) {
            leastSignificantBits = (leastSignificantBits << 8 | (long)(b[j] & 0xFF));
        }
        return new UUID(mostSignificantBits, leastSignificantBits);
    }
    
    public static boolean isWindows() {
        final String osName = toLowerCase(getSystemProperty("os.name"));
        return osName != null && osName.contains("windows");
    }
    
    public static List<String> toArgumentList(final String s) throws ParseException {
        if (s == null || s.isEmpty()) {
            return Collections.emptyList();
        }
        int quoteStartPos = -1;
        boolean inEscape = false;
        final ArrayList<String> argList = new ArrayList<String>(20);
        final StringBuilder currentArg = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (inEscape) {
                currentArg.append(c);
                inEscape = false;
            }
            else if (c == '\\') {
                inEscape = true;
            }
            else if (c == '\"') {
                if (quoteStartPos >= 0) {
                    quoteStartPos = -1;
                }
                else {
                    quoteStartPos = i;
                }
            }
            else if (c == ' ') {
                if (quoteStartPos >= 0) {
                    currentArg.append(c);
                }
                else if (currentArg.length() > 0) {
                    argList.add(currentArg.toString());
                    currentArg.setLength(0);
                }
            }
            else {
                currentArg.append(c);
            }
        }
        if (s.endsWith("\\") && !s.endsWith("\\\\")) {
            throw new ParseException(UtilityMessages.ERR_ARG_STRING_DANGLING_BACKSLASH.get(), s.length() - 1);
        }
        if (quoteStartPos >= 0) {
            throw new ParseException(UtilityMessages.ERR_ARG_STRING_UNMATCHED_QUOTE.get(quoteStartPos), quoteStartPos);
        }
        if (currentArg.length() > 0) {
            argList.add(currentArg.toString());
        }
        return Collections.unmodifiableList((List<? extends String>)argList);
    }
    
    public static <T> T[] toArray(final Collection<T> collection, final Class<T> type) {
        if (collection == null) {
            return null;
        }
        final T[] array = (T[])Array.newInstance(type, collection.size());
        return collection.toArray(array);
    }
    
    public static <T> List<T> toList(final T[] array) {
        if (array == null) {
            return null;
        }
        final ArrayList<T> l = new ArrayList<T>(array.length);
        l.addAll((Collection<? extends T>)Arrays.asList(array));
        return l;
    }
    
    public static <T> List<T> toNonNullList(final T[] array) {
        if (array == null) {
            return new ArrayList<T>(0);
        }
        final ArrayList<T> l = new ArrayList<T>(array.length);
        l.addAll((Collection<? extends T>)Arrays.asList(array));
        return l;
    }
    
    public static boolean bothNullOrEqual(final Object o1, final Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o2 != null && o1.equals(o2);
    }
    
    public static boolean bothNullOrEqualIgnoreCase(final String s1, final String s2) {
        if (s1 == null) {
            return s2 == null;
        }
        return s2 != null && s1.equalsIgnoreCase(s2);
    }
    
    public static boolean stringsEqualIgnoreCaseOrderIndependent(final String[] a1, final String[] a2) {
        if (a1 == null) {
            return a2 == null;
        }
        if (a2 == null) {
            return false;
        }
        if (a1.length != a2.length) {
            return false;
        }
        if (a1.length == 1) {
            return a1[0].equalsIgnoreCase(a2[0]);
        }
        final HashSet<String> s1 = new HashSet<String>(computeMapCapacity(a1.length));
        for (final String s2 : a1) {
            s1.add(toLowerCase(s2));
        }
        final HashSet<String> s3 = new HashSet<String>(computeMapCapacity(a2.length));
        for (final String s4 : a2) {
            s3.add(toLowerCase(s4));
        }
        return s1.equals(s3);
    }
    
    public static <T> boolean arraysEqualOrderIndependent(final T[] a1, final T[] a2) {
        if (a1 == null) {
            return a2 == null;
        }
        if (a2 == null) {
            return false;
        }
        if (a1.length != a2.length) {
            return false;
        }
        if (a1.length == 1) {
            return a1[0].equals(a2[0]);
        }
        final HashSet<T> s1 = new HashSet<T>((Collection<? extends T>)Arrays.asList(a1));
        final HashSet<T> s2 = new HashSet<T>((Collection<? extends T>)Arrays.asList(a2));
        return s1.equals(s2);
    }
    
    public static int numBytesInUTF8CharacterWithFirstByte(final byte b) {
        if ((b & 0x7F) == b) {
            return 1;
        }
        if ((b & 0xE0) == 0xC0) {
            return 2;
        }
        if ((b & 0xF0) == 0xE0) {
            return 3;
        }
        if ((b & 0xF8) == 0xF0) {
            return 4;
        }
        return -1;
    }
    
    public static boolean isSensitiveToCodeAttribute(final String name) {
        final String lowerBaseName = Attribute.getBaseName(name).toLowerCase();
        return StaticUtils.TO_CODE_SENSITIVE_ATTRIBUTE_NAMES.contains(lowerBaseName);
    }
    
    public static Set<String> getSensitiveToCodeAttributeBaseNames() {
        return StaticUtils.TO_CODE_SENSITIVE_ATTRIBUTE_NAMES;
    }
    
    public static void setSensitiveToCodeAttributes(final String... names) {
        setSensitiveToCodeAttributes(toList(names));
    }
    
    public static void setSensitiveToCodeAttributes(final Collection<String> names) {
        if (names == null || names.isEmpty()) {
            StaticUtils.TO_CODE_SENSITIVE_ATTRIBUTE_NAMES = Collections.emptySet();
        }
        else {
            final LinkedHashSet<String> nameSet = new LinkedHashSet<String>(names.size());
            for (final String s : names) {
                nameSet.add(Attribute.getBaseName(s).toLowerCase());
            }
            StaticUtils.TO_CODE_SENSITIVE_ATTRIBUTE_NAMES = Collections.unmodifiableSet((Set<? extends String>)nameSet);
        }
    }
    
    public static IOException createIOExceptionWithCause(final String message, final Throwable cause) {
        if (cause == null) {
            return new IOException(message);
        }
        if (message == null) {
            return new IOException(cause);
        }
        return new IOException(message, cause);
    }
    
    public static List<String> stringToLines(final String s) {
        final ArrayList<String> l = new ArrayList<String>(10);
        if (s == null) {
            return l;
        }
        final BufferedReader reader = new BufferedReader(new StringReader(s));
        try {
            while (true) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                l.add(line);
            }
            return l;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            l.clear();
            l.add(s);
            return l;
        }
        finally {
            try {
                reader.close();
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
            }
        }
    }
    
    public static String linesToString(final CharSequence... lines) {
        if (lines == null) {
            return "";
        }
        return linesToString(Arrays.asList(lines));
    }
    
    public static String linesToString(final List<? extends CharSequence> lines) {
        if (lines == null) {
            return "";
        }
        final StringBuilder buffer = new StringBuilder();
        for (final CharSequence line : lines) {
            buffer.append(line);
            buffer.append(StaticUtils.EOL);
        }
        return buffer.toString();
    }
    
    public static File constructPath(final File baseDirectory, final String... pathElements) {
        Validator.ensureNotNull(baseDirectory);
        File f = baseDirectory;
        if (pathElements != null) {
            for (final String pathElement : pathElements) {
                f = new File(f, pathElement);
            }
        }
        return f;
    }
    
    public static byte[] byteArray(final int... bytes) {
        if (bytes == null || bytes.length == 0) {
            return StaticUtils.NO_BYTES;
        }
        final byte[] byteArray = new byte[bytes.length];
        for (int i = 0; i < bytes.length; ++i) {
            byteArray[i] = (byte)(bytes[i] & 0xFF);
        }
        return byteArray;
    }
    
    public static boolean isWithinUnitTest() {
        return StaticUtils.IS_WITHIN_UNIT_TESTS;
    }
    
    public static void throwErrorOrRuntimeException(final Throwable throwable) throws Error, RuntimeException {
        Validator.ensureNotNull(throwable);
        if (throwable instanceof Error) {
            throw (Error)throwable;
        }
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
        }
        throw new RuntimeException(throwable);
    }
    
    public static void rethrowIfErrorOrRuntimeException(final Throwable throwable) throws Error, RuntimeException {
        if (throwable instanceof Error) {
            throw (Error)throwable;
        }
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
        }
    }
    
    public static void rethrowIfError(final Throwable throwable) throws Error {
        if (throwable instanceof Error) {
            throw (Error)throwable;
        }
    }
    
    public static int computeMapCapacity(final int expectedItemCount) {
        switch (expectedItemCount) {
            case 0: {
                return 0;
            }
            case 1: {
                return 2;
            }
            case 2: {
                return 3;
            }
            case 3: {
                return 5;
            }
            case 4: {
                return 6;
            }
            case 5: {
                return 7;
            }
            case 6: {
                return 9;
            }
            case 7: {
                return 10;
            }
            case 8: {
                return 11;
            }
            case 9: {
                return 13;
            }
            case 10: {
                return 14;
            }
            case 11: {
                return 15;
            }
            case 12: {
                return 17;
            }
            case 13: {
                return 18;
            }
            case 14: {
                return 19;
            }
            case 15: {
                return 21;
            }
            case 16: {
                return 22;
            }
            case 17: {
                return 23;
            }
            case 18: {
                return 25;
            }
            case 19: {
                return 26;
            }
            case 20: {
                return 27;
            }
            case 30: {
                return 41;
            }
            case 40: {
                return 54;
            }
            case 50: {
                return 67;
            }
            case 60: {
                return 81;
            }
            case 70: {
                return 94;
            }
            case 80: {
                return 107;
            }
            case 90: {
                return 121;
            }
            case 100: {
                return 134;
            }
            case 110: {
                return 147;
            }
            case 120: {
                return 161;
            }
            case 130: {
                return 174;
            }
            case 140: {
                return 187;
            }
            case 150: {
                return 201;
            }
            case 160: {
                return 214;
            }
            case 170: {
                return 227;
            }
            case 180: {
                return 241;
            }
            case 190: {
                return 254;
            }
            case 200: {
                return 267;
            }
            default: {
                Validator.ensureTrue(expectedItemCount >= 0, "StaticUtils.computeMapOrSetCapacity.expectedItemCount must be greater than or equal to zero.");
                if (expectedItemCount <= 536870911) {
                    return expectedItemCount * 4 / 3 + 1;
                }
                final int computedCapacity = (int)(expectedItemCount / 0.75) + 1;
                if (computedCapacity <= expectedItemCount) {
                    return expectedItemCount;
                }
                return computedCapacity;
            }
        }
    }
    
    @SafeVarargs
    public static <T> Set<T> setOf(final T... items) {
        return Collections.unmodifiableSet((Set<? extends T>)new LinkedHashSet<T>((Collection<? extends T>)Arrays.asList(items)));
    }
    
    @SafeVarargs
    public static <T> HashSet<T> hashSetOf(final T... items) {
        return new HashSet<T>((Collection<? extends T>)Arrays.asList(items));
    }
    
    @SafeVarargs
    public static <T> LinkedHashSet<T> linkedHashSetOf(final T... items) {
        return new LinkedHashSet<T>((Collection<? extends T>)Arrays.asList(items));
    }
    
    @SafeVarargs
    public static <T> TreeSet<T> treeSetOf(final T... items) {
        return new TreeSet<T>((Collection<? extends T>)Arrays.asList(items));
    }
    
    public static <K, V> Map<K, V> mapOf(final K key, final V value) {
        return Collections.singletonMap(key, value);
    }
    
    public static <K, V> Map<K, V> mapOf(final K key1, final V value1, final K key2, final V value2) {
        final LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(computeMapCapacity(2));
        map.put(key1, value1);
        map.put(key2, value2);
        return Collections.unmodifiableMap((Map<? extends K, ? extends V>)map);
    }
    
    public static <K, V> Map<K, V> mapOf(final K key1, final V value1, final K key2, final V value2, final K key3, final V value3) {
        final LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(computeMapCapacity(3));
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return Collections.unmodifiableMap((Map<? extends K, ? extends V>)map);
    }
    
    public static <K, V> Map<K, V> mapOf(final K key1, final V value1, final K key2, final V value2, final K key3, final V value3, final K key4, final V value4) {
        final LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(computeMapCapacity(4));
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        return Collections.unmodifiableMap((Map<? extends K, ? extends V>)map);
    }
    
    public static <K, V> Map<K, V> mapOf(final K key1, final V value1, final K key2, final V value2, final K key3, final V value3, final K key4, final V value4, final K key5, final V value5) {
        final LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(computeMapCapacity(5));
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        return Collections.unmodifiableMap((Map<? extends K, ? extends V>)map);
    }
    
    public static <K, V> Map<K, V> mapOf(final K key1, final V value1, final K key2, final V value2, final K key3, final V value3, final K key4, final V value4, final K key5, final V value5, final K key6, final V value6) {
        final LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(computeMapCapacity(6));
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key6, value6);
        return Collections.unmodifiableMap((Map<? extends K, ? extends V>)map);
    }
    
    public static <K, V> Map<K, V> mapOf(final K key1, final V value1, final K key2, final V value2, final K key3, final V value3, final K key4, final V value4, final K key5, final V value5, final K key6, final V value6, final K key7, final V value7) {
        final LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(computeMapCapacity(7));
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key6, value6);
        map.put(key7, value7);
        return Collections.unmodifiableMap((Map<? extends K, ? extends V>)map);
    }
    
    public static <K, V> Map<K, V> mapOf(final K key1, final V value1, final K key2, final V value2, final K key3, final V value3, final K key4, final V value4, final K key5, final V value5, final K key6, final V value6, final K key7, final V value7, final K key8, final V value8) {
        final LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(computeMapCapacity(8));
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key6, value6);
        map.put(key7, value7);
        map.put(key8, value8);
        return Collections.unmodifiableMap((Map<? extends K, ? extends V>)map);
    }
    
    public static <K, V> Map<K, V> mapOf(final K key1, final V value1, final K key2, final V value2, final K key3, final V value3, final K key4, final V value4, final K key5, final V value5, final K key6, final V value6, final K key7, final V value7, final K key8, final V value8, final K key9, final V value9) {
        final LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(computeMapCapacity(9));
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key6, value6);
        map.put(key7, value7);
        map.put(key8, value8);
        map.put(key9, value9);
        return Collections.unmodifiableMap((Map<? extends K, ? extends V>)map);
    }
    
    public static <K, V> Map<K, V> mapOf(final K key1, final V value1, final K key2, final V value2, final K key3, final V value3, final K key4, final V value4, final K key5, final V value5, final K key6, final V value6, final K key7, final V value7, final K key8, final V value8, final K key9, final V value9, final K key10, final V value10) {
        final LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(computeMapCapacity(10));
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key6, value6);
        map.put(key7, value7);
        map.put(key8, value8);
        map.put(key9, value9);
        map.put(key10, value10);
        return Collections.unmodifiableMap((Map<? extends K, ? extends V>)map);
    }
    
    @SafeVarargs
    public static <T> Map<T, T> mapOf(final T... items) {
        if (items == null || items.length == 0) {
            return Collections.emptyMap();
        }
        Validator.ensureTrue(items.length % 2 == 0, "StaticUtils.mapOf.items must have an even number of elements");
        final int numEntries = items.length / 2;
        final LinkedHashMap<T, T> map = new LinkedHashMap<T, T>(computeMapCapacity(numEntries));
        int i = 0;
        while (i < items.length) {
            map.put(items[i++], items[i++]);
        }
        return Collections.unmodifiableMap((Map<? extends T, ? extends T>)map);
    }
    
    @SafeVarargs
    public static <K, V> Map<K, V> mapOfObjectPairs(final ObjectPair<K, V>... items) {
        if (items == null || items.length == 0) {
            return Collections.emptyMap();
        }
        final LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(computeMapCapacity(items.length));
        for (final ObjectPair<K, V> item : items) {
            map.put(item.getFirst(), item.getSecond());
        }
        return Collections.unmodifiableMap((Map<? extends K, ? extends V>)map);
    }
    
    public static Properties getSystemProperties(final String... propertyNames) {
        try {
            final Properties properties = System.getProperties();
            final String forceThrowPropertyName = StaticUtils.class.getName() + ".forceGetSystemPropertiesToThrow";
            final Object forceThrowPropertyValue = properties.getProperty(forceThrowPropertyName);
            if (forceThrowPropertyValue != null) {
                throw new SecurityException(forceThrowPropertyName + '=' + forceThrowPropertyValue);
            }
            return System.getProperties();
        }
        catch (final SecurityException e) {
            Debug.debugException(e);
            final Properties properties = new Properties();
            if (propertyNames != null) {
                for (final String propertyName : propertyNames) {
                    final Object propertyValue = System.getProperty(propertyName);
                    if (propertyValue != null) {
                        ((Hashtable<String, Object>)properties).put(propertyName, propertyValue);
                    }
                }
            }
            return properties;
        }
    }
    
    public static String getSystemProperty(final String name) {
        try {
            return System.getProperty(name);
        }
        catch (final Throwable t) {
            Debug.debugException(t);
            return null;
        }
    }
    
    public static String getSystemProperty(final String name, final String defaultValue) {
        try {
            return System.getProperty(name, defaultValue);
        }
        catch (final Throwable t) {
            Debug.debugException(t);
            return defaultValue;
        }
    }
    
    public static String setSystemProperty(final String name, final String value) {
        try {
            if (value == null) {
                return System.clearProperty(name);
            }
            return System.setProperty(name, value);
        }
        catch (final Throwable t) {
            Debug.debugException(t);
            return null;
        }
    }
    
    public static String clearSystemProperty(final String name) {
        try {
            return System.clearProperty(name);
        }
        catch (final Throwable t) {
            Debug.debugException(t);
            return null;
        }
    }
    
    public static Map<String, String> getEnvironmentVariables() {
        try {
            return System.getenv();
        }
        catch (final Throwable t) {
            Debug.debugException(t);
            return Collections.emptyMap();
        }
    }
    
    public static String getEnvironmentVariable(final String name) {
        try {
            return System.getenv(name);
        }
        catch (final Throwable t) {
            Debug.debugException(t);
            return null;
        }
    }
    
    public static void setLoggerLevel(final Logger logger, final Level logLevel) {
        try {
            logger.setLevel(logLevel);
        }
        catch (final Throwable t) {
            Debug.debugException(t);
        }
    }
    
    public static void setLogHandlerLevel(final Handler logHandler, final Level logLevel) {
        try {
            logHandler.setLevel(logLevel);
        }
        catch (final Throwable t) {
            Debug.debugException(t);
        }
    }
    
    public static Set<InetAddress> getAllLocalAddresses(final NameResolver nameResolver) {
        NameResolver resolver;
        if (nameResolver == null) {
            resolver = LDAPConnectionOptions.DEFAULT_NAME_RESOLVER;
        }
        else {
            resolver = nameResolver;
        }
        final LinkedHashSet<InetAddress> localAddresses = new LinkedHashSet<InetAddress>(computeMapCapacity(10));
        try {
            localAddresses.add(resolver.getLocalHost());
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        try {
            final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                final NetworkInterface networkInterface = networkInterfaces.nextElement();
                final Enumeration<InetAddress> interfaceAddresses = networkInterface.getInetAddresses();
                while (interfaceAddresses.hasMoreElements()) {
                    localAddresses.add(interfaceAddresses.nextElement());
                }
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        try {
            localAddresses.add(resolver.getLoopbackAddress());
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        return Collections.unmodifiableSet((Set<? extends InetAddress>)localAddresses);
    }
    
    public static String getCanonicalHostNameIfAvailable(final NameResolver nameResolver, final InetAddress address) {
        NameResolver resolver;
        if (nameResolver == null) {
            resolver = LDAPConnectionOptions.DEFAULT_NAME_RESOLVER;
        }
        else {
            resolver = nameResolver;
        }
        final String hostAddress = address.getHostAddress();
        final String trimmedHostAddress = trimInterfaceNameFromHostAddress(hostAddress);
        final String canonicalHostName = resolver.getCanonicalHostName(address);
        if (canonicalHostName == null || canonicalHostName.equalsIgnoreCase(hostAddress) || canonicalHostName.equalsIgnoreCase(trimmedHostAddress)) {
            return null;
        }
        return canonicalHostName;
    }
    
    public static Set<String> getAvailableCanonicalHostNames(final NameResolver nameResolver, final Collection<InetAddress> addresses) {
        NameResolver resolver;
        if (nameResolver == null) {
            resolver = LDAPConnectionOptions.DEFAULT_NAME_RESOLVER;
        }
        else {
            resolver = nameResolver;
        }
        final Set<String> canonicalHostNames = new LinkedHashSet<String>(computeMapCapacity(addresses.size()));
        for (final InetAddress address : addresses) {
            final String canonicalHostName = getCanonicalHostNameIfAvailable(resolver, address);
            if (canonicalHostName != null) {
                canonicalHostNames.add(canonicalHostName);
            }
        }
        return Collections.unmodifiableSet((Set<? extends String>)canonicalHostNames);
    }
    
    public static String trimInterfaceNameFromHostAddress(final String hostAddress) {
        final int percentPos = hostAddress.indexOf(37);
        if (percentPos > 0) {
            return hostAddress.substring(0, percentPos);
        }
        return hostAddress;
    }
    
    public static String getEnvironmentVariable(final String name, final String defaultValue) {
        final String value = getEnvironmentVariable(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
    
    static {
        NO_BYTES = new byte[0];
        NO_CHARS = new char[0];
        NO_CONTROLS = new Control[0];
        NO_STRINGS = new String[0];
        EOL = getSystemProperty("line.separator", "\n");
        EOL_BYTES = getBytes(StaticUtils.EOL);
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        int terminalWidth = 80;
        final String columnsEnvVar = getEnvironmentVariable("COLUMNS");
        if (columnsEnvVar != null) {
            try {
                terminalWidth = Integer.parseInt(columnsEnvVar);
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        TERMINAL_WIDTH_COLUMNS = terminalWidth;
        DATE_FORMATTERS = new ThreadLocal<SimpleDateFormat>();
        UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
        StaticUtils.TO_CODE_SENSITIVE_ATTRIBUTE_NAMES = setOf("userpassword", "2.5.4.35", "authpassword", "1.3.6.1.4.1.4203.1.3.4");
    }
}
