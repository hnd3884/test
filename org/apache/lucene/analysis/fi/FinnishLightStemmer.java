package org.apache.lucene.analysis.fi;

import org.apache.lucene.analysis.util.StemmerUtil;

public class FinnishLightStemmer
{
    public int stem(final char[] s, int len) {
        if (len < 4) {
            return len;
        }
        for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u00e4':
                case '\u00e5': {
                    s[i] = 'a';
                    break;
                }
                case '\u00f6': {
                    s[i] = 'o';
                    break;
                }
            }
        }
        len = this.step1(s, len);
        len = this.step2(s, len);
        len = this.step3(s, len);
        len = this.norm1(s, len);
        len = this.norm2(s, len);
        return len;
    }
    
    private int step1(final char[] s, final int len) {
        if (len > 8) {
            if (StemmerUtil.endsWith(s, len, "kin")) {
                return this.step1(s, len - 3);
            }
            if (StemmerUtil.endsWith(s, len, "ko")) {
                return this.step1(s, len - 2);
            }
        }
        if (len > 11) {
            if (StemmerUtil.endsWith(s, len, "dellinen")) {
                return len - 8;
            }
            if (StemmerUtil.endsWith(s, len, "dellisuus")) {
                return len - 9;
            }
        }
        return len;
    }
    
    private int step2(final char[] s, final int len) {
        if (len > 5) {
            if (StemmerUtil.endsWith(s, len, "lla") || StemmerUtil.endsWith(s, len, "tse") || StemmerUtil.endsWith(s, len, "sti")) {
                return len - 3;
            }
            if (StemmerUtil.endsWith(s, len, "ni")) {
                return len - 2;
            }
            if (StemmerUtil.endsWith(s, len, "aa")) {
                return len - 1;
            }
        }
        return len;
    }
    
    private int step3(final char[] s, final int len) {
        if (len > 8) {
            if (StemmerUtil.endsWith(s, len, "nnen")) {
                s[len - 4] = 's';
                return len - 3;
            }
            if (StemmerUtil.endsWith(s, len, "ntena")) {
                s[len - 5] = 's';
                return len - 4;
            }
            if (StemmerUtil.endsWith(s, len, "tten")) {
                return len - 4;
            }
            if (StemmerUtil.endsWith(s, len, "eiden")) {
                return len - 5;
            }
        }
        if (len > 6) {
            if (StemmerUtil.endsWith(s, len, "neen") || StemmerUtil.endsWith(s, len, "niin") || StemmerUtil.endsWith(s, len, "seen") || StemmerUtil.endsWith(s, len, "teen") || StemmerUtil.endsWith(s, len, "inen")) {
                return len - 4;
            }
            if (s[len - 3] == 'h' && this.isVowel(s[len - 2]) && s[len - 1] == 'n') {
                return len - 3;
            }
            if (StemmerUtil.endsWith(s, len, "den")) {
                s[len - 3] = 's';
                return len - 2;
            }
            if (StemmerUtil.endsWith(s, len, "ksen")) {
                s[len - 4] = 's';
                return len - 3;
            }
            if (StemmerUtil.endsWith(s, len, "ssa") || StemmerUtil.endsWith(s, len, "sta") || StemmerUtil.endsWith(s, len, "lla") || StemmerUtil.endsWith(s, len, "lta") || StemmerUtil.endsWith(s, len, "tta") || StemmerUtil.endsWith(s, len, "ksi") || StemmerUtil.endsWith(s, len, "lle")) {
                return len - 3;
            }
        }
        if (len > 5) {
            if (StemmerUtil.endsWith(s, len, "na") || StemmerUtil.endsWith(s, len, "ne")) {
                return len - 2;
            }
            if (StemmerUtil.endsWith(s, len, "nei")) {
                return len - 3;
            }
        }
        if (len > 4) {
            if (StemmerUtil.endsWith(s, len, "ja") || StemmerUtil.endsWith(s, len, "ta")) {
                return len - 2;
            }
            if (s[len - 1] == 'a') {
                return len - 1;
            }
            if (s[len - 1] == 'n' && this.isVowel(s[len - 2])) {
                return len - 2;
            }
            if (s[len - 1] == 'n') {
                return len - 1;
            }
        }
        return len;
    }
    
    private int norm1(final char[] s, final int len) {
        if (len > 5 && StemmerUtil.endsWith(s, len, "hde")) {
            s[len - 3] = 'k';
            s[len - 2] = 's';
            s[len - 1] = 'i';
        }
        if (len > 4 && (StemmerUtil.endsWith(s, len, "ei") || StemmerUtil.endsWith(s, len, "at"))) {
            return len - 2;
        }
        if (len > 3) {
            switch (s[len - 1]) {
                case 'a':
                case 'e':
                case 'i':
                case 'j':
                case 's':
                case 't': {
                    return len - 1;
                }
            }
        }
        return len;
    }
    
    private int norm2(final char[] s, int len) {
        if (len > 8 && (s[len - 1] == 'e' || s[len - 1] == 'o' || s[len - 1] == 'u')) {
            --len;
        }
        if (len > 4) {
            if (s[len - 1] == 'i') {
                --len;
            }
            if (len > 4) {
                char ch = s[0];
                for (int i = 1; i < len; ++i) {
                    if (s[i] == ch && (ch == 'k' || ch == 'p' || ch == 't')) {
                        len = StemmerUtil.delete(s, i--, len);
                    }
                    else {
                        ch = s[i];
                    }
                }
            }
        }
        return len;
    }
    
    private boolean isVowel(final char ch) {
        switch (ch) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
            case 'y': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
