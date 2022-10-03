package org.apache.lucene.analysis.ru;

import org.apache.lucene.analysis.util.StemmerUtil;

public class RussianLightStemmer
{
    public int stem(final char[] s, int len) {
        len = this.removeCase(s, len);
        return this.normalize(s, len);
    }
    
    private int normalize(final char[] s, final int len) {
        if (len > 3) {
            switch (s[len - 1]) {
                case '\u0438':
                case '\u044c': {
                    return len - 1;
                }
                case '\u043d': {
                    if (s[len - 2] == '\u043d') {
                        return len - 1;
                    }
                    break;
                }
            }
        }
        return len;
    }
    
    private int removeCase(final char[] s, final int len) {
        if (len > 6 && (StemmerUtil.endsWith(s, len, "\u0438\u044f\u043c\u0438") || StemmerUtil.endsWith(s, len, "\u043e\u044f\u043c\u0438"))) {
            return len - 4;
        }
        if (len > 5 && (StemmerUtil.endsWith(s, len, "\u0438\u044f\u043c") || StemmerUtil.endsWith(s, len, "\u0438\u044f\u0445") || StemmerUtil.endsWith(s, len, "\u043e\u044f\u0445") || StemmerUtil.endsWith(s, len, "\u044f\u043c\u0438") || StemmerUtil.endsWith(s, len, "\u043e\u044f\u043c") || StemmerUtil.endsWith(s, len, "\u043e\u044c\u0432") || StemmerUtil.endsWith(s, len, "\u0430\u043c\u0438") || StemmerUtil.endsWith(s, len, "\u0435\u0433\u043e") || StemmerUtil.endsWith(s, len, "\u0435\u043c\u0443") || StemmerUtil.endsWith(s, len, "\u0435\u0440\u0438") || StemmerUtil.endsWith(s, len, "\u0438\u043c\u0438") || StemmerUtil.endsWith(s, len, "\u043e\u0433\u043e") || StemmerUtil.endsWith(s, len, "\u043e\u043c\u0443") || StemmerUtil.endsWith(s, len, "\u044b\u043c\u0438") || StemmerUtil.endsWith(s, len, "\u043e\u0435\u0432"))) {
            return len - 3;
        }
        if (len > 4 && (StemmerUtil.endsWith(s, len, "\u0430\u044f") || StemmerUtil.endsWith(s, len, "\u044f\u044f") || StemmerUtil.endsWith(s, len, "\u044f\u0445") || StemmerUtil.endsWith(s, len, "\u044e\u044e") || StemmerUtil.endsWith(s, len, "\u0430\u0445") || StemmerUtil.endsWith(s, len, "\u0435\u044e") || StemmerUtil.endsWith(s, len, "\u0438\u0445") || StemmerUtil.endsWith(s, len, "\u0438\u044f") || StemmerUtil.endsWith(s, len, "\u0438\u044e") || StemmerUtil.endsWith(s, len, "\u044c\u0432") || StemmerUtil.endsWith(s, len, "\u043e\u044e") || StemmerUtil.endsWith(s, len, "\u0443\u044e") || StemmerUtil.endsWith(s, len, "\u044f\u043c") || StemmerUtil.endsWith(s, len, "\u044b\u0445") || StemmerUtil.endsWith(s, len, "\u0435\u044f") || StemmerUtil.endsWith(s, len, "\u0430\u043c") || StemmerUtil.endsWith(s, len, "\u0435\u043c") || StemmerUtil.endsWith(s, len, "\u0435\u0439") || StemmerUtil.endsWith(s, len, "\u0451\u043c") || StemmerUtil.endsWith(s, len, "\u0435\u0432") || StemmerUtil.endsWith(s, len, "\u0438\u0439") || StemmerUtil.endsWith(s, len, "\u0438\u043c") || StemmerUtil.endsWith(s, len, "\u043e\u0435") || StemmerUtil.endsWith(s, len, "\u043e\u0439") || StemmerUtil.endsWith(s, len, "\u043e\u043c") || StemmerUtil.endsWith(s, len, "\u043e\u0432") || StemmerUtil.endsWith(s, len, "\u044b\u0435") || StemmerUtil.endsWith(s, len, "\u044b\u0439") || StemmerUtil.endsWith(s, len, "\u044b\u043c") || StemmerUtil.endsWith(s, len, "\u043c\u0438"))) {
            return len - 2;
        }
        if (len > 3) {
            switch (s[len - 1]) {
                case '\u0430':
                case '\u0435':
                case '\u0438':
                case '\u0439':
                case '\u043e':
                case '\u0443':
                case '\u044b':
                case '\u044c':
                case '\u044f': {
                    return len - 1;
                }
            }
        }
        return len;
    }
}
