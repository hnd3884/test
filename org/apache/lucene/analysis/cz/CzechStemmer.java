package org.apache.lucene.analysis.cz;

import org.apache.lucene.analysis.util.StemmerUtil;

public class CzechStemmer
{
    public int stem(final char[] s, int len) {
        len = this.removeCase(s, len);
        len = this.removePossessives(s, len);
        if (len > 0) {
            len = this.normalize(s, len);
        }
        return len;
    }
    
    private int removeCase(final char[] s, final int len) {
        if (len > 7 && StemmerUtil.endsWith(s, len, "atech")) {
            return len - 5;
        }
        if (len > 6 && (StemmerUtil.endsWith(s, len, "\u011btem") || StemmerUtil.endsWith(s, len, "etem") || StemmerUtil.endsWith(s, len, "at\u016fm"))) {
            return len - 4;
        }
        if (len > 5 && (StemmerUtil.endsWith(s, len, "ech") || StemmerUtil.endsWith(s, len, "ich") || StemmerUtil.endsWith(s, len, "\u00edch") || StemmerUtil.endsWith(s, len, "\u00e9ho") || StemmerUtil.endsWith(s, len, "\u011bmi") || StemmerUtil.endsWith(s, len, "emi") || StemmerUtil.endsWith(s, len, "\u00e9mu") || StemmerUtil.endsWith(s, len, "\u011bte") || StemmerUtil.endsWith(s, len, "ete") || StemmerUtil.endsWith(s, len, "\u011bti") || StemmerUtil.endsWith(s, len, "eti") || StemmerUtil.endsWith(s, len, "\u00edho") || StemmerUtil.endsWith(s, len, "iho") || StemmerUtil.endsWith(s, len, "\u00edmi") || StemmerUtil.endsWith(s, len, "\u00edmu") || StemmerUtil.endsWith(s, len, "imu") || StemmerUtil.endsWith(s, len, "\u00e1ch") || StemmerUtil.endsWith(s, len, "ata") || StemmerUtil.endsWith(s, len, "aty") || StemmerUtil.endsWith(s, len, "\u00fdch") || StemmerUtil.endsWith(s, len, "ama") || StemmerUtil.endsWith(s, len, "ami") || StemmerUtil.endsWith(s, len, "ov\u00e9") || StemmerUtil.endsWith(s, len, "ovi") || StemmerUtil.endsWith(s, len, "\u00fdmi"))) {
            return len - 3;
        }
        if (len > 4 && (StemmerUtil.endsWith(s, len, "em") || StemmerUtil.endsWith(s, len, "es") || StemmerUtil.endsWith(s, len, "\u00e9m") || StemmerUtil.endsWith(s, len, "\u00edm") || StemmerUtil.endsWith(s, len, "\u016fm") || StemmerUtil.endsWith(s, len, "at") || StemmerUtil.endsWith(s, len, "\u00e1m") || StemmerUtil.endsWith(s, len, "os") || StemmerUtil.endsWith(s, len, "us") || StemmerUtil.endsWith(s, len, "\u00fdm") || StemmerUtil.endsWith(s, len, "mi") || StemmerUtil.endsWith(s, len, "ou"))) {
            return len - 2;
        }
        if (len > 3) {
            switch (s[len - 1]) {
                case 'a':
                case 'e':
                case 'i':
                case 'o':
                case 'u':
                case 'y':
                case '\u00e1':
                case '\u00e9':
                case '\u00ed':
                case '\u00fd':
                case '\u011b':
                case '\u016f': {
                    return len - 1;
                }
            }
        }
        return len;
    }
    
    private int removePossessives(final char[] s, final int len) {
        if (len > 5 && (StemmerUtil.endsWith(s, len, "ov") || StemmerUtil.endsWith(s, len, "in") || StemmerUtil.endsWith(s, len, "\u016fv"))) {
            return len - 2;
        }
        return len;
    }
    
    private int normalize(final char[] s, final int len) {
        if (StemmerUtil.endsWith(s, len, "\u010dt")) {
            s[len - 2] = 'c';
            s[len - 1] = 'k';
            return len;
        }
        if (StemmerUtil.endsWith(s, len, "\u0161t")) {
            s[len - 2] = 's';
            s[len - 1] = 'k';
            return len;
        }
        switch (s[len - 1]) {
            case 'c':
            case '\u010d': {
                s[len - 1] = 'k';
                return len;
            }
            case 'z':
            case '\u017e': {
                s[len - 1] = 'h';
                return len;
            }
            default: {
                if (len > 1 && s[len - 2] == 'e') {
                    s[len - 2] = s[len - 1];
                    return len - 1;
                }
                if (len > 2 && s[len - 2] == '\u016f') {
                    s[len - 2] = 'o';
                    return len;
                }
                return len;
            }
        }
    }
}
