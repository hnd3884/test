package org.apache.xmlbeans.impl.common;

public class XmlWhitespace
{
    public static final int WS_UNSPECIFIED = 0;
    public static final int WS_PRESERVE = 1;
    public static final int WS_REPLACE = 2;
    public static final int WS_COLLAPSE = 3;
    
    public static boolean isSpace(final char ch) {
        switch (ch) {
            case '\t':
            case '\n':
            case '\r':
            case ' ': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isAllSpace(final String v) {
        for (int i = 0, len = v.length(); i < len; ++i) {
            if (!isSpace(v.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAllSpace(final CharSequence v) {
        for (int i = 0, len = v.length(); i < len; ++i) {
            if (!isSpace(v.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static String collapse(final String v) {
        return collapse(v, 3);
    }
    
    public static String collapse(String v, final int wsr) {
        if (wsr == 1 || wsr == 0) {
            return v;
        }
        if (v.indexOf(10) >= 0) {
            v = v.replace('\n', ' ');
        }
        if (v.indexOf(9) >= 0) {
            v = v.replace('\t', ' ');
        }
        if (v.indexOf(13) >= 0) {
            v = v.replace('\r', ' ');
        }
        if (wsr == 2) {
            return v;
        }
        int j = 0;
        final int len = v.length();
        if (len == 0) {
            return v;
        }
        int i = 0;
        Label_0218: {
            if (v.charAt(0) != ' ') {
                while (true) {
                    for (j = 2; j < len; j += 2) {
                        if (v.charAt(j) == ' ') {
                            if (v.charAt(j - 1) != ' ') {
                                if (j != len - 1) {
                                    ++j;
                                    if (v.charAt(j) != ' ') {
                                        continue;
                                    }
                                }
                            }
                            i = j;
                            break Label_0218;
                        }
                    }
                    if (j == len && v.charAt(j - 1) == ' ') {
                        continue;
                    }
                    break;
                }
                return v;
            }
            else {
                while (j + 1 < v.length() && v.charAt(j + 1) == ' ') {
                    ++j;
                }
                i = 0;
            }
        }
        final char[] ch = v.toCharArray();
    Label_0224:
        while (++j < len) {
            if (v.charAt(j) != ' ') {
                while (true) {
                    ch[i++] = ch[j++];
                    if (j >= len) {
                        break;
                    }
                    if (ch[j] != ' ') {
                        continue;
                    }
                    ch[i++] = ch[j++];
                    if (j >= len) {
                        break;
                    }
                    if (ch[j] == ' ') {
                        continue Label_0224;
                    }
                }
                return new String(ch, 0, (i == 0 || ch[i - 1] != ' ') ? i : (i - 1));
            }
        }
        return new String(ch, 0, (i == 0 || ch[i - 1] != ' ') ? i : (i - 1));
    }
}
