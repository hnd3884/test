package org.apache.lucene.analysis.ar;

import org.apache.lucene.analysis.util.StemmerUtil;

public class ArabicStemmer
{
    public static final char ALEF = '\u0627';
    public static final char BEH = '\u0628';
    public static final char TEH_MARBUTA = '\u0629';
    public static final char TEH = '\u062a';
    public static final char FEH = '\u0641';
    public static final char KAF = '\u0643';
    public static final char LAM = '\u0644';
    public static final char NOON = '\u0646';
    public static final char HEH = '\u0647';
    public static final char WAW = '\u0648';
    public static final char YEH = '\u064a';
    public static final char[][] prefixes;
    public static final char[][] suffixes;
    
    public int stem(final char[] s, int len) {
        len = this.stemPrefix(s, len);
        len = this.stemSuffix(s, len);
        return len;
    }
    
    public int stemPrefix(final char[] s, final int len) {
        for (int i = 0; i < ArabicStemmer.prefixes.length; ++i) {
            if (this.startsWithCheckLength(s, len, ArabicStemmer.prefixes[i])) {
                return StemmerUtil.deleteN(s, 0, len, ArabicStemmer.prefixes[i].length);
            }
        }
        return len;
    }
    
    public int stemSuffix(final char[] s, int len) {
        for (int i = 0; i < ArabicStemmer.suffixes.length; ++i) {
            if (this.endsWithCheckLength(s, len, ArabicStemmer.suffixes[i])) {
                len = StemmerUtil.deleteN(s, len - ArabicStemmer.suffixes[i].length, len, ArabicStemmer.suffixes[i].length);
            }
        }
        return len;
    }
    
    boolean startsWithCheckLength(final char[] s, final int len, final char[] prefix) {
        if (prefix.length == 1 && len < 4) {
            return false;
        }
        if (len < prefix.length + 2) {
            return false;
        }
        for (int i = 0; i < prefix.length; ++i) {
            if (s[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }
    
    boolean endsWithCheckLength(final char[] s, final int len, final char[] suffix) {
        if (len < suffix.length + 2) {
            return false;
        }
        for (int i = 0; i < suffix.length; ++i) {
            if (s[len - suffix.length + i] != suffix[i]) {
                return false;
            }
        }
        return true;
    }
    
    static {
        prefixes = new char[][] { "\u0627\u0644".toCharArray(), "\u0648\u0627\u0644".toCharArray(), "\u0628\u0627\u0644".toCharArray(), "\u0643\u0627\u0644".toCharArray(), "\u0641\u0627\u0644".toCharArray(), "\u0644\u0644".toCharArray(), "\u0648".toCharArray() };
        suffixes = new char[][] { "\u0647\u0627".toCharArray(), "\u0627\u0646".toCharArray(), "\u0627\u062a".toCharArray(), "\u0648\u0646".toCharArray(), "\u064a\u0646".toCharArray(), "\u064a\u0647".toCharArray(), "\u064a\u0629".toCharArray(), "\u0647".toCharArray(), "\u0629".toCharArray(), "\u064a".toCharArray() };
    }
}
