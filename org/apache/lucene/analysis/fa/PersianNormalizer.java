package org.apache.lucene.analysis.fa;

import org.apache.lucene.analysis.util.StemmerUtil;

public class PersianNormalizer
{
    public static final char YEH = '\u064a';
    public static final char FARSI_YEH = '\u06cc';
    public static final char YEH_BARREE = '\u06d2';
    public static final char KEHEH = '\u06a9';
    public static final char KAF = '\u0643';
    public static final char HAMZA_ABOVE = '\u0654';
    public static final char HEH_YEH = '\u06c0';
    public static final char HEH_GOAL = '\u06c1';
    public static final char HEH = '\u0647';
    
    public int normalize(final char[] s, int len) {
        for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u06cc':
                case '\u06d2': {
                    s[i] = '\u064a';
                    break;
                }
                case '\u06a9': {
                    s[i] = '\u0643';
                    break;
                }
                case '\u06c0':
                case '\u06c1': {
                    s[i] = '\u0647';
                    break;
                }
                case '\u0654': {
                    len = StemmerUtil.delete(s, i, len);
                    --i;
                    break;
                }
            }
        }
        return len;
    }
}
