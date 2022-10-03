package org.apache.lucene.analysis.hu;

import org.apache.lucene.analysis.util.StemmerUtil;

public class HungarianLightStemmer
{
    public int stem(final char[] s, int len) {
        for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u00e1': {
                    s[i] = 'a';
                    break;
                }
                case '\u00e9':
                case '\u00eb': {
                    s[i] = 'e';
                    break;
                }
                case '\u00ed': {
                    s[i] = 'i';
                    break;
                }
                case '\u00f3':
                case '\u00f5':
                case '\u00f6':
                case '\u0151': {
                    s[i] = 'o';
                    break;
                }
                case '\u00fa':
                case '\u00fb':
                case '\u00fc':
                case '\u0169':
                case '\u0171': {
                    s[i] = 'u';
                    break;
                }
            }
        }
        len = this.removeCase(s, len);
        len = this.removePossessive(s, len);
        len = this.removePlural(s, len);
        return this.normalize(s, len);
    }
    
    private int removeCase(final char[] s, final int len) {
        if (len > 6 && StemmerUtil.endsWith(s, len, "kent")) {
            return len - 4;
        }
        if (len > 5) {
            if (StemmerUtil.endsWith(s, len, "nak") || StemmerUtil.endsWith(s, len, "nek") || StemmerUtil.endsWith(s, len, "val") || StemmerUtil.endsWith(s, len, "vel") || StemmerUtil.endsWith(s, len, "ert") || StemmerUtil.endsWith(s, len, "rol") || StemmerUtil.endsWith(s, len, "ban") || StemmerUtil.endsWith(s, len, "ben") || StemmerUtil.endsWith(s, len, "bol") || StemmerUtil.endsWith(s, len, "nal") || StemmerUtil.endsWith(s, len, "nel") || StemmerUtil.endsWith(s, len, "hoz") || StemmerUtil.endsWith(s, len, "hez") || StemmerUtil.endsWith(s, len, "tol")) {
                return len - 3;
            }
            if ((StemmerUtil.endsWith(s, len, "al") || StemmerUtil.endsWith(s, len, "el")) && !this.isVowel(s[len - 3]) && s[len - 3] == s[len - 4]) {
                return len - 3;
            }
        }
        if (len > 4) {
            if (StemmerUtil.endsWith(s, len, "at") || StemmerUtil.endsWith(s, len, "et") || StemmerUtil.endsWith(s, len, "ot") || StemmerUtil.endsWith(s, len, "va") || StemmerUtil.endsWith(s, len, "ve") || StemmerUtil.endsWith(s, len, "ra") || StemmerUtil.endsWith(s, len, "re") || StemmerUtil.endsWith(s, len, "ba") || StemmerUtil.endsWith(s, len, "be") || StemmerUtil.endsWith(s, len, "ul") || StemmerUtil.endsWith(s, len, "ig")) {
                return len - 2;
            }
            if ((StemmerUtil.endsWith(s, len, "on") || StemmerUtil.endsWith(s, len, "en")) && !this.isVowel(s[len - 3])) {
                return len - 2;
            }
            switch (s[len - 1]) {
                case 'n':
                case 't': {
                    return len - 1;
                }
                case 'a':
                case 'e': {
                    if (s[len - 2] == s[len - 3] && !this.isVowel(s[len - 2])) {
                        return len - 2;
                    }
                    break;
                }
            }
        }
        return len;
    }
    
    private int removePossessive(final char[] s, final int len) {
        if (len > 6) {
            if (!this.isVowel(s[len - 5]) && (StemmerUtil.endsWith(s, len, "atok") || StemmerUtil.endsWith(s, len, "otok") || StemmerUtil.endsWith(s, len, "etek"))) {
                return len - 4;
            }
            if (StemmerUtil.endsWith(s, len, "itek") || StemmerUtil.endsWith(s, len, "itok")) {
                return len - 4;
            }
        }
        if (len > 5) {
            if (!this.isVowel(s[len - 4]) && (StemmerUtil.endsWith(s, len, "unk") || StemmerUtil.endsWith(s, len, "tok") || StemmerUtil.endsWith(s, len, "tek"))) {
                return len - 3;
            }
            if (this.isVowel(s[len - 4]) && StemmerUtil.endsWith(s, len, "juk")) {
                return len - 3;
            }
            if (StemmerUtil.endsWith(s, len, "ink")) {
                return len - 3;
            }
        }
        if (len > 4) {
            if (!this.isVowel(s[len - 3]) && (StemmerUtil.endsWith(s, len, "am") || StemmerUtil.endsWith(s, len, "em") || StemmerUtil.endsWith(s, len, "om") || StemmerUtil.endsWith(s, len, "ad") || StemmerUtil.endsWith(s, len, "ed") || StemmerUtil.endsWith(s, len, "od") || StemmerUtil.endsWith(s, len, "uk"))) {
                return len - 2;
            }
            if (this.isVowel(s[len - 3]) && (StemmerUtil.endsWith(s, len, "nk") || StemmerUtil.endsWith(s, len, "ja") || StemmerUtil.endsWith(s, len, "je"))) {
                return len - 2;
            }
            if (StemmerUtil.endsWith(s, len, "im") || StemmerUtil.endsWith(s, len, "id") || StemmerUtil.endsWith(s, len, "ik")) {
                return len - 2;
            }
        }
        if (len > 3) {
            switch (s[len - 1]) {
                case 'a':
                case 'e': {
                    if (!this.isVowel(s[len - 2])) {
                        return len - 1;
                    }
                    break;
                }
                case 'd':
                case 'm': {
                    if (this.isVowel(s[len - 2])) {
                        return len - 1;
                    }
                    break;
                }
                case 'i': {
                    return len - 1;
                }
            }
        }
        return len;
    }
    
    private int removePlural(final char[] s, final int len) {
        if (len > 3 && s[len - 1] == 'k') {
            switch (s[len - 2]) {
                case 'a':
                case 'e':
                case 'o': {
                    if (len > 4) {
                        return len - 2;
                    }
                    break;
                }
            }
            return len - 1;
        }
        return len;
    }
    
    private int normalize(final char[] s, final int len) {
        if (len > 3) {
            switch (s[len - 1]) {
                case 'a':
                case 'e':
                case 'i':
                case 'o': {
                    return len - 1;
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
