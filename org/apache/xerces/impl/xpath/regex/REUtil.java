package org.apache.xerces.impl.xpath.regex;

import java.text.CharacterIterator;

public final class REUtil
{
    static final int CACHESIZE = 20;
    static final RegularExpression[] regexCache;
    
    private REUtil() {
    }
    
    static final int composeFromSurrogates(final int n, final int n2) {
        return 65536 + (n - 55296 << 10) + n2 - 56320;
    }
    
    static final boolean isLowSurrogate(final int n) {
        return (n & 0xFC00) == 0xDC00;
    }
    
    static final boolean isHighSurrogate(final int n) {
        return (n & 0xFC00) == 0xD800;
    }
    
    static final String decomposeToSurrogates(int n) {
        final char[] array = new char[2];
        n -= 65536;
        array[0] = (char)((n >> 10) + 55296);
        array[1] = (char)((n & 0x3FF) + 56320);
        return new String(array);
    }
    
    static final String substring(final CharacterIterator characterIterator, final int n, final int n2) {
        final char[] array = new char[n2 - n];
        for (int i = 0; i < array.length; ++i) {
            array[i] = characterIterator.setIndex(i + n);
        }
        return new String(array);
    }
    
    static final int getOptionValue(final int n) {
        int n2 = 0;
        switch (n) {
            case 105: {
                n2 = 2;
                break;
            }
            case 109: {
                n2 = 8;
                break;
            }
            case 115: {
                n2 = 4;
                break;
            }
            case 120: {
                n2 = 16;
                break;
            }
            case 117: {
                n2 = 32;
                break;
            }
            case 119: {
                n2 = 64;
                break;
            }
            case 70: {
                n2 = 256;
                break;
            }
            case 72: {
                n2 = 128;
                break;
            }
            case 88: {
                n2 = 512;
                break;
            }
            case 44: {
                n2 = 1024;
                break;
            }
            case 98: {
                n2 = 2048;
                break;
            }
            case 104: {
                n2 = 4096;
                break;
            }
        }
        return n2;
    }
    
