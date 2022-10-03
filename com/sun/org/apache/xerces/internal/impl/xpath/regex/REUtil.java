package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.text.CharacterIterator;

public final class REUtil
{
    static final int CACHESIZE = 20;
    static final RegularExpression[] regexCache;
    
    private REUtil() {
    }
    
    static final int composeFromSurrogates(final int high, final int low) {
        return 65536 + (high - 55296 << 10) + low - 56320;
    }
    
    static final boolean isLowSurrogate(final int ch) {
        return (ch & 0xFC00) == 0xDC00;
    }
    
    static final boolean isHighSurrogate(final int ch) {
        return (ch & 0xFC00) == 0xD800;
    }
    
    static final String decomposeToSurrogates(int ch) {
        final char[] chs = new char[2];
        ch -= 65536;
        chs[0] = (char)((ch >> 10) + 55296);
        chs[1] = (char)((ch & 0x3FF) + 56320);
        return new String(chs);
    }
    
    static final String substring(final CharacterIterator iterator, final int begin, final int end) {
        final char[] src = new char[end - begin];
        for (int i = 0; i < src.length; ++i) {
            src[i] = iterator.setIndex(i + begin);
        }
        return new String(src);
    }
    
    static final int getOptionValue(final int ch) {
        int ret = 0;
        switch (ch) {
            case 105: {
                ret = 2;
                break;
            }
            case 109: {
                ret = 8;
                break;
            }
            case 115: {
                ret = 4;
                break;
            }
            case 120: {
                ret = 16;
                break;
            }
            case 117: {
                ret = 32;
                break;
            }
            case 119: {
                ret = 64;
                break;
            }
            case 70: {
                ret = 256;
                break;
            }
            case 72: {
                ret = 128;
                break;
            }
            case 88: {
                ret = 512;
                break;
            }
            case 44: {
                ret = 1024;
                break;
            }
        }
        return ret;
    }
    
    static final int parseOptions(final String opts) throws ParseException {
        if (opts == null) {
            return 0;
        }
        int options = 0;
        for (int i = 0; i < opts.length(); ++i) {
            final int v = getOptionValue(opts.charAt(i));
            if (v == 0) {
                throw new ParseException("Unknown Option: " + opts.substring(i), -1);
            }
            options |= v;
        }
        return options;
    }
    
    static final String createOptionString(final int options) {
        final StringBuffer sb = new StringBuffer(9);
        if ((options & 0x100) != 0x0) {
            sb.append('F');
        }
        if ((options & 0x80) != 0x0) {
            sb.append('H');
        }
        if ((options & 0x200) != 0x0) {
            sb.append('X');
        }
        if ((options & 0x2) != 0x0) {
            sb.append('i');
        }
        if ((options & 0x8) != 0x0) {
            sb.append('m');
        }
        if ((options & 0x4) != 0x0) {
            sb.append('s');
        }
        if ((options & 0x20) != 0x0) {
            sb.append('u');
        }
        if ((options & 0x40) != 0x0) {
            sb.append('w');
        }
        if ((options & 0x10) != 0x0) {
            sb.append('x');
        }
        if ((options & 0x400) != 0x0) {
            sb.append(',');
        }
        return sb.toString().intern();
    }
    
    static String stripExtendedComment(final String regex) {
        final int len = regex.length();
        final StringBuffer buffer = new StringBuffer(len);
        int offset = 0;
        while (offset < len) {
            int ch = regex.charAt(offset++);
            if (ch != 9 && ch != 10 && ch != 12 && ch != 13) {
                if (ch == 32) {
                    continue;
                }
                if (ch == 35) {
                    while (offset < len) {
                        ch = regex.charAt(offset++);
                        if (ch == 13) {
                            break;
                        }
                        if (ch == 10) {
                            break;
                        }
                    }
                }
                else if (ch == 92 && offset < len) {
                    final int next;
                    if ((next = regex.charAt(offset)) == 35 || next == 9 || next == 10 || next == 12 || next == 13 || next == 32) {
                        buffer.append((char)next);
                        ++offset;
                    }
                    else {
                        buffer.append('\\');
                        buffer.append((char)next);
                        ++offset;
                    }
                }
                else {
                    buffer.append((char)ch);
                }
            }
        }
        return buffer.toString();
    }
    
