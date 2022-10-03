package org.apache.lucene.analysis.ar;

import org.apache.lucene.analysis.util.StemmerUtil;

public class ArabicNormalizer
{
    public static final char ALEF = '\u0627';
    public static final char ALEF_MADDA = '\u0622';
    public static final char ALEF_HAMZA_ABOVE = '\u0623';
    public static final char ALEF_HAMZA_BELOW = '\u0625';
    public static final char YEH = '\u064a';
    public static final char DOTLESS_YEH = '\u0649';
    public static final char TEH_MARBUTA = '\u0629';
    public static final char HEH = '\u0647';
    public static final char TATWEEL = '\u0640';
    public static final char FATHATAN = '\u064b';
    public static final char DAMMATAN = '\u064c';
    public static final char KASRATAN = '\u064d';
    public static final char FATHA = '\u064e';
    public static final char DAMMA = '\u064f';
    public static final char KASRA = '\u0650';
    public static final char SHADDA = '\u0651';
    public static final char SUKUN = '\u0652';
    
    public int normalize(final char[] s, int len) {
        for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u0622':
                case '\u0623':
                case '\u0625': {
                    s[i] = '\u0627';
                    break;
                }
                case '\u0649': {
                    s[i] = '\u064a';
                    break;
                }
                case '\u0629': {
                    s[i] = '\u0647';
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
            }
        }
        return len;
    }
}
