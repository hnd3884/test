package org.apache.lucene.analysis.no;

import org.apache.lucene.analysis.util.StemmerUtil;

public class NorwegianMinimalStemmer
{
    final boolean useBokmaal;
    final boolean useNynorsk;
    
    public NorwegianMinimalStemmer(final int flags) {
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
        if (len > 5 && (StemmerUtil.endsWith(s, len, "ene") || (StemmerUtil.endsWith(s, len, "ane") && this.useNynorsk))) {
            return len - 3;
        }
        if (len > 4 && (StemmerUtil.endsWith(s, len, "er") || StemmerUtil.endsWith(s, len, "en") || StemmerUtil.endsWith(s, len, "et") || (StemmerUtil.endsWith(s, len, "ar") && this.useNynorsk))) {
            return len - 2;
        }
        if (len > 3) {
            switch (s[len - 1]) {
                case 'a':
                case 'e': {
                    return len - 1;
                }
            }
        }
        return len;
    }
}
