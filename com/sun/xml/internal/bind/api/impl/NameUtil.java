package com.sun.xml.internal.bind.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class NameUtil
{
    protected static final int UPPER_LETTER = 0;
    protected static final int LOWER_LETTER = 1;
    protected static final int OTHER_LETTER = 2;
    protected static final int DIGIT = 3;
    protected static final int OTHER = 4;
    private static final byte[] actionTable;
    private static final byte ACTION_CHECK_PUNCT = 0;
    private static final byte ACTION_CHECK_C2 = 1;
    private static final byte ACTION_BREAK = 2;
    private static final byte ACTION_NOBREAK = 3;
    
    protected boolean isPunct(final char c) {
        return c == '-' || c == '.' || c == ':' || c == '_' || c == '·' || c == '\u0387' || c == '\u06dd' || c == '\u06de';
    }
    
    protected static boolean isDigit(final char c) {
        return (c >= '0' && c <= '9') || Character.isDigit(c);
    }
    
    protected static boolean isUpper(final char c) {
        return (c >= 'A' && c <= 'Z') || Character.isUpperCase(c);
    }
    
    protected static boolean isLower(final char c) {
        return (c >= 'a' && c <= 'z') || Character.isLowerCase(c);
    }
    
    protected boolean isLetter(final char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || Character.isLetter(c);
    }
    
    private String toLowerCase(final String s) {
        return s.toLowerCase(Locale.ENGLISH);
    }
    
    private String toUpperCase(final char c) {
        return String.valueOf(c).toUpperCase(Locale.ENGLISH);
    }
    
    private String toUpperCase(final String s) {
        return s.toUpperCase(Locale.ENGLISH);
    }
    
    public String capitalize(final String s) {
        if (!isLower(s.charAt(0))) {
            return s;
        }
        final StringBuilder sb = new StringBuilder(s.length());
        sb.append(this.toUpperCase(s.charAt(0)));
        sb.append(this.toLowerCase(s.substring(1)));
        return sb.toString();
    }
    
    private int nextBreak(final String s, final int start) {
        final int n = s.length();
        char c1 = s.charAt(start);
        int t1 = this.classify(c1);
        for (int i = start + 1; i < n; ++i) {
            final int t2 = t1;
            c1 = s.charAt(i);
            t1 = this.classify(c1);
            switch (NameUtil.actionTable[t2 * 5 + t1]) {
                case 0: {
                    if (this.isPunct(c1)) {
                        return i;
                    }
                    break;
                }
                case 1: {
                    if (i >= n - 1) {
                        break;
                    }
                    final char c2 = s.charAt(i + 1);
                    if (isLower(c2)) {
                        return i;
                    }
                    break;
                }
                case 2: {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private static byte decideAction(final int t0, final int t1) {
        if (t0 == 4 && t1 == 4) {
            return 0;
        }
        if (!xor(t0 == 3, t1 == 3)) {
            return 2;
        }
        if (t0 == 1 && t1 != 1) {
            return 2;
        }
        if (!xor(t0 <= 2, t1 <= 2)) {
            return 2;
        }
        if (!xor(t0 == 2, t1 == 2)) {
            return 2;
        }
        if (t0 == 0 && t1 == 0) {
            return 1;
        }
        return 3;
    }
    
    private static boolean xor(final boolean x, final boolean y) {
        return (x && y) || (!x && !y);
    }
    
    protected int classify(final char c0) {
        switch (Character.getType(c0)) {
            case 1: {
                return 0;
            }
            case 2: {
                return 1;
            }
            case 3:
            case 4:
            case 5: {
                return 2;
            }
            case 9: {
                return 3;
            }
            default: {
                return 4;
            }
        }
    }
    
    public List<String> toWordList(final String s) {
        final ArrayList<String> ss = new ArrayList<String>();
        int b;
        for (int n = s.length(), i = 0; i < n; i = b) {
            while (i < n && this.isPunct(s.charAt(i))) {
                ++i;
            }
            if (i >= n) {
                break;
            }
            b = this.nextBreak(s, i);
            final String w = (b == -1) ? s.substring(i) : s.substring(i, b);
            ss.add(escape(this.capitalize(w)));
            if (b == -1) {
                break;
            }
        }
        return ss;
    }
    
    protected String toMixedCaseName(final List<String> ss, final boolean startUpper) {
        final StringBuilder sb = new StringBuilder();
        if (!ss.isEmpty()) {
            sb.append(startUpper ? ss.get(0) : this.toLowerCase(ss.get(0)));
            for (int i = 1; i < ss.size(); ++i) {
                sb.append(ss.get(i));
            }
        }
        return sb.toString();
    }
    
    protected String toMixedCaseVariableName(final String[] ss, final boolean startUpper, final boolean cdrUpper) {
        if (cdrUpper) {
            for (int i = 1; i < ss.length; ++i) {
                ss[i] = this.capitalize(ss[i]);
            }
        }
        final StringBuilder sb = new StringBuilder();
        if (ss.length > 0) {
            sb.append(startUpper ? ss[0] : this.toLowerCase(ss[0]));
            for (int j = 1; j < ss.length; ++j) {
                sb.append(ss[j]);
            }
        }
        return sb.toString();
    }
    
    public String toConstantName(final String s) {
        return this.toConstantName(this.toWordList(s));
    }
    
    public String toConstantName(final List<String> ss) {
        final StringBuilder sb = new StringBuilder();
        if (!ss.isEmpty()) {
            sb.append(this.toUpperCase(ss.get(0)));
            for (int i = 1; i < ss.size(); ++i) {
                sb.append('_');
                sb.append(this.toUpperCase(ss.get(i)));
            }
        }
        return sb.toString();
    }
    
    public static void escape(final StringBuilder sb, final String s, final int start) {
        for (int n = s.length(), i = start; i < n; ++i) {
            final char c = s.charAt(i);
            if (Character.isJavaIdentifierPart(c)) {
                sb.append(c);
            }
            else {
                sb.append('_');
                if (c <= '\u000f') {
                    sb.append("000");
                }
                else if (c <= '\u00ff') {
                    sb.append("00");
                }
                else if (c <= '\u0fff') {
                    sb.append('0');
                }
                sb.append(Integer.toString(c, 16));
            }
        }
    }
    
    private static String escape(final String s) {
        for (int n = s.length(), i = 0; i < n; ++i) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                final StringBuilder sb = new StringBuilder(s.substring(0, i));
                escape(sb, s, i);
                return sb.toString();
            }
        }
        return s;
    }
    
    static {
        actionTable = new byte[25];
        for (int t0 = 0; t0 < 5; ++t0) {
            for (int t2 = 0; t2 < 5; ++t2) {
                NameUtil.actionTable[t0 * 5 + t2] = decideAction(t0, t2);
            }
        }
    }
}
