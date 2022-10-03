package org.apache.lucene.analysis.ckb;

import org.apache.lucene.analysis.util.StemmerUtil;

public class SoraniNormalizer
{
    static final char YEH = '\u064a';
    static final char DOTLESS_YEH = '\u0649';
    static final char FARSI_YEH = '\u06cc';
    static final char KAF = '\u0643';
    static final char KEHEH = '\u06a9';
    static final char HEH = '\u0647';
    static final char AE = '\u06d5';
    static final char ZWNJ = '\u200c';
    static final char HEH_DOACHASHMEE = '\u06be';
    static final char TEH_MARBUTA = '\u0629';
    static final char REH = '\u0631';
    static final char RREH = '\u0695';
    static final char RREH_ABOVE = '\u0692';
    static final char TATWEEL = '\u0640';
    static final char FATHATAN = '\u064b';
    static final char DAMMATAN = '\u064c';
    static final char KASRATAN = '\u064d';
    static final char FATHA = '\u064e';
    static final char DAMMA = '\u064f';
    static final char KASRA = '\u0650';
    static final char SHADDA = '\u0651';
    static final char SUKUN = '\u0652';
    
    public int normalize(final char[] s, int len) {
        for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u0649':
                case '\u064a': {
                    s[i] = '\u06cc';
                    break;
                }
                case '\u0643': {
                    s[i] = '\u06a9';
                    break;
                }
                case '\u200c': {
                    if (i > 0 && s[i - 1] == '\u0647') {
                        s[i - 1] = '\u06d5';
                    }
                    len = StemmerUtil.delete(s, i, len);
                    --i;
                    break;
                }
                case '\u0647': {
                    if (i == len - 1) {
                        s[i] = '\u06d5';
                        break;
                    }
                    break;
                }
                case '\u0629': {
                    s[i] = '\u06d5';
                    break;
                }
                case '\u06be': {
                    s[i] = '\u0647';
                    break;
                }
                case '\u0631': {
                    if (i == 0) {
                        s[i] = '\u0695';
                        break;
                    }
                    break;
                }
                case '\u0692': {
                    s[i] = '\u0695';
                    break;
                }
                case '\u0640':
                case '\u064b':
                case '\u064c':
                case '\u064d':
                case '\u064e':
                case '\u064f':
                case '\u0650':
                case '\u0651':
                case '\u0652': {
                    len = StemmerUtil.delete(s, i, len);
                    --i;
                    break;
                }
                default: {
                    if (Character.getType(s[i]) == 16) {
                        len = StemmerUtil.delete(s, i, len);
                        --i;
                        break;
                    }
                    break;
                }
            }
        }
        return len;
    }
}
