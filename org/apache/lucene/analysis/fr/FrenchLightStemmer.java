package org.apache.lucene.analysis.fr;

import org.apache.lucene.analysis.util.StemmerUtil;

public class FrenchLightStemmer
{
    public int stem(final char[] s, int len) {
        if (len > 5 && s[len - 1] == 'x') {
            if (s[len - 3] == 'a' && s[len - 2] == 'u' && s[len - 4] != 'e') {
                s[len - 2] = 'l';
            }
            --len;
        }
        if (len > 3 && s[len - 1] == 'x') {
            --len;
        }
        if (len > 3 && s[len - 1] == 's') {
            --len;
        }
        if (len > 9 && StemmerUtil.endsWith(s, len, "issement")) {
            len -= 6;
            s[len - 1] = 'r';
            return this.norm(s, len);
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "issant")) {
            len -= 4;
            s[len - 1] = 'r';
            return this.norm(s, len);
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "ement")) {
            len -= 4;
            if (len > 3 && StemmerUtil.endsWith(s, len, "ive")) {
                --len;
                s[len - 1] = 'f';
            }
            return this.norm(s, len);
        }
        if (len > 11 && StemmerUtil.endsWith(s, len, "ficatrice")) {
            len -= 5;
            s[len - 2] = 'e';
            s[len - 1] = 'r';
            return this.norm(s, len);
        }
        if (len > 10 && StemmerUtil.endsWith(s, len, "ficateur")) {
            len -= 4;
            s[len - 2] = 'e';
            s[len - 1] = 'r';
            return this.norm(s, len);
        }
        if (len > 9 && StemmerUtil.endsWith(s, len, "catrice")) {
            len -= 3;
            s[len - 4] = 'q';
            s[len - 3] = 'u';
            s[len - 2] = 'e';
            return this.norm(s, len);
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "cateur")) {
            len -= 2;
            s[len - 4] = 'q';
            s[len - 3] = 'u';
            s[len - 2] = 'e';
            s[len - 1] = 'r';
            return this.norm(s, len);
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "atrice")) {
            len -= 4;
            s[len - 2] = 'e';
            s[len - 1] = 'r';
            return this.norm(s, len);
        }
        if (len > 7 && StemmerUtil.endsWith(s, len, "ateur")) {
            len -= 3;
            s[len - 2] = 'e';
            s[len - 1] = 'r';
            return this.norm(s, len);
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "trice")) {
            --len;
            s[len - 3] = 'e';
            s[len - 2] = 'u';
            s[len - 1] = 'r';
        }
        if (len > 5 && StemmerUtil.endsWith(s, len, "i\u00e8me")) {
            return this.norm(s, len - 4);
        }
        if (len > 7 && StemmerUtil.endsWith(s, len, "teuse")) {
            len -= 2;
            s[len - 1] = 'r';
            return this.norm(s, len);
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "teur")) {
            --len;
            s[len - 1] = 'r';
            return this.norm(s, len);
        }
        if (len > 5 && StemmerUtil.endsWith(s, len, "euse")) {
            return this.norm(s, len - 2);
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "\u00e8re")) {
            --len;
            s[len - 2] = 'e';
            return this.norm(s, len);
        }
        if (len > 7 && StemmerUtil.endsWith(s, len, "ive")) {
            --len;
            s[len - 1] = 'f';
            return this.norm(s, len);
        }
        if (len > 4 && (StemmerUtil.endsWith(s, len, "folle") || StemmerUtil.endsWith(s, len, "molle"))) {
            len -= 2;
            s[len - 1] = 'u';
            return this.norm(s, len);
        }
        if (len > 9 && StemmerUtil.endsWith(s, len, "nnelle")) {
            return this.norm(s, len - 5);
        }
        if (len > 9 && StemmerUtil.endsWith(s, len, "nnel")) {
            return this.norm(s, len - 3);
        }
        if (len > 4 && StemmerUtil.endsWith(s, len, "\u00e8te")) {
            --len;
            s[len - 2] = 'e';
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "ique")) {
            len -= 4;
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "esse")) {
            return this.norm(s, len - 3);
        }
        if (len > 7 && StemmerUtil.endsWith(s, len, "inage")) {
            return this.norm(s, len - 3);
        }
        if (len > 9 && StemmerUtil.endsWith(s, len, "isation")) {
            len -= 7;
            if (len > 5 && StemmerUtil.endsWith(s, len, "ual")) {
                s[len - 2] = 'e';
            }
            return this.norm(s, len);
        }
        if (len > 9 && StemmerUtil.endsWith(s, len, "isateur")) {
            return this.norm(s, len - 7);
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "ation")) {
            return this.norm(s, len - 5);
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "ition")) {
            return this.norm(s, len - 5);
        }
        return this.norm(s, len);
    }
    
    private int norm(final char[] s, int len) {
        if (len > 4) {
            for (int i = 0; i < len; ++i) {
                switch (s[i]) {
                    case '\u00e0':
                    case '\u00e1':
                    case '\u00e2': {
                        s[i] = 'a';
                        break;
                    }
                    case '\u00f4': {
                        s[i] = 'o';
                        break;
                    }
                    case '\u00e8':
                    case '\u00e9':
                    case '\u00ea': {
                        s[i] = 'e';
                        break;
                    }
                    case '\u00f9':
                    case '\u00fb': {
                        s[i] = 'u';
                        break;
                    }
                    case '\u00ee': {
                        s[i] = 'i';
                        break;
                    }
                    case '\u00e7': {
                        s[i] = 'c';
                        break;
                    }
                }
            }
            char ch = s[0];
            for (int j = 1; j < len; ++j) {
                if (s[j] == ch && Character.isLetter(ch)) {
                    len = StemmerUtil.delete(s, j--, len);
                }
                else {
                    ch = s[j];
                }
            }
        }
        if (len > 4 && StemmerUtil.endsWith(s, len, "ie")) {
            len -= 2;
        }
        if (len > 4) {
            if (s[len - 1] == 'r') {
                --len;
            }
            if (s[len - 1] == 'e') {
                --len;
            }
            if (s[len - 1] == 'e') {
                --len;
            }
            if (s[len - 1] == s[len - 2] && Character.isLetter(s[len - 1])) {
                --len;
            }
        }
        return len;
    }
}