    static final int parseOptions(final String s) throws ParseException {
        if (s == null) {
            return 0;
        }
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            final int optionValue = getOptionValue(s.charAt(i));
            if (optionValue == 0) {
                throw new ParseException("Unknown Option: " + s.substring(i), -1);
            }
            n |= optionValue;
        }
        return n;
    }
    
    static final String createOptionString(final int n) {
        final StringBuffer sb = new StringBuffer(9);
        if ((n & 0x100) != 0x0) {
            sb.append('F');
        }
        if ((n & 0x80) != 0x0) {
            sb.append('H');
        }
        if ((n & 0x200) != 0x0) {
            sb.append('X');
        }
        if ((n & 0x2) != 0x0) {
            sb.append('i');
        }
        if ((n & 0x8) != 0x0) {
            sb.append('m');
        }
        if ((n & 0x4) != 0x0) {
            sb.append('s');
        }
        if ((n & 0x20) != 0x0) {
            sb.append('u');
        }
        if ((n & 0x40) != 0x0) {
            sb.append('w');
        }
        if ((n & 0x10) != 0x0) {
            sb.append('x');
        }
        if ((n & 0x400) != 0x0) {
            sb.append(',');
        }
        return sb.toString().intern();
    }
    
    static String stripExtendedComment(final String s) {
        final int length = s.length();
        final StringBuffer sb = new StringBuffer(length);
        int i = 0;
        int n = 0;
        while (i < length) {
            final char char1 = s.charAt(i++);
            if (char1 == '\t' || char1 == '\n' || char1 == '\f' || char1 == '\r' || char1 == ' ') {
                if (n <= 0) {
                    continue;
                }
                sb.append(char1);
            }
            else if (char1 == '#') {
                while (i < length) {
                    final char char2 = s.charAt(i++);
                    if (char2 == '\r') {
                        break;
                    }
                    if (char2 == '\n') {
                        break;
                    }
                }
            }
            else if (char1 == '\\' && i < length) {
                final char char3;
                if ((char3 = s.charAt(i)) == '#' || char3 == '\t' || char3 == '\n' || char3 == '\f' || char3 == '\r' || char3 == ' ') {
                    sb.append(char3);
                    ++i;
                }
                else {
                    sb.append('\\');
                    sb.append(char3);
                    ++i;
                }
            }
            else if (char1 == '[') {
                ++n;
                sb.append(char1);
                if (i >= length) {
                    continue;
                }
                final char char4 = s.charAt(i);
                if (char4 == '[' || char4 == ']') {
                    sb.append(char4);
                    ++i;
                }
                else {
                    if (char4 != '^' || i + 1 >= length) {
                        continue;
                    }
                    final char char5 = s.charAt(i + 1);
                    if (char5 != '[' && char5 != ']') {
                        continue;
                    }
                    sb.append('^');
                    sb.append(char5);
                    i += 2;
                }
            }
            else {
                if (n > 0 && char1 == ']') {
                    --n;
                }
                sb.append(char1);
            }
        }
        return sb.toString();
    }
    
    public static void main(final String[] array) {
        String s = null;
        try {
            String s2 = "";
            String s3 = null;
            if (array.length == 0) {
                System.out.println("Error:Usage: java REUtil -i|-m|-s|-u|-w|-X regularExpression String");
                System.exit(0);
            }
            for (int i = 0; i < array.length; ++i) {
                if (array[i].length() == 0 || array[i].charAt(0) != '-') {
                    if (s == null) {
                        s = array[i];
                    }
                    else if (s3 == null) {
                        s3 = array[i];
                    }
                    else {
                        System.err.println("Unnecessary: " + array[i]);
                    }
                }
                else if (array[i].equals("-i")) {
                    s2 += "i";
                }
                else if (array[i].equals("-m")) {
                    s2 += "m";
                }
                else if (array[i].equals("-s")) {
                    s2 += "s";
                }
                else if (array[i].equals("-u")) {
                    s2 += "u";
                }
                else if (array[i].equals("-w")) {
                    s2 += "w";
                }
                else if (array[i].equals("-X")) {
                    s2 += "X";
                }
                else {
                    System.err.println("Unknown option: " + array[i]);
                }
            }
            final RegularExpression regularExpression = new RegularExpression(s, s2);
            System.out.println("RegularExpression: " + regularExpression);
            final Match match = new Match();
            regularExpression.matches(s3, match);
            for (int j = 0; j < match.getNumberOfGroups(); ++j) {
                if (j == 0) {
                    System.out.print("Matched range for the whole pattern: ");
                }
                else {
                    System.out.print("[" + j + "]: ");
                }
                if (match.getBeginning(j) < 0) {
                    System.out.println("-1");
                }
                else {
                    System.out.print(match.getBeginning(j) + ", " + match.getEnd(j) + ", ");
                    System.out.println("\"" + match.getCapturedText(j) + "\"");
                }
            }
        }
        catch (final ParseException ex) {
            if (s == null) {
                ex.printStackTrace();
            }
            else {
                System.err.println("org.apache.xerces.utils.regex.ParseException: " + ex.getMessage());
                final String s4 = "        ";
                System.err.println(s4 + s);
                final int location = ex.getLocation();
                if (location >= 0) {
                    System.err.print(s4);
                    for (int k = 0; k < location; ++k) {
                        System.err.print("-");
                    }
                    System.err.println("^");
                }
            }
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
        }
    }
    
    public static RegularExpression createRegex(final String s, final String s2) throws ParseException {
        RegularExpression regularExpression = null;
        final int options = parseOptions(s2);
        synchronized (REUtil.regexCache) {
            int i;
            for (i = 0; i < 20; ++i) {
                final RegularExpression regularExpression2 = REUtil.regexCache[i];
                if (regularExpression2 == null) {
                    i = -1;
                    break;
                }
                if (regularExpression2.equals(s, options)) {
                    regularExpression = regularExpression2;
                    break;
                }
            }
            if (regularExpression != null) {
                if (i != 0) {
                    System.arraycopy(REUtil.regexCache, 0, REUtil.regexCache, 1, i);
                    REUtil.regexCache[0] = regularExpression;
                }
            }
            else {
                regularExpression = new RegularExpression(s, s2);
                System.arraycopy(REUtil.regexCache, 0, REUtil.regexCache, 1, 19);
                REUtil.regexCache[0] = regularExpression;
            }
        }
        return regularExpression;
    }
    
    public static boolean matches(final String s, final String s2) throws ParseException {
        return createRegex(s, null).matches(s2);
    }
    
    public static boolean matches(final String s, final String s2, final String s3) throws ParseException {
        return createRegex(s, s2).matches(s3);
    }
    
    public static String quoteMeta(final String s) {
        final int length = s.length();
        StringBuffer sb = null;
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (".*+?{[()|\\^$".indexOf(char1) >= 0) {
                if (sb == null) {
                    sb = new StringBuffer(i + (length - i) * 2);
                    if (i > 0) {
                        sb.append(s.substring(0, i));
                    }
                }
                sb.append('\\');
                sb.append(char1);
            }
            else if (sb != null) {
                sb.append(char1);
            }
        }
        return (sb != null) ? sb.toString() : s;
    }
    
    static void dumpString(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            System.out.print(Integer.toHexString(s.charAt(i)));
            System.out.print(" ");
        }
        System.out.println();
    }
    
    static void setupRange(final Token token, final String s) {
        for (int length = s.length(), i = 0; i < length; i += 2) {
            token.addRange(s.charAt(i), s.charAt(i + 1));
        }
    }
    
    static void setupRange(final Token token, final int[] array) {
        for (int length = array.length, i = 0; i < length; i += 2) {
            token.addRange(array[i], array[i + 1]);
        }
    }
    
    static {
        regexCache = new RegularExpression[20];
    }
}