    public static void main(final String[] argv) {
        String pattern = null;
        try {
            String options = "";
            String target = null;
            if (argv.length == 0) {
                System.out.println("Error:Usage: java REUtil -i|-m|-s|-u|-w|-X regularExpression String");
                System.exit(0);
            }
            for (int i = 0; i < argv.length; ++i) {
                if (argv[i].length() == 0 || argv[i].charAt(0) != '-') {
                    if (pattern == null) {
                        pattern = argv[i];
                    }
                    else if (target == null) {
                        target = argv[i];
                    }
                    else {
                        System.err.println("Unnecessary: " + argv[i]);
                    }
                }
                else if (argv[i].equals("-i")) {
                    options += "i";
                }
                else if (argv[i].equals("-m")) {
                    options += "m";
                }
                else if (argv[i].equals("-s")) {
                    options += "s";
                }
                else if (argv[i].equals("-u")) {
                    options += "u";
                }
                else if (argv[i].equals("-w")) {
                    options += "w";
                }
                else if (argv[i].equals("-X")) {
                    options += "X";
                }
                else {
                    System.err.println("Unknown option: " + argv[i]);
                }
            }
            final RegularExpression reg = new RegularExpression(pattern, options);
            System.out.println("RegularExpression: " + reg);
            final Match match = new Match();
            reg.matches(target, match);
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
        catch (final ParseException pe) {
            if (pattern == null) {
                pe.printStackTrace();
            }
            else {
                System.err.println("com.sun.org.apache.xerces.internal.utils.regex.ParseException: " + pe.getMessage());
                final String indent = "        ";
                System.err.println(indent + pattern);
                final int loc = pe.getLocation();
                if (loc >= 0) {
                    System.err.print(indent);
                    for (int k = 0; k < loc; ++k) {
                        System.err.print("-");
                    }
                    System.err.println("^");
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static RegularExpression createRegex(final String pattern, final String options) throws ParseException {
        RegularExpression re = null;
        final int intOptions = parseOptions(options);
        synchronized (REUtil.regexCache) {
            int i;
            for (i = 0; i < 20; ++i) {
                final RegularExpression cached = REUtil.regexCache[i];
                if (cached == null) {
                    i = -1;
                    break;
                }
                if (cached.equals(pattern, intOptions)) {
                    re = cached;
                    break;
                }
            }
            if (re != null) {
                if (i != 0) {
                    System.arraycopy(REUtil.regexCache, 0, REUtil.regexCache, 1, i);
                    REUtil.regexCache[0] = re;
                }
            }
            else {
                re = new RegularExpression(pattern, options);
                System.arraycopy(REUtil.regexCache, 0, REUtil.regexCache, 1, 19);
                REUtil.regexCache[0] = re;
            }
        }
        return re;
    }
    
    public static boolean matches(final String regex, final String target) throws ParseException {
        return createRegex(regex, null).matches(target);
    }
    
    public static boolean matches(final String regex, final String options, final String target) throws ParseException {
        return createRegex(regex, options).matches(target);
    }
    
    public static String quoteMeta(final String literal) {
        final int len = literal.length();
        StringBuffer buffer = null;
        for (int i = 0; i < len; ++i) {
            final int ch = literal.charAt(i);
            if (".*+?{[()|\\^$".indexOf(ch) >= 0) {
                if (buffer == null) {
                    buffer = new StringBuffer(i + (len - i) * 2);
                    if (i > 0) {
                        buffer.append(literal.substring(0, i));
                    }
                }
                buffer.append('\\');
                buffer.append((char)ch);
            }
            else if (buffer != null) {
                buffer.append((char)ch);
            }
        }
        return (buffer != null) ? buffer.toString() : literal;
    }
    
    static void dumpString(final String v) {
        for (int i = 0; i < v.length(); ++i) {
            System.out.print(Integer.toHexString(v.charAt(i)));
            System.out.print(" ");
        }
        System.out.println();
    }
    
    static {
        regexCache = new RegularExpression[20];
    }
}
