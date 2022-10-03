package org.apache.lucene.analysis.sv;

import org.apache.lucene.analysis.util.StemmerUtil;

public class SwedishLightStemmer
{
    public int stem(final char[] s, int len) {
        if (len > 4 && s[len - 1] == 's') {
            --len;
        }
        if (len > 7 && (StemmerUtil.endsWith(s, len, "elser") || StemmerUtil.endsWith(s, len, "heten"))) {
            return len - 5;
        }
        if (len > 6 && (StemmerUtil.endsWith(s, len, "arne") || StemmerUtil.endsWith(s, len, "erna") || StemmerUtil.endsWith(s, len, "ande") || StemmerUtil.endsWith(s, len, "else") || StemmerUtil.endsWith(s, len, "aste") || StemmerUtil.endsWith(s, len, "orna") || StemmerUtil.endsWith(s, len, "aren"))) {
            return len - 4;
        }
        if (len > 5 && (StemmerUtil.endsWith(s, len, "are") || StemmerUtil.endsWith(s, len, "ast") || StemmerUtil.endsWith(s, len, "het"))) {
            return len - 3;
        }
        if (len > 4 && (StemmerUtil.endsWith(s, len, "ar") || StemmerUtil.endsWith(s, len, "er") || StemmerUtil.endsWith(s, len, "or") || StemmerUtil.endsWith(s, len, "en") || StemmerUtil.endsWith(s, len, "at") || StemmerUtil.endsWith(s, len, "te") || StemmerUtil.endsWith(s, len, "et"))) {
            return len - 2;
        }
        if (len > 3) {
            switch (s[len - 1]) {
                case 'a':
                case 'e':
                case 'n':
                case 't': {
                    return len - 1;
                }
            }
        }
        return len;
    }
}
