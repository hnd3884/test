package org.apache.lucene.analysis.pt;

import org.apache.lucene.analysis.util.StemmerUtil;

public class PortugueseLightStemmer
{
    public int stem(final char[] s, int len) {
        if (len < 4) {
            return len;
        }
        len = this.removeSuffix(s, len);
        if (len > 3 && s[len - 1] == 'a') {
            len = this.normFeminine(s, len);
        }
        if (len > 4) {
            switch (s[len - 1]) {
                case 'a':
                case 'e':
                case 'o': {
                    --len;
                    break;
                }
            }
        }
        for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u00e0':
                case '\u00e1':
                case '\u00e2':
                case '\u00e3':
                case '\u00e4': {
                    s[i] = 'a';
                    break;
                }
                case '\u00f2':
                case '\u00f3':
                case '\u00f4':
                case '\u00f5':
                case '\u00f6': {
                    s[i] = 'o';
                    break;
                }
                case '\u00e8':
                case '\u00e9':
                case '\u00ea':
                case '\u00eb': {
                    s[i] = 'e';
                    break;
                }
                case '\u00f9':
                case '\u00fa':
                case '\u00fb':
                case '\u00fc': {
                    s[i] = 'u';
                    break;
                }
                case '\u00ec':
                case '\u00ed':
                case '\u00ee':
                case '\u00ef': {
                    s[i] = 'i';
                    break;
                }
                case '\u00e7': {
                    s[i] = 'c';
                    break;
                }
            }
        }
        return len;
    }
    
    private int removeSuffix(final char[] s, int len) {
        if (len > 4 && StemmerUtil.endsWith(s, len, "es")) {
            switch (s[len - 3]) {
                case 'l':
                case 'r':
                case 's':
                case 'z': {
                    return len - 2;
                }
            }
        }
        if (len > 3 && StemmerUtil.endsWith(s, len, "ns")) {
            s[len - 2] = 'm';
            return len - 1;
        }
        if (len > 4 && (StemmerUtil.endsWith(s, len, "eis") || StemmerUtil.endsWith(s, len, "\u00e9is"))) {
            s[len - 3] = 'e';
            s[len - 2] = 'l';
            return len - 1;
        }
        if (len > 4 && StemmerUtil.endsWith(s, len, "ais")) {
            s[len - 2] = 'l';
            return len - 1;
        }
        if (len > 4 && StemmerUtil.endsWith(s, len, "\u00f3is")) {
            s[len - 3] = 'o';
            s[len - 2] = 'l';
            return len - 1;
        }
        if (len > 4 && StemmerUtil.endsWith(s, len, "is")) {
            s[len - 1] = 'l';
            return len;
        }
        if (len > 3 && (StemmerUtil.endsWith(s, len, "\u00f5es") || StemmerUtil.endsWith(s, len, "\u00e3es"))) {
            --len;
            s[len - 2] = '\u00e3';
            s[len - 1] = 'o';
            return len;
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "mente")) {
            return len - 5;
        }
        if (len > 3 && s[len - 1] == 's') {
            return len - 1;
        }
        return len;
    }
    
    private int normFeminine(final char[] s, final int len) {
        if (len > 7 && (StemmerUtil.endsWith(s, len, "inha") || StemmerUtil.endsWith(s, len, "iaca") || StemmerUtil.endsWith(s, len, "eira"))) {
            s[len - 1] = 'o';
            return len;
        }
        if (len > 6) {
            if (StemmerUtil.endsWith(s, len, "osa") || StemmerUtil.endsWith(s, len, "ica") || StemmerUtil.endsWith(s, len, "ida") || StemmerUtil.endsWith(s, len, "ada") || StemmerUtil.endsWith(s, len, "iva") || StemmerUtil.endsWith(s, len, "ama")) {
                s[len - 1] = 'o';
                return len;
            }
            if (StemmerUtil.endsWith(s, len, "ona")) {
                s[len - 3] = '\u00e3';
                s[len - 2] = 'o';
                return len - 1;
            }
            if (StemmerUtil.endsWith(s, len, "ora")) {
                return len - 1;
            }
            if (StemmerUtil.endsWith(s, len, "esa")) {
                s[len - 3] = '\u00ea';
                return len - 1;
            }
            if (StemmerUtil.endsWith(s, len, "na")) {
                s[len - 1] = 'o';
                return len;
            }
        }
        return len;
    }
}
