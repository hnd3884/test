package org.apache.lucene.analysis.no;

import org.apache.lucene.analysis.util.StemmerUtil;

public class NorwegianLightStemmer
{
    public static final int BOKMAAL = 1;
    public static final int NYNORSK = 2;
    final boolean useBokmaal;
    final boolean useNynorsk;
    
    public NorwegianLightStemmer(final int flags) {
        if (flags <= 0 || flags > 3) {
            throw new IllegalArgumentException("invalid flags");
        }
        this.useBokmaal = ((flags & 0x1) != 0x0);
        this.useNynorsk = ((flags & 0x2) != 0x0);
    }
    
    public int stem(final char[] s, int len) {
        if (len > 4 && s[len - 1] == 's') {
            --len;
        }
        if (len > 7 && ((StemmerUtil.endsWith(s, len, "heter") && this.useBokmaal) || (StemmerUtil.endsWith(s, len, "heten") && this.useBokmaal) || (StemmerUtil.endsWith(s, len, "heita") && this.useNynorsk))) {
            return len - 5;
        }
        if (len > 8 && this.useNynorsk && (StemmerUtil.endsWith(s, len, "heiter") || StemmerUtil.endsWith(s, len, "leiken") || StemmerUtil.endsWith(s, len, "leikar"))) {
            return len - 6;
        }
        if (len > 5 && (StemmerUtil.endsWith(s, len, "dom") || (StemmerUtil.endsWith(s, len, "het") && this.useBokmaal))) {
            return len - 3;
        }
        if (len > 6 && this.useNynorsk && (StemmerUtil.endsWith(s, len, "heit") || StemmerUtil.endsWith(s, len, "semd") || StemmerUtil.endsWith(s, len, "leik"))) {
            return len - 4;
        }
        if (len > 7 && (StemmerUtil.endsWith(s, len, "elser") || StemmerUtil.endsWith(s, len, "elsen"))) {
            return len - 5;
        }
        if (len > 6 && ((StemmerUtil.endsWith(s, len, "ende") && this.useBokmaal) || (StemmerUtil.endsWith(s, len, "ande") && this.useNynorsk) || StemmerUtil.endsWith(s, len, "else") || (StemmerUtil.endsWith(s, len, "este") && this.useBokmaal) || (StemmerUtil.endsWith(s, len, "aste") && this.useNynorsk) || (StemmerUtil.endsWith(s, len, "eren") && this.useBokmaal) || (StemmerUtil.endsWith(s, len, "aren") && this.useNynorsk))) {
            return len - 4;
        }
        if (len > 5 && ((StemmerUtil.endsWith(s, len, "ere") && this.useBokmaal) || (StemmerUtil.endsWith(s, len, "are") && this.useNynorsk) || (StemmerUtil.endsWith(s, len, "est") && this.useBokmaal) || (StemmerUtil.endsWith(s, len, "ast") && this.useNynorsk) || StemmerUtil.endsWith(s, len, "ene") || (StemmerUtil.endsWith(s, len, "ane") && this.useNynorsk))) {
            return len - 3;
        }
        if (len > 4 && (StemmerUtil.endsWith(s, len, "er") || StemmerUtil.endsWith(s, len, "en") || StemmerUtil.endsWith(s, len, "et") || (StemmerUtil.endsWith(s, len, "ar") && this.useNynorsk) || (StemmerUtil.endsWith(s, len, "st") && this.useBokmaal) || StemmerUtil.endsWith(s, len, "te"))) {
            return len - 2;
        }
        if (len > 3) {
            switch (s[len - 1]) {
                case 'a':
                case 'e':
                case 'n': {
                    return len - 1;
                }
            }
        }
        return len;
    }
}
